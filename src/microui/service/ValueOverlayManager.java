package microui.service;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;

import microui.component.TextView;
import microui.constants.AutoResizeMode;
import microui.core.base.View;
import microui.core.interfaces.ValuePreviewSource;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;

public final class ValueOverlayManager extends View {
	private static ValueOverlayManager instance;
	private static final TextView text;
	private static ValuePreviewSource source;
	
	static {
		final BooleanSupplier s = () -> {
			return source != null && source.isContentPrepared();
		};
		
		text = new TextView();
		text.setPosition(0, 0);
		text.setBackgroundColor(new LerpedColor(Color.TRANSPARENT, new Color(0,128), s).setSpeed(.1f));
		text.setTextColor(new LerpedColor(Color.TRANSPARENT, Color.WHITE, s).setSpeed(.1f));
	}
	
	private ValueOverlayManager() {
		super();
		setVisible(true);
		
	}
	
	public static void setBackgroundColor(AbstractColor backgroundColor) {
		text.setBackgroundColor(backgroundColor);
	}

	public static void setAlignX(int alignX) {
		text.setAlignX(alignX);
	}

	public static void setAlignY(int alignY) {
		text.setAlignY(alignY);
	}
	
	public static void setPadding(float left, float right, float top, float bottom) {
		text.setPadding(left, right, top, bottom);
	}

	public static void setTextSize(float textSize) {
		text.setTextSize(textSize);
	}
	
	public static float getTextSize() {
		return text.getTextSize();
	}

	public static void setSize(float width, float height) {
		text.setSize(width, height);
	}
	
	public static float getWidth() {
		return text.getWidth();
	}
	
	public static float getHeight() {
		return text.getHeight();
	}

	public static void setAutoResizeModeEnabled(boolean autoResizeModeEnabled) {
		text.setAutoResizeModeEnabled(autoResizeModeEnabled);
	}
	
	public static void setAutoResizeMode(AutoResizeMode autoResizeMode) {
		text.setAutoResizeMode(autoResizeMode);
	}

	public static void setTextColor(AbstractColor textColor) {
		text.setTextColor(textColor);
	}

	public static void setMargin(float left, float right, float top, float bottom) {
		text.setMargin(left, right, top, bottom);
	}

	public static void setConstrainDimensionsEnabled(boolean constrainDimensionsEnabled) {
		text.setConstrainDimensionsEnabled(constrainDimensionsEnabled);
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
}