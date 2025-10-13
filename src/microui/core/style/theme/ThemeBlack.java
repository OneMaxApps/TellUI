package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.GradientLoopColor;

public class ThemeBlack extends AbstractTheme {

	@Override
	public AbstractColor getBackgroundColor() {
		return new Color(32);
	}

	@Override
	public AbstractColor getTextViewColor() {
		return Color.GRAY_200L;
	}

	@Override
	public AbstractColor getButtonTextColor() {
		return new Color(164);
	}

	@Override
	public AbstractColor getStrokeColor() {
		return Color.BLACK;
	}

	@Override
	public AbstractColor getHoverColor() {
		return new Color(200, 24);
	}

	@Override
	public AbstractColor getRipplesColor() {
		return new Color(100, 10);
	}

	@Override
	public AbstractColor getPrimaryColor() {
		return new Color(232, 128);
	}

	@Override
	public AbstractColor getEditableTextColor() {
		return Color.WHITE;
	}

	@Override
	public AbstractColor getSelectColor() {
		return new Color(0, 0, 154, 32);
	}

	@Override
	public AbstractColor getCursorColor() {
		return new Color(255, 232);
	}

	@Override
	public AbstractColor getEditableBackgroundColor() {
		return Color.GRAY_128L;
	}

	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new GradientColor(Color.TRANSPARENT, new Color(0, 200), () -> true);
	}

	@Override
	public AbstractColor getTooltipTextColor() {
		return new GradientColor(Color.TRANSPARENT, new GradientLoopColor(Color.WHITE, new Color(255, 154)),
				() -> true);
	}

}