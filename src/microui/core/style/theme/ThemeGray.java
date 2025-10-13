package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;

public class ThemeGray extends AbstractTheme {

	@Override
	public AbstractColor getBackgroundColor() {
		return new Color(124);
	}

	@Override
	public AbstractColor getTextViewColor() {
		return new Color(12);
	}

	@Override
	public AbstractColor getButtonTextColor() {
		return new Color(32);
	}

	@Override
	public AbstractColor getStrokeColor() {
		return new Color(128);
	}

	@Override
	public AbstractColor getHoverColor() {
		return new Color(132, 128);
	}

	@Override
	public AbstractColor getRipplesColor() {
		return new Color(200, 128);
	}

	@Override
	public AbstractColor getPrimaryColor() {
		return new Color(64);
	}

	@Override
	public AbstractColor getEditableTextColor() {
		return new Color(0);
	}

	@Override
	public AbstractColor getSelectColor() {
		return new Color(0, 200, 255, 32);
	}

	@Override
	public AbstractColor getCursorColor() {
		return new Color(0);
	}

	@Override
	public AbstractColor getEditableBackgroundColor() {
		return new Color(232);
	}

	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new Color(200);
	}

	@Override
	public AbstractColor getTooltipTextColor() {
		return new Color(32);
	}
}