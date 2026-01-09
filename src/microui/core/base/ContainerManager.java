package microui.core.base;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;
import static microui.util.Debugger.getAdditionalInfo;
import static microui.util.Debugger.isHotKeySwitchEnabled;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import microui.core.ImageBuffer;
import microui.core.exception.DuplicateItemException;
import microui.core.exception.RenderException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.service.TooltipManager;
import microui.util.Debugger;
import microui.util.MathUtils;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * Singleton manager for handling multiple Container instances and providing animation transitions between them.
 * Manages the lifecycle, rendering, and event handling for containers in a GUI application.
 * <p>
 * This class implements the Singleton pattern and is responsible for:
 * - Managing a collection of Container objects
 * - Handling transitions between containers with various animation effects
 * - Propagating keyboard and mouse events to the active container
 * - Managing tooltips and debug information
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 8.11.2025
 * </p>
 * 
 * @see Container
 * @see AnimatorMode
 */
public final class ContainerManager extends View implements Scrollable, KeyPressable {
	private static ContainerManager instance;
	private static boolean initialized, canDraw;
	private final List<Container> list;
	private final Animator animator;
	private final TooltipManager tooltipManager;
	private Container prevContainer, currentContainer;
	private boolean animatorEnabled;

	private ContainerManager() {
		setVisible(true);
		list = new ArrayList<Container>();
		animator = new Animator(this);
		tooltipManager = TooltipManager.getInstance();

		setAnimatorEnabled(true);

		getContext().registerMethod("keyPressed", this);
		getContext().registerMethod("keyEvent", this);
		getContext().registerMethod("mouseEvent", this);

		new Render();
	}

	/**
	 * Renders the current state of the ContainerManager.
	 * If animator is enabled, uses animator for drawing; otherwise draws current container directly.
	 */
	@Override
	public void render() {
		if (isAnimatorEnabled()) {
			animator.draw();
		} else {
			if (currentContainer != null) {
				currentContainer.draw();
			}
		}

		tooltipManager.draw();
	}

	/**
	 * Draws the ContainerManager and debug information if enabled.
	 * 
	 * @throws RenderException if draw() is called manually without FLEXIBLE render mode
	 */
	@Override
	public void draw() {
		if (!canDraw) {
			throw new RenderException("Cannot call draw() manually. set FLEXIBLE render mode first");
		}
		super.draw();
		debugOnDraw();

	}

	/**
	 * Handles key press events by propagating them to the current container.
	 */
	@Override
	public void keyPressed() {
		if (currentContainer == null) {
			return;
		}

		currentContainer.keyPressed();
	}
	
	/**
	 * Handles key press events with KeyEvent details by propagating them to the current container.
	 * 
	 * @param e the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (currentContainer == null) {
			return;
		}

		currentContainer.keyPressed(e);
	}

	/**
	 * Processes key events from the Processing environment.
	 * Handles debug hotkeys and propagates events to keyboard manager and containers.
	 * 
	 * @param keyEvent the key event from Processing
	 * @throws NullPointerException if keyEvent is null
	 */
	public void keyEvent(KeyEvent keyEvent) {
		requireNonNull(keyEvent,"keyEvent");
		
		if (keyEvent.getAction() == KeyEvent.PRESS) {

			if (isHotKeySwitchEnabled()) {
				if (keyEvent.isAltDown()) {
					Debugger.setEnabled(!Debugger.isEnabled());
				}
			}

			keyPressed();
			keyPressed(keyEvent);
		}
	}

	/**
	 * Handles mouse wheel events by propagating them to the current container.
	 * 
	 * @param mouseEvent the mouse wheel event
	 */
	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		requireNonNull(mouseEvent,"mouseEvent");
		
		if (currentContainer == null) {
			return;
		}

