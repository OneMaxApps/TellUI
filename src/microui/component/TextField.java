package microui.component;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.BACKSPACE;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CONTROL;
import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

import microui.core.TextController;
import microui.core.base.Component;
import microui.core.interfaces.KeyPressable;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;
import microui.event.KeyboardManager;
import microui.util.Clipboard;
import microui.util.MathUtils;
import microui.util.Metrics;
import microui.util.Value;
import processing.core.PFont;
import processing.core.PGraphics;

public final class TextField extends Component implements KeyPressable {
	private static final int LEFT_OFFSET = 10;

	private final Stroke stroke;

	private final Text text;
	private final Cursor cursor;
	private final Selection selection;

	private final Value scroll;
	private PGraphics pg;

	private boolean isFocused, componentSizeChanged;

	public TextField(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinWidth(20);
		setMinHeight(10);
		setMaxWidth(200);
		setMaxHeight(40);

		stroke = new Stroke();

		setBackgroundColor(getTheme().getEditableBackgroundColor());

		text = new Text();
		cursor = new Cursor();
		selection = new Selection();

		scroll = new Value(0);

		createPGraphics();

	}

	public TextField() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	@Override
	protected final void render() {

		checkDimensions();

		ctx.pushStyle();
		getBackgroundColor().apply();
		ctx.rect(getX(), getY(), getWidth(), getHeight());

		pg.beginDraw();
		pg.clear();
		text.draw(pg);
		if (isFocused) {
			cursor.draw(pg);
			selection.draw(pg);
		}
		pg.endDraw();

		ctx.image(pg, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());

		if (!isFocused) {
			ctx.fill(0, 10);
			ctx.rect(getX(), getY(), getWidth(), getHeight());
		}
		ctx.popStyle();

		ctx.pushStyle();
		stroke.apply();
		ctx.noFill();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();

		events();
	}

	public final Text getText() {
		return text;
	}

	public final Cursor getCursor() {
		return cursor;
	}

	public final AbstractColor getSelectionColor() {
		return selection.color;
	}

