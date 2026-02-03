package microui.component;

import static java.awt.event.KeyEvent.*;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.util.MathUtils.constrain;
import static microui.util.MathUtils.convert;
import static processing.core.PConstants.BACKSPACE;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.DELETE;
import static processing.core.PConstants.ENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

import java.util.HashMap;

import microui.core.base.Component;
import microui.core.controller.FullSingleLineTextController;
import microui.core.controller.FullSingleLineTextController.ValidationMode;
import microui.core.interfaces.KeyPressable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.core.style.LerpedLoopColor;
import microui.event.Listener;
import microui.util.BoundedValue;
import microui.util.Clipboard;
import microui.util.Metrics;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;

/**
 * A single-line text input field with support for text editing, selection,
 * copy/paste, undo/redo, validation, and password masking.
 * 
 * <p>
 * The component uses off-screen buffering for efficient rendering and includes
 * sophisticated text width caching for performance.
 * </p>
 */
public final class TextField extends Component implements KeyPressable {
	private static final float WIDTH_RATIO_FOR_SCROLL = .8f;
	private static final int DEFAULT_MIN_WIDTH = 20;
	private static final int DEFAULT_MIN_HEIGHT = 10;
	private static final int DEFAULT_MAX_WIDTH = 200;
	private static final int DEFAULT_MAX_HEIGHT = 40;
	private static final int DEFAULT_HORIZONTAL_PADDING = 10;
	private static final int DEFAULT_VERTICAL_PADDING = 5;
	private static final int DEFAULT_SCROLL_VALUE = 0;
	private static final byte COUNT_OF_FRAMES_BEFORE_BUFFER_INITIALIZATION = 2;

	private final BoundedValue scroll;

	private final Text text;
	private final Cursor cursor;
	private final Selection selection;
	private final TextWidthPool textWidthPool;

	private PGraphics pg;
	private Listener onTextChangedListener, onEnterPressedListener, onFocusChangedListener;

	private boolean focused, componentSizeChanged;

