package microui.core.base;

import static java.util.Objects.requireNonNull;
import static processing.core.PApplet.map;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import microui.MicroUI;
import microui.component.TextView;
import microui.constants.AutoResizeMode;
import microui.core.ImageBuffer;
import microui.core.effect.SpatialAnimator;
import microui.core.effect.Transition;
import microui.core.exception.DuplicateItemException;
import microui.core.exception.RenderException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.interfaces.ValuePreviewSource;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.core.style.LerpedLoopColor;
import microui.event.PointerManager;
import microui.feedback.Tooltip;
import microui.layout.LayoutManager;
import microui.util.Debugger;
import microui.util.Environment;
import microui.util.MathUtils;
import microui.util.SpatialState;
import processing.core.PFont;
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
		tooltipManager = new TooltipManager();
		valueOverlayManager = new ValueOverlayManager();
		
		new Renderer(this);
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
		containerManager.navigateTo(containerManager.get(textId));
	}
	
	public void navigateTo(int id) {
		containerManager.navigateTo(containerManager.get(id));
	}
	
	public void navigateTo(Container container, Transition transition) {
		containerManager.setTransition(transition);
		containerManager.navigateTo(container);
	}
	
	public void navigateTo(String textId, Transition transition) {
		containerManager.setTransition(transition);
		containerManager.navigateTo(containerManager.get(textId));
	}
	
	public void navigateTo(int id, Transition transition) {
		containerManager.setTransition(transition);
		containerManager.navigateTo(containerManager.get(id));
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
	
	
	// == EXTENDED API == //
	
	public TooltipManager getTooltipManager() {
		return tooltipManager;
	}
	
	public ValueOverlayManager getOverlayManager() {
		return valueOverlayManager;
	}
	
	@Override
	protected void render() {
		if (!Renderer.isAllowed()) {
			throw new RenderException("Cannot call draw() of UIHost manually");
		}
		
		containerManager.draw();
		tooltipManager.draw();
		valueOverlayManager.draw();
		
		if (Debugger.isEnabled() && Debugger.isShowFpsEnabled()) {
			System.out.println("fps: " + ctx.frameRate);
		}
	}
	
	public static final class Renderer {
		private final UIHost uiHost;
		private static boolean allowed;
		
		private Renderer(UIHost uiHost) {
			this.uiHost = uiHost;
			
			MicroUI.getContext().registerMethod("draw", this);
		}
		
		public void draw() {
			allowed = true;
			uiHost.draw();
			allowed = false;
			
			if (!ctx.mousePressed) {
				PointerManager.release();
			}
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
			ctx.registerMethod("keyEvent", this);
			ctx.registerMethod("mouseEvent", this);
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
			
			if (current == container) {
				throw new IllegalStateException("Cannot navigate to itself");
			}
			
			previous = current;
			current = container;
			
			transitionManager.activate();
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
		
		public final float getTransitionProgressStep() {
			return transitionManager.getProgressStep();
		}

		public final void setTransitionProgressStep(float progressStep) {
			transitionManager.setProgressStep(progressStep);
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
				return map(timer,TIMER_START, TIMER_END, start, end);
			}
		}

	}
	
	public static final class TooltipManager extends View {
		private Tooltip tooltip;
		
		private TooltipManager() {
			setVisible(true);
		}
		
		public void setTooltip(Tooltip tooltip) {
			this.tooltip = requireNonNull(tooltip, "tooltip");
		}
		
		@Override
		protected void render() {
			if (Environment.isAndroid()) {
				return;
			}
				
			if (tooltip != null) {
				tooltip.getContent().setAbsolutePosition(getConstrainedX(), getConstrainedY());
				tooltip.draw();
			}
		}
		
		private float getConstrainedX() {
			return MathUtils.constrain(ctx.mouseX, 0, ctx.width - tooltip.getContent().getAbsoluteWidth());
		}

		private float getConstrainedY() {
			return MathUtils.constrain(ctx.mouseY, 0, ctx.height - tooltip.getContent().getAbsoluteHeight());
		}
		
	}

	public static final class ValueOverlayManager extends View {
		private static final int DEFAULT_TEXT_SIZE = 24;
		private static final int DEFAULT_PADDING_AROUND = 10;
		private final TextView text;
		private ValuePreviewSource source;
		private String tmpText;
		private float cachedTextWidth, cachedTextHeight;
		
		private ValueOverlayManager() {
			super();
			setVisible(true);
			
			text = new TextView();
			
			
			initTextStyle();
			
		}
		
		// == TEXT API == //

		/**
		 * Returns the background color of the value overlay.
		 *
		 * @return the background color.
		 */
		public AbstractColor getBackgroundColor() {
			return text.getBackgroundColor();
		}

		/**
		 * Sets the background color of the value overlay.
		 *
		 * @param backgroundColor the new background color.
		 * @return this component for chaining.
		 */
		public Component setBackgroundColor(AbstractColor backgroundColor) {
			return text.setBackgroundColor(backgroundColor);
		}
		
		/**
		 * Returns the text color.
		 *
		 * @return the text color.
		 */
		public AbstractColor getTextColor() {
			return text.getTextColor();
		}
		
		/**
		 * Sets the text color.
		 *
		 * @param textColor the new text color.
		 */
		public void setTextColor(AbstractColor textColor) {
			text.setTextColor(textColor);
		}

		/**
		 * Returns the text size.
		 *
		 * @return the text size in pixels.
		 */
		public float getTextSize() {
			return text.getTextSize();
		}

		/**
		 * Sets the text size. If the size differs from the current one,
		 * cached text dimensions are updated.
		 *
		 * @param textSize the new text size in pixels.
		 */
		public void setTextSize(float textSize) {
			if (textSize != text.getTextSize()) {
				text.setTextSize(textSize);
				updateCachedTextData();
			}
		}

		/**
		 * Returns the font used for text.
		 *
		 * @return the current PFont, or null if default.
		 */
		public PFont getFont() {
			return text.getFont();
		}

		/**
		 * Sets the font for text. If the font differs from the current one,
		 * cached text dimensions are updated.
		 *
		 * @param font the new PFont.
		 */
		public void setFont(PFont font) {
			if (font != text.getFont()) {
				text.setFont(font);
				updateCachedTextData();
			}
		}
		
		/**
		 * Returns the current auto-resize mode.
		 *
		 * @return the auto-resize mode.
		 */
		public AutoResizeMode getAutoResizeMode() {
			return text.getAutoResizeMode();
		}
		
		/**
		 * Checks whether auto-resize mode is enabled.
		 *
		 * @return true if auto-resize is enabled, false otherwise.
		 */
		public boolean isAutoResizeModeEnabled() {
			return text.isAutoResizeModeEnabled();
		}
		
		/**
		 * Enables or disables auto-resize mode. If the state changes,
		 * cached text dimensions are updated.
		 *
		 * @param autoResizeModeEnabled true to enable, false to disable.
		 */
		public void setAutoResizeModeEnabled(boolean autoResizeModeEnabled) {
			if (autoResizeModeEnabled != text.isAutoResizeModeEnabled()) {
				text.setAutoResizeModeEnabled(autoResizeModeEnabled);
				updateCachedTextData();
			}
		}

		/**
		 * Sets the auto-resize mode. If the mode differs from the current one,
		 * cached text dimensions are updated.
		 *
		 * @param autoResizeMode the new auto-resize mode.
		 */
		public void setAutoResizeMode(AutoResizeMode autoResizeMode) {
			if (autoResizeMode != text.getAutoResizeMode()) {
				text.setAutoResizeMode(autoResizeMode);
				updateCachedTextData();
			}
		}
		
		/**
		 * Sets the spatial animator for the underlying text view.
		 *
		 * @param spatialAnimator the animator to use.
		 */
		public void setSpatialAnimator(SpatialAnimator spatialAnimator) {
			text.setSpatialAnimator(spatialAnimator);
		}
		
		/**
		 * Returns the current value preview source.
		 *
		 * @return the source, or null if not set.
		 */
		public ValuePreviewSource getSource() {
			return source;
		}

		/**
		 * Sets the value preview source. The source provides the text to display.
		 *
		 * @param source the source, cannot be null.
		 * @throws NullPointerException if source is null.
		 */
		public void setSource(ValuePreviewSource source) {
			this.source = requireNonNull(source,"source");
		}
		
		@Override
		protected void render() {
			if (source != null) {
				if (source.isContentPrepared() && !source.getSource().isBlank()) {
					text.setText(source.getSource());
				} else {
					source = null;
				}
			}
			
			text.draw();
		}
		
		private SpatialAnimator createDefaultAnimator() {
			final SpatialState startState = new SpatialState(0,0,0,0);
			
			startState.setSupplierX(() -> -text.getAbsoluteWidth());
			startState.setSupplierY(() -> text.getPaddingTop());
			startState.setSupplierWidth(() -> text.getWidth());
			startState.setSupplierHeight(() -> text.getHeight());
			
			final SpatialState endState = new SpatialState();
			
			endState.setSupplierX(() -> text.getPaddingLeft());
			endState.setSupplierY(() -> text.getPaddingTop());
			
			endState.setSupplierWidth(() -> {
				text.setWidth(getTextWidth());
				return text.getWidth();
			});
			
			endState.setSupplierHeight(() -> {
				text.setHeight(getTextHeight());
				return text.getHeight();
			});
			
			final var animator = new SpatialAnimator(startState, endState, () -> source != null && source.isContentPrepared());
			animator.setSpeed(.05f);
			
			return animator;
		}
		
		private float getTextWidth() {
			if (isCacheValid()) {
				return cachedTextWidth;
			}
			
			updateCachedTextData();
			return cachedTextWidth;
		}
		
		private float getTextHeight() {
			if (isCacheValid()) {
				return cachedTextHeight;
			}
			
			updateCachedTextData();
			return cachedTextHeight;
		}
		
		private void updateCachedTextData() {
			ctx.pushStyle();
			if(getFont() != null) {
				ctx.textFont(getFont());
			}
			ctx.textSize(getTextSize());
			final String[] lines = text.getText().split("\n");
			float textWidth = 0;
			for(String l : lines) {
				textWidth = Math.max(textWidth, ctx.textWidth(l));
			}
			
			cachedTextHeight = (ctx.textAscent() + ctx.textDescent()) * lines.length;
			cachedTextWidth = textWidth;
			
			ctx.popStyle();
			
			tmpText = text.getText();
		}
		
		private void initTextStyle() {
			final BooleanSupplier condition = () -> {
				return source != null && source.isContentPrepared();
			};
			
			text.setConstrainDimensionsEnabled(false);
			text.setId(IGNORE_INTERNAL_COMPONENT_ID);
			
			text.setConstrainDimensionsEnabled(false);
			text.setBackgroundColor(new LerpedColor(Color.TRANSPARENT, new Color(0,128), condition).setSpeed(.1f));
			text.setTextColor(new LerpedColor(Color.TRANSPARENT, new LerpedLoopColor(Color.WHITE, new Color(255,200)).setSpeed(.1f), condition).setSpeed(.1f));
			text.setAutoResizeModeEnabled(false);
			text.setTextSize(DEFAULT_TEXT_SIZE);
			text.setPadding(DEFAULT_PADDING_AROUND);
			text.setAlignX(LEFT);
			text.setAlignY(TOP);
			text.setClipModeEnabled(false);
			text.setSpatialAnimator(createDefaultAnimator());
		}
		
		private boolean isCacheValid() {
			return tmpText == text.getText() || ( tmpText != null && tmpText.equals(text.getText()));
		}
	}
}