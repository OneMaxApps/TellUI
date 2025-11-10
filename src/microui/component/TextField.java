package microui.component;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
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

import microui.core.TextController;
import microui.core.TextController.ValidationMode;
import microui.core.base.Component;
import microui.core.interfaces.KeyPressable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.GradientLoopColor;
import microui.event.Listener;
import microui.util.BoundedValue;
import microui.util.Clipboard;
import microui.util.MathUtils;
import microui.util.Metrics;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;

public final class TextField extends Component implements KeyPressable {
	private static final float WIDTH_RATIO_FOR_SCROLL = .8f;
	private static final int DEFAULT_MIN_WIDTH = 20;
	private static final int DEFAULT_MIN_HEIGHT = 10;
	private static final int DEFAULT_MAX_WIDTH = 200;
	private static final int DEFAULT_MAX_HEIGHT = 40;

	private static final int DEFAULT_HORIZONTAL_PADDING = 10;
	private static final int DEFAULT_VERTICAL_PADDING = 5;
	private static final int DEFAULT_SCROLL_VALUE = 0;

	private final BoundedValue scroll;

	private final Text text;
	private final Cursor cursor;
	private final Selection selection;

	private PGraphics pg;
	private Listener onTextChangedListener, onEnterPressedListener;

	private boolean focused, componentSizeChanged;

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

		scroll.setOnChangeValueListener(() -> {
			cursor.column.recalculateX();
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

	public TextField() {
		this(0, 0, 0, 0);
		prepareBoundsInCenter();
	}

	public boolean isEmpty() {
		return text.isEmpty();
	}

	public void setOnEnterPressedListener(Listener onEnterPressedListener) {
		this.onEnterPressedListener = requireNonNull(onEnterPressedListener, "onEnterPressedListener");
	}

	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
	}

	public ValidationMode getValidationMode() {
		return text.getValidationMode();
	}

	public void setValidationMode(ValidationMode validationMode) {
		text.setValidationMode(validationMode);
	}

	public String getText() {
		return text.getAsString();
	}

	public void setText(String text) {
		this.text.set(text);
	}

	public void setText(StringBuilder text) {
		this.text.set(text);
	}

	public boolean isTextConstrainEnabled() {
		return text.isConstrainEnabled();
	}

	public void setTextConstrainEnabled(boolean enabled) {
		text.setConstrainEnabled(enabled);
	}

	public int getMaxChars() {
		return text.getMaxChars();
	}

	public void setMaxChars(int max) {
		text.setMaxChars(max);
	}

	public int getDigits() {
		return text.getDigits();
	}

	public AbstractColor getTextColor() {
		return text.getColor();
	}

	public void setTextColor(AbstractColor color) {
		text.setColor(color);
	}

	public AbstractColor getCursorColor() {
		return cursor.getColor();
	}

	public void setCursorColor(AbstractColor color) {
		cursor.setColor(color);
	}

	public AbstractColor getSelectionColor() {
		return selection.getColor();
	}

	public void setSelectionColor(AbstractColor color) {
		selection.setColor(color);
	}

	public float getTextSize() {
		return text.getTextSize();
	}

	public void setTextSize(float size) {
		text.setTextSize(size);
	}

	public float getCursorWeight() {
		return cursor.getWeight();
	}

	public void setCursorWeight(float weight) {
		cursor.setWeight(weight);
	}

	public float getCursorBlinkRate() {
		return cursor.getBlinkRate();
	}

	public void setCursorBlinkRate(float rate) {
		cursor.setBlinkRate(rate);
	}

	public PFont getFont() {
		return text.getFont();
	}

	public TextField setFont(PFont font) {
		text.setFont(font);

		return this;
	}

	public String getHint() {
		return text.getHint();
	}

	public void setHint(String hint) {
		text.setHint(hint);
	}

	public AbstractColor getHintColor() {
		return text.getHintColor();
	}

	public void setHintColor(AbstractColor hintColor) {
		text.setHintColor(hintColor);
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!focused) {
			return;
		}
		cursor.blink.reset();

		if (e.isShiftDown()) {
			onKeyShiftPressed(e);
			return;
		}

		if (e.isControlDown()) {
			onKeyControlPressed(e);
			return;
		}

		switch (e.getKeyCode()) {
		case ENTER:
			if (onEnterPressedListener != null) {
				if (isFocused()) {
					onEnterPressedListener.action();
					setFocused(false);
				}
			}
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

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (text != null) {
			text.recalculateTextPosition();
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

	private void onKeyShiftPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case LEFT, RIGHT, VK_HOME, VK_END:
			if (!selection.isStarted()) {
				selection.setStartColumn(cursor.column.get());
				selection.setEndColumn(cursor.column.get());
				selection.setStarted(true);
			}
			break;
		}

		switch (e.getKeyCode()) {
		case LEFT:
			cursor.column.back();
			selection.setEndColumn(cursor.column.get());
			if (cursor.isCloseToLeftSide()) {
				scroll.append(-cursor.column.getPrevCharWidth());
			}
			break;

		case RIGHT:
			cursor.column.next();
			selection.setEndColumn(cursor.column.get());
			if (cursor.isCloseToRightSide()) {
				scroll.append(cursor.column.getNextCharWidth());
			}
			break;

		case VK_HOME:
			cursor.column.goToStart();
			scroll.set(scroll.getMin());
			selection.setEndColumn(cursor.column.get());
			break;

		case VK_END:
			cursor.column.goToEnd();
			scroll.set(scroll.getMax());
			selection.setEndColumn(cursor.column.get());
			break;

		default:
			if (selection.isSelected()) {
				return;
			}

			onPrintableKeyPressed();
			break;

		}
	}
	
	private void onKeyControlPressed(KeyEvent e) {
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
		}
	}