	/**
	 * Constructs a TextField with specified position and dimensions. The text field
	 * is initialized with default styling and editing capabilities.
	 *
	 * @param x the x-coordinate of the text field's top-left corner
	 * @param y the y-coordinate of the text field's top-left corner
	 * @param w the width of the text field
	 * @param h the height of the text field
	 */
	public TextField(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinSize(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
		setMaxSize(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
		setPadding(DEFAULT_HORIZONTAL_PADDING, DEFAULT_VERTICAL_PADDING);

		prepareBackgroundColor();

		scroll = new BoundedValue(DEFAULT_SCROLL_VALUE);

		text = new Text(this);
		cursor = new Cursor(this);
		selection = new Selection(this);
		textWidthPool = new TextWidthPool(this);

		scroll.setOnChangeValueListener(() -> {
			cursor.recalculateX();
			text.setX(-scroll.get());
		});

		createPGraphics();

		setTextSize(getMaxHeight());

		onDoubleClick(() -> {
			selection.selectWord();
		});

		onPress(() -> {
			if (selection.isSelected()) {
				selection.reset();
			}
		});
	}

	/**
	 * Constructs a TextField with default maximum size, centered on the screen.
	 * This is a convenience constructor for creating a centered text field.
	 */
	public TextField() {
		this(0, 0, 0, 0);
		prepareBoundsInCenter();
	}

	/**
	 * Gets the password mask character used in password mode.
	 *
	 * @return the current password mask character
	 */
	public char getPasswordChar() {
		return text.getPasswordChar();
	}

	/**
	 * Sets the password mask character used in password mode.
	 *
	 * @param passwordChar the character to use for masking passwords
	 * @return this TextField instance for method chaining
	 */
	public TextField setPasswordChar(char passwordChar) {
		text.setPasswordChar(passwordChar);
		return this;
	}

	/**
	 * Checks if password mode is enabled.
	 *
	 * @return true if password mode is enabled, false otherwise
	 */
	public boolean isPasswordModeEnabled() {
		return text.isPasswordModeEnabled();
	}

	/**
	 * Enables or disables password mode. When enabled, entered characters are
	 * masked with the password character.
	 *
	 * @param passwordModeEnabled true to enable password masking, false to disable
	 * @return this TextField instance for method chaining
	 */
	public TextField setPasswordModeEnabled(boolean passwordModeEnabled) {
		text.setPasswordModeEnabled(passwordModeEnabled);
		return this;
	}

	/**
	 * Checks if input validation is enabled.
	 *
	 * @return true if validation is enabled, false otherwise
	 */
	public final boolean isValidationEnabled() {
		return text.isValidationEnabled();
	}

	/**
	 * Enables or disables input validation.
	 *
	 * @param validation true to enable validation, false to disable
	 * @return this TextField instance for method chaining
	 */
	public TextField setValidationEnabled(boolean validation) {
		text.setValidationEnabled(validation);
		return this;
	}

	/**
	 * Checks if the text field is empty.
	 *
	 * @return true if no text is entered, false otherwise
	 */
	public boolean isEmpty() {
		return text.isEmpty();
	}

	/**
	 * Sets the listener to be called when the text field's focus changes.
	 *
	 * @param onFocusChangedListener the listener for focus changes
	 * @return this TextField instance for method chaining
	 * @throws NullPointerException if the listener is null
	 */
	public TextField setOnFocusChangedListener(Listener onFocusChangedListener) {
		this.onFocusChangedListener = requireNonNull(onFocusChangedListener, "onFocusChangedListener");
		return this;
	}

	/**
	 * Sets the listener to be called when the Enter key is pressed.
	 *
	 * @param onEnterPressedListener the listener for Enter key presses
	 * @return this TextField instance for method chaining
	 * @throws NullPointerException if the listener is null
	 */
	public TextField setOnEnterPressedListener(Listener onEnterPressedListener) {
		this.onEnterPressedListener = requireNonNull(onEnterPressedListener, "onEnterPressedListener");
		return this;
	}

	/**
	 * Sets the listener to be called when the text content changes.
	 *
	 * @param onTextChangedListener the listener for text changes
	 * @return this TextField instance for method chaining
	 * @throws NullPointerException if the listener is null
	 */
	public TextField setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
		return this;
	}

	/**
	 * Gets the current validation mode for text input.
	 *
	 * @return the current validation mode
	 */
	public ValidationMode getValidationMode() {
		return text.getValidationMode();
	}

	/**
	 * Sets the validation mode for text input.
	 *
	 * @param validationMode the validation mode to use
	 * @return this TextField instance for method chaining
	 */
	public TextField setValidationMode(ValidationMode validationMode) {
		text.setValidationMode(validationMode);
		return this;
	}

	/**
	 * Gets the current text content. In password mode, returns the masked text.
	 *
	 * @return the current text content
	 */
	public String getText() {
		return text.getAsString();
	}

	/**
	 * Gets the hidden text content (actual characters, not masked). Only meaningful
	 * in password mode.
	 *
	 * @return the actual text content without masking
	 */
	public String getHiddenText() {
		return text.getHiddenText();
	}

	/**
	 * Sets the text content.
	 *
	 * @param text the text to set
	 * @return this TextField instance for method chaining
	 */
	public TextField setText(String text) {
		this.text.set(text);
		return this;
	}

	/**
	 * Sets the text content from a StringBuilder.
	 *
	 * @param text the StringBuilder containing text to set
	 * @return this TextField instance for method chaining
	 */
	public TextField setText(StringBuilder text) {
		this.text.set(text);
		return this;
	}

	/**
	 * Checks if text length constraint is enabled.
	 *
	 * @return true if text length is constrained, false otherwise
	 */
	public boolean isTextConstrainEnabled() {
		return text.isConstrainEnabled();
	}

	/**
	 * Enables or disables text length constraint.
	 *
	 * @param enabled true to enable length constraint, false to disable
	 * @return this TextField instance for method chaining
	 */
	public TextField setTextConstrainEnabled(boolean enabled) {
		text.setConstrainEnabled(enabled);
		return this;
	}

	/**
	 * Gets the maximum allowed characters when constraint is enabled.
	 *
	 * @return the maximum character limit
	 */
	public int getMaxChars() {
		return text.getMaxChars();
	}

	/**
	 * Sets the maximum allowed characters when constraint is enabled.
	 *
	 * @param max the maximum character limit
	 * @return this TextField instance for method chaining
	 */
	public TextField setMaxChars(int max) {
		text.setMaxChars(max);
		return this;
	}

	/**
	 * Gets the number of digits when in digit-only validation mode.
	 *
	 * @return the number of digits, or -1 if not in digit mode
	 */
	public int getDigitsStrict() {
		return text.getDigitsStrict();
	}

	/**
	 * Gets the number of digits or returns a default value.
	 *
	 * @param defaultValue the default value to return if not in digit mode
	 * @return the number of digits, or defaultValue if not in digit mode
	 */
	public int getDigitsOrDefault(int defaultValue) {
		return text.getDigitsOrDefault(defaultValue);
	}

	/**
	 * Gets the text color.
	 *
	 * @return the current text color
	 */
	public AbstractColor getTextColor() {
		return text.getColor();
	}

	/**
	 * Sets the text color.
	 *
	 * @param color the color for text rendering
	 * @return this TextField instance for method chaining
	 */
	public TextField setTextColor(AbstractColor color) {
		text.setColor(color);
		return this;
	}

	/**
	 * Gets the cursor color.
	 *
	 * @return the current cursor color
	 */
	public AbstractColor getCursorColor() {
		return cursor.getColor();
	}

	/**
	 * Sets the cursor color.
	 *
	 * @param color the color for the cursor
	 * @return this TextField instance for method chaining
	 */
	public TextField setCursorColor(AbstractColor color) {
		cursor.setColor(color);
		return this;
	}

	/**
	 * Gets the selection highlight color.
	 *
	 * @return the current selection color
	 */
	public AbstractColor getSelectionColor() {
		return selection.getColor();
	}

	/**
	 * Sets the selection highlight color.
	 *
	 * @param color the color for text selection highlighting
	 * @return this TextField instance for method chaining
	 */
	public TextField setSelectionColor(AbstractColor color) {
		selection.setColor(color);
		return this;
	}

	/**
	 * Gets the text size in pixels.
	 *
	 * @return the current text size
	 */
	public float getTextSize() {
		return text.getTextSize();
	}

	/**
	 * Sets the text size in pixels. Minimum size is 1 pixel.
	 *
	 * @param size the text size to set
	 * @return this TextField instance for method chaining
	 * @throws IllegalArgumentException if size is less than 1
	 */
	public TextField setTextSize(float size) {
		text.setTextSize(size);
		return this;
	}

	/**
	 * Gets the cursor stroke weight (thickness).
	 *
	 * @return the current cursor weight
	 */
	public float getCursorWeight() {
		return cursor.getWeight();
	}

	/**
	 * Sets the cursor stroke weight (thickness). Valid range is 1 to 10 pixels.
	 *
	 * @param weight the cursor weight to set
	 * @return this TextField instance for method chaining
	 * @throws IllegalArgumentException if weight is outside 1-10 range
	 */
	public TextField setCursorWeight(float weight) {
		cursor.setWeight(weight);
		return this;
	}

	/**
	 * Gets the cursor blink rate. Higher values make the cursor blink faster.
	 *
	 * @return the current cursor blink rate
	 */
	public float getCursorBlinkRate() {
		return cursor.getBlinkRate();
	}

	/**
	 * Sets the cursor blink rate. Valid range is 1 to 30 (frames per blink cycle).
	 *
	 * @param rate the blink rate to set
	 * @return this TextField instance for method chaining
	 * @throws IllegalArgumentException if rate is outside 1-30 range
	 */
	public TextField setCursorBlinkRate(float rate) {
		cursor.setBlinkRate(rate);
		return this;
	}

	/**
	 * Gets the current font.
	 *
	 * @return the current font, or null if using default font
	 */
	public PFont getFont() {
		return text.getFont();
	}

	/**
	 * Sets the font for text rendering.
	 *
	 * @param font the font to use for text
	 * @return this TextField instance for method chaining
	 * @throws NullPointerException if font is null
	 */
	public TextField setFont(PFont font) {
		text.setFont(font);
		return this;
	}

	/**
	 * Gets the hint text displayed when the field is empty and not focused.
	 *
	 * @return the current hint text
	 */
	public String getHint() {
		return text.getHint();
	}

	/**
	 * Sets the hint text displayed when the field is empty and not focused.
	 *
	 * @param hint the hint text to display
	 * @return this TextField instance for method chaining
	 * @throws NullPointerException if hint is null
	 */
	public TextField setHint(String hint) {
		text.setHint(hint);
		return this;
	}

	/**
	 * Gets the hint text color.
	 *
	 * @return the current hint color
	 */
	public AbstractColor getHintColor() {
		return text.getHintColor();
	}

	/**
	 * Sets the hint text color.
	 *
	 * @param hintColor the color for hint text
	 * @return this TextField instance for method chaining
	 */
	public TextField setHintColor(AbstractColor hintColor) {
		text.setHintColor(hintColor);
		return this;
	}

	/**
	 * Checks if the text field currently has keyboard focus.
	 *
	 * @return true if focused, false otherwise
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * Sets whether the text field has keyboard focus.
	 *
	 * @param focused true to give focus, false to remove focus
	 * @return this TextField instance for method chaining
	 */
	public TextField setFocused(boolean focused) {
		if (this.focused == focused) {
			return this;
		}

		this.focused = focused;
		notifyFocusChanged();
		return this;
	}

	/**
	 * Handles key press events for text editing and navigation. Only processes keys
	 * when the text field is focused.
	 *
	 * @param e the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (!focused) {
			return;
		}

		cursor.blink.reset();

		if (e.isShiftDown()) {
			onShiftDown(e);
			return;
		}

		if (e.isControlDown()) {
			onControlDown(e);
			return;
		}

		onRegularKeyPressed(e);
	}

	/**
	 * Called when the text field's bounds change. Updates internal components to
	 * match the new bounds.
	 */
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (text != null) {
			text.recalculateY();
		}

		if (scroll != null) {
			updateScrollMax();
		}

		if (cursor != null) {
			cursor.updateTransforms();
		}

		if (selection != null) {
			selection.recalculateBounds();
		}

		componentSizeChanged = true;
	}

