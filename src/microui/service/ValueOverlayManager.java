package microui.service;

import static java.util.Objects.requireNonNull;
import static processing.core.PConstants.CENTER;
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
	private static ValueOverlayManager instance;
	private static final TextView text;
	private static ValuePreviewSource source;
	private static String tmpText;
	private static float cachedTextWidth, cachedTextHeight;
	private static int cachedTextLinesCount;
	
	static {
		final BooleanSupplier s = () -> {
			return source != null && source.isContentPrepared();
		};
		
		text = new TextView();
		text.setConstrainDimensionsEnabled(false);
		
		text.setBackgroundColor(new LerpedColor(Color.TRANSPARENT, new Color(0,128), s).setSpeed(.1f));
		text.setTextColor(new LerpedColor(Color.TRANSPARENT, Color.WHITE, s).setSpeed(.1f));
		
		text.setAutoResizeModeEnabled(false);
		text.setTextSize(24);
		text.setPadding(10);
		text.setAlignX(LEFT);
		text.setAlignY(TOP);
		
		text.setSpatialAnimator(createDefaultAnimator());
	}
	
	private ValueOverlayManager() {
		super();
		setVisible(true);
		
	}
	
	// == TEXT API == //
	public static AbstractColor getBackgroundColor() {
		return text.getBackgroundColor();
	}

	public Component setBackgroundColor(AbstractColor backgroundColor) {
		return text.setBackgroundColor(backgroundColor);
	}
	
	public static AbstractColor getTextColor() {
		return text.getTextColor();
	}
	
	public static void setTextColor(AbstractColor textColor) {
		text.setTextColor(textColor);
	}

	public static float getTextSize() {
		return text.getTextSize();
	}

	public static void setTextSize(float textSize) {
		text.setTextSize(textSize);
	}

	public static PFont getFont() {
		return text.getFont();
	}

	public static void setFont(PFont font) {
		text.setFont(font);
	}
	
	public static AutoResizeMode getAutoResizeMode() {
		return text.getAutoResizeMode();
	}
	
	public static boolean isAutoResizeModeEnabled() {
		return text.isAutoResizeModeEnabled();
	}
	
	public static void setAutoResizeModeEnabled(boolean autoResizeModeEnabled) {
		text.setAutoResizeModeEnabled(autoResizeModeEnabled);
	}

	public static void setAutoResizeMode(AutoResizeMode autoResizeMode) {
		text.setAutoResizeMode(autoResizeMode);
	}
	
	public static void setSpatialAnimator(SpatialAnimator spatialAnimator) {
		text.setSpatialAnimator(spatialAnimator);
	}
	
	public static ValuePreviewSource getSource() {
		return source;
	}

	public static void setSource(ValuePreviewSource source) {
		ValueOverlayManager.source = requireNonNull(source,"source");
	}

	public static ValueOverlayManager getInstance() {
		if (instance == null) {
			instance = new ValueOverlayManager();
		}
		
		return instance;
	}
	
	@Override
	protected void render() {
		if (source != null && source.isContentPrepared()) {
			text.setText(source.getSource());
			
		}
		
		text.draw();
	}
	
	private static SpatialAnimator createDefaultAnimator() {
		final SpatialState startState = new SpatialState();
		
		startState.setSupplierX(() -> -text.getWidth());
		startState.setSupplierY(() -> -text.getHeight());
		startState.setSupplierWidth(() -> text.getWidth());
		startState.setSupplierHeight(() -> text.getHeight());
		
		final SpatialState endState = new SpatialState(10,10,0,0);
		
		
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
	
	private static float getTextWidth() {
		if (tmpText == text.getText() || ( tmpText != null && tmpText.equals(text.getText()))) {
			return cachedTextWidth;
		} else {
			updateCachedTextData();
			tmpText = text.getText();
		}

		return cachedTextWidth;
	}
	
	private static float getTextHeight() {
		if (tmpText == text.getText() || ( tmpText != null && tmpText.equals(text.getText()))) {
			correctTextAlign();
			return cachedTextHeight;
		}
		
		updateCachedTextData();
		
		tmpText = text.getText();
		
		correctTextAlign();
		
		return cachedTextHeight;
	}
	
	private static void updateCachedTextData() {
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
		ctx.popStyle();
		
		cachedTextWidth = textWidth * 1.1f;
		
		cachedTextLinesCount = lines.length;
		cachedTextHeight = getTextSize() * cachedTextLinesCount * 1.1f;
	}
	
	private static void correctTextAlign() {
		if (cachedTextLinesCount > 1) {
			text.setAlignY(TOP);
		} else {
			text.setAlignY(CENTER);
		}
	}
	
	
}