	private final void events() {

		if (isPressed()) {
			if (!isFocused) {
				isFocused = true;
			}
			cursor.blink.reset();
		}

		if (mustNotHaveFocus()) {
			if (isFocused) {
				isFocused = false;
			}
			selection.reset();
		}

		if (isDragging()) {
			if (text.isEmpty()) {
				return;
			}

			cursor.column.set((int) MathUtils.convert(ctx.mouseX - getX(), text.getX(), text.getX() + text.getWidth(),
					0, text.length()));

			if (ctx.frameCount % 3 == 0) {
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

		} else {
			selection.setStarted(false);
		}

		if (isDoubleClick()) {
			if (selection.isSelected()) {
				selection.reset();
			} else {
				selection.selectAll();
			}
		}
	}

	private final boolean mustNotHaveFocus() {
		return ctx.mousePressed && isLeave() && !isDragging();
	}

	private final void updateScrollMax() {
		if (text.isEmpty() || text.getWidth() < getWidth() * .8f) {
			scroll.setMax(0);
			return;
		}

		scroll.setMax((text.getWidth() - getWidth() * .8f));
	}

	private final void checkDimensions() {
		if (ctx.mousePressed) {
			return;
		}
		if (componentSizeChanged) {
			createPGraphics();
		}
	}

	private final void createPGraphics() {
		pg = ctx.createGraphics((int) Math.max(1, getWidth()), (int) Math.max(1, getHeight()), ctx.sketchRenderer());
		componentSizeChanged = false;
		Metrics.register(pg);
	}

	@Override
	public void onChangeBounds() {
		super.onChangeBounds();

		if (text != null) {
			text.updatePosition();
			text.size.set(getHeight() * .8f);
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

	public final boolean isFocused() {
		return isFocused;
	}

	public final void setFocused(boolean focused) {
		this.isFocused = focused;
	}

	@Override
	public final void keyPressed() {
		if (!isFocused) {
			return;
		}

		cursor.blink.reset();

		if (KeyboardManager.checkKey(CONTROL)) {
			if (KeyboardManager.checkKey(VK_C)) {
				Clipboard.set(selection.getText());
			}
			if (KeyboardManager.checkKey(VK_V)) {
				if (Clipboard.isEmpty()) {
					return;
				}

				for (char ch : Clipboard.get().toCharArray()) {
					text.insert(cursor.column.get(), ch);
					updateScrollMax();
					cursor.column.updatePositionX();
				}
			}

			if (KeyboardManager.checkKey(VK_X)) {
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

			if (KeyboardManager.checkKey(VK_A)) {
				selection.selectAll();
			}
			return;
		}

		switch (ctx.keyCode) {
		case LEFT:
			cursor.column.back();
			if (cursor.isInStart()) {
				return;
			}
			if (cursor.isCloseToLeftSide()) {
				scroll.append(-cursor.column.getBackCharWidth());
			}

			break;

		case RIGHT:
			cursor.column.next();
			if (cursor.isInEnd()) {
				return;
			}
			if (cursor.isCloseToRightSide()) {
				scroll.append(cursor.column.getNextCharWidth());
			}
			break;

		case BACKSPACE:
			if (selection.isSelectedAll()) {
				text.clear();
				scroll.setMax(0);
				cursor.column.set(0);
				selection.reset();
				break;
			}
			if (selection.isSelected()) {
				text.deleteSelectedArea();
				selection.reset();
				break;
			}

			if (cursor.isInStart()) {
				break;
			}
			text.removeCharAt(cursor.column.get() - 1);
			scroll.append(-cursor.column.getNextCharWidth());
			cursor.column.back();

			break;
		case VK_HOME:
			cursor.column.goToStart();
			scroll.set(scroll.getMin());
			break;
		case VK_END:
			cursor.column.goToEnd();
			scroll.set(scroll.getMax());
			break;

		default:

			if (selection.isSelectedAll()) {
				text.clear();
				scroll.setMax(0);
				cursor.column.set(0);
				selection.reset();
			}

			text.insert(cursor.column.get(), ctx.key);

			break;

		}

		updateScrollMax();
		cursor.column.updatePositionX();
	}

	public final class Text extends TextController {
		private final AbstractColor color;
		private final Size size;
		private final Font font;
		private float x, y;
		private String hint;

		private Text() {
			super();
			color = getTheme().getEditableTextColor();
			size = new Size();
			font = new Font();

			updatePosition();
			size.set(getHeight() * .8f);
		}

		private final void draw(final PGraphics pg) {
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

		public final AbstractColor getColor() {
			return color;
		}

		public final Size getSize() {
			return size;
		}

		public final Font getFont() {
			return font;
		}

		private final void updatePosition() {
			updatePositionX();
			y = TextField.this.getHeight() * .5f;
		}

		private final void updatePositionX() {
			if (scroll == null) {
				x = LEFT_OFFSET;
			} else {
				x = LEFT_OFFSET - scroll.get();
			}
		}

		public final String getHint() {
			return hint;
		}

		public final void setHint(String hint) {
			this.hint = hint;
		}

		@Override
		protected void inInserting() {
			cursor.column.next();
			updatePositionX();
			scroll.append(cursor.column.getBackCharWidth());
		}

		private final float getX() {
			return x;
		}

		private final void deleteSelectedArea() {
			for (int i = selection.getStartColumn(); i < selection.getEndColumn(); i++) {
				scroll.append(-cursor.column.getNextCharWidth());
				if (selection.getRealStartColumn() < selection.getRealEndColumn()) {
					cursor.column.back();
				}
			}

			sb.delete(selection.getStartColumn(), selection.getEndColumn());
		}

		private final float getWidth() {
			return pg.textWidth(getAsString());
		}

		public final class Size extends AbstractSize {

			private Size() {
			}

			@Override
			public final void set(final float size) {
				if (size < 1 || size > TextField.this.getHeight()) {
					return;
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

		public final class Font {
			private PFont font;

			private Font() {
			}

			public final void set(PFont font) {
				if (font == null) {
					return;
				}
				this.font = font;
			}

			public final PFont get() {
				return font;
			}

			private final void use(PGraphics pg) {
				if (pg == null || font == null) {
					return;
				}
				pg.textFont(font);
			}
		}
	}

	public final class Cursor {
		private final AbstractColor color;
		private final Size weight;
		private final Blink blink;
		private final Column column;
		private float positionX, positionY, height;

		private Cursor() {
			color = getTheme().getCursorColor();
			weight = new Size(2);
			column = new Column();
			blink = new Blink();
			updateTransforms();
		}

		private final void draw(final PGraphics pg) {
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

		public final AbstractColor getColor() {
			return color;
		}

		public final float getWeight() {
			return weight.get();
		}

		public final void setWeight(float weight) {
			this.weight.set(weight);
		}

		public final void setBlinkRate(float rate) {
			blink.setRate(rate);
		}

		private final void updateTransforms() {
			positionY = TextField.this.getHeight() * .1f;
			height = TextField.this.getHeight() * .9f;
		}

		private final boolean isInStart() {
			return column.get() == 0;
		}

		private final boolean isInEnd() {
			return column.get() == text.length();
		}

		private final boolean isCloseToLeftSide() {
			return positionX < getWidth() * .1f;
		}

		private final boolean isCloseToRightSide() {
			return positionX > getWidth() * .8f;
		}

		private final class Blink {
			private int duration, maxDuration;
			private float rate;

			private Blink() {
				maxDuration = 60;
				rate = 1;
			}

			private final boolean isBlinking() {
				return duration < maxDuration / 2;
			}

			private final void updateState() {
				if (duration < maxDuration) {
					duration += rate;
				} else {
					duration = 0;
				}
			}

			private final void reset() {
				duration = 0;
			}

			public final void setRate(final float rate) {
				if (rate <= 0 || rate >= maxDuration / 2) {
					return;
				}
				this.rate = rate;
			}
		}

		private final class Column {
			private int column;

			private Column() {
			}

			private final void set(final int column) {
				if (column < 0 || column > text.length()) {
					return;
				}
				this.column = column;
				updatePositionX();
			}

			private final int get() {
				return column;
			}

			private final void goToStart() {
				set(0);
			}

			private final void goToEnd() {
				set(text.length());
			}

			private final void back() {
				if (column > 0) {
					column--;
					updatePositionX();
				}

			}

			private final void next() {
				if (column < text.length()) {
					column++;
					updatePositionX();
				}
			}

			private final void updatePositionX() {
				if (text.isEmpty()) {
					positionX = 0;
					return;
				}

				positionX = pg.textWidth(text.getAsString().substring(0, column)) - scroll.get();
			}

			private final float getCurrentCharWidth() {
				if (text.isEmpty()) {
					return 0;
				}
				return pg.textWidth(text.getAsString().charAt((int) Math.min(column, text.length() - 1)));
			}

			private final float getNextCharWidth() {
				if (text.isEmpty()) {
					return 0;
				}
				return pg.textWidth(text.getAsString().charAt((int) Math.min(column + 1, text.length() - 1)));
			}

			private final float getBackCharWidth() {
				if (text.isEmpty()) {
					return 0;
				}
				return pg.textWidth(
						text.getAsString().charAt((int) Math.max(0, Math.min(column - 1, text.length() - 1))));
			}
		}

		public final class Size extends AbstractSize {

			private Size(final float size) {
				set(size);
			}

			@Override
			public void set(float size) {
				if (size < 1 || size > 10) {
					return;
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
		private final AbstractColor color;
		private float x, y, w, h;
		private int startColumn, endColumn;
		private boolean isStarted;

		private Selection() {
			color = getTheme().getSelectColor();
			y = getHeight() * .1f;
			h = getHeight() * .8f;
		}

		private final void draw(final PGraphics pg) {
			pg.pushStyle();
			pg.noStroke();
			color.apply(pg);
			pg.rect(x + LEFT_OFFSET - scroll.get(), y, startColumn < endColumn ? w : -w, h);
			pg.popStyle();
		}

		private final void updateTransforms() {

			y = getHeight() * .1f;
			h = getHeight() * .8f;

			if (pg == null) {
				return;
			}
			if (text.isEmpty()) {
				x = w = 0;
				return;
			}
			x = pg.textWidth(text.getAsString().substring(0, getStartColumn()));
			w = pg.textWidth(text.getAsString().substring(getStartColumn(), getEndColumn()));
		}

		private final String getText() {
			if (text.isEmpty()) {
				return "";
			}
			return text.getAsString().substring(getStartColumn(), getEndColumn());
		}

		private final int getStartColumn() {
			return (int) Math.min(startColumn, endColumn);
		}

		private final void setStartColumn(int startColumn) {
			startColumn = (int) MathUtils.constrain(startColumn, 0, text.length());
			if (pg == null) {
				return;
			}

			x = pg.textWidth(text.getAsString().substring(0, startColumn));

			this.startColumn = startColumn;
		}

		private final int getEndColumn() {
			return (int) Math.max(startColumn, endColumn);
		}

		private final void setEndColumn(int endColumn) {
			endColumn = (int) MathUtils.constrain(endColumn, 0, text.length());
			if (pg == null) {
				return;
			}

			w = pg.textWidth(text.getAsString().substring(getStartColumn(), getEndColumn()));

			this.endColumn = endColumn;
		}

		private final int getRealStartColumn() {
			return startColumn;
		}

		private final int getRealEndColumn() {
			return endColumn;
		}

		private final void reset() {
			startColumn = endColumn = 0;
			isStarted = false;

			updateTransforms();
		}

		private final boolean isSelected() {
			return startColumn != endColumn;
		}

		private final void selectAll() {
			startColumn = 0;
			endColumn = text.length();
			updateTransforms();
		}

		private final boolean isSelectedAll() {
			return getStartColumn() == 0 && getEndColumn() == text.length();
		}

		private final boolean isStarted() {
			return isStarted;
		}

		private final void setStarted(boolean isStarted) {
			this.isStarted = isStarted;
		}

	}

	private abstract class AbstractSize {
		protected float size;

		private AbstractSize() {
		}

		public abstract void set(final float size);

		public final float get() {
			return size;
		}

		protected abstract void use(PGraphics pg);

	}
}