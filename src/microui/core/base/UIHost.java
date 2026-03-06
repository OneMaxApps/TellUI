package microui.core.base;

import static processing.core.PApplet.map;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import microui.MicroUI;
import microui.core.ImageBuffer;
import microui.core.effect.Transition;
import microui.core.exception.DuplicateItemException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.event.PointerManager;
import microui.layout.LayoutManager;
import microui.service.TooltipManager;
import microui.service.ValueOverlayManager;
import microui.util.Debugger;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public final class UIHost extends View {
	private static UIHost instance;
	private final ContainerManager containerManager;
	private final TooltipManager tooltipManager;
	private final ValueOverlayManager valueOverlayManager;
	
	private UIHost() {
		setVisible(true);
		
		containerManager = new ContainerManager();
		tooltipManager = TooltipManager.getInstance();
		valueOverlayManager = ValueOverlayManager.getInstance();
		
		new Renderer(this);
	}	

	public static UIHost getInstance() {
		if (instance == null) {
			instance = new UIHost();
		}

		return instance;
	}
	
	public void addContainer(Container container) {
		containerManager.add(container);
	}
	
	public void addContainer(LayoutManager layoutManager) {
		containerManager.add(new Container(layoutManager));
	}
	
	public void addContainer(LayoutManager layoutManager, String textId) {
		containerManager.add((Container) new Container(layoutManager).setTextId(textId));
	}
	
	public void addContainer(LayoutManager layoutManager, int id) {
		containerManager.add((Container) new Container(layoutManager).setId(id));
	}

	public void removeContainer(Container container) {
		containerManager.remove(container);
	}
	
	public void removeContainer(String textId) {
		containerManager.remove(containerManager.get(textId));
	}
	
	public void removeContainer(int id) {
		containerManager.remove(containerManager.get(id));
	}
	
	public void navigateTo(Container container) {
		containerManager.navigateTo(container);
	}
	
	public void navigateTo(String textId) {
		containerManager.navigateTo(containerManager.get(textId));
	}
	
	public void navigateTo(int id) {
		containerManager.navigateTo(containerManager.get(id));
	}
	
	public void navigateTo(Container container, Transition transition) {
		containerManager.transitionManager.setTransition(transition);
		containerManager.navigateTo(container);
	}
	
	public void navigateTo(String textId, Transition transition) {
		containerManager.transitionManager.setTransition(transition);
		containerManager.navigateTo(containerManager.get(textId));
	}
	
	public void navigateTo(int id, Transition transition) {
		containerManager.transitionManager.setTransition(transition);
		containerManager.navigateTo(containerManager.get(id));
	}

	public boolean isTransitionEnabled() {
		return containerManager.transitionManager.isEnabled();
	}
	
	public void setTransitionEnabled(boolean enabled) {
		containerManager.transitionManager.setEnabled(enabled);
	}
	
	public float getTransitionProgressStep() {
		return containerManager.transitionManager.getProgressStep();
	}
	
	public void setTransitionProgressStep(float step) {
		containerManager.transitionManager.setProgressStep(step);
	}
	
	@Override
	protected void render() {
		containerManager.draw();
		tooltipManager.draw();
		valueOverlayManager.draw();
	}
	
	public static final class Renderer {
		private final UIHost uiHost;
		
		private Renderer(UIHost uiHost) {
			this.uiHost = uiHost;
			
			MicroUI.getContext().registerMethod("draw", this);
		}
		
		public void draw() {
			uiHost.draw();
			
			if (!ctx.mousePressed) {
				PointerManager.release();
			}
		}
		
	}
	
	public static final class ContainerManager extends View implements Scrollable, KeyPressable {
		private final List<Container> list;
		private final TransitionManager transitionManager;
		private Container current, previous;
		
		private ContainerManager() {
			setVisible(true);
			list = new ArrayList<Container>();
			transitionManager = new TransitionManager();
			ctx.registerMethod("keyEvent", this);
			ctx.registerMethod("mouseEvent", this);
		}
		
		@Override
		protected void render() {
			transitionManager.update();
			
			if (transitionManager.isActivated()) {
				transitionManager.draw();
			} else {
				if (current != null) {
					current.draw();
				}
			}
			
		}
		
		public void add(Container container) {
			addInternal(container);
		}
		
		public void remove(Container container) {
			removeInternal(container);
		}
		
		public Container find(String textId) {
			Objects.requireNonNull(textId,"textId");
			
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getTextId().equals(textId)) {
					return c;
				}
			}
			
			return null;
		}
		
		public Container find(int id) {
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getId() == id) {
					return c;
				}
			}
			
			return null;
		}
		
		public Container get(String textId) {
			final var c = find(textId);
			
			if(c == null) {
				throw new NoSuchElementException("Container with text id: " + textId +" not found");
			}
			
			return c;
		}
		
		public Container get(int id) {
			final var c = find(id);
			
			if(c == null) {
				throw new NoSuchElementException("Container with id: " + id +" not found");
			}
			
			return c;
		}
		
		public void navigateTo(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (!list.contains(container)) {
				throw new NoSuchElementException("Container not found");
			}
			
			previous = current;
			current = container;
			
			transitionManager.activate();
		}
		
		@Override
		public void keyPressed(KeyEvent keyEvent) {
			if(transitionManager.isEnabled() && transitionManager.isActivated()) {
				return;
			}
			
			if (current != null) {
				current.keyPressed(keyEvent);
			}
		}
		
		@Override
		public void mouseWheel(MouseEvent mouseEvent) {
			if(transitionManager.isEnabled() && transitionManager.isActivated()) {
				return;
			}
			
			if (current != null) {
				current.mouseWheel(mouseEvent);
			}
		}
		
		public void keyEvent(KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.PRESS) {

				if (Debugger.isHotKeyEnabled()) {
					if (keyEvent.isAltDown()) {
						Debugger.setEnabled(!Debugger.isEnabled());
					}
				}
				
				keyPressed(keyEvent);
			}
		}
		
		public void mouseEvent(MouseEvent mouseEvent) {
			if (mouseEvent.getAction() == MouseEvent.WHEEL) {
				mouseWheel(mouseEvent);
			}
		}
		
		private void addInternal(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (list.contains(container)) {
				throw new DuplicateItemException("Container already added");
			}
			
			if (current == null) {
				current = container;
			}
			
			container.setConstrainDimensionsEnabled(true);
			container.setMinMaxSize(ctx.width,ctx.height,ctx.width,ctx.height);
			
			list.add(container);
		}
		
		private void removeInternal(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (!list.contains(container)) {
				throw new NoSuchElementException("Container not found");
			}
			
			if (current == container) {
				current = null;
			}
			
			if (previous == container) {
				previous = null;
			}
			
			list.remove(container);
		}
		
		private final class TransitionManager extends View {
			private static final int TIMER_START = 0;
			private static final int TIMER_END = 1;
			private static final int MIN_PROGRESS_STEP = 0;
			private static final int MAX_PROGRESS_STEP = 1;
			private static final float DEFAULT_PROGRESS_STEP = .02f;
			
			private ImageBuffer currentImage, previousImage;
			private Transition transition;
			private float timer, progressStep;
			private boolean enabled, activated;
			
			public TransitionManager() {
				setVisible(true);
				
				setEnabled(true);
				setProgressStep(DEFAULT_PROGRESS_STEP);
				
				currentImage = new ImageBuffer();
				previousImage = new ImageBuffer();
				
				transition = new Transition();
				
				setTransition(Transition.SLIDE_LEFT);
			}
			
			@Override
			protected void render() {
				if (!enabled) {
					return;
				}
				
				if (activated) {
					currentImage.draw();
					previousImage.draw();
				}
			}
			
			public void update() {
				if (!enabled) {
					return;
				}
				
				if (activated) {
					
					if (!previousImage.isLoaded()) {
						previous.draw();
						previousImage.set(ctx.get(0,0,ctx.width,ctx.height));
					}

					if (!currentImage.isLoaded()) {
						current.draw();
						
						if (previous.isVisible()) {
							previous.setVisible(false);
						} else {
							currentImage.set(ctx.get(0,0,ctx.width,ctx.height));
							previous.setVisible(true);
						}
					}
					
					
					if(timer < TIMER_END) {
						timer += progressStep;
					} else {
						activated = false;
						timer = TIMER_START;
					}
					
					final var t = transition;
					
					final float cx = lerpFromTime(t.getCurrentStart().getX(), t.getCurrentEnd().getX());
					final float cy = lerpFromTime(t.getCurrentStart().getY(), t.getCurrentEnd().getY());
					final float cw = lerpFromTime(t.getCurrentStart().getWidth(), t.getCurrentEnd().getWidth());
					final float ch = lerpFromTime(t.getCurrentStart().getHeight(), t.getCurrentEnd().getHeight());
					
					currentImage.setBounds(cx, cy, cw, ch);
					
					final float px = lerpFromTime(t.getPreviousStart().getX(), t.getPreviousEnd().getX());
					final float py = lerpFromTime(t.getPreviousStart().getY(), t.getPreviousEnd().getY());
					final float pw = lerpFromTime(t.getPreviousStart().getWidth(), t.getPreviousEnd().getWidth());
					final float ph = lerpFromTime(t.getPreviousStart().getHeight(), t.getPreviousEnd().getHeight());
					
					previousImage.setBounds(px, py, pw, ph);
				}
				
				if(!activated) {
					if (currentImage.isLoaded()) {
						currentImage.removeTexture();
					}
					
					if (previousImage.isLoaded()) {
						previousImage.removeTexture();
					}
				}
			}


			public final float getProgressStep() {
				return progressStep;
			}

			public final void setProgressStep(float progressStep) {
				if (progressStep < MIN_PROGRESS_STEP || progressStep > MAX_PROGRESS_STEP) {
					throw new IllegalArgumentException("Progress step must be between " + MIN_PROGRESS_STEP + " and " + MAX_PROGRESS_STEP);
				}
				this.progressStep = progressStep;
			}

			public boolean isActivated() {
				return activated;
			}
			
			public void activate() {
				activated = true;
			}
			
			public void setTransition(Transition transition) {
				this.transition = Objects.requireNonNull(transition,"transition");
			}

			public final boolean isEnabled() {
				return enabled;
			}

			public final void setEnabled(boolean enabled) {
				this.enabled = enabled;
			}
		
			private float lerpFromTime(float start, float end) {
				return map(timer,TIMER_START, TIMER_END, start, end);
			}
		}
	}
}