		if (mouseEvent.getAction() == MouseEvent.WHEEL) {
			currentContainer.mouseWheel(mouseEvent);
		}

	}

	/**
	 * Processes mouse events from the Processing environment.
	 * Currently only handles mouse wheel events.
	 * 
	 * @param mouseEvent the mouse event from Processing
	 */
	public void mouseEvent(MouseEvent mouseEvent) {
		mouseWheel(mouseEvent);
	}

	/**
	 * Returns the current animator mode.
	 * 
	 * @return the current animator mode
	 */
	public AnimatorMode getAnimatorMode() {
		return animator.getAnimatorMode();
	}

	/**
	 * Sets the animator mode for container transitions.
	 * 
	 * @param animatorMode the animator mode to set
	 */
	public void setAnimatorMode(AnimatorMode animatorMode) {
		animator.setAnimatorMode(animatorMode);
	}

	/**
	 * Returns the raw animation speed.
	 * 
	 * @return the raw animation speed value
	 */
	public float getAnimatorSpeed() {
		return animator.getRawSpeed();
	}

	/**
	 * Sets the animation speed.
	 * 
	 * @param speed the speed to set (must be greater than 0)
	 * @throws IllegalArgumentException if speed is less than or equal to 0
	 */
	public void setAnimatorSpeed(float speed) {
		animator.setSpeed(speed);
	}

	/**
	 * Checks if easing is enabled for animations.
	 * 
	 * @return true if easing is enabled, false otherwise
	 */
	public boolean isAnimatorEasingEnabled() {
		return animator.isEasingEnabled();
	}

	/**
	 * Enables or disables easing for animations.
	 * 
	 * @param easing true to enable easing, false to disable
	 */
	public void setAnimatorEasingEnabled(boolean easing) {
		animator.setEasingEnabled(easing);
	}

	/**
	 * Checks if the animator is enabled.
	 * 
	 * @return true if animator is enabled, false otherwise
	 */
	public boolean isAnimatorEnabled() {
		return animatorEnabled;
	}

	/**
	 * Enables or disables the animator for container transitions.
	 * 
	 * @param animatorEnabled true to enable animator, false to disable
	 */
	public void setAnimatorEnabled(boolean animatorEnabled) {
		this.animatorEnabled = animatorEnabled;
	}

	/**
	 * Adds a container to the manager.
	 * 
	 * @param container the container to add
	 */
	public void add(Container container) {
		addInternal(container);
	}

	/**
	 * Adds a container to the manager with a text ID.
	 * 
	 * @param container the container to add
	 * @param textId the text identifier for the container
	 */
	public void add(Container container, String textId) {
		addInternal(container);

		container.setTextId(textId);
	}

	/**
	 * Adds a container to the manager with a numeric ID.
	 * 
	 * @param container the container to add
	 * @param id the numeric identifier for the container
	 */
	public void add(Container container, int id) {
		addInternal(container);
		container.setId(id);
	}

	/**
	 * Removes one or more containers from the manager.
	 * 
	 * @param containers the containers to remove
	 * @throws NullPointerException if containers is null
	 */
	public void remove(Container... containers) {
		requireNonNull(containers, "containers");

		for (Container container : containers) {
			removeInternal(container);
		}

	}

	/**
	 * Removes containers by their numeric IDs.
	 * 
	 * @param ids the numeric IDs of containers to remove
	 * @throws NullPointerException if ids is null
	 */
	public void removeById(int... ids) {
		requireNonNull(ids, "Ids");

		for (int i : ids) {
			removeInternal(i);
		}

	}

	/**
	 * Removes containers by their text IDs.
	 * 
	 * @param textIds the text IDs of containers to remove
	 * @throws NullPointerException if textIds is null
	 */
	public void removeByTextId(final String... textIds) {
		requireNonNull(textIds, "textIds");

		for (String currentTextId : textIds) {
			removeInternal(currentTextId);
		}

	}

	/**
	 * Switches to the specified container with animation.
	 * 
	 * @param container the container to switch to
	 */
	public void switchOn(Container container) {
		launchContainer(container);
	}

	/**
	 * Switches to the specified container with a specific animation mode.
	 * 
	 * @param container the container to switch to
	 * @param animatorMode the animation mode to use
	 */
	public void switchOn(Container container, AnimatorMode animatorMode) {
		setAnimatorMode(animatorMode);
		switchOn(container);
	}

	/**
	 * Switches to a container by its numeric ID.
	 * 
	 * @param id the numeric ID of the container to switch to
	 */
	public void switchOn(int id) {
		launchContainer(getById(id));
	}

	/**
	 * Switches to a container by its numeric ID with a specific animation mode.
	 * 
	 * @param id the numeric ID of the container to switch to
	 * @param animatorMode the animation mode to use
	 */
	public void switchOn(int id, AnimatorMode animatorMode) {
		setAnimatorMode(animatorMode);
		switchOn(id);
	}

	/**
	 * Switches to a container by its text ID.
	 * 
	 * @param textId the text ID of the container to switch to
	 */
	public void switchOn(String textId) {
		launchContainer(getByTextId(textId));
	}

	/**
	 * Switches to a container by its text ID with a specific animation mode.
	 * 
	 * @param textId the text ID of the container to switch to
	 * @param animatorMode the animation mode to use
	 */
	public void switchOn(String textId, AnimatorMode animatorMode) {
		setAnimatorMode(animatorMode);
		switchOn(textId);
	}

	/**
	 * Switches to the previous container in the list (circular navigation).
	 */
	public void switchOnPrevious() {
		if (animator.isAnimating()) {
			return;
		}

		switchOn(getPreviousContainer());
	}

	/**
	 * Switches to the next container in the list (circular navigation).
	 */
	public void switchOnNext() {
		if (animator.isAnimating()) {
			return;
		}

		switchOn(getNextContainer());
	}

	/**
	 * Searches for a container by its numeric ID.
	 * 
	 * @param id the numeric ID to search for
	 * @return the container with the specified ID, or null if not found
	 */
	public Container findById(final int id) {
		for (int i = 0; i < list.size(); i++) {
			Container c = list.get(i);
			if (c.getId() == id) {
				return c;
			}
		}

		return null;
	}

	/**
	 * Retrieves a container by its numeric ID.
	 * 
	 * @param id the numeric ID of the container to retrieve
	 * @return the container with the specified ID
	 * @throws NoSuchElementException if no container with the specified ID is found
	 */
	public Container getById(int id) {
		final Container c = findById(id);

		if (c == null) {
			throw new NoSuchElementException("Container with id: " + id + " not found");
		}

		return c;
	}

	/**
	 * Searches for a container by its text ID.
	 * 
	 * @param textId the text ID to search for (cannot be null)
	 * @return the container with the specified text ID, or null if not found
	 * @throws NullPointerException if textId is null
	 */
	public Container findByTextId(final String textId) {
		requireNonNull(textId, "textId");

		for (int i = 0; i < list.size(); i++) {
			Container c = list.get(i);
			if (c.getTextId().equals(textId)) {
				return c;
			}
		}

		return null;
	}

	/**
	 * Retrieves a container by its text ID.
	 * 
	 * @param textId the text ID of the container to retrieve (cannot be null)
	 * @return the container with the specified text ID
	 * @throws NullPointerException if textId is null
	 * @throws NoSuchElementException if no container with the specified text ID is found
	 */
	public Container getByTextId(String textId) {
		final Container c = findByTextId(textId);

		if (c == null) {
			throw new NoSuchElementException("Container with textId: " + textId + " not found");
		}

		return c;
	}

	/**
	 * Checks if the ContainerManager has been initialized.
	 * 
	 * @return true if initialized, false otherwise
	 */
	public static final boolean isInitialized() {
		return initialized;
	}

	/**
	 * Checks if manual drawing is allowed.
	 * 
	 * @return true if manual drawing is allowed, false otherwise
	 */
	public static final boolean canDraw() {
		return canDraw;
	}

	/**
	 * Returns the singleton instance of ContainerManager.
	 * Creates the instance if it doesn't exist.
	 * 
	 * @return the singleton ContainerManager instance
	 */
	public static ContainerManager getInstance() {
		if (instance == null) {
			instance = new ContainerManager();
			initialized = true;
		}

		return instance;
	}

	/**
	 * Animation modes for container transitions.
	 */
	public static enum AnimatorMode {
		/** Slide animation from left to right. */
		SLIDE_LEFT, 
		/** Slide animation from right to left. */
		SLIDE_RIGHT, 
		/** Slide animation from top to bottom. */
		SLIDE_TOP, 
		/** Slide animation from bottom to top. */
		SLIDE_BOTTOM, 
		/** Random slide animation direction. */
		SLIDE_RANDOM;
	}

	/**
	 * Internal class for handling automatic rendering through Processing's draw cycle.
	 */
	public final class Render {

		private Render() {
			getContext().registerMethod("draw", this);
		}

		/**
		 * Called by Processing's draw cycle to render the ContainerManager.
		 */
		public void draw() {
			canDraw = true;
			ContainerManager.this.draw();
			canDraw = false;
		}
	}

	private int getCurrentContainerIndex() {
		return list.indexOf(currentContainer);
	}

	private int getPreviousContainerIndex() {
		final int curr = getCurrentContainerIndex();

		if (curr == 0) {
			return list.size() - 1;
		}

		return curr - 1;
	}

	private int getNextContainerIndex() {
		final int curr = getCurrentContainerIndex();

		if (curr == list.size() - 1) {
			return 0;
		}

		return curr + 1;
	}

	private Container getPreviousContainer() {
		return list.get(getPreviousContainerIndex());
	}

	private Container getNextContainer() {
		return list.get(getNextContainerIndex());
	}

	private void launchContainer(Container container) {
		requireNonNull(container, "container");

		if (list.size() < 2) {
			throw new IllegalStateException(
					"Cannot switch containers, because ContainerManager has less than 2 containers");
		}

		if (!list.contains(container)) {
			throw new NoSuchElementException("Container not found in ContainerManager");
		}

		prevContainer = currentContainer;
		currentContainer = container;
		animator.setAnimating(true);
	}

	private void addInternal(Container container) {
		requireNonNull(container, "container");

		if (list.contains(container)) {
			throw new DuplicateItemException("Container cannot be added twice");
		}

		container.setConstrainDimensionsEnabled(true);
		container.setMaxSize(ctx.width, ctx.height);
		container.setMinSize(ctx.width, ctx.height);

		list.add(container);

		if (currentContainer == null) {
			currentContainer = container;
		}
	}

	private void removeInternal(Container container) {
		requireNonNull(container, "container");

		if (!list.contains(container)) {
			throw new NoSuchElementException("Container not found in ContainerManager");
		}

		list.remove(container);

		if (prevContainer == container) {
			prevContainer = null;
		}

		if (currentContainer == container) {
			currentContainer = null;
		}
	}

	private void removeInternal(int id) {
		removeInternal(getById(id));
	}

	private void removeInternal(String textId) {
		removeInternal(getByTextId(textId));
	}

	private void debugOnDraw() {
		if (Debugger.isEnabled()) {
			ctx.push();
			ctx.fill(255);
			ctx.textSize(24);
			ctx.text("fps: " + (int) ctx.frameRate + "\n" + getAdditionalInfo(), 10, 24);
			ctx.pop();
		}
	}

	private static final class Animator extends View {
		private final ContainerManager manager;
		private static final float MAX_DIST = MathUtils.dist(0, 0, ctx.width, ctx.height);
		private final ImageBuffer prevImage, currentImage;
		private static final byte[][] DIRECTIONS = {{-1,0},{1,0},{0,-1},{0,1}};
		private AnimatorMode animatorMode;
		private float speed;
		private byte randDirX, randDirY;
		private boolean animating, newContainerPrepared, easing;

		private Animator(ContainerManager manager) {
			super();
			setVisible(true);

			this.manager = requireNonNull(manager, "manager");

			prevImage = new ImageBuffer();
			currentImage = new ImageBuffer();

			prevImage.setSize(ctx.width, ctx.height);
			currentImage.setSize(ctx.width, ctx.height);

			setSpeed(max(1, ctx.width * .1f));
			setAnimatorMode(AnimatorMode.SLIDE_RANDOM);
			setEasingEnabled(true);
		}

		public boolean isAnimating() {
			return animating;
		}

		public void setAnimating(boolean animating) {
			this.animating = animating;
		}

		public float getRawSpeed() {
			return speed;
		}

		public void setSpeed(float speed) {
			if (speed <= 0) {
				throw new IllegalArgumentException("Animator speed must be greater than 0");
			}
			this.speed = speed;
		}

		public AnimatorMode getAnimatorMode() {
			return animatorMode;
		}

		public void setAnimatorMode(AnimatorMode animatorMode) {
			this.animatorMode = requireNonNull(animatorMode, "animatorMode");
		}

		public boolean isEasingEnabled() {
			return easing;
		}

		public void setEasingEnabled(boolean easing) {
			this.easing = easing;
		}
		
		@Override
		protected void render() {
			if (isAnimating()) {
				update();
			}

			if (!isAnimating()) {
				if (prevImage.get() != null) {
					prevImage.removeTexture();
				}

				if (currentImage.get() != null) {
					currentImage.removeTexture();
				}

				if (manager.currentContainer != null) {
					manager.currentContainer.draw();
				}
			}

		}

		private void update() {
			if (!prevImage.isLoaded()) {
				manager.prevContainer.draw();
				prevImage.set(getScreenBuffer());
				return;
			}

			switch (animatorMode) {
			case SLIDE_LEFT:
				slideDirection(-1, 0);
				break;
			case SLIDE_RIGHT:
				slideDirection(1, 0);
				break;
			case SLIDE_TOP:
				slideDirection(0, -1);
				break;
			case SLIDE_BOTTOM:
				slideDirection(0, 1);
				break;
			case SLIDE_RANDOM:
				if (!newContainerPrepared) {
					final byte[] d = DIRECTIONS[(byte) ctx.random(DIRECTIONS.length)];
					randDirX = d[0];
					randDirY = d[1];
				}
				slideDirection(randDirX, randDirY);
				break;
			}

			if (isAnimating()) {
				if (!currentImage.isLoaded()) {
					manager.currentContainer.draw();
					currentImage.set(getScreenBuffer());
				}

				prevImage.draw();
				currentImage.draw();
			}
		}

		private PImage getScreenBuffer() {
			return ctx.get(0, 0, ctx.width, ctx.height);
		}

		private int getSpeedInternal() {
			int additionalMinSpeed = 1;
			if (easing) {
				float dist = max(abs(currentImage.getX()), abs(currentImage.getY()));
				return (int) (additionalMinSpeed + MathUtils.convert(abs(dist), 0, MAX_DIST, .1f, speed));
			}
			return (int) (additionalMinSpeed + speed);
		}

		private void complete() {
			prevImage.setBounds(0, 0, ctx.width, ctx.height);
			currentImage.setBoundsFrom(prevImage);
			setAnimating(false);
			newContainerPrepared = false;
		}

		private void slideDirection(int dirX, int dirY) {
			if (!newContainerPrepared) {
				currentImage.setPosition(dirX == -1 ? ctx.width : dirX == 1 ? -ctx.width : 0,
						dirY == -1 ? ctx.height : dirY == 1 ? -ctx.height : 0);
				newContainerPrepared = true;
			}

			if (dirX == -1) {
				prevImage.appendX(-getSpeedInternal());
				currentImage.appendX(-getSpeedInternal(), 0, ctx.width);
			}

			if (dirX == 1) {
				prevImage.appendX(getSpeedInternal());
				currentImage.appendX(getSpeedInternal(), -ctx.width, 0);
			}

			if (dirY == -1) {
				prevImage.appendY(-getSpeedInternal());
				currentImage.appendY(-getSpeedInternal(), 0, ctx.height);
			}

			if (dirY == 1) {
				prevImage.appendY(getSpeedInternal());
				currentImage.appendY(getSpeedInternal(), -ctx.height, 0);
			}

			if (dirX != 0) {
				if ((int) currentImage.getX() == 0) {
					complete();
				}
			}

			if (dirY != 0) {
				if ((int) currentImage.getY() == 0) {
					complete();
				}
			}
		}

	}

}