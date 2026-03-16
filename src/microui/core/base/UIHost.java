package microui.core.base;

import static microui.util.MathUtils.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import microui.MicroUI;
import microui.core.ImageBuffer;
import microui.core.effect.Transition;
import microui.core.exception.DuplicateItemException;
import microui.core.exception.RenderException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.interfaces.ValuePreviewSource;
import microui.event.Listener;
import microui.event.PointerManager;
import microui.feedback.TooltipManager;
import microui.feedback.ValueOverlayManager;
import microui.layout.LayoutManager;
import microui.util.Debugger;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public final class UIHost extends View {
	private static UIHost instance;
	private final ContainerManager containerManager;
	private final TooltipManager tooltipManager;
	private final ValueOverlayManager valueOverlayManager;
	private final SurfaceResizeManager surfaceResizeManager;
	private final ToastManager toastManager;
	
	private UIHost() {
		setVisible(true);
		
		containerManager = new ContainerManager();
		tooltipManager = TooltipManager.getInstance();
		valueOverlayManager = ValueOverlayManager.getInstance();
		surfaceResizeManager = new SurfaceResizeManager();
		surfaceResizeManager.addListener(() -> {
			containerManager.onResize();
		});
		
		toastManager = new ToastManager();
		
		new HostBridge(this);
	}

	public static UIHost getInstance() {
		if (instance == null) {
			instance = new UIHost();
		}

		return instance;
	}
	
	
	// == CONTAINER MANAGER API == // 
	
	public void addContainer(Container container) {
		containerManager.add(container);
	}
	
	public void addContainer(LayoutManager layoutManager) {
		containerManager.add(layoutManager);
	}
	
	public void addContainer(LayoutManager layoutManager, String textId) {
		containerManager.add(layoutManager, textId);
	}
	
	public void addContainer(LayoutManager layoutManager, int id) {
		containerManager.add(layoutManager, id);
	}

	public void removeContainer(Container container) {
		containerManager.remove(container);
	}
	
	public void removeContainer(String textId) {
		containerManager.remove(textId);
	}
	
	public void removeContainer(int id) {
		containerManager.remove(id);
	}
	
	public Container getContainer(String textId) {
		return containerManager.get(textId);
	}
	
	public Container getContainer(int id) {
		return containerManager.get(id);
	}
	
	public void navigateTo(Container container) {
		containerManager.navigateTo(container);
	}
	
	public void navigateTo(String textId) {
		containerManager.navigateTo(textId);
	}
	
	public void navigateTo(int id) {
		containerManager.navigateTo(id);
	}
	
	public void navigateTo(Container container, Transition transition) {
		containerManager.navigateTo(container, transition);
	}
	
	public void navigateTo(String textId, Transition transition) {
		containerManager.navigateTo(textId, transition);
	}
	
	public void navigateTo(int id, Transition transition) {
		containerManager.navigateTo(id, transition);
	}

	public boolean isTransitionEnabled() {
		return containerManager.isTransitionEnabled();
	}
	
	public void setTransitionEnabled(boolean enabled) {
		containerManager.setTransitionEnabled(enabled);
	}
	
	public float getTransitionProgressStep() {
		return containerManager.getTransitionProgressStep();
	}
	
	public void setTransitionProgressStep(float step) {
		containerManager.setTransitionProgressStep(step);
	}
	
	
	// == SURFACE API == //
	
	public void setIcon(PImage icon) {
		Objects.requireNonNull(icon,"icon");
		ctx.getSurface().setIcon(icon);
	}
	
	public void setTitle(String title) {
		Objects.requireNonNull(title,"title");
		ctx.getSurface().setTitle(title);
	}
	
	public void setLocation(int x, int y) {
		ctx.getSurface().setLocation(x, y);
	}
	
	public void setResizable(boolean enabled) {
		ctx.getSurface().setResizable(enabled);
	}
	
	public void addOnSurfaceResizeListener(Listener listener) {
		surfaceResizeManager.addListener(listener);
	}
	
	public void removeOnSurfaceResizeListener(Listener listener) {
		surfaceResizeManager.removeListener(listener);
	}
	
	
	
	// == OVERLAY HINT MANAGER API == //
	
	public void setToast(String text, long ms) {
		toastManager.setToast(text, ms);
	}
	
	public void setToast(String text) {
		toastManager.setToast(text, 1000);
	}
	
	@Override
	protected void render() {
		if (!HostBridge.isAllowed()) {
			throw new RenderException("Cannot call draw() of UIHost manually");
		}
		
		surfaceResizeManager.listen();
		
		containerManager.draw();
		tooltipManager.draw();
		
		toastManager.listen();
		valueOverlayManager.draw();
		
		if (Debugger.isEnabled() && Debugger.isShowFpsEnabled()) {
			System.out.println("fps: " + ctx.frameRate);
		}
		
	}
	
	private void keyEvent(KeyEvent keyEvent) {
		containerManager.keyEvent(keyEvent);
	}
	
	private void mouseEvent(MouseEvent mouseEvent) {
		containerManager.mouseEvent(mouseEvent);
	}

	public static final class HostBridge {
		private final UIHost uiHost;
		private static boolean allowed;
		
		private HostBridge(UIHost uiHost) {
			this.uiHost = uiHost;
			
			ctx.registerMethod("draw", this);
			ctx.registerMethod("keyEvent", this);
			ctx.registerMethod("mouseEvent", this);
		}
		
		public void draw() {
			allowed = true;
			uiHost.draw();
			allowed = false;
			
			if (!ctx.mousePressed) {
				PointerManager.release();
			}
		}
		
		public void keyEvent(KeyEvent keyEvent) {
			uiHost.keyEvent(keyEvent);
		}
		
		public void mouseEvent(MouseEvent mouseEvent) {
			uiHost.mouseEvent(mouseEvent);
		}
		
		public static boolean isAllowed() {
			return allowed;
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
			
		public void add(Container container) {
			addInternal(container);
		}
		
		public void add(LayoutManager layoutManager) {
			addInternal(new Container(layoutManager));
		}
		
		public void add(LayoutManager layoutManager, String textId) {
			addInternal((Container) new Container(layoutManager).setTextId(textId));
		}
		
		public void add(LayoutManager layoutManager, int id) {
			addInternal((Container) new Container(layoutManager).setId(id));
		}
		
		public void remove(Container container) {
			removeInternal(container);
		}
		
		public void remove(String textId) {
			removeInternal(getInternal(textId));
		}
		
		public void remove(int id) {
			removeInternal(getInternal(id));
		}
		
		public Container find(String textId) {
			return findInternal(textId);
		}
		
		public Container find(int id) {
			return findInternal(id);
		}
		
		public Container get(String textId) {
			return getInternal(textId);
		}
		
		public Container get(int id) {
			return getInternal(id);
		}
		
		public void navigateTo(Container container) {
			navigateToInternal(container);
		}
		
		public void navigateTo(String textId) {
			navigateToInternal(getInternal(textId));
		}
		
		public void navigateTo(int id) {
			navigateToInternal(getInternal(id));
		}
		
		public void navigateTo(Container container, Transition transition) {
			setTransition(transition);
			navigateToInternal(container);
		}
		
		public void navigateTo(String textId, Transition transition) {
			setTransition(transition);
			navigateToInternal(getInternal(textId));
		}
		
		public void navigateTo(int id, Transition transition) {
			setTransition(transition);
			navigateToInternal(getInternal(id));
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
		
		// == TRANSITION MANAGER FACADE API == //
		
		public void setTransition(Transition transition) {
			transitionManager.setTransition(transition);
		}
		
		public boolean isTransitionEnabled() {
			return transitionManager.isEnabled();
		}
		
		public void setTransitionEnabled(boolean enabled) {
			transitionManager.setEnabled(enabled);
		}
		
		public float getTransitionProgressStep() {
			return transitionManager.getProgressStep();
		}

		public void setTransitionProgressStep(float progressStep) {
			transitionManager.setProgressStep(progressStep);
		}

		public void onResize() {
			for (int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				final var minWidth = Math.min(ctx.width,c.getMinWidth());
				final var maxWidth = Math.max(ctx.width,c.getMaxWidth());
				final var minHeight = Math.min(ctx.height,c.getMinHeight());
				final var maxHeight = Math.max(ctx.height,c.getMaxHeight());
				
				c.setConstrainDimensionsEnabled(false);
				c.setSize(ctx.width,ctx.height);
				
				c.setMinMaxSize(minWidth,minHeight,maxWidth,maxHeight);
				
				c.setConstrainDimensionsEnabled(true);
			}
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

			if (transitionManager.isActivated()) {
				if (current == container || previous == container) {
					throw new IllegalStateException("Cannot remove container when transition of containers activated");
				}
			}
			
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
		
		private Container findInternal(String textId) {
			Objects.requireNonNull(textId,"textId");
			
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getTextId().equals(textId)) {
					return c;
				}
			}
			
			return null;
		}
		
		private Container findInternal(int id) {
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getId() == id) {
					return c;
				}
			}
			
			return null;
		}
		
		private Container getInternal(String textId) {
			final var c = findInternal(textId);
			
			if(c == null) {
				throw new NoSuchElementException("Container with text id: " + textId +" not found");
			}
			
			return c;
		}
		
		private Container getInternal(int id) {
			final var c = find(id);
			
			if(c == null) {
				throw new NoSuchElementException("Container with id: " + id +" not found");
			}
			
			return c;
		}
		
		private void navigateToInternal(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (!list.contains(container)) {
				throw new NoSuchElementException("Container not found");
			}
			
			if (current == container) {
				throw new IllegalStateException("Cannot navigate to itself");
			}
			
			previous = current;
			current = container;
			
			transitionManager.activate();
		}
		
		private final class TransitionManager extends View {
			private static final int TIMER_START = 0;
			private static final int TIMER_END = 1;
			private static final float MIN_PROGRESS_STEP = .00000001f;
			private static final int MAX_PROGRESS_STEP = 1;
			private static final float DEFAULT_PROGRESS_STEP = .1f;
			
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
					activated = false;
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
				return convert(timer,TIMER_START, TIMER_END, start, end);
			}
		}

	}

	private static final class SurfaceResizeManager {
		private final PApplet context;
		private final List<Listener> listenerList;
		private int cachedWidth, cachedHeight;
		
		
		private SurfaceResizeManager() {
			context = MicroUI.getContext();
			listenerList = new ArrayList<Listener>();
			
			validateDimensions();
		}
		
		public void addListener(Listener listener) {
			Objects.requireNonNull(listener,"listener");
			
			if (listenerList.contains(listener)) {
				throw new DuplicateItemException("Listener already added");
			}
			
			listenerList.add(listener);
		}
		
		public void removeListener(Listener listener) {
			Objects.requireNonNull(listener,"listener");
			
			if (!listenerList.contains(listener)) {
				throw new NoSuchElementException("Listener not found");
			}
			
			listenerList.remove(listener);
		}
		
		public void listen() {
			if (isResized()) {
				validateDimensions();
			}
		}
		
		private boolean isResized() {
			return cachedWidth != context.width || cachedHeight != context.height;
		}
		
		private void validateDimensions() {
			cachedWidth = context.width;
			cachedHeight = context.height;
			
			notifyListeners();
		}
		
		private void notifyListeners() {
			if (!listenerList.isEmpty()) {
				for (int i = 0; i < listenerList.size(); i++) {
					listenerList.get(i).action();
				}
			}
		}
	}

	private final static class ToastManager implements ValuePreviewSource {
		private static final int DEFAULT_MS = 0;
		private String text;
		private long ms;
		private long lastUpdateTime;
		
		public ToastManager() {
			validateLastUpdateTime();
		}
		
		@Override
		public String getSource() {
			return text;
		}

		@Override
		public boolean isContentPrepared() {
			return ms > DEFAULT_MS && text != null;
		}
		
		public void listen() {
			if (isContentPrepared()) {
				final long currentMs = System.currentTimeMillis();
				final long delta =  currentMs - lastUpdateTime;
				
				if (delta > 0) {
					lastUpdateTime = currentMs;
					ms -= delta;
				}
				
				ValueOverlayManager.getInstance().setSource(this);
			}
		}
		
		public void setToast(String text, long ms) {
			setToast(text);
			setMs(ms);
			validateLastUpdateTime();
		}

		private void setToast(String text) {
			Objects.requireNonNull(text, "text");
			if (text.isBlank()) {
				throw new IllegalArgumentException("Text cannot be blank");
			}
			
			this.text = text;
		}

		private void setMs(long ms) {
			if (ms <= DEFAULT_MS) {
				throw new IllegalArgumentException("Milliseconds should be greater than " + DEFAULT_MS);
			}
			
			this.ms = ms;
		}
		
		private void validateLastUpdateTime() {
			lastUpdateTime = System.currentTimeMillis();
		}
	}
}