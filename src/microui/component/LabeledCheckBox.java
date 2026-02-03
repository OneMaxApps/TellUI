package microui.component;

import static microui.component.CheckBox.DEFAULT_SIZE;
import static microui.core.base.Container.Mode.RESPECT_CONSTRAINTS;
import static processing.core.PConstants.LEFT;

import microui.core.base.Component;
import microui.core.base.Container;
import microui.core.effect.Hover;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.event.Listener;
import microui.layout.RowLayout;
import microui.layout.RowLayoutParams;
import processing.core.PFont;

/**
 * A composite component that combines a {@link CheckBox} with a
 * {@link TextView} to create a labeled checkbox. The component provides hover
 * effects and allows the entire area (checkbox + label) to be clickable.
 * 
 * <p>
 * The layout uses a {@link RowLayout} with the checkbox taking 10% of the width
 * and the label text taking 90% of the width by default.
 * </p>
 * 
 * @see CheckBox
 * @see TextView
 * @see Container
 * @see RowLayout
 */
public final class LabeledCheckBox extends Component {
	private final Container container;
	private final CheckBox checkBox;
	private final TextView textView;
	private final Hover hover;

	/**
	 * Constructs a LabeledCheckBox with specified position and dimensions. The
	 * component is initialized with default sizing constraints and layout.
	 *
	 * @param x      the x-coordinate of the component's top-left corner
	 * @param y      the y-coordinate of the component's top-left corner
	 * @param width  the width of the component
	 * @param height the height of the component
	 */
	public LabeledCheckBox(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinSize(DEFAULT_SIZE);
		setMaxSize(ctx.width / 2, DEFAULT_SIZE);

		checkBox = new CheckBox();
		checkBox.setPriority(1);
		checkBox.setMarginLeft(10);

		textView = new TextView();

		hover = new Hover(this);
		hover.setColor(new Color(32, 16));

		onClick(() -> {
			if (!checkBox.isHover()) {
				checkBox.toggle();
			}
		});

		textView.setPadding(20, 0);
		textView.setAutoResizeModeEnabled(false);
		textView.setAlignX(LEFT);
		textView.setTextSize(DEFAULT_SIZE);
		textView.setConstrainDimensionsEnabled(true);
		textView.setMaxSize(ctx.width, DEFAULT_SIZE);

		container = new Container(new RowLayout(), x, y, width, height);
		container.setMode(RESPECT_CONSTRAINTS);
		container.add(checkBox, new RowLayoutParams(.1f)); // Checkbox takes 10% width
		container.add(textView, new RowLayoutParams(.9f)); // Text takes 90% width
	}

	/**
	 * Constructs a LabeledCheckBox with the specified label text. The component is
	 * centered on the screen and sized to maximum dimensions.
	 *
	 * @param label the text to display next to the checkbox
	 */
	public LabeledCheckBox(String label) {
		this(0, 0, DEFAULT_SIZE, DEFAULT_SIZE);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);

		setText(label);
	}

	/**
	 * Constructs a LabeledCheckBox with an empty label. The component is centered
	 * on the screen and sized to maximum dimensions.
	 */
	public LabeledCheckBox() {
		this("");
	}

	/**
	 * Checks if hover effects are enabled for this component.
	 *
	 * @return true if hover effects are enabled, false otherwise
	 */
	public boolean isHoverEnabled() {
		return hover.isEnabled();
	}

	/**
	 * Enables or disables hover effects for this component.
	 *
	 * @param enabled true to enable hover effects, false to disable
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setHoverEnabled(boolean enabled) {
		hover.setEnabled(enabled);
		return this;
	}

	/**
	 * Gets the color used for hover effects.
	 *
	 * @return the current hover color
	 */
	public AbstractColor getHoverColor() {
		return hover.getColor();
	}

	/**
	 * Sets the color used for hover effects.
	 *
	 * @param color the color to use for hover effects
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setHoverColor(AbstractColor color) {
		hover.setColor(color);
		return this;
	}

	/**
	 * Gets the animation speed for hover effects.
	 *
	 * @return the current hover animation speed
	 */
	public float getHoverSpeed() {
		return hover.getSpeed();
	}

	/**
	 * Sets the animation speed for hover effects.
	 *
	 * @param speed the animation speed for hover effects
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setHoverSpeed(float speed) {
		hover.setSpeed(speed);
		return this;
	}

	/**
	 * Gets the checked state of the checkbox.
	 *
	 * @return true if the checkbox is checked, false otherwise
	 */
	public boolean isChecked() {
		return checkBox.isChecked();
	}

	/**
	 * Sets the checked state of the checkbox.
	 *
	 * @param isChecked true to check the checkbox, false to uncheck it
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setChecked(boolean isChecked) {
		checkBox.setChecked(isChecked);
		return this;
	}

	/**
	 * Toggles the checked state of the checkbox.
	 *
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox toggle() {
		checkBox.toggle();
		return this;
	}

	/**
	 * Gets the color used for the check mark.
	 *
	 * @return the current mark color
	 */
	public AbstractColor getMarkColor() {
		return checkBox.getMarkColor();
	}

	/**
	 * Sets the color used for the check mark.
	 *
	 * @param color the color to use for the check mark
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setMarkColor(AbstractColor color) {
		checkBox.setMarkColor(color);
		return this;
	}

	/**
	 * Sets a listener to be called when the checkbox state changes.
	 *
	 * @param listener the listener to call on state changes
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox onStateChangedListener(Listener listener) {
		checkBox.setOnStateChangedListener(listener);
		return this;
	}

	/**
	 * Gets the text displayed next to the checkbox.
	 *
	 * @return the current label text
	 */
	public String getText() {
		return textView.getText();
	}

	/**
	 * Sets the text to display next to the checkbox.
	 *
	 * @param text the label text to display
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setText(String text) {
		this.textView.setText(text);
		return this;
	}

	/**
	 * Gets the font used for the label text.
	 *
	 * @return the current font for the label
	 */
	public PFont getFont() {
		return textView.getFont();
	}

	/**
	 * Sets the font for the label text.
	 *
	 * @param font the font to use for the label
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setFont(PFont font) {
		textView.setFont(font);
		return this;
	}

	/**
	 * Gets the color of the label text.
	 *
	 * @return the current text color
	 */
	public AbstractColor getTextColor() {
		return textView.getTextColor();
	}

	/**
	 * Sets the color of the label text.
	 *
	 * @param color the color to use for the label text
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setTextColor(AbstractColor color) {
		textView.setTextColor(color);
		return this;
	}

	/**
	 * Checks if the label text is visible.
	 *
	 * @return true if the text is visible, false otherwise
	 */
	public boolean isTextVisible() {
		return textView.isVisible();
	}

	/**
	 * Sets the visibility of the label text.
	 *
	 * @param isVisible true to show the text, false to hide it
	 * @return this LabeledCheckBox instance for method chaining
	 */
	public LabeledCheckBox setTextVisible(boolean isVisible) {
		textView.setVisible(isVisible);
		return this;
	}

	/**
	 * Renders the labeled checkbox. The rendering includes hover effects and the
	 * container with its children.
	 */
	@Override
	protected void render() {
		hover.draw();
		container.draw();
	}

	/**
	 * Called when the component's bounds change. Updates the bounds of the internal
	 * container to match the component's new bounds.
	 */
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (container != null) {
			container.setBoundsFrom(this);
		}
	}
}