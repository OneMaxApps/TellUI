package microui.service;

import static java.util.Objects.requireNonNull;
import static processing.core.PConstants.LEFT;

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
	
	static {
		final BooleanSupplier s = () -> {
			return source != null && source.isContentPrepared();
		};
		
		text = new TextView();
		text.setConstrainDimensionsEnabled(false);
		text.setSize(ctx.width/4, ctx.height*.1f);
		text.setPosition(0, 0);
		text.setBackgroundColor(new LerpedColor(Color.TRANSPARENT, new Color(0,128), s).setSpeed(.1f));
		text.setTextColor(new LerpedColor(Color.TRANSPARENT, Color.WHITE, s).setSpeed(.1f));
		
		
		text.setSpatialAnimator(createBaseAnimator());
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

	public static float getWidth() {
		return text.getWidth();
	}

	public static void setWidth(float width) {
		text.setWidth(width);
	}
	
	public static float getHeight() {
		return text.getHeight();
	}

	public static void setHeight(float height) {
		text.setHeight(height);
	}

	public static float getTextSize() {
		return text.getTextSize();
	}

	public static void setTextSize(float textSize) {
		text.setTextSize(textSize);
	}

	public static void setFont(PFont font) {
		text.setFont(font);
	}
	
	public static PFont getFont() {
		return text.getFont();
	}

	public static String getText() {
		return text.getText();
	}

	public static AutoResizeMode getAutoResizeMode() {
		return text.getAutoResizeMode();
	}

	public static AbstractColor getTextColor() {
		return text.getTextColor();
	}

	public static void setTextColor(AbstractColor textColor) {
		text.setTextColor(textColor);
	}
	
	public static void setAutoResizeModeEnabled(boolean autoResizeModeEnabled) {
		text.setAutoResizeModeEnabled(autoResizeModeEnabled);
	}

	public static void setAutoResizeMode(AutoResizeMode autoResizeMode) {
		text.setAutoResizeMode(autoResizeMode);
	}

	public static void setConstrainDimensionsEnabled(boolean constrainDimensionsEnabled) {
		text.setConstrainDimensionsEnabled(constrainDimensionsEnabled);
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
	
	private static SpatialAnimator createBaseAnimator() {
		final SpatialState startState = new SpatialState();
		
		startState.setSupplierX(() -> {
			return -text.getWidth();
		});
		
		startState.setSupplierY(() -> {
			return -text.getHeight();
		});
		
		startState.setSupplierWidth(() -> {
			return text.getWidth();
		});
		
		startState.setSupplierHeight(() -> {
			return text.getHeight();
		});
		
		final SpatialState endState = new SpatialState();
		
		endState.setSupplierX(() -> {
			return 0f;
		});
		
		endState.setSupplierY(() -> {
			return 0f;
		});
		
		endState.setSupplierWidth(() -> {
			return text.getWidth();
		});
		
		endState.setSupplierHeight(() -> {
			return text.getHeight();
		});
		
		return new SpatialAnimator(startState, endState, () -> source != null && source.isContentPrepared());
		
	}
}