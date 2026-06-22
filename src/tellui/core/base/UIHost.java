package tellui.core.base;

import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.UP;
import static tellui.util.MathUtils.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import tellui.TellUI;
import tellui.core.ImageBuffer;
import tellui.core.effect.Transition;
import tellui.core.exception.DuplicateItemException;
import tellui.core.exception.RenderException;
import tellui.core.exception.ValueOutOfRangeException;
import tellui.core.interfaces.KeyPressable;
import tellui.core.interfaces.Listener;
import tellui.core.interfaces.Scrollable;
import tellui.core.interfaces.ValuePreviewSource;
import tellui.event.PointerManager;
import tellui.feedback.TooltipManager;
import tellui.feedback.ValueOverlayManager;
import tellui.layout.LayoutManager;
import tellui.util.Debugger;

/**
 * Manages the core UI lifecycle, including overlay information, container manager,
 * toast system, transition system, and surface control.
 */
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

	/**
	 * Returns the singleton instance of UIHost.
	 * 
	 * @return the instance of UIHost
	 */
	public static UIHost getInstance() {
		if (instance == null) {
			instance = new UIHost();
		}

		return instance;
	}
	
	
	// == CONTAINER MANAGER API == // 
	
	/**
	 * Adds a container to the container manager.
	 *
	 * @param container the container to add (must not be {@code null})
	 * @return the added container
	 */
	public Container addContainer(Container container) {
		return containerManager.add(container);
	}
	
	/**
	 * Creates and adds a new container with the specified layout manager.
	 *
	 * @param layoutManager the layout manager for the new container (must not be {@code null})
	 * @return the newly created container
	 */
	public Container addContainer(LayoutManager layoutManager) {
		return containerManager.add(layoutManager);
	}
	
	/**
	 * Creates and adds a new container with the specified layout manager and text identifier.
	 *
	 * @param layoutManager the layout manager for the new container (must not be {@code null})
	 * @param textId        the text identifier for the container
	 * @return the newly created container with the given {@code textId}
	 */
	public Container addContainer(LayoutManager layoutManager, String textId) {
		return containerManager.add(layoutManager, textId);
	}
	
	/**
	 * Creates and adds a new container with the specified layout manager and numeric identifier.
	 *
	 * @param layoutManager the layout manager for the new container (must not be {@code null})
	 * @param id            the numeric identifier for the container
	 * @return the newly created container with the given {@code id}
	 */
	public Container addContainer(LayoutManager layoutManager, int id) {
		return containerManager.add(layoutManager, id);
	}
	
	/**
	 * Removes the specified container from the container manager.
	 *
	 * @param container the container to remove (must not be {@code null})
	 */
	public void removeContainer(Container container) {
		containerManager.remove(container);
	}
	
	/**
	 * Removes the container with the specified text identifier.
	 *
	 * @param textId the text identifier of the container to remove (must not be {@code null})
	 */
	public void removeContainer(String textId) {
		containerManager.remove(textId);
	}
	
	/**
	 * Removes the container with the specified numeric identifier.
	 *
	 * @param id the numeric identifier of the container to remove
	 */
	public void removeContainer(int id) {
		containerManager.remove(id);
	}
	
	/**
	 * Retrieves the container with the specified text identifier.
	 *
	 * @param textId the text identifier of the container (must not be {@code null} or blank)
	 * @return the container with the given {@code textId}
	 */
	public Container getContainer(String textId) {
		return containerManager.get(textId);
	}
	
	/**
	 * Retrieves the container with the specified numeric identifier.
	 *
	 * @param id the numeric identifier of the container
	 * @return the container with the given {@code id}
	 */
	public Container getContainer(int id) {
		return containerManager.get(id);
	}
	
	/**
	 * Finds a container by its text identifier. Returns {@code null} if not found.
	 *
	 * @param textId the text identifier of the container (may be {@code null})
	 * @return the container with the given {@code textId}, or {@code null} if none found
	 */
	public Container findContainer(String textId) {
		return containerManager.find(textId);
	}
	
	/**
	 * Finds a container by its numeric identifier. Returns {@code null} if not found.
	 *
	 * @param id the numeric identifier of the container
	 * @return the container with the given {@code id}, or {@code null} if none found
	 */
	public Container findContainer(int id) {
		return containerManager.find(id);
	}
	
	/**
	 * Navigates to the specified container with the default transition.
	 *
	 * @param container the target container (must not be {@code null})
	 */
	public void navigateTo(Container container) {
		containerManager.navigateTo(container);
	}
	
	/**
	 * Navigates to the container with the specified text identifier using the default transition.
	 *
	 * @param textId the text identifier of the target container (must not be {@code null})
	 */
	public void navigateTo(String textId) {
		containerManager.navigateTo(textId);
	}
	
	/**
	 * Navigates to the container with the specified numeric identifier using the default transition.
	 *
	 * @param id the numeric identifier of the target container
	 */
	public void navigateTo(int id) {
		containerManager.navigateTo(id);
	}
	
	/**
	 * Navigates to the specified container using the given transition.
	 *
	 * @param container  the target container (must not be {@code null})
	 * @param transition the transition effect to use (must not be {@code null})
	 */
	public void navigateTo(Container container, Transition transition) {
		containerManager.navigateTo(container, transition);
	}
	
	/**
	 * Navigates to the specified container using a predefined transition identified by a constant.
	 * The constant must be one of {@code LEFT}, {@code RIGHT}, {@code UP}, or {@code DOWN}
	 * from {@link processing.core.PConstants}.
	 *
	 * @param container  the target container (must not be {@code null})
	 * @param transition the transition constant (LEFT, RIGHT, UP, DOWN)
	 */
	public void navigateTo(Container container, int transition) {
		containerManager.navigateTo(container, transition);
	}
	
	/**
	 * Navigates to the container with the specified text identifier using the given transition.
	 *
	 * @param textId     the text identifier of the target container (must not be {@code null})
	 * @param transition the transition effect to use (must not be {@code null})
	 */
	public void navigateTo(String textId, Transition transition) {
		containerManager.navigateTo(textId, transition);
	}
	
	/**
	 * Navigates to the container with the specified text identifier using a predefined transition constant.
	 *
	 * @param textId     the text identifier of the target container (must not be {@code null})
	 * @param transition the transition constant (LEFT, RIGHT, UP, DOWN)
	 */
	public void navigateTo(String textId, int transition) {
		containerManager.navigateTo(textId, transition);
	}
	
	/**
	 * Navigates to the container with the specified numeric identifier using the given transition.
	 *
	 * @param id         the numeric identifier of the target container
	 * @param transition the transition effect to use (must not be {@code null})
	 */
	public void navigateTo(int id, Transition transition) {
		containerManager.navigateTo(id, transition);
	}
	
	/**
	 * Navigates to the container with the specified numeric identifier using a predefined transition constant.
	 *
	 * @param id         the numeric identifier of the target container
	 * @param transition the transition constant (LEFT, RIGHT, UP, DOWN)
	 */
	public void navigateTo(int id, int transition) {
		containerManager.navigateTo(id, transition);
	}

	/**
	 * Returns whether transitions are enabled globally.
	 *
	 * @return {@code true} if transitions are enabled, {@code false} otherwise
	 */
	public boolean isTransitionEnabled() {
		return containerManager.isTransitionEnabled();
	}
	
	/**
	 * Enables or disables transitions globally.
	 *
	 * @param enabled {@code true} to enable transitions, {@code false} to disable
	 */
	public void setTransitionEnabled(boolean enabled) {
		containerManager.setTransitionEnabled(enabled);
	}
	
	/**
	 * Returns the current progress step used for transition animations.
	 *
	 * @return the progress step value (between 0 and 1)
	 */
	public float getTransitionProgressStep() {
		return containerManager.getTransitionProgressStep();
	}
	
	/**
	 * Sets the progress step for transition animations.
	 * The step determines how fast the transition progresses.
	 *
	 * @param step the progress step (must be between {@code MIN_PROGRESS_STEP} and {@code MAX_PROGRESS_STEP})
	 */
	public void setTransitionProgressStep(float step) {
		containerManager.setTransitionProgressStep(step);
	}
	
	
	// == SURFACE API == //
	
	/**
	 * Sets the icon for the application surface (window).
	 *
	 * @param icon the icon image (must not be {@code null})
	 * @throws NullPointerException if {@code icon} is {@code null}
	 */
	public void setIcon(PImage icon) {
		Objects.requireNonNull(icon,"icon");
		ctx.getSurface().setIcon(icon);
	}
	
	/**
	 * Sets the title of the application surface (window).
	 *
	 * @param title the window title (must not be {@code null})
	 * @throws NullPointerException if {@code title} is {@code null}
	 */
	public void setTitle(String title) {
		Objects.requireNonNull(title,"title");
		ctx.getSurface().setTitle(title);
	}
	
	/**
	 * Sets the location of the application surface on the screen.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void setLocation(int x, int y) {
		ctx.getSurface().setLocation(x, y);
	}
	
	/**
	 * Sets whether the application surface is resizable by the user.
	 *
	 * @param enabled {@code true} to allow resizing, {@code false} to disallow
	 */
	public void setResizable(boolean enabled) {
		ctx.getSurface().setResizable(enabled);
	}
	
	/**
	 * Adds a listener to be notified when the surface is resized.
	 *
	 * @param listener the listener to add (must not be {@code null})
	 */
	public void addOnSurfaceResizeListener(Listener listener) {
		surfaceResizeManager.addListener(listener);
	}
	
	/**
	 * Removes a previously added surface resize listener.
	 *
	 * @param listener the listener to remove (must not be {@code null})
	 */
	public void removeOnSurfaceResizeListener(Listener listener) {
		surfaceResizeManager.removeListener(listener);
	}
	
	
	
	// == OVERLAY HINT MANAGER API == //
	
	/**
	 * Shows a toast message for the specified duration.
	 *
	 * @param text the toast message (must not be {@code null} or blank)
	 * @param ms the duration in milliseconds (must be positive)
	 */
	public void setToast(String text, long ms) {
		toastManager.setToast(text, ms);
	}
	
	/**
	 * Shows a toast message for the default duration (1000 ms).
	 *
	 * @param text the toast message (must not be {@code null} or blank)
	 */
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

	/**
	 * Provides the bridge between Processing and TellUI.
	 */
	public static final class HostBridge {
		private final UIHost uiHost;
		private static boolean allowed;
		
		private HostBridge(UIHost uiHost) {
			this.uiHost = uiHost;
			
			ctx.registerMethod("draw", this);
			ctx.registerMethod("keyEvent", this);
			ctx.registerMethod("mouseEvent", this);
		}
		
		/**
		 * Called by Processing's draw loop. Do not call manually.
		 * This method triggers the UI rendering and manages internal state.
		 */
		public void draw() {
			allowed = true;
			uiHost.draw();
			allowed = false;
			
			if (!ctx.mousePressed) {
				PointerManager.release();
			}
		}
		
		/**
		 * Called by Processing on key events. Dispatches the event to the container manager.
		 *
		 * @param keyEvent the Processing key event
		 */
		public void keyEvent(KeyEvent keyEvent) {
			uiHost.keyEvent(keyEvent);
		}
		
		/**
		 * Called by Processing on mouse events. Dispatches the event to the container manager.
		 *
		 * @param mouseEvent the Processing mouse event
		 */
		public void mouseEvent(MouseEvent mouseEvent) {
			uiHost.mouseEvent(mouseEvent);
		}
		
		/**
		 * Checks whether it is allowed to call {@code draw()} on UIHost directly.
		 * Used internally to prevent manual rendering.
		 *
		 * @return {@code true} if {@code draw()} is being called from the host bridge, {@code false} otherwise
		 */
		public static boolean isAllowed() {
			return allowed;
		}
		
	}
	
	private static final class ContainerManager extends View implements Scrollable, KeyPressable {
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
			
		public Container add(Container container) {
			return addInternal(container);
		}
		
		public Container add(LayoutManager layoutManager) {
			return addInternal(new Container(layoutManager));
		}
		
		public Container add(LayoutManager layoutManager, String textId) {
			return addInternal((Container) new Container(layoutManager).setTextId(textId));
		}
		
		public Container add(LayoutManager layoutManager, int id) {
			return addInternal((Container) new Container(layoutManager).setId(id));
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
		
		public void navigateTo(Container container, int transition) {
			setTransition(transition);
			navigateToInternal(container);
		}
		
		public void navigateTo(String textId, Transition transition) {
			setTransition(transition);
			navigateToInternal(getInternal(textId));
		}
		
		public void navigateTo(String textId, int transition) {
			setTransition(transition);
			navigateToInternal(getInternal(textId));
		}
		
		public void navigateTo(int id, Transition transition) {
			setTransition(transition);
			navigateToInternal(getInternal(id));
		}
		
		public void navigateTo(int id, int transition) {
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
		
		public void setTransition(int transition) {
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
		
		private Container addInternal(Container container) {
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
			
			return container;
		}
		
		private void removeInternal(Container container) {
			Objects.requireNonNull(container,"container");

			if (transitionManager.isActivated()) {
				if (current == container || previous == container) {
					throw new IllegalStateException("Cannot remove container while a transition is active");
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
			
			if (textId.isBlank()) {
				throw new IllegalArgumentException("TextId cannot be blank");
			}
			
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getTextId().equals(textId)) {
					return c;
				}
			}
			
			return null;
		}
		
		private Container findInternal(int id) {
			if (id <= View.MIN_ID) {
				throw new IllegalArgumentException("Id cannot be equal or lower than MIN_ID (0)");
			}
			
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
			final var c = findInternal(id);
			
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
				
				timer = TIMER_START;
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
						previousImage.set(getScreenshot());
					}

					if (!currentImage.isLoaded()) {
						current.draw();
						
						if (previous.isVisible()) {
							previous.setVisible(false);
						} else {
							currentImage.set(getScreenshot());
							previous.setVisible(true);
						}
					}
					
					if(timer < TIMER_END && previousImage.isLoaded() && currentImage.isLoaded()) {
						timer += progressStep;
						
					} else {
						if (timer >= TIMER_END) {
							activated = false;
							timer = TIMER_START;
						}
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
					throw new ValueOutOfRangeException("progressStep", progressStep, MIN_PROGRESS_STEP, MAX_PROGRESS_STEP);
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
			
			public void setTransition(int transition) {
				switch (transition) {
					case LEFT :
						this.transition = Transition.SLIDE_LEFT;
						break;
						
					case RIGHT :
						this.transition = Transition.SLIDE_RIGHT;
						break;
						
					case UP :
						this.transition = Transition.SLIDE_UP;
						break;
						
					case DOWN :
						this.transition = Transition.SLIDE_DOWN;
						break;
					default:
						throw new IllegalArgumentException("Transition constant must be only LEFT, RIGHT, UP or DOWN");
				}
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
			
			private PImage getScreenshot() {
				return ctx.get(0, 0, ctx.width, ctx.height);
			}
		}

	}

	private static final class SurfaceResizeManager {
		private final PApplet context;
		private final List<Listener> listenerList;
		private int cachedWidth, cachedHeight;
		
		
		private SurfaceResizeManager() {
			context = TellUI.getContext();
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
				throw new IllegalArgumentException("Milliseconds must be greater than " + DEFAULT_MS);
			}
			
			this.ms = ms;
		}
		
		private void validateLastUpdateTime() {
			lastUpdateTime = System.currentTimeMillis();
		}
	}
}