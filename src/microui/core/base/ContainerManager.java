package microui.core.base;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;
import static microui.util.Debugger.getAdditionalInfo;
import static microui.util.Debugger.isDebugModeEnabled;
import static microui.util.Debugger.isHotKeySwitchEnabled;
import static microui.util.Debugger.setDebugModeEnabled;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import microui.core.ImageBuffer;
import microui.core.exception.DuplicateItemException;
import microui.core.exception.RenderException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.event.KeyboardManager;
import microui.service.TooltipManager;
import microui.util.MathUtils;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

//Status: STABLE - Do not modify
//Last Reviewed: 29.10.2025

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

	@Override
	public void draw() {
		if (!canDraw) {
			throw new RenderException("Cannot call draw() manually. set FLEXIBLE render mode first");
		}
		super.draw();
		debugOnDraw();

	}

	@Override
	public void keyPressed() {
		if (currentContainer == null) {
			return;
		}

		currentContainer.keyPressed();
	}

	public void keyEvent(KeyEvent keyEvent) {
		requireNonNull(keyEvent,"keyEvent");
		
		if (keyEvent.getAction() == KeyEvent.PRESS) {

			if (isHotKeySwitchEnabled()) {
				if (keyEvent.isAltDown()) {
					setDebugModeEnabled(!isDebugModeEnabled());
				}
			}

			KeyboardManager.keyPressed();
			keyPressed();
		}
		if (keyEvent.getAction() == KeyEvent.RELEASE) {
			KeyboardManager.keyReleased();
		}
	}

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

	public void mouseEvent(MouseEvent mouseEvent) {
		mouseWheel(mouseEvent);
	}

	public AnimatorMode getAnimatorMode() {
		return animator.getAnimatorMode();
	}

	public void setAnimatorMode(AnimatorMode animatorMode) {
		animator.setAnimatorMode(animatorMode);
	}

	public float getAnimatorSpeed() {
		return animator.getRawSpeed();
	}

	public void setAnimatorSpeed(float speed) {
		animator.setSpeed(speed);
	}

	public boolean isAnimatorEasingEnabled() {
		return animator.isEasingEnabled();
	}

	public void setAnimatorEasingEnabled(boolean easing) {
		animator.setEasingEnabled(easing);
	}

	public boolean isAnimatorEnabled() {
		return animatorEnabled;
	}

	public void setAnimatorEnabled(boolean animatorEnabled) {
		this.animatorEnabled = animatorEnabled;
	}

	public void add(Container container) {
		addInternal(container);
	}

	public void add(Container container, String textId) {
		addInternal(container);

		container.setTextId(textId);
	}

	public void add(Container container, int id) {
		addInternal(container);
		container.setId(id);
	}

	public void remove(Container... containers) {
		requireNonNull(containers, "containers");

		for (Container container : containers) {
			removeInternal(container);
		}

	}

	public void removeById(int... ids) {
		requireNonNull(ids, "Ids");

		for (int i : ids) {
			removeInternal(i);
		}

	}

	public void removeByTextId(final String... textIds) {
		requireNonNull(textIds, "textIds");

		for (String currentTextId : textIds) {
			removeInternal(currentTextId);
		}

	}

	public void switchOn(Container container) {
		launchContainer(container);
	}

	public void switchOn(Container container, AnimatorMode animatorMode) {
		setAnimatorMode(animatorMode);
		switchOn(container);
	}

	public void switchOn(int id) {
		launchContainer(getById(id));
	}

	public void switchOn(int id, AnimatorMode animatorMode) {
		setAnimatorMode(animatorMode);
		switchOn(id);
	}

	public void switchOn(String textId) {
		launchContainer(getByTextId(textId));
	}

	public void switchOn(String textId, AnimatorMode animatorMode) {
		setAnimatorMode(animatorMode);
		switchOn(textId);
	}

	public void switchOnPrevious() {
		if (animator.isAnimating()) {
			return;
		}

		switchOn(getPreviousContainer());
	}

	public void switchOnNext() {
		if (animator.isAnimating()) {
			return;
		}

		switchOn(getNextContainer());
	}

	public Container findById(final int id) {
		for (int i = 0; i < list.size(); i++) {
			Container c = list.get(i);
			if (c.getId() == id) {
				return c;
			}
		}

		return null;
	}

	public Container getById(int id) {
		final Container c = findById(id);

		if (c == null) {
			throw new NoSuchElementException("Container with id: " + id + " not found");
		}

		return c;
	}

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

	public Container getByTextId(String textId) {
		final Container c = findByTextId(textId);

		if (c == null) {
			throw new NoSuchElementException("Container with textId: " + textId + " not found");
		}

		return c;
	}

	public static final boolean isInitialized() {
		return initialized;
	}

	public static final boolean canDraw() {
		return canDraw;
	}

	public static ContainerManager getInstance() {
		if (instance == null) {
			instance = new ContainerManager();
			initialized = true;
		}

		return instance;
	}

	public static enum AnimatorMode {
		SLIDE_LEFT, SLIDE_RIGHT, SLIDE_TOP, SLIDE_BOTTOM, SLIDE_RANDOM;
	}

	public final class Render {

		private Render() {
			getContext().registerMethod("draw", this);
		}

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
		if (isDebugModeEnabled()) {
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