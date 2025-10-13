package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientLoopColor;

public class ThemeGlass extends AbstractTheme {

	@Override
	public AbstractColor getBackgroundColor() {
		return new GradientLoopColor(new Color(200, 200, 255, 32), new Color(255, 32));
	}

	@Override
	public AbstractColor getTextViewColor() {
		return new Color(255, 232);
	}

	@Override
	public AbstractColor getButtonTextColor() {
		return new Color(200, 200);
	}

	@Override
	public AbstractColor getStrokeColor() {
		return new Color(224, 224, 255, 128);
	}

	@Override
	public AbstractColor getHoverColor() {
		return new Color(0, 200, 255, 32);
	}

	@Override
	public AbstractColor getRipplesColor() {
		return new Color(132, 132, 154, 32);
	}

	@Override
	public AbstractColor getPrimaryColor() {
		return new GradientLoopColor(new Color(100, 200, 232, 100), new Color(164, 164, 232, 32));
	}

	@Override
	public AbstractColor getEditableTextColor() {
		return new Color(232, 232, 254, 232);
	}

	@Override
	public AbstractColor getSelectColor() {
		return new Color(100, 100);
	}

	@Override
	public AbstractColor getCursorColor() {
		return new Color(200, 200, 232, 232);
	}

	@Override
	public AbstractColor getEditableBackgroundColor() {
		return new Color(200, 32);
	}

	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new Color(200, 200, 232, 200);
	}

	@Override
	public AbstractColor getTooltipTextColor() {
		return new Color(0);
	}

}