	private boolean offScreenBufferPrepared() {
		return ctx.frameCount > 2 && pg != null;
	}

	private float getTextWidth(String text) {
		if (this.text == null) {
			return 0;
		}

		requireNonNull(text, "text");

		float width;

		if (!offScreenBufferPrepared()) {
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
		} else {
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

	}

	private float getTextWidth(char ch) {
		return getTextWidth(String.valueOf(ch));
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

	private void prepareBackgroundColor() {
		final AbstractColor tc = getTheme().getEditableBackgroundColor();
		final Color preFocusedColor = new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), 200);
		final GradientColor gd = new GradientColor(preFocusedColor, tc, () -> isFocused());
		setBackgroundColor(gd);
	}

	private void prepareBoundsInCenter() {
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	private void pasteTextFromClipboard() {
		if (Clipboard.isEmpty()) {
			return;
		}

		if (selection.isSelected()) {
			text.deleteSelectedArea();
			selection.reset();
		}

		text.insert(cursor.column.get(), Clipboard.get());

		for (int i = 0; i < Clipboard.get().length(); i++) {
			cursor.column.next();
		}

		updateScrollMax();

		scroll.append(getTextWidth(Clipboard.get()));

	}

	private void cutTextToClipboard() {
		if (selection.isSelected()) {
			Clipboard.set(selection.getText());
			text.deleteSelectedArea();
			selection.reset();
		}

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
		if (selection.isSelectedAll()) {
			text.clear();
			scroll.setMax(0);
			cursor.column.set(0);
			selection.reset();

			if (onTextChangedListener != null) {
				onTextChangedListener.action();
			}

			return;
		}
		if (selection.isSelected()) {
			text.deleteSelectedArea();
			selection.reset();

			if (onTextChangedListener != null) {
				onTextChangedListener.action();
			}

			return;
		}

		if (cursor.isAtStart()) {
			return;
		}

		scroll.append(-cursor.column.getCurrentCharWidth());
		text.removeCharAt(cursor.column.get() - 1);
		cursor.column.back();

		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}

	}

	private void onKeyDeletePressed() {
		if (selection.isSelectedAll()) {
			text.clear();
			scroll.setMax(0);
			cursor.column.set(0);
			selection.reset();

			if (onTextChangedListener != null) {
				onTextChangedListener.action();
			}

			return;
		}

		if (selection.isSelected()) {
			text.deleteSelectedArea();
			selection.reset();

			if (onTextChangedListener != null) {
				onTextChangedListener.action();
			}

			return;
		}

		if (cursor.isAtEnd()) {
			return;
		}

		text.removeCharAt(cursor.column.get());

		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
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
			text.deleteSelectedArea();
			selection.reset();
		}

