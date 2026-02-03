package microui.core.style.theme;

import microui.core.style.AbstractColor;

/**
 * Abstract base class for defining color themes in the MicroUI framework.
 * Provides a contract for all theme implementations to define consistent color
 * schemes across different UI components and states.
 * <p>
 * Themes define the visual appearance of the application by providing color
 * values for various UI elements and interaction states. Subclasses must
 * implement all abstract methods to provide a complete theme.
 * </p>
 * 
 * @see AbstractColor
 */
public abstract class AbstractTheme {

	/**
	 * Returns the background color for containers and main UI areas.
	 * 
	 * @return the background color
	 */
	public abstract AbstractColor getBackgroundColor();

	/**
	 * Returns the color for non-editable text elements (labels, headers, etc.).
	 * 
	 * @return the text view color
	 */
	public abstract AbstractColor getTextViewColor();

	/**
	 * Returns the color for text displayed on buttons.
	 * 
	 * @return the button text color
	 */
	public abstract AbstractColor getButtonTextColor();

	/**
	 * Returns the color for strokes/borders around UI elements.
	 * 
	 * @return the stroke color
	 */
	public abstract AbstractColor getStrokeColor();

	/**
	 * Returns the color for hover effects when users mouse over interactive
	 * elements.
	 * 
	 * @return the hover color
	 */
	public abstract AbstractColor getHoverColor();

	/**
	 * Returns the color for ripple animation effects on click/touch interactions.
	 * 
	 * @return the ripples color
	 */
	public abstract AbstractColor getRipplesColor();

	/**
	 * Returns the primary accent color used for highlights and primary actions.
	 * 
	 * @return the primary color
	 */
	public abstract AbstractColor getPrimaryColor();

	/**
	 * Returns the color for editable text (text fields, text areas, etc.).
	 * 
	 * @return the editable text color
	 */
	public abstract AbstractColor getEditableTextColor();

	/**
	 * Returns the color for selected items or highlighted selections.
	 * 
	 * @return the selection color
	 */
	public abstract AbstractColor getSelectColor();

	/**
	 * Returns the color for text cursor/caret in editable text fields.
	 * 
	 * @return the cursor color
	 */
	public abstract AbstractColor getCursorColor();

	/**
	 * Returns the background color for editable text fields and areas.
	 * 
	 * @return the editable background color
	 */
	public abstract AbstractColor getEditableBackgroundColor();

	/**
	 * Returns the background color for tooltip popups.
	 * 
	 * @return the tooltip background color
	 */
	public abstract AbstractColor getTooltipBackgroundColor();

	/**
	 * Returns the text color for tooltip popups.
	 * 
	 * @return the tooltip text color
	 */
	public abstract AbstractColor getTooltipTextColor();

	/**
	 * Returns the background color for menu button items.
	 * 
	 * @return the menu button item color
	 */
	public abstract AbstractColor getMenuButtonItemColor();

	/**
	 * Returns the text color for menu button items.
	 * 
	 * @return the menu button item text color
	 */
	public abstract AbstractColor getMenuButtonItemTextColor();

}