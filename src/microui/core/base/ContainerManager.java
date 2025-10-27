package microui.core.base;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;

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
import microui.util.Debugger;
import microui.util.MathUtils;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

//Status: STABLE - Do not modify
//Last Reviewed: 27.10.2025
public final class ContainerManager extends View implements Scrollable, KeyPressable {
	private static ContainerManager instance;
	private static boolean isInitialized, isCanDraw;
	private final List<Container> containerList;
	private final Animation animation;
	private final TooltipManager tooltipManager;
	private Container prevContainer, currentContainer;
	private boolean isAnimationEnabled;

	private ContainerManager() {
		setVisible(true);
		containerList = new ArrayList<Container>();
		animation = new Animation();
		tooltipManager = TooltipManager.getInstance();

		setAnimationEnabled(true);

		getContext().registerMethod("keyPressed", this);
		getContext().registerMethod("keyEvent", this);
		getContext().registerMethod("mouseEvent", this);
		new Render();
	}

	@Override
	public void render() {
		if (isAnimationEnabled()) {
			animation.draw();
		} else {
			if (currentContainer != null) {
				currentContainer.draw();
			}
		}

		tooltipManager.draw();
	}

	@Override
	public void draw() {
		if (!isCanDraw) {
			throw new RenderException("Cannot call draw() manually. Enable flexibleRenderMode first");
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
		if (keyEvent.getAction() == KeyEvent.PRESS) {
			if (Debugger.isHotKeySwitchEnabled()) {
				if (keyEvent.isAltDown()) {
					Debugger.setDebugModeEnabled(!Debugger.isDebugModeEnabled());
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

	public AnimationType getAnimationType() {
		return animation.getAnimationType();
	}

	public void setAnimationType(AnimationType animationType) {
		animation.setAnimationType(animationType);
	}

	public float getAnimationSpeed() {
		return animation.getRawSpeed();
	}

	public void setAnimationSpeed(float speed) {
		animation.setSpeed(speed);
	}

	public boolean isAnimationEasingEnabled() {
		return animation.isEasingEnabled();
	}

	public void setAnimationEasingEnabled(boolean isEasing) {
		animation.setEasingEnabled(isEasing);
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

	public void remove(final Container... containers) {
		requireNonNull(containers, "containers");

		for (Container container : containers) {
			removeInternal(container);
		}

	}

	public void removeById(final int... ids) {
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

	public void switchOn(Container container, AnimationType animationType) {
		setAnimationType(animationType);
		switchOn(container);
	}

	public void switchOn(int id) {
		launchContainer(getById(id));
	}

	public void switchOn(int id, AnimationType animationType) {
		setAnimationType(animationType);
		switchOn(id);
	}

	public void switchOn(String textId) {
		launchContainer(getByTextId(textId));
	}

	public void switchOn(String textId, AnimationType animationType) {
		setAnimationType(animationType);
		switchOn(textId);
	}

	public void switchOnPrevious() {
		if (animation.isAnimationRunningEnabled()) {
			return;
		}
		
		switchOn(getPreviousContainer());
	}

	public void switchOnNext() {
		if (animation.isAnimationRunningEnabled()) {
			return;
		}

		switchOn(getNextContainer());
	}

	public Container findById(final int id) {
		for (int i = 0; i < containerList.size(); i++) {
			Container c = containerList.get(i);
			if (c.getId() == id) {
				return c;
			}
		}
		
		return null;
	}

	public Container getById(int id) {
		final Container c = findById(id);
		
		if (c == null) {
			throw new NoSuchElementException("Container with id: " + id + " not found in ContainerManager");
		}
		
		return c;
	}

	public Container findByTextId(final String textId) {
		requireNonNull(textId,"textId");
		
		for (int i = 0; i < containerList.size(); i++) {
			Container c = containerList.get(i);
			if (c.getTextId().equals(textId)) {
				return c;
			}
		}
		
		return null;
	}

	public Container getByTextId(String textId) {
		final Container c = findByTextId(textId);
		
		if(c == null) {
			throw new NoSuchElementException("Container with textId: " + textId + " not found in ContainerManager");
		}
		
		return c;
	}

	public boolean isAnimationEnabled() {
		return isAnimationEnabled;
	}

	public void setAnimationEnabled(boolean isAnimationEnabled) {
		this.isAnimationEnabled = isAnimationEnabled;
	}

	public static final boolean isInitialized() {
		return isInitialized;
	}

	public static final boolean isCanDraw() {
		return isCanDraw;
	}

	public static enum AnimationType {
		SLIDE_LEFT, SLIDE_RIGHT, SLIDE_TOP, SLIDE_BOTTOM, SLIDE_RANDOM;
	}

	public static ContainerManager getInstance() {
		if (instance == null) {
			instance = new ContainerManager();
			isInitialized = true;
		}

		return instance;
	}

	public final class Render {

		private Render() {
			getContext().registerMethod("draw", this);
		}

		public void draw() {
			isCanDraw = true;
			ContainerManager.this.draw();
			isCanDraw = false;
		}
	}
	
	private int getCurrentContainerIndex() {
		return containerList.indexOf(currentContainer);
	}
	
	private int getPreviousContainerIndex() {
		final int curr = getCurrentContainerIndex();
		
		if(curr == 0) {
			return containerList.size()-1;
		}
		
		return curr-1;
	}
	
	private int getNextContainerIndex() {
		final int curr = getCurrentContainerIndex();
		
		if(curr == containerList.size()-1) {
			return 0;
		}
		
		return curr+1;
	}
	
	private Container getPreviousContainer() {
		return containerList.get(getPreviousContainerIndex());
	}
	
	private Container getNextContainer() {
		return containerList.get(getNextContainerIndex());
	}

	private void launchContainer(Container container) {
		requireNonNull(container,"container");
		
		if (containerList.size() < 2) {
			throw new IllegalStateException("Cannot switch containers, because ContainerManager has less than 2 containers");
		}
		
		if (!containerList.contains(container)) {
			throw new NoSuchElementException("Container not found in ContainerManager");
		}

		prevContainer = currentContainer;
		currentContainer = container;
		animation.setAnimationRunningEnabled(true);
	}

	private void addInternal(Container container) {
		requireNonNull(container, "container");
		
		if (containerList.contains(container)) {
			throw new DuplicateItemException("Container cannot be added twice");
		}

		container.setConstrainDimensionsEnabled(true);
		container.setMaxSize(ctx.width, ctx.height);
		container.setMinSize(ctx.width, ctx.height);

		containerList.add(container);

		if (currentContainer == null) {
			currentContainer = container;
		}
	}

	private void removeInternal(Container container) {
		requireNonNull(container,"container");
		
		if(!containerList.contains(container)) {
			throw new NoSuchElementException("Container not found in ContainerManager");
		}
		
		containerList.remove(container);

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
		if (Debugger.isDebugModeEnabled()) {
			ctx.push();
			ctx.fill(255);
			ctx.textSize(24);
			ctx.text("fps: " + (int) ctx.frameRate+"\n"+Debugger.getAdditionalInfo(), 10, 24);
			ctx.pop();
		}
	}

	private final class Animation extends View {
		private static final float MAX_DIST = MathUtils.dist(0, 0, ctx.width, ctx.height);
		private AnimationType animationType;
		private final ImageBuffer prevContainerImage, currentContainerImage;
		private float speed;
		private int randDirX, randDirY;
		private boolean isAnimationRunningEnabled, isNewContainerPrepared, isEasing;

		private Animation() {
			super();
			setVisible(true);

			prevContainerImage = new ImageBuffer();
			currentContainerImage = new ImageBuffer();

			prevContainerImage.setSize(ctx.width, ctx.height);
			currentContainerImage.setSize(ctx.width, ctx.height);

			setSpeed(max(1, ctx.width * .1f));
			setAnimationType(AnimationType.SLIDE_RANDOM);
			setEasingEnabled(true);
		}

		@Override
		protected void render() {
			if (isAnimationRunningEnabled()) {
				if (!prevContainerImage.isLoaded()) {
					prevContainer.draw();
					prevContainerImage.set(getScreenBuffer());
					return;
				}

				switch (animationType) {
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
					if (!isNewContainerPrepared) {
						do {
							randDirX = (int) ctx.random(-1, 2);
							randDirY = (int) ctx.random(-1, 2);
						} while (randDirX != 0 && randDirY != 0 || randDirX == randDirY);

					}
					slideDirection(randDirX, randDirY);
					break;
				}

				if (isAnimationRunningEnabled()) {
					if (!currentContainerImage.isLoaded()) {
						currentContainer.draw();
						currentContainerImage.set(getScreenBuffer());
					}

					prevContainerImage.draw();
					currentContainerImage.draw();
				}

			}

			if (!isAnimationRunningEnabled()) {
				prevContainerImage.removeTexture();
				currentContainerImage.removeTexture();

				if(currentContainer != null) {
					currentContainer.draw();
				}
				
			}
		}

		PImage getScreenBuffer() {
			return ctx.get(0, 0, ctx.width, ctx.height);

		}

		boolean isAnimationRunningEnabled() {
			return isAnimationRunningEnabled;
		}

		void setAnimationRunningEnabled(boolean isAnimationRunningEnabled) {
			this.isAnimationRunningEnabled = isAnimationRunningEnabled;
		}

		void complete() {
			prevContainerImage.setBounds(0, 0, ctx.width, ctx.height);
			currentContainerImage.setBoundsFrom(prevContainerImage);
			setAnimationRunningEnabled(false);
			isNewContainerPrepared = false;
		}

		float getRawSpeed() {
			return speed;
		}

		void setSpeed(float speed) {
			if (speed <= 0) {
				throw new IllegalArgumentException("Animation speed must be greater than 0");
			}
			this.speed = speed;
		}

		AnimationType getAnimationType() {
			return animationType;
		}

		void setAnimationType(AnimationType animationType) {
			this.animationType = requireNonNull(animationType,"animationType");
		}

		boolean isEasingEnabled() {
			return isEasing;
		}

		void setEasingEnabled(boolean isEasing) {
			this.isEasing = isEasing;
		}

		private int getSpeedInternal() {
			int additionalMinSpeed = 1;
			if (isEasing) {
				float dist = max(abs(currentContainerImage.getX()), abs(currentContainerImage.getY()));
				return (int) (additionalMinSpeed + MathUtils.convert(abs(dist), 0, MAX_DIST, .1f, speed));
			}
			return (int) (additionalMinSpeed + speed);
		}

		private void slideDirection(int dirX, int dirY) {
			if (!isNewContainerPrepared) {
				currentContainerImage.setPosition(dirX == -1 ? ctx.width : dirX == 1 ? -ctx.width : 0,
						dirY == -1 ? ctx.height : dirY == 1 ? -ctx.height : 0);
				isNewContainerPrepared = true;
			}

			if (dirX == -1) {
				prevContainerImage.appendX(-getSpeedInternal());
				currentContainerImage.appendX(-getSpeedInternal(), 0, ctx.width);
			}

			if (dirX == 1) {
				prevContainerImage.appendX(getSpeedInternal());
				currentContainerImage.appendX(getSpeedInternal(), -ctx.width, 0);
			}

			if (dirY == -1) {
				prevContainerImage.appendY(-getSpeedInternal());
				currentContainerImage.appendY(-getSpeedInternal(), 0, ctx.height);
			}

			if (dirY == 1) {
				prevContainerImage.appendY(getSpeedInternal());
				currentContainerImage.appendY(getSpeedInternal(), -ctx.height, 0);
			}

			if (dirX != 0) {
				if ((int) currentContainerImage.getX() == 0) {
					complete();
				}
			}

			if (dirY != 0) {
				if ((int) currentContainerImage.getY() == 0) {
					complete();
				}
			}
		}

	}

}