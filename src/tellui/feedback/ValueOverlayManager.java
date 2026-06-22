package tellui.feedback;

import static java.util.Objects.requireNonNull;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import java.util.function.BooleanSupplier;

import processing.core.PFont;
import tellui.component.TextView;
import tellui.constants.AutoResizeMode;
import tellui.core.base.Component;
import tellui.core.base.View;
import tellui.core.effect.SpatialAnimator;
import tellui.core.interfaces.ValuePreviewSource;
import tellui.core.style.AbstractColor;
import tellui.core.style.Color;
import tellui.core.style.LerpedColor;
import tellui.core.style.LerpedLoopColor;
import telluii.util.SpatialState;

/**
 * Manages the display of a value overlay – a transient text preview
 * (e.g., for volume, brightness, etc.) that appears when a value changes.
 * The overlay is implemented as a {@link TextView} with animated appearance
 * and disappearance. It uses a {@link ValuePreviewSource} to obtain the text
 * to display and automatically hides when the source is no longer prepared.
 * <p>
 * This class is a singleton; use {@link #getInstance()} to obtain the instance.
 * </p>
 *
 * @see ValuePreviewSource
 * @see TextView
 */
public final class ValueOverlayManager extends View {
	private static ValueOverlayManager instance;
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
	 * Returns the singleton instance of the value overlay manager.
	 *
	 * @return the unique {@code ValueOverlayManager} instance
	 */
	public static final ValueOverlayManager getInstance() {
		if (instance == null) {
			instance = new ValueOverlayManager();
		}
		
		return instance;
	}

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
		
		text.setBounds(0, 0, 0, 0);
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