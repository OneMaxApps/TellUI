package microui.core.style.theme;

import microui.core.style.AbstractColor;

public abstract class AbstractTheme {
	public abstract AbstractColor getBackgroundColor();

	public abstract AbstractColor getTextViewColor();

	public abstract AbstractColor getButtonTextColor();

	public abstract AbstractColor getStrokeColor();

	public abstract AbstractColor getHoverColor();

	public abstract AbstractColor getRipplesColor();

	public abstract AbstractColor getPrimaryColor();

	public abstract AbstractColor getEditableTextColor();

	public abstract AbstractColor getSelectColor();

	public abstract AbstractColor getCursorColor();

	public abstract AbstractColor getEditableBackgroundColor();

	public abstract AbstractColor getTooltipBackgroundColor();

	public abstract AbstractColor getTooltipTextColor();
}