		text.insert(cursor.column.get(), ctx.key);

		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}

	private void mouseEventsUpdateState() {

		if (isDragging() || isPressed()) {
			setFocused(true);

			cursor.blink.reset();

			if (text.isEmpty()) {
				return;
			}

			final float value = ctx.mouseX - getX() + cursor.column.getNextCharWidth() / 2;
			final float start = text.getX();
			final float end = text.getX() + text.getWidth();
			final int start1 = 0;
			final int end1 = text.length();

			final int newColumn = (int) convert(value, start, end, start1, end1);

			cursor.column.set(newColumn);
		}

		if (mustNotHaveFocus()) {
			if (focused) {
				focused = false;
			}
			selection.reset();
		}

		if (isDragging()) {
			if (text.isEmpty()) {
				return;
			}
			onDragging();
		}

	}

	private void onDragging() {
		final float maxSpeed = constrain(cursor.column.getCurrentCharWidth() / 4, 1,
				cursor.column.getCurrentCharWidth() / 4);
		final float distFromMouseToCenterOfTextField = MathUtils.convert(ctx.mouseX, getPadX(),
				getPadX() + getPadWidth(), -maxSpeed, maxSpeed);

		scroll.append(distFromMouseToCenterOfTextField);

		if (!selection.isStarted()) {
			selection.setStartColumn(cursor.column.get());
			selection.setEndColumn(cursor.column.get());
			selection.setStarted(true);
		} else {
			selection.setEndColumn(cursor.column.get());
		}

	}

	private boolean mustNotHaveFocus() {
		return ctx.mousePressed && !isHover() && !isDragging();
	}

	private void updateScrollMax() {
		if (text.isEmpty() || text.getWidth() < getWidth() * WIDTH_RATIO_FOR_SCROLL) {
			scroll.setMax(0);
			return;
		}

		scroll.setMax((text.getWidth() - getWidth() * WIDTH_RATIO_FOR_SCROLL));
	}

	private void checkDimensions() {
		if (ctx.mousePressed) {
			return;
		}
		if (componentSizeChanged) {
			createPGraphics();
		}
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

	private static final class Text extends TextController {
		private static final int MIN_TEXT_SIZE = 1;
		private final TextField tf;
		private AbstractColor color, hintColor;
		private PFont font;
		private String hint;
		private float x, y, textWidth, textSize;

		private Text(TextField textField) {
			super();
			this.tf = requireNonNull(textField, "textField");
			color = getTheme().getEditableTextColor();
			hintColor = new GradientLoopColor(color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 232));
			recalculateTextPosition();
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
			pg.text(mustShowHint() ? hint : getAsString(), x, y);

			pg.popStyle();

			recalculateTextPositionX();
		}

		public final AbstractColor getHintColor() {
			return hintColor;
		}

		public final void setHintColor(AbstractColor hintColor) {
			this.hintColor = requireNonNull(hintColor, "hintColor");
		}

		public final float getTextSize() {
			return textSize;
		}

		public final void setTextSize(float textSize) {
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

		@Override
		protected void onAfterInsert() {
			final Cursor.Column column = tf.cursor.column;
			final BoundedValue scroll = tf.scroll;

			column.next();
			scroll.append(column.getPrevCharWidth());
		}

		@Override
		protected void onTextChanged() {
			if (tf == null) {
				return;
			}

			recalculateTextWidth();
			recalculateTextPositionX();

			tf.updateScrollMax();
			tf.cursor.column.recalculateX();

		}

		private boolean mustShowHint() {
			return !tf.isFocused() && isEmpty() && hint != null;
		}

		private void recalculateTextPositionX() {
			x = -tf.scroll.get();
		}

		private void recalculateTextPosition() {
			recalculateTextPositionX();
			y = tf.getHeight() / 2;
		}

		private float getX() {
			return x;
		}

		private void deleteSelectedArea() {
			final Selection s = tf.selection;
			final Cursor c = tf.cursor;

			final int esc = s.getEffectiveStartColumn();
			final int eec = s.getEffectiveEndColumn();

			final int selectedChars = eec - esc;

			for (int i = 0; i < selectedChars; i++) {
				final float nextCharWidth = c.column.getNextCharWidth();

				if (s.getStartColumn() < s.getEndColumn()) {
					//
					tf.scroll.append(-nextCharWidth);
					c.column.back();
				}

			}

			remove(esc, eec);

			recalculateTextWidth();
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
				this.column = (int) constrain(column, 0, tf.text.length());
				recalculateX();
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

			private void recalculateX() {
				if (tf.isEmpty()) {
					tf.cursor.positionX = 0;
					return;
				}

				final String text = tf.getText();
				final float scrollValue = tf.scroll.get();
				float subTextWidth = 0;

				subTextWidth = tf.getTextWidth(text.substring(0, (int) constrain(column, 0, tf.getText().length())));

				tf.cursor.positionX = subTextWidth - scrollValue;
			}

			private float getCurrentCharWidth() {
				if (tf.isEmpty()) {
					return 0;
				}
				return tf.getTextWidth(tf.getText().charAt((int) constrain(column, 0, tf.getText().length() - 1)));
			}

			private float getNextCharWidth() {
				if (tf.isEmpty() || column == tf.getText().length() - 1) {
					return 0;
				}
				return tf.getTextWidth(tf.getText().charAt((int) constrain(column + 1, 0, tf.getText().length() - 1)));
			}

			private float getPrevCharWidth() {
				if (tf.isEmpty() || column == 0) {
					return 0;
				}
				return tf.getTextWidth(tf.getText().charAt((int) constrain(column - 1, 0, tf.getText().length() - 1)));
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

			final String s = tf.getText();
			final int sc = getEffectiveStartColumn();
			final int ec = getEffectiveEndColumn();

			x = tf.getTextWidth(s.substring(0, sc));
			w = tf.getTextWidth(s.substring(sc, ec));
		}

		private String getText() {
			if (tf.text.isEmpty()) {
				return "";
			}
			return tf.text.getAsString().substring(getEffectiveStartColumn(), getEffectiveEndColumn());
		}

		private int getEffectiveStartColumn() {
			return (int) Math.min(startColumn, endColumn);
		}

		private void setStartColumn(int startColumn) {
			this.startColumn = (int) constrain(startColumn, 0, tf.text.length());

			x = tf.getTextWidth(tf.getText().substring(0, getEffectiveEndColumn()));

		}

		private int getEffectiveEndColumn() {
			return (int) Math.max(startColumn, endColumn);
		}

		private void setEndColumn(int endColumn) {
			this.endColumn = (int) constrain(endColumn, 0, tf.text.length());

			w = tf.getTextWidth(tf.getText().substring(getEffectiveStartColumn(), getEffectiveEndColumn()));
		}

		private int getStartColumn() {
			return startColumn;
		}

		private int getEndColumn() {
			return endColumn;
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
			endColumn = tf.text.length();
			recalculateBounds();
		}

		private void selectWord() {
			final String str = tf.text.getAsString();

			if (str.isEmpty()) {
				return;
			}

			final int currentColumn = (int) constrain(tf.cursor.column.get(), 0, str.length() - 1);

			if (!Character.isLetterOrDigit(str.charAt(currentColumn))) {
				selectAll();
				return;
			}

			int start = 0;

			for (int i = currentColumn; i > 0; i--) {
				if (str.charAt(i) == ' ') {
					start = i + 1;
					break;
				}
			}

			int end = str.length();

			for (int i = currentColumn; i < str.length(); i++) {
				if (str.charAt(i) == ' ') {
					end = i;
					break;
				}
			}

			startColumn = start;
			endColumn = end;
			recalculateBounds();
		}

		private boolean isSelectedAll() {
			if (tf.isEmpty()) {
				return false;
			}

			return getEffectiveStartColumn() == 0 && getEffectiveEndColumn() == tf.text.length();
		}

		private boolean isStarted() {
			return started;
		}

		private void setStarted(boolean started) {
			this.started = started;
		}

	}
}