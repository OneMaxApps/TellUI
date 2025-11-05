package microui.component;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.event.KeyboardManager.checkKeyPressed;
import static microui.util.MathUtils.constrain;
import static microui.util.MathUtils.convert;
import static processing.core.PConstants.BACKSPACE;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CONTROL;
import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

import microui.core.TextController;
import microui.core.TextController.ValidationMode;
import microui.core.base.Component;
import microui.core.interfaces.KeyPressable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.event.Listener;
import microui.util.BoundedValue;
import microui.util.Clipboard;
import microui.util.MathUtils;
import microui.util.Metrics;
import processing.core.PFont;
import processing.core.PGraphics;

public final class TextField extends Component implements KeyPressable {
	private static final int DEFAULT_MIN_WIDTH = 20;
	private static final int DEFAULT_MIN_HEIGHT = 10;
	private static final int DEFAULT_MAX_WIDTH = 200;
	private static final int DEFAULT_MAX_HEIGHT = 40;

	private static final int DEFAULT_HORIZONTAL_PADDING = 10;
	private static final int DEFAULT_VERTICAL_PADDING = 5;
	private static final int DEFAULT_SCROLL_VALUE = 0;

	private final Text text;
	private final Cursor cursor;
	private final Selection selection;

	private final BoundedValue scroll;
	private PGraphics pg;
	private Listener onTypeListener;

	private boolean focused, componentSizeChanged;