	/**
	 * Renders the text field, including background, text, cursor, and selection.
	 * Also handles focus changes based on mouse interaction.
	 */
	@Override
	protected void render() {
		checkDimensions();

		ctx.pushStyle();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());

		screenOffBufferOnDraw();

		ctx.popStyle();

		mouseEventsUpdateState();
	}

	// == PRIVATE KEYBOARD CONTROL API ==

	private void onRegularKeyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case ENTER:
			onEnterPressed();
			break;
		case LEFT:
			onKeyLeftPressed();
			break;
		case RIGHT:
			onKeyRightPressed();
			break;
		case BACKSPACE:
			onKeyBackspacePressed();
			break;
		case DELETE:
			onKeyDeletePressed();
			break;
		case VK_HOME:
			onKeyHomePressed();
			break;
		case VK_END:
			onKeyEndPressed();
			break;
		default:
			onPrintableKeyPressed();
			break;
		}
	}

	private void prepareSelectionOnShiftDown() {
		if (!selection.isStarted()) {
			selection.setStartColumn(cursor.column.get());
			selection.setEndColumn(cursor.column.get());
			selection.setStarted(true);
		}
	}

	private void onShiftWithLeftPressed() {
		cursor.column.back();
		selection.setEndColumn(cursor.column.get());
		if (cursor.isCloseToLeftSide()) {
			scroll.append(-cursor.column.getPrevCharWidth());
		}
	}

	private void onShiftWithRightPressed() {
		cursor.column.next();
		selection.setEndColumn(cursor.column.get());
		if (cursor.isCloseToRightSide()) {
			scroll.append(cursor.column.getNextCharWidth());
		}
	}

	private void onShiftWithHomePressed() {
		cursor.column.goToStart();
		scroll.set(scroll.getMin());
		selection.setEndColumn(cursor.column.get());
	}

	private void onShiftWithEndPressed() {
		cursor.column.goToEnd();
		scroll.set(scroll.getMax());
		selection.setEndColumn(cursor.column.get());
	}

	private void onShiftDown(KeyEvent e) {
		switch (e.getKeyCode()) {
		case LEFT:
		case RIGHT:
		case VK_HOME:
		case VK_END:
			prepareSelectionOnShiftDown();
			break;
		}

		switch (e.getKeyCode()) {
		case LEFT:
			onShiftWithLeftPressed();
			break;
		case RIGHT:
			onShiftWithRightPressed();
			break;
		case VK_HOME:
			onShiftWithHomePressed();
			break;
		case VK_END:
			onShiftWithEndPressed();
			break;
		default:
			if (selection.isSelected()) {
				return;
			}
			onPrintableKeyPressed();
			break;
		}
	}

	private void onControlDown(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_C:
			Clipboard.set(selection.getText());
			break;
		case VK_V:
			pasteTextFromClipboard();
			break;
		case VK_X:
			cutTextToClipboard();
			break;
		case VK_A:
			selection.selectAll();
			break;
		case VK_Z:
			text.undo();
			break;
		case VK_Y:
			text.redo();
			break;
		}
	}

	private void onEnterPressed() {
		notifyEnterPressed();
	}

	private void onKeyLeftPressed() {
		cursor.column.back();
		selection.reset();

		if (cursor.isAtStart()) {
			return;
		}
		if (cursor.isCloseToLeftSide()) {
			scroll.append(-cursor.column.getPrevCharWidth());
		}
	}

	private void onKeyRightPressed() {
		cursor.column.next();
		selection.reset();

		if (cursor.isAtEnd()) {
			return;
		}

		if (cursor.isCloseToRightSide()) {
			scroll.append(cursor.column.getNextCharWidth());
		}
	}

	private void onKeyBackspacePressed() {
		if (selection.isSelected()) {
			deleteSelectedText();
			selection.reset();
			return;
		}

		if (cursor.isAtStart()) {
			return;
		}

		scroll.append(-cursor.column.getCurrentCharWidth());
		text.removeCharAt(cursor.column.get() - 1);
		cursor.column.back();
	}

	private void onKeyDeletePressed() {
		if (selection.isSelected()) {
			deleteSelectedText();
			selection.reset();
			return;
		}

		if (cursor.isAtEnd()) {
			return;
		}

		text.removeCharAt(cursor.column.get());
	}

	private void onKeyHomePressed() {
		selection.reset();
		cursor.column.goToStart();
		scroll.set(scroll.getMin());
	}

	private void onKeyEndPressed() {
		selection.reset();
		cursor.column.goToEnd();
		scroll.set(scroll.getMax());
	}

	private void onPrintableKeyPressed() {
		if (selection.isSelected()) {
			deleteSelectedText();
			selection.reset();
		}

		text.insert(cursor.column.get(), ctx.key);
	}

	// == PRIVATE MOUSE CONTROL API ==

	private void mouseEventsUpdateState() {
		if (isDragging() || isPressed()) {
			setFocused(true);
			cursor.blink.reset();
			cursor.column.set(getRecalculatedColumnPositionFromMouse());
		}

		if (mustLostFocus()) {
			setFocused(false);
			selection.reset();
		}

		if (isDragging()) {
			onDragging();
		}
	}

	private int getRecalculatedColumnPositionFromMouse() {
		final float mouseX = ctx.mouseX - (getX() + cursor.column.getCurrentCharWidth() / 2) + scroll.get();
		int correctNewIndex = getText().length();

		for (int i = 0; i <= getText().length(); i++) {
			if (mouseX < textWidthPool.getWidthUntil(i)) {
				correctNewIndex = i;
				break;
			}
		}

		return correctNewIndex;
	}

	private float getSpeedForDragging() {
		final float charWidth = cursor.column.getCurrentCharWidth();
		final float minCharWidth = 1;
		final float maxCharWidth = charWidth / 4;
		final float maxSpeed = max(minCharWidth, maxCharWidth);
		final float speed = convert(ctx.mouseX, getPadX(), getPadX() + getPadWidth(), -maxSpeed, maxSpeed);

		return speed;
	}

	private void onDragging() {
		if (isEmpty()) {
			return;
		}

		scroll.append(getSpeedForDragging());

		if (!selection.isStarted()) {
			selection.setStartColumn(cursor.column.get());
			selection.setEndColumn(cursor.column.get());
			selection.setStarted(true);
		} else {
			selection.setEndColumn(cursor.column.get());
		}
	}

	// == PRIVATE TEXT CONTROL API ==

	private void deleteSelectedText() {
		final int esc = selection.getEffectiveStartColumn();
		final int eec = selection.getEffectiveEndColumn();

		if (selection.getStartColumn() < selection.getEndColumn()) {
			cursor.column.set(esc);
			final float selectedTextWidth = textWidthPool.getWidthBetween(esc, eec);
			scroll.append(-selectedTextWidth);
		}

		text.remove(esc, eec);
	}

	private float getTextWidthFromPApplet(String text) {
		float width = 0;

		ctx.pushStyle();
		if (this.text.font != null) {
			ctx.textFont(this.text.getFont(), this.text.getTextSize());
		} else {
			ctx.textSize(this.text.getTextSize());
		}
		ctx.textAlign(CORNER, CENTER);
		width = ctx.textWidth(text);
		ctx.popStyle();

		return width;
	}

	private float getTextWidthFromPGraphics(String text) {
		if (pg == null) {
			return 0;
		}

		float width = 0;

		pg.pushStyle();
		if (this.text.font != null) {
			pg.textFont(this.text.getFont(), this.text.getTextSize());
		} else {
			pg.textSize(this.text.getTextSize());
		}
		pg.textAlign(CORNER, CENTER);
		width = pg.textWidth(text);
		pg.popStyle();

		return width;
	}

	private float getTextWidth(String text) {
		if (this.text == null) {
			return 0;
		}

		requireNonNull(text, "text");

		if (!offScreenBufferPrepared()) {
			return getTextWidthFromPApplet(text);
		} else {
			return getTextWidthFromPGraphics(text);
		}
	}

	private float getCharWidth(char ch) {
		return getTextWidth(String.valueOf(ch));
	}

	// == PRIVATE CLIPBOARD CONTROL API ==

	private void pasteTextFromClipboard() {
		if (Clipboard.isEmpty()) {
			return;
		}

		final String txt = Clipboard.get();
		final Cursor.Column column = cursor.column;

		if (selection.isSelected()) {
			deleteSelectedText();
			selection.reset();
		}

		text.insert(column.get(), txt);
		column.set(column.get() + txt.length());

		updateScrollMax();
		scroll.append(getTextWidth(Clipboard.get()));
	}

	private void cutTextToClipboard() {
		if (selection.isSelected()) {
			Clipboard.set(selection.getText());
			deleteSelectedText();
			selection.reset();
		}
	}

	// == PRIVATE EVENT MANAGEMENT API ==

	private void notifyTextChanged() {
		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}

	private void notifyEnterPressed() {
		if (onEnterPressedListener != null) {
			onEnterPressedListener.action();
			setFocused(false);
		}
	}

	private void notifyFocusChanged() {
		if (onFocusChangedListener != null) {
			onFocusChangedListener.action();
		}
	}

	// == PRIVATE BUFFER MANAGEMENT API ==

	private boolean offScreenBufferPrepared() {
		return ctx.frameCount > COUNT_OF_FRAMES_BEFORE_BUFFER_INITIALIZATION && pg != null;
	}

	private void screenOffBufferOnDraw() {
		pg.beginDraw();
		pg.clear();
		text.draw(pg);
		if (focused) {
			selection.draw(pg);
			cursor.draw(pg);
		}
		pg.endDraw();

		ctx.image(pg, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
	}

	private void createPGraphics() {
		if (pg != null) {
			pg.dispose();
		}

		final int newWidth = (int) max(1, getWidth());
		final int newHeight = (int) max(1, getHeight());
		pg = ctx.createGraphics(newWidth, newHeight, ctx.sketchRenderer());

		componentSizeChanged = false;
		Metrics.register(pg);
	}

	private void checkDimensions() {
		if (ctx.mousePressed) {
			return;
		}
		if (componentSizeChanged) {
			createPGraphics();
		}
	}

	// == PRIVATE STYLE API ==

	private void prepareBackgroundColor() {
		final AbstractColor tc = getTheme().getEditableBackgroundColor();
		final Color preFocusedColor = new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), 200);
		final LerpedColor gd = new LerpedColor(preFocusedColor, tc, () -> isFocused());
		setBackgroundColor(gd);
	}

	private void prepareBoundsInCenter() {
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	private boolean mustLostFocus() {
		return ctx.mousePressed && !isHover() && !isDragging();
	}

	private void updateScrollMax() {
		if (text.isEmpty() || text.getWidth() < getWidth() * WIDTH_RATIO_FOR_SCROLL) {
			scroll.setMax(0);
			return;
		}

		scroll.setMax((text.getWidth() - getWidth() * WIDTH_RATIO_FOR_SCROLL));
	}

	private static final class Text {
		private static final int MIN_TEXT_SIZE = 1;

		private final TextField tf;
		private final FullSingleLineTextController controller;
		private AbstractColor color, hintColor;
		private PFont font;
		private String hint;
		private float x, y, textWidth, textSize;

		private Text(TextField textField) {
			super();
			this.tf = requireNonNull(textField, "textField");
			controller = new FullSingleLineTextController();

			controller.setOnAfterCharInsertListener(() -> {
				final Cursor.Column column = tf.cursor.column;
				final BoundedValue scroll = tf.scroll;

				column.next();
				scroll.append(column.getPrevCharWidth());
			});

			controller.setOnTextChangedListener(() -> {
				recalculateTextWidth();
				tf.updateScrollMax();
				tf.cursor.recalculateX();
				tf.notifyTextChanged();
				tf.textWidthPool.clear();
			});

			controller.setOnHistoryChangedListener(() -> {
				if (tf.selection.isSelected()) {
					tf.selection.reset();
				}
				tf.cursor.column.goToEnd();
				tf.scroll.set(tf.scroll.getMax());
			});

			color = getTheme().getEditableTextColor();
			hintColor = new LerpedLoopColor(color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 232));
			recalculateY();
			setTextSize(textField.getHeight());
		}

		public void draw(final PGraphics pg) {
			pg.pushStyle();

			if (mustShowHint()) {
				hintColor.apply(pg);
			} else {
				color.apply(pg);
			}

			if (font != null) {
				pg.textFont(font);
			}

			pg.textSize(textSize);
			pg.textAlign(CORNER, CENTER);
			pg.text(mustShowHint() ? hint : controller.getAsString(), getX(), y);

			pg.popStyle();
		}

		public AbstractColor getHintColor() {
			return hintColor;
		}

		public void setHintColor(AbstractColor hintColor) {
			this.hintColor = requireNonNull(hintColor, "hintColor");
		}

		public float getTextSize() {
			return textSize;
		}

		public void setTextSize(float textSize) {
			if (textSize < MIN_TEXT_SIZE) {
				throw new IllegalArgumentException("Text size must be >= " + MIN_TEXT_SIZE);
			}

			if (this.textSize == textSize) {
				return;
			}

			this.textSize = textSize;
			recalculateTextWidth();
		}

		public AbstractColor getColor() {
			return color;
		}

		public void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		public String getHint() {
			return hint;
		}

		public void setHint(String hint) {
			this.hint = requireNonNull(hint, "hint");
		}

		public PFont getFont() {
			return font;
		}

		public void setFont(PFont font) {
			if (this.font == font) {
				return;
			}

			this.font = requireNonNull(font, "font");
			recalculateTextWidth();
		}

		public void undo() {
			controller.undo();
		}

		public void redo() {
			controller.redo();
		}

		public final char getPasswordChar() {
			return controller.getPasswordChar();
		}

		public final void setPasswordChar(char passwordChar) {
			controller.setPasswordChar(passwordChar);
		}

		public final boolean isPasswordModeEnabled() {
			return controller.isPasswordModeEnabled();
		}

		public final void setPasswordModeEnabled(boolean passwordModeEnabled) {
			controller.setPasswordModeEnabled(passwordModeEnabled);
		}

		public final ValidationMode getValidationMode() {
			return controller.getValidationMode();
		}

		public final void setValidationMode(ValidationMode validationMode) {
			controller.setValidationMode(validationMode);
		}

		public final boolean isConstrainEnabled() {
			return controller.isConstrainEnabled();
		}

		public final void setConstrainEnabled(boolean constrainEnabled) {
			controller.setConstrainEnabled(constrainEnabled);
		}

		public final int getMaxChars() {
			return controller.getMaxChars();
		}

		public final void setMaxChars(int maxChars) {
			controller.setMaxChars(maxChars);
		}

		public final String getAsString() {
			return controller.getAsString();
		}

		public final String getHiddenText() {
			return controller.getHiddenText();
		}

		public final int getDigitsStrict() {
			return controller.getDigitsStrict();
		}

		public int getDigitsOrDefault(int defaultValue) {
			return controller.getDigitsOrDefault(defaultValue);
		}

		public void insert(int pos, char ch) {
			controller.insert(pos, ch);
		}

		public void insert(int pos, String text) {
			controller.insert(pos, text);
		}

		public void set(String text) {
			controller.set(text);
		}

		public void set(StringBuilder text) {
			controller.set(text);
		}

		public void removeCharAt(int pos) {
			controller.removeCharAt(pos);
		}

		public void remove(int firstChar, int lastChar) {
			controller.remove(firstChar, lastChar);
		}

		public int length() {
			return controller.length();
		}

		public boolean isEmpty() {
			return controller.isEmpty();
		}

		public boolean isValidationEnabled() {
			return controller.isValidationEnabled();
		}

		public void setValidationEnabled(boolean validation) {
			controller.setValidationEnabled(validation);
		}

		private boolean mustShowHint() {
			return !tf.isFocused() && isEmpty() && hint != null;
		}

		private void recalculateY() {
			y = tf.getHeight() / 2;
		}

		private float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		private void recalculateTextWidth() {
			textWidth = tf.getTextWidth(getAsString());
		}

		private float getWidth() {
			return textWidth;
		}
	}

	private static final class Cursor {
		private static final int DEFAULT_CURSOR_WEIGHT = 2;
		private static final int MIN_CURSOR_WEIGHT = 1;
		private static final int MAX_CURSOR_WEIGHT = 10;
		private static final float CLOSE_TO_LEFT_SIDE_OF_WIDTH_RATIO = .1f;
		private static final float CLOSE_TO_RIGHT_SIDE_OF_WIDTH_RATIO = .9f;

		private final TextField tf;
		private final Blink blink;
		private final Column column;
		private AbstractColor color;
		private float positionX, positionY, weight, height;

		private Cursor(TextField textField) {
			this.tf = requireNonNull(textField, "textField");

			color = getTheme().getCursorColor();
			setWeight(DEFAULT_CURSOR_WEIGHT);
			column = new Column(tf);
			blink = new Blink();
			updateTransforms();
		}

		public void draw(final PGraphics pg) {
			if (pg == null) {
				return;
			}

			blink.updateState();

			if (blink.isBlinking()) {
				pg.pushStyle();
				color.applyStroke(pg);
				pg.strokeWeight(getWeight());
				pg.line(positionX, positionY, positionX, height);
				pg.popStyle();
			}
		}

		public AbstractColor getColor() {
			return color;
		}

		public void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		public float getWeight() {
			return weight;
		}

		public void setWeight(float weight) {
			if (weight < MIN_CURSOR_WEIGHT) {
				throw new IllegalArgumentException("Weight of cursor cannot be less than " + MIN_CURSOR_WEIGHT);
			}

			if (weight > MAX_CURSOR_WEIGHT) {
				throw new IllegalArgumentException("Weight of cursor cannot be greater than " + MAX_CURSOR_WEIGHT);
			}

			this.weight = weight;
		}

		public float getBlinkRate() {
			return blink.getRate();
		}

		public void setBlinkRate(float rate) {
			blink.setRate(rate);
		}

		private void updateTransforms() {
			positionY = tf.getHeight() * .1f;
			height = tf.getHeight() * .9f;
		}

		private boolean isAtStart() {
			return column.get() == 0;
		}

		private boolean isAtEnd() {
			return column.get() == tf.text.length();
		}

		private boolean isCloseToLeftSide() {
			return positionX < tf.getWidth() * CLOSE_TO_LEFT_SIDE_OF_WIDTH_RATIO;
		}

		private boolean isCloseToRightSide() {
			return positionX > tf.getWidth() * CLOSE_TO_RIGHT_SIDE_OF_WIDTH_RATIO;
		}

		private void recalculateX() {
			if (tf.isEmpty()) {
				positionX = 0;
				return;
			}

			final float scrollValue = tf.scroll.get();
			final float subTextWidth = tf.textWidthPool.getWidthUntil(column.get());

			positionX = subTextWidth - scrollValue;
		}

		private static final class Blink {
			private static final byte MAX_DURATION = 60;
			private static final byte MIN_BLINK_RATE = 1;
			private static final byte MAX_BLINK_RATE = MAX_DURATION / 2;

			private byte duration, rate;

			private Blink() {
				rate = 1;
			}

			public byte getRate() {
				return rate;
			}

			public void setRate(final float rate) {
				if (rate < MIN_BLINK_RATE || rate > MAX_BLINK_RATE) {
					throw new IllegalArgumentException(
							"Rate for blink must be between " + MIN_BLINK_RATE + " and " + MAX_BLINK_RATE);
				}
				this.rate = (byte) rate;
			}

			private boolean isBlinking() {
				return duration < MAX_DURATION / 2;
			}

			private void updateState() {
				if (duration < MAX_DURATION) {
					duration += rate;
				} else {
					duration = 0;
				}
			}

			private void reset() {
				duration = 0;
			}
		}

		private static final class Column {
			private final TextField tf;
			private int column;

			private Column(TextField textField) {
				tf = requireNonNull(textField, "textField");
			}

			private void set(int column) {
				if (tf.isEmpty()) {
					this.column = 0;
					tf.cursor.recalculateX();
					return;
				}

				this.column = (int) constrain(column, 0, tf.text.length());
				tf.cursor.recalculateX();
			}

			private int get() {
				return column;
			}

			private void goToStart() {
				set(0);
			}

			private void goToEnd() {
				set(tf.text.length());
			}

			private void back() {
				set(get() - 1);
			}

			private void next() {
				set(get() + 1);
			}

			private float getCurrentCharWidth() {
				if (tf.isEmpty()) {
					return 0;
				}
				return tf.textWidthPool.getCharWidth(column);
			}

			private float getNextCharWidth() {
				if (tf.isEmpty() || column == tf.getText().length() - 1) {
					return 0;
				}
				return tf.textWidthPool.getCharWidth(column + 1);
			}

			private float getPrevCharWidth() {
				if (tf.isEmpty() || column == 0) {
					return 0;
				}
				return tf.textWidthPool.getCharWidth(column - 1);
			}
		}
	}

	private static final class Selection {
		private final TextField tf;
		private AbstractColor color;
		private float x, w, h;
		private int startColumn, endColumn;
		private boolean started;

		private Selection(TextField textField) {
			this.tf = requireNonNull(textField, "textField");
			color = getTheme().getSelectColor();
			h = textField.getHeight();
		}

		public void draw(final PGraphics pg) {
			requireNonNull(pg, "pg");

			pg.pushStyle();
			pg.noStroke();
			color.apply(pg);
			pg.rect(x - tf.scroll.get(), 0, startColumn < endColumn ? w : -w, h);
			pg.popStyle();
		}

		public final AbstractColor getColor() {
			return color;
		}

		public final void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		private void recalculateBounds() {
			h = tf.getHeight();

			if (tf.isEmpty()) {
				x = w = 0;
				return;
			}

			final int sc = getEffectiveStartColumn();
			final int ec = getEffectiveEndColumn();

			x = tf.textWidthPool.getWidthUntil(sc);
			w = tf.textWidthPool.getWidthBetween(sc, ec);
		}

		private String getText() {
			if (tf.text.isEmpty()) {
				return "";
			}

			return tf.getText().substring(getEffectiveStartColumn(), getEffectiveEndColumn());
		}

		private int getEffectiveStartColumn() {
			return (int) Math.min(getStartColumn(), getEndColumn());
		}

		private int getStartColumn() {
			return (int) constrain(startColumn, 0, tf.getText().length());
		}

		private void setStartColumn(int startColumn) {
			this.startColumn = (int) constrain(startColumn, 0, tf.text.length());
			x = tf.textWidthPool.getWidthUntil(getEffectiveEndColumn());
		}

		private int getEndColumn() {
			return (int) constrain(endColumn, 0, tf.getText().length());
		}

		private int getEffectiveEndColumn() {
			return (int) Math.max(getStartColumn(), getEndColumn());
		}

		private void setEndColumn(int endColumn) {
			this.endColumn = (int) constrain(endColumn, 0, tf.text.length());
			w = tf.textWidthPool.getWidthBetween(getEffectiveStartColumn(), getEffectiveEndColumn());
		}

		private void reset() {
			startColumn = endColumn = 0;
			started = false;
			recalculateBounds();
		}

		private boolean isSelected() {
			return startColumn != endColumn;
		}

		private void selectAll() {
			startColumn = 0;
			endColumn = tf.getText().length();
			recalculateBounds();
		}

		private int findStartIndexOfWord(String word, int currentColumn) {
			requireNonNull(word, "word");
			int index = 0;
			currentColumn = (int) constrain(currentColumn, 0, tf.getText().length() - 1);

			for (int i = currentColumn; i > 0; i--) {
				if (word.charAt(i) == ' ') {
					index = i + 1;
					break;
				}
			}

			return index;
		}

		private int findEndIndexOfWord(String word, int currentColumn) {
			requireNonNull(word, "word");
			int endIndex = word.length();
			currentColumn = (int) constrain(currentColumn, 0, tf.getText().length() - 1);

			for (int i = currentColumn; i < word.length(); i++) {
				if (word.charAt(i) == ' ') {
					endIndex = i;
					break;
				}
			}

			return endIndex;
		}

		private void selectWord() {
			final String str = tf.getText();

			if (str.isEmpty()) {
				return;
			}

			final int currentColumn = (int) constrain(tf.cursor.column.get(), 0, str.length() - 1);

			if (!Character.isLetterOrDigit(str.charAt(currentColumn))) {
				selectAll();
				return;
			}

			startColumn = findStartIndexOfWord(str, currentColumn);
			endColumn = findEndIndexOfWord(str, currentColumn);
			recalculateBounds();
		}

		private boolean isStarted() {
			return started;
		}

		private void setStarted(boolean started) {
			this.started = started;
		}
	}

	private static final class TextWidthPool {
		private final TextField tf;
		private HashMap<Integer, Float> untilWidth, charWidth;
		private HashMap<Long, Float> betweenWidth;

		public TextWidthPool(TextField tf) {
			super();
			this.tf = requireNonNull(tf, "tf");
			untilWidth = new HashMap<Integer, Float>();
			charWidth = new HashMap<Integer, Float>();
			betweenWidth = new HashMap<Long, Float>();
		}

		public void clear() {
			untilWidth.clear();
			charWidth.clear();
			betweenWidth.clear();
		}

		public float getWidthUntil(int index) {
			final String text = tf.getText();

			if (text.isEmpty()) {
				return 0;
			}

			index = (int) constrain(index, 0, text.length());

			if (!untilWidth.containsKey(index)) {
				untilWidth.put(index, tf.getTextWidth(text.substring(0, index)));
			}

			return untilWidth.get(index);
		}

		public float getCharWidth(int index) {
			final String text = tf.getText();

			if (text.isEmpty()) {
				return 0;
			}

			index = (int) constrain(index, 0, text.length() - 1);

			if (!charWidth.containsKey(index)) {
				charWidth.put(index, tf.getCharWidth(text.charAt(index)));
			}

			return charWidth.get(index);
		}

		public float getWidthBetween(int startIndex, int endIndex) {
			final String text = tf.getText();

			if (text.isEmpty()) {
				return 0;
			}

			startIndex = (int) constrain(startIndex, 0, text.length());
			endIndex = (int) constrain(endIndex, 0, text.length());

			final long newKey = getRangeKey(startIndex, endIndex);

			Float valueInMap = betweenWidth.get(newKey);

			if (valueInMap == null) {
				float newValue = tf.getTextWidth(text.substring(startIndex, endIndex));
				betweenWidth.put(newKey, newValue);
				return newValue;
			}

			return valueInMap.floatValue();
		}

		private long getRangeKey(int startIndex, int endIndex) {
			return ((long) startIndex << 32) | (endIndex & 0xFFFFFFFFL);
		}
	}
}