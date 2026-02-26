package microui.service;

import static java.util.Objects.requireNonNull;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import java.util.function.BooleanSupplier;

import microui.component.TextView;
import microui.constants.AutoResizeMode;
import microui.core.base.Component;
import microui.core.base.View;
import microui.core.effect.SpatialAnimator;
import microui.core.interfaces.ValuePreviewSource;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.util.SpatialState;
import processing.core.PFont;

public final class ValueOverlayManager extends View {
	private static final int DEFAULT_TEXT_SIZE = 24;
	private static final int DEFAULT_PADDING_AROUND = 10;
	private static ValueOverlayManager instance;
	private final TextView text;
	private ValuePreviewSource source;
	private String tmpText;
	private float cachedTextWidth, cachedTextHeight;
	
	private ValueOverlayManager() {
		super();
		setVisible(true);
		
		text = new TextView();
		prepareTextConfig();
		
	}
	
	// == TEXT API == //
	public AbstractColor getBackgroundColor() {
		return text.getBackgroundColor();
	}

	public Component setBackgroundColor(AbstractColor backgroundColor) {
		return text.setBackgroundColor(backgroundColor);
	}
	
	public AbstractColor getTextColor() {
		return text.getTextColor();
	}
	
	public void setTextColor(AbstractColor textColor) {
		text.setTextColor(textColor);
	}

	public float getTextSize() {
		return text.getTextSize();
	}

	public void setTextSize(float textSize) {
		if (textSize != text.getTextSize()) {
			text.setTextSize(textSize);
			updateCachedTextData();
		}
	}

	public PFont getFont() {
		return text.getFont();
	}

	public void setFont(PFont font) {
		if (font != text.getFont()) {
			text.setFont(font);
			updateCachedTextData();
		}
	}
	
	public AutoResizeMode getAutoResizeMode() {
		return text.getAutoResizeMode();
	}
	
	public boolean isAutoResizeModeEnabled() {
		return text.isAutoResizeModeEnabled();
	}
	
	public void setAutoResizeModeEnabled(boolean autoResizeModeEnabled) {
		if (autoResizeModeEnabled != text.isAutoResizeModeEnabled()) {
			text.setAutoResizeModeEnabled(autoResizeModeEnabled);
			updateCachedTextData();
		}
	}

	public void setAutoResizeMode(AutoResizeMode autoResizeMode) {
		if (autoResizeMode != text.getAutoResizeMode()) {
			text.setAutoResizeMode(autoResizeMode);
			updateCachedTextData();
		}
	}
	
	public void setSpatialAnimator(SpatialAnimator spatialAnimator) {
		text.setSpatialAnimator(spatialAnimator);
	}
	
	
	
	public ValuePreviewSource getSource() {
		return source;
	}

	public void setSource(ValuePreviewSource source) {
		this.source = requireNonNull(source,"source");
	}

	public static ValueOverlayManager getInstance() {
		if (instance == null) {
			instance = new ValueOverlayManager();
		}
		
		return instance;
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
		final SpatialState startState = new SpatialState();
		
		startState.setSupplierX(() -> -text.getAbsoluteWidth());
		startState.setSupplierY(() -> -text.getAbsoluteHeight());
		startState.setSupplierWidth(() -> text.getAbsoluteWidth());
		startState.setSupplierHeight(() -> text.getAbsoluteHeight());
		
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
		
		return new SpatialAnimator(startState, endState, () -> source != null && source.isContentPrepared());
		
	}
	
	private float getTextWidth() {
		if (isTextNotChanged()) {
			return cachedTextWidth;
		}
		
		updateCachedTextData();
		return cachedTextWidth;
	}
	
	private float getTextHeight() {
		if (isTextNotChanged()) {
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
	
	private void prepareTextConfig() {
		final BooleanSupplier condition = () -> {
			return source != null && source.isContentPrepared();
		};
		
		text.setConstrainDimensionsEnabled(false);
		text.setBackgroundColor(new LerpedColor(Color.TRANSPARENT, new Color(0,128), condition).setSpeed(.1f));
		text.setTextColor(new LerpedColor(Color.TRANSPARENT, Color.WHITE, condition).setSpeed(.1f));
		text.setAutoResizeModeEnabled(false);
		text.setTextSize(DEFAULT_TEXT_SIZE);
		text.setPadding(DEFAULT_PADDING_AROUND);
		text.setAlignX(LEFT);
		text.setAlignY(TOP);
		text.setClipModeEnabled(false);
		text.setSpatialAnimator(createDefaultAnimator());
	}
	
	private boolean isTextNotChanged() {
		return tmpText == text.getText() || ( tmpText != null && tmpText.equals(text.getText()));
	}
}