	public TextField(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinSize(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
		setMaxSize(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
		setPadding(DEFAULT_HORIZONTAL_PADDING, DEFAULT_VERTICAL_PADDING);

		prepareBackgroundColor();
		
		text = new Text(this);
		cursor = new Cursor(this);
		selection = new Selection(this);

		scroll = new BoundedValue(DEFAULT_SCROLL_VALUE);

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

	public final void setOnTypeListener(Listener onTypeListener) {
		this.onTypeListener = requireNonNull(onTypeListener,"onTypeListener");
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

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public void keyPressed() {
		if (!focused) {
			return;
		}

		cursor.blink.reset();

		if (checkKeyPressed(CONTROL)) {
			if (checkKeyPressed(VK_C)) {
				Clipboard.set(selection.getText());
			}
			if (checkKeyPressed(VK_V)) {
				pasteTextFromClipboard();
			}
			if (checkKeyPressed(VK_X)) {
				cutTextToClipboard();
			}
			if (checkKeyPressed(VK_A)) {
				selection.selectAll();
			}

			return;
		}

		switch (ctx.keyCode) {
		case LEFT:
			onKeyLeftPressed();
			break;

		case RIGHT:
			onKeyRightPressed();
			break;

		case BACKSPACE:
			onKeyBackspacePressed();
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

		updateScrollMax();
		cursor.column.updatePositionX();
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (text != null) {
			text.recalculatePosition();
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

	private void screenOffBufferOnDraw() {
		pg.beginDraw();
		pg.clear();
		text.draw(pg);
		if (focused) {
			cursor.draw(pg);
			selection.draw(pg);
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

		for (char ch : Clipboard.get().toCharArray()) {
			text.insert(cursor.column.get(), ch);
			updateScrollMax();
			cursor.column.updatePositionX();
		}
	}

	private void cutTextToClipboard() {
		if (selection.isSelectedAll()) {
			Clipboard.set(selection.getText());
			text.clear();
			scroll.setMax(0);
			cursor.column.set(0);
			selection.reset();
		}

		if (selection.isSelected()) {
			Clipboard.set(selection.getText());
			text.deleteSelectedArea();
			selection.reset();
		}

	}

	private void onKeyLeftPressed() {
		cursor.column.back();
		if (cursor.isAtStart()) {
			return;
		}
		if (cursor.isCloseToLeftSide()) {
			scroll.append(-cursor.column.getBackCharWidth());
		}
	}

	private void onKeyRightPressed() {
		cursor.column.next();
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
			return;
		}
		if (selection.isSelected()) {
			text.deleteSelectedArea();
			selection.reset();
			return;
		}

		if (cursor.isAtStart()) {
			return;
		}

		text.removeCharAt(cursor.column.get() - 1);
		scroll.append(-cursor.column.getNextCharWidth());
		cursor.column.back();
	}

	private void onKeyHomePressed() {
		cursor.column.goToStart();
		scroll.set(scroll.getMin());
	}

	private void onKeyEndPressed() {
		cursor.column.goToEnd();
		scroll.set(scroll.getMax());
	}

	private void onPrintableKeyPressed() {
		if (selection.isSelectedAll()) {
			text.clear();
			scroll.setMax(0);
			cursor.column.set(0);
			selection.reset();
		}

		text.insert(cursor.column.get(), ctx.key);
		
		if (onTypeListener != null) {
			onTypeListener.action();
		}
	}

	private void mouseEventsUpdateState() {

		if (isDragging() || isPressed()) {
			setFocused(true);

			cursor.blink.reset();

			if (text.isEmpty()) {
				return;
			}

			final float value = ctx.mouseX - getX();
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
		} else {
			selection.setStarted(false);
		}

	}

	private void onDragging() {
		final float maxSpeed = constrain(cursor.column.getCurrentCharWidth() / 4, 1,
				cursor.column.getCurrentCharWidth() / 4);
		final float distFromMouseToCenterOfTextField = MathUtils.convert(ctx.mouseX, getPadX(),
				getPadX() + getPadWidth(), -maxSpeed, maxSpeed);

		scroll.append(distFromMouseToCenterOfTextField);

		if (!selection.isStarted()) {
			selection.setStarted(true);
			selection.setStartColumn(cursor.column.get());
		} else {
			selection.setEndColumn(cursor.column.get());
		}

		updateScrollMax();
		cursor.column.updatePositionX();
	}

	private boolean mustNotHaveFocus() {
		return ctx.mousePressed && !isHover() && !isDragging();
	}

	private void updateScrollMax() {
		if (text.isEmpty() || text.getWidth() < getWidth() * .8f) {
			scroll.setMax(0);
			return;
		}

		scroll.setMax((text.getWidth() - getWidth() * .8f));
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
		pg = ctx.createGraphics((int) Math.max(1, getWidth()), (int) Math.max(1, getHeight()), ctx.sketchRenderer());
		componentSizeChanged = false;
		Metrics.register(pg);
	}

	private static final class Text extends TextController {
		private static final int MIN_TEXT_SIZE = 1;
		private final TextField textField;
		private AbstractColor color;
		private PFont font;
		private String hint;
		private float x, y, textWidth, textSize;
		
		private Text(TextField textField) {
			super();
			this.textField = requireNonNull(textField, "textField");
			color = getTheme().getEditableTextColor();

			recalculatePosition();
			setTextSize(textField.getHeight() * .8f);
		}

		public void draw(final PGraphics pg) {
			pg.pushStyle();

			color.apply(pg);

			if (font != null) {
				pg.textFont(font);
			}

			pg.textSize(textSize);
			pg.textAlign(CORNER, CENTER);
			if (isEmpty() && hint != null) {
				pg.text(hint, x, y);
			} else {
				pg.text(getAsString(), x, y);
			}
			pg.popStyle();

			recalculatePositionX();
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
		protected void onInsert() {
			textField.cursor.column.next();
			recalculatePositionX();
			textField.scroll.append(textField.cursor.column.getBackCharWidth());
			recalculateTextWidth();
		}

		private void recalculatePositionX() {
			if (textField.scroll == null) {
				x = 0;
			} else {
				x = -textField.scroll.get();
			}
		}

		private void recalculatePosition() {
			recalculatePositionX();
			y = textField.getHeight() * .5f;
		}

		private float getX() {
			return x;
		}

		private void deleteSelectedArea() {
			final Selection s = textField.selection;
			final Cursor c = textField.cursor;

			for (int i = s.getEffectiveStartColumn(); i < s.getEffectiveEndColumn(); i++) {
				final float nextCharWidth = c.column.getNextCharWidth();
				textField.scroll.append(-nextCharWidth);

				if (s.getStartColumn() < s.getEndColumn()) {
					c.column.back();
				}

			}

			remove(s.getEffectiveStartColumn(), s.getEffectiveEndColumn());

			recalculateTextWidth();
		}

		private void recalculateTextWidth() {
			if (textField.pg != null && textField.pg.textFont != null) {
				textWidth = textField.pg.textWidth(getAsString());
			}
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
			column = new Column();
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

		private final class Column {
			private int column;

			private Column() {
			}

			private void set(final int column) {
				this.column = (int) constrain(column, 0, tf.text.length());
				updatePositionX();
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
				if (column > 0) {
					column--;
					updatePositionX();
				}

			}

			private void next() {
				if (column < tf.text.length()) {
					column++;
					updatePositionX();
				}
			}

			private void updatePositionX() {
				if (tf.text.isEmpty()) {
					positionX = 0;
					return;
				}

				final String text = tf.text.getAsString();
				final float scrollValue = tf.scroll.get();
				final float subTextWidth = tf.pg.textWidth(text.substring(0, column));

				positionX = subTextWidth - scrollValue;
			}

			private float getCurrentCharWidth() {
				if (tf.text.isEmpty()) {
					return 0;
				}
				return tf.pg.textWidth(tf.text.getAsString().charAt((int) Math.min(column, tf.text.length() - 1)));
			}

			private float getNextCharWidth() {
				if (tf.text.isEmpty()) {
					return 0;
				}
				return tf.pg.textWidth(tf.text.getAsString().charAt((int) Math.min(column + 1, tf.text.length() - 1)));
			}

			private float getBackCharWidth() {
				if (tf.text.isEmpty()) {
					return 0;
				}
				return tf.pg.textWidth(
						tf.text.getAsString().charAt((int) Math.max(0, Math.min(column - 1, tf.text.length() - 1))));
			}
		}
	}

	private static final class Selection {
		private final TextField tf;
		private AbstractColor color;
		private float x, y, w, h;
		private int startColumn, endColumn;
		private boolean started;

		private Selection(TextField textField) {
			this.tf = requireNonNull(textField, "textField");

			color = getTheme().getSelectColor();
			y = textField.getHeight() * .1f;
			h = textField.getHeight() * .8f;
		}

		public void draw(final PGraphics pg) {
			pg.pushStyle();
			pg.noStroke();
			color.apply(pg);
			pg.rect(x - tf.scroll.get(), y, startColumn < endColumn ? w : -w, h);
			pg.popStyle();
		}

		public final AbstractColor getColor() {
			return color;
		}

		public final void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		private void recalculateBounds() {
			y = tf.getHeight() * .1f;
			h = tf.getHeight() * .8f;

			if (tf.pg == null) {
				return;
			}

			if (tf.text.isEmpty()) {
				x = w = 0;
				return;
			}

			x = tf.pg.textWidth(tf.text.getAsString().substring(0, getEffectiveStartColumn()));
			w = tf.pg.textWidth(tf.text.getAsString().substring(getEffectiveStartColumn(), getEffectiveEndColumn()));
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
			startColumn = (int) MathUtils.constrain(startColumn, 0, tf.text.length());
			if (tf.pg == null) {
				return;
			}

			x = tf.pg.textWidth(tf.text.getAsString().substring(0, startColumn));

			this.startColumn = startColumn;
		}

		private int getEffectiveEndColumn() {
			return (int) Math.max(startColumn, endColumn);
		}

		private void setEndColumn(int endColumn) {
			endColumn = (int) constrain(endColumn, 0, tf.text.length());

			if (tf.pg == null) {
				return;
			}

			w = tf.pg.textWidth(tf.text.getAsString().substring(getEffectiveStartColumn(), getEffectiveEndColumn()));

			this.endColumn = endColumn;
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