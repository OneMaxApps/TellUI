package microui.component;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.event.KeyboardManager.checkKey;
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
import microui.core.style.Stroke;
import microui.util.BoundedValue;
import microui.util.Clipboard;
import microui.util.MathUtils;
import microui.util.Metrics;
import processing.core.PFont;
import processing.core.PGraphics;

public final class TextField extends Component implements KeyPressable {
	private static final int LEFT_OFFSET = 10;

	private final Stroke stroke;

	private final Text text;
	private final Cursor cursor;
	private final Selection selection;

	private final BoundedValue scroll;
	private PGraphics pg;

	private boolean focused, componentSizeChanged;

	public TextField(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinSize(20, 10);
		setMaxSize(200, 40);
		setBackgroundColor(getTheme().getEditableBackgroundColor());

		stroke = new Stroke();

		text = new Text();
		cursor = new Cursor();
		selection = new Selection();

		scroll = new BoundedValue(0);

		setTextSize(getMaxHeight());

		createPGraphics();
		
		onDoubleClick(() -> {
			if (selection.isSelected()) {
				selection.reset();
			} else {
				selection.selectAll();
			}
		});
		
		
	}

	public TextField() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
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
		;
	}

	public AbstractColor getSelectionColor() {
		return selection.getColor();
	}

	public void setSelectionColor(AbstractColor color) {
		selection.setColor(color);
	}

	public float getTextSize() {
		return text.size.get();
	}

	public void setTextSize(float size) {
		text.size.set(size);
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
		return text.font.get();
	}

	public void setFont(PFont font) {
		text.font.set(font);
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
	public void onChangeBounds() {
		super.onChangeBounds();

		if (text != null) {
			text.updatePosition();
		}

		if (scroll != null) {
			updateScrollMax();
		}

		if (cursor != null) {
			cursor.updateTransforms();
		}

		if (selection != null) {
			selection.updateTransforms();
		}

		componentSizeChanged = true;
	}

	@Override
	public void keyPressed() {
		if (!focused) {
			return;
		}

		cursor.blink.reset();

		if (checkKey(CONTROL)) {
			if (checkKey(VK_C)) {
				Clipboard.set(selection.getText());
			}
			if (checkKey(VK_V)) {
				pasteTextFromClipboard();
			}
			if (checkKey(VK_X)) {
				cutTextToClipboard();
			}
			if (checkKey(VK_A)) {
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
	protected void render() {
		checkDimensions();

		ctx.pushStyle();
		getBackgroundColor().apply();
		ctx.rect(getX(), getY(), getWidth(), getHeight());

		pg.beginDraw();
		pg.clear();
		text.draw(pg);
		if (focused) {
			cursor.draw(pg);
			selection.draw(pg);
		}
		pg.endDraw();

		ctx.image(pg, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());

		if (!focused) {
			ctx.fill(0, 10);
			ctx.rect(getX(), getY(), getWidth(), getHeight());
		}
		ctx.popStyle();

		ctx.pushStyle();
		stroke.apply();
		ctx.noFill();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();

		mouseEvents();
	}

	private void pasteTextFromClipboard() {
		if (Clipboard.isEmpty()) {
			return;
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
			text.deleteSelectedArea();
			selection.reset();
		}
	}

	private void onKeyLeftPressed() {
		cursor.column.back();
		if (cursor.isInStart()) {
			return;
		}
		if (cursor.isCloseToLeftSide()) {
			scroll.append(-cursor.column.getBackCharWidth());
		}
	}

	private void onKeyRightPressed() {
		cursor.column.next();
		if (cursor.isInEnd()) {
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

		if (cursor.isInStart()) {
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
	}

	private void mouseEvents() {

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
		if (ctx.frameCount % 2 == 0) {
			if (cursor.isCloseToLeftSide()) {
				scroll.append(-cursor.column.getCurrentCharWidth());
			}
			if (cursor.isCloseToRightSide()) {
				scroll.append(cursor.column.getCurrentCharWidth());
			}
		}

		if (!selection.isStarted()) {
			selection.setStartColumn(cursor.column.get());
			selection.setStarted(true);
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

	private final class Text extends TextController {
		private AbstractColor color;
		private final TextSize size;
		private final Font font;
		private float x, y;
		private String hint;

		private Text() {
			super();
			color = getTheme().getEditableTextColor();
			size = new TextSize();
			font = new Font();

			updatePosition();
			size.set(getHeight() * .8f);
		}

		public void draw(final PGraphics pg) {
			pg.pushStyle();

			color.apply(pg);
			font.use(pg);
			size.use(pg);
			pg.textAlign(CORNER, CENTER);
			if (isEmpty() && hint != null) {
				pg.text(hint, x, y);
			} else {
				pg.text(getAsString(), x, y);
			}
			pg.popStyle();

			updatePositionX();
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

		@Override
		protected void onInsert() {
			cursor.column.next();
			updatePositionX();
			scroll.append(cursor.column.getBackCharWidth());
		}

		private void updatePosition() {
			updatePositionX();
			y = TextField.this.getHeight() * .5f;
		}

		private void updatePositionX() {
			if (scroll == null) {
				x = LEFT_OFFSET;
			} else {
				x = LEFT_OFFSET - scroll.get();
			}
		}

		private float getX() {
			return x;
		}

		private void deleteSelectedArea() {
			final Selection s = selection;
			final Cursor c = cursor;

			for (int i = s.getEffectiveStartColumn(); i < s.getEffectiveEndColumn(); i++) {
				final float nextCharWidth = c.column.getNextCharWidth();
				scroll.append(-nextCharWidth);

				if (s.getStartColumn() < s.getEndColumn()) {
					c.column.back();
				}

			}

			remove(selection.getEffectiveStartColumn(), selection.getEffectiveEndColumn());

		}

		private float getWidth() {
			return pg.textWidth(getAsString());
		}

		private final class TextSize extends AbstractSize {

			private TextSize() {
			}

			@Override
			public final void set(final float size) {
				if (size < 1) {
					throw new IllegalArgumentException("Text size cannot be less than 1");
				}
				this.size = size;
			}

			@Override
			protected final void use(PGraphics pg) {
				if (pg == null) {
					return;
				}
				pg.textSize(size);
			}
		}

		private final class Font {
			private PFont font;

			private Font() {
			}

			public final void set(PFont font) {
				this.font = requireNonNull(font, "font");
			}

			public final PFont get() {
				return font;
			}

			private void use(PGraphics pg) {
				if (pg == null || font == null) {
					return;
				}
				pg.textFont(font);
			}
		}
	}

	private final class Cursor {
		private AbstractColor color;
		private final Weight weight;
		private final Blink blink;
		private final Column column;
		private float positionX, positionY, height;

		private Cursor() {
			color = getTheme().getCursorColor();
			weight = new Weight(2);
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
				weight.use(pg);
				pg.line(positionX + LEFT_OFFSET, positionY, positionX + LEFT_OFFSET, height);
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
			return weight.get();
		}

		public void setWeight(float weight) {
			this.weight.set(weight);
		}

		public float getBlinkRate() {
			return blink.getRate();
		}

		public void setBlinkRate(float rate) {
			blink.setRate(rate);
		}

		private void updateTransforms() {
			positionY = TextField.this.getHeight() * .1f;
			height = TextField.this.getHeight() * .9f;
		}

		private boolean isInStart() {
			return column.get() == 0;
		}

		private boolean isInEnd() {
			return column.get() == text.length();
		}

		private boolean isCloseToLeftSide() {
			return positionX < getWidth() * .1f;
		}

		private boolean isCloseToRightSide() {
			return positionX > getWidth() * .8f;
		}

		private final class Blink {
			private static final byte MAX_DURATION = 60;
			private int duration;
			private int rate;

			private Blink() {
				rate = 1;
			}

			public float getRate() {
				return rate;
			}

			public void setRate(final float rate) {
				if (rate < 1 || rate >= MAX_DURATION / 2) {
					throw new IllegalArgumentException("Rate for blink must be between 1 and " + MAX_DURATION / 2);
				}
				this.rate = (int) rate;
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
				if (column < 0 || column > text.length()) {
					return;
				}
				this.column = column;
				updatePositionX();
			}

			private int get() {
				return column;
			}

			private void goToStart() {
				set(0);
			}

			private void goToEnd() {
				set(text.length());
			}

			private void back() {
				if (column > 0) {
					column--;
					updatePositionX();
				}

			}

			private void next() {
				if (column < text.length()) {
					column++;
					updatePositionX();
				}
			}

			private void updatePositionX() {
				if (text.isEmpty()) {
					positionX = 0;
					return;
				}

				positionX = pg.textWidth(text.getAsString().substring(0, column)) - scroll.get();
			}

			private float getCurrentCharWidth() {
				if (text.isEmpty()) {
					return 0;
				}
				return pg.textWidth(text.getAsString().charAt((int) Math.min(column, text.length() - 1)));
			}

			private float getNextCharWidth() {
				if (text.isEmpty()) {
					return 0;
				}
				return pg.textWidth(text.getAsString().charAt((int) Math.min(column + 1, text.length() - 1)));
			}

			private float getBackCharWidth() {
				if (text.isEmpty()) {
					return 0;
				}
				return pg.textWidth(
						text.getAsString().charAt((int) Math.max(0, Math.min(column - 1, text.length() - 1))));
			}
		}

		private final class Weight extends AbstractSize {

			private Weight(final float size) {
				set(size);
			}

			@Override
			public void set(float size) {
				if (size < 1 || size > 10) {
					throw new IllegalArgumentException("Cursor weight must be between 1 and 10");
				}
				this.size = size;
			}

			@Override
			protected void use(PGraphics pg) {
				if (pg == null) {
					return;
				}
				pg.strokeWeight(size);
			}

		}
	}

	private final class Selection {
		private AbstractColor color;
		private float x, y, w, h;
		private int startColumn, endColumn;
		private boolean isStarted;

		private Selection() {
			color = getTheme().getSelectColor();
			y = getHeight() * .1f;
			h = getHeight() * .8f;
		}

		public void draw(final PGraphics pg) {
			pg.pushStyle();
			pg.noStroke();
			color.apply(pg);
			pg.rect(x + LEFT_OFFSET - scroll.get(), y, startColumn < endColumn ? w : -w, h);
			pg.popStyle();
		}

		public final AbstractColor getColor() {
			return color;
		}

		public final void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		private void updateTransforms() {

			y = getHeight() * .1f;
			h = getHeight() * .8f;

			if (pg == null) {
				return;
			}
			if (text.isEmpty()) {
				x = w = 0;
				return;
			}
			x = pg.textWidth(text.getAsString().substring(0, getEffectiveStartColumn()));
			w = pg.textWidth(text.getAsString().substring(getEffectiveStartColumn(), getEffectiveEndColumn()));
		}

		private String getText() {
			if (text.isEmpty()) {
				return "";
			}
			return text.getAsString().substring(getEffectiveStartColumn(), getEffectiveEndColumn());
		}

		private int getEffectiveStartColumn() {
			return (int) Math.min(startColumn, endColumn);
		}

		private void setStartColumn(int startColumn) {
			startColumn = (int) MathUtils.constrain(startColumn, 0, text.length());
			if (pg == null) {
				return;
			}

			x = pg.textWidth(text.getAsString().substring(0, startColumn));

			this.startColumn = startColumn;
		}

		private int getEffectiveEndColumn() {
			return (int) Math.max(startColumn, endColumn);
		}

		private void setEndColumn(int endColumn) {
			endColumn = (int) MathUtils.constrain(endColumn, 0, text.length());
			if (pg == null) {
				return;
			}

			w = pg.textWidth(text.getAsString().substring(getEffectiveStartColumn(), getEffectiveEndColumn()));

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
			isStarted = false;

			updateTransforms();
		}

		private boolean isSelected() {
			return startColumn != endColumn;
		}

		private void selectAll() {
			startColumn = 0;
			endColumn = text.length();
			updateTransforms();
		}

		private boolean isSelectedAll() {
			return getEffectiveStartColumn() == 0 && getEffectiveEndColumn() == text.length();
		}

		private boolean isStarted() {
			return isStarted;
		}

		private void setStarted(boolean isStarted) {
			this.isStarted = isStarted;
		}

	}

	private abstract class AbstractSize {
		protected float size;

		private AbstractSize() {
		}

		public abstract void set(final float size);

		public float get() {
			return size;
		}

		protected abstract void use(PGraphics pg);

	}
}