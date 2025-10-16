package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.GradientLoopColor;

public class ThemeWhite extends AbstractTheme {

	@Override
	public AbstractColor getBackgroundColor() {
		return Color.GRAY_232L;
	}

	@Override
	public AbstractColor getTextViewColor() {
		return Color.BLACK;
	}

	@Override
	public AbstractColor getButtonTextColor() {
		return Color.BLACK;
	}

	@Override
	public AbstractColor getStrokeColor() {
		return Color.BLACK;
	}

	@Override
	public AbstractColor getHoverColor() {
		return new Color(100, 200, 255, 24);
	}

	@Override
	public AbstractColor getRipplesColor() {
		return Color.WHITE;
	}

	@Override
	public AbstractColor getPrimaryColor() {
		return Color.GRAY_128L;
	}

	@Override
	public AbstractColor getEditableTextColor() {
		return Color.BLACK;
	}

	@Override
	public AbstractColor getSelectColor() {
		return new Color(0, 0, 200, 32);
	}

	@Override
	public AbstractColor getCursorColor() {
		return new Color(0, 232);
	}

	@Override
	public AbstractColor getEditableBackgroundColor() {
		return Color.WHITE;
	}

	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new GradientColor(new Color(232, 0), Color.GRAY_232L, () -> true);

	}

	@Override
	public AbstractColor getTooltipTextColor() {
		return new GradientColor(Color.TRANSPARENT, new GradientLoopColor(Color.BLACK, new Color(0, 154)), () -> true);
	}
	
	@Override
	public AbstractColor getMenuButtonItemColor() {
		return Color.GRAY_232L;
	}

	@Override
	public AbstractColor getMenuButtonItemTextColor() {
		return Color.BLACK;
	}
}