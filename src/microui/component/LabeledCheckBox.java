package microui.component;

import static microui.component.CheckBox.DEFAULT_SIZE;
import static processing.core.PConstants.LEFT;

import microui.constants.ContainerMode;
import microui.core.base.Component;
import microui.core.base.Container;
import microui.core.effect.Hover;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.event.Listener;
import microui.layout.RowLayout;
import microui.layout.RowLayoutParams;
import processing.core.PFont;

public final class LabeledCheckBox extends Component {
	private final Container container;
	private final CheckBox checkBox;
	private final TextView textView;
	private final Hover hover;

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
		container.setContainerMode(ContainerMode.RESPECT_CONSTRAINTS);
		container.addContentView(checkBox, new RowLayoutParams(.1f));
		container.addContentView(textView, new RowLayoutParams(.9f));

	}

	public LabeledCheckBox(String label) {
		this(0, 0, DEFAULT_SIZE, DEFAULT_SIZE);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);

		setText(label);
	}

	public LabeledCheckBox() {
		this("");
	}

	@Override
	protected void render() {
		hover.draw();
		container.draw();
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (container != null) {
			container.setBoundsFrom(this);
		}
	}

	public boolean isHoverEnabled() {
		return hover.isEnabled();
	}

	public LabeledCheckBox setHoverEnabled(boolean enabled) {
		hover.setEnabled(enabled);
		return this;
	}

	public AbstractColor getHoverColor() {
		return hover.getColor();
	}

	public LabeledCheckBox setHoverColor(AbstractColor color) {
		hover.setColor(color);
		return this;
	}

	public float getHoverSpeed() {
		return hover.getSpeed();
	}

	public LabeledCheckBox setHoverSpeed(float speed) {
		hover.setSpeed(speed);
		return this;
	}

	public boolean isChecked() {
		return checkBox.isChecked();
	}

	public LabeledCheckBox setChecked(boolean isChecked) {
		checkBox.setChecked(isChecked);
		return this;
	}

	public LabeledCheckBox toggle() {
		checkBox.toggle();
		return this;
	}

	public AbstractColor getMarkColor() {
		return checkBox.getMarkColor();
	}

	public LabeledCheckBox setMarkColor(AbstractColor color) {
		checkBox.setMarkColor(color);
		return this;
	}

	public LabeledCheckBox onStateChangedListener(Listener listener) {
		checkBox.onStateChangedListener(listener);
		return this;
	}

	public String getText() {
		return textView.getText();
	}

	public LabeledCheckBox setText(String text) {
		this.textView.setText(text);
		return this;
	}

	public PFont getFont() {
		return textView.getFont();
	}

	public LabeledCheckBox setFont(PFont font) {
		textView.setFont(font);
		return this;
	}

	public AbstractColor getTextColor() {
		return textView.getTextColor();
	}

	public LabeledCheckBox setTextColor(AbstractColor color) {
		textView.setTextColor(color);
		return this;
	}

	public boolean isTextVisible() {
		return textView.isVisible();
	}

	public LabeledCheckBox setTextVisible(boolean isVisible) {
		textView.setVisible(isVisible);
		return this;
	}
}