package microui.component;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.lang.Math.max;
import static microui.component.EditTextOld.CharValidate.isValidChar;
import static microui.constants.Orientation.VERTICAL;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PApplet.constrain;
import static processing.core.PApplet.min;
import static processing.core.PConstants.BACKSPACE;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CONTROL;
import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.TAB;
import static processing.core.PConstants.UP;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import microui.core.base.Component;
import microui.core.base.SpatialView;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.Stroke;
import microui.event.Event;
import microui.event.KeyboardManager;
import microui.util.Clipboard;
import microui.util.Metrics;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

@Deprecated
public class EditTextOld extends Component implements Scrollable, KeyPressable {
	private boolean isFocused;

	private final Stroke stroke;

	private final Scroll scrollV, scrollH;
	private final Items items;
	private final Selection selection;
	private final Cursor cursor;

	private PGraphics graphics;
	private PFont font;

	public EditTextOld(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinMaxSize(40, 20, ctx.width, ctx.height);

		setBackgroundColor(getTheme().getEditableBackgroundColor());

		setPaddingLeft(10);
		
		scrollV = new Scroll();
		scrollH = new Scroll();
		initDefaultScrollsSettings();

		stroke = new Stroke(getTheme().getStrokeColor());

		items = new Items();
		selection = new Selection();
		cursor = new Cursor();

		createGraphics();

		scrollsValuesUpdate();

		onClick(() -> setFocus(true));

		onDoubleClick(() -> {
			getCurrentItem().setFullSelectEnabled(true);
		});
		
		onPress(() -> {
			if (getCurrentItem().isSelecting()) {
				selection.unselect();
			}
		});

	}

	public EditTextOld() {
		this(ctx.width * .1f, ctx.height * .1f, ctx.width * .8f, ctx.height * .8f);
	}

	@Override
	protected void render() {
		
		ctx.pushStyle();
		stroke.apply();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();
		
		scrollH.setVisible(isFocused);
		scrollV.setVisible(isFocused);
		
		graphics.beginDraw();
		graphics.clear();
		items.draw(graphics);
		cursor.draw(graphics);
		graphics.endDraw();

		ctx.image(graphics, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());

		scrollV.draw();
		scrollH.draw();

		mouseEvents();
		selection.update();

		
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		if (scrollH == null || scrollV == null) {
			return;
		}

		scrollsTransformsUpdate();
	}

	@Override
	public final void mouseWheel(MouseEvent e) {
		if (isFocused) {
			if (KeyboardManager.checkKeyPressed(CONTROL)) {
				items.setTextSize(items.getTextSize() + e.getCount());
				return;
			}

			if (scrollH.isHover()) {
				scrollH.mouseWheel(e);
				return;
			}

			scrollV.mouseWheel(e, isHover());
		}

	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		scrollsValuesUpdate();

		if (!isFocused) {
			return;
		}

		cursor.resetTimer();

		if (KeyboardManager.checkKeyPressed(CONTROL)) {
			if (KeyboardManager.checkKeyPressed(VK_C)) {
				Clipboard.set(selection.getText());
			}
			if (KeyboardManager.checkKeyPressed(VK_V)) {
				ctrlV();
			}
			if (KeyboardManager.checkKeyPressed(VK_X)) {
				ctrlX();
			}
			if (KeyboardManager.checkKeyPressed(VK_A)) {
				items.selectAllText();
				selection.setSelectedAllText(true);
			}
			return;
		}

		switch (ctx.keyCode) {
		case UP:
			items.goUpToEditing();
			selection.unselect();
			break;

		case DOWN:
			items.goDownToEditing();
			selection.unselect();
			break;

		case LEFT:
			cursor.back();
			selection.unselect();
			break;

		case RIGHT:
			cursor.next();
			selection.unselect();
			break;

		case TAB:
			keyTab();
			selection.unselect();
			break;

		case BACKSPACE:
			keyBackspace();
			break;

		case VK_HOME:
			keyHome();
			selection.unselect();
			break;

		case VK_END:
			keyEnd();
			selection.unselect();
			break;

		case VK_PAGE_UP:
			keyPageUp();
			selection.unselect();
			break;

		case VK_PAGE_DOWN:
			keyPageDown();
			selection.unselect();
			break;

		case VK_ENTER:
			keyEnter();
			items.deleteAllSelectedText();
			selection.unselect();
			break;

		default:
			items.deleteAllSelectedText();
			selection.unselect();

			if (isValidChar(ctx.key)) {
				getCurrentItem().insert(String.valueOf(ctx.key));
				cursor.next();
				scrollH.setMaxValue(items.getMaxTextWidthFromItems());
			}
			break;
		}
	}

	public final boolean isFocused() {
		return isFocused;
	}

	public final void setFocus(boolean isFocused) {
		this.isFocused = isFocused;
	}

	public final void loadText(String path) {
		items.clear();
		String[] lines = ctx.loadStrings(path);
		for (int i = 0; i < lines.length; i++) {
			items.add(lines[i]);
		}

		scrollV.setMinMaxValue(getHeight() - items.getTotalHeight(), 0);
		scrollV.setValue(0);

		scrollH.setMaxValue(items.getMaxTextWidthFromItems());
		scrollH.setValue(scrollH.getMinValue());
	}

	public final void setFont(PFont font) {
		this.font = font;
	}

	public final void createFont(String path) {
		this.font = ctx.createFont(path, items.textSize);
	}

	public final void createFont(String path, int textSize) {
		this.font = ctx.createFont(path, textSize);
	}

	public final void loadFont(String path) {
		this.font = ctx.loadFont(path);
	}

	public final String getSelectedText() {
		return selection.getText();
	}

	public final AbstractColor getItemsTextColor() {
		return items.color;
	}

	public final float getTextSize() {
		return items.textSize;
	}

	public final void setTextSize(float size) {
		items.setTextSize(size);
	}

	public final AbstractColor getCursorColor() {
		return cursor.color;
	}

	public final int getCurrentColumn() {
		return cursor.getCurrentColumn();
	}

	public final int getCurrentRow() {
		return cursor.getCurrentRow();
	}

	public final int getRows() {
		return cursor.getRows();
	}

	public final Scroll getScrollV() {
		return scrollV;
	}

	public final Scroll getScrollH() {
		return scrollH;
	}

	public final AbstractColor getSelectColor() {
		return items.selectColor;
	}

	private float getPreferredScrollWeight() {
		final int min = 10;
		final float max = (getWidth() + getHeight()) * .00225f;
		return max(min, max);
	}

	private Items.Item getCurrentItem() {
		return items.list.get(getCurrentRow());
	}

	private void mouseEvents() {
		if (!ctx.mousePressed) {
			checkDimensions();
		}

		if (ctx.mousePressed && isLeave() && !isDragging()) {
			isFocused = false;
		}
	}

	private void initDefaultScrollsSettings() {
		scrollV.setConstrainDimensionsEnabled(false);
		scrollH.setConstrainDimensionsEnabled(false);

		scrollV.setStrokeColor(new Color(255, 32));
		scrollH.setStrokeColor(new Color(255, 32));

		scrollV.setBackgroundColor(Color.TRANSPARENT);
		scrollH.setBackgroundColor(Color.TRANSPARENT);

		scrollsPositionUpdate();

		scrollV.setOrientation(VERTICAL);

		scrollsSizeUpdate();

		scrollH.setMinValue(0);
		scrollH.setValue(scrollH.getMinValue());

		scrollsValuesUpdate();

		scrollV.setScrollingVelocity(.1f);
		scrollH.setScrollingVelocity(.5f);

	}

	private void createGraphics() {
		graphics = ctx.createGraphics((int) getWidth(), (int) getHeight(), ctx.sketchRenderer());
		Metrics.register(graphics);
	}

	private void removeEmptyItem(int index) {
		if (items.count() <= 1) {
			return;
		}

		index = constrain(index, 0, items.count() - 1);

		if (!items.get(index).isEmpty()) {
			return;
		}

		items.list.remove(index);
		items.get(index - 1).setEditing(true);
		for (int i = index; i < items.count(); i++) {
			items.get(i).formUp();
		}
		items.appendTotalHeight(-items.getTextSize());
	}

	private void scrollsPositionUpdate() {
		if (scrollV != null && scrollH != null) {

			float x = getX(), y = getY(), w = getWidth(), h = getHeight(),
					preferredScrollWeight = getPreferredScrollWeight();

			scrollV.setPosition(x + w - preferredScrollWeight, y);
			scrollH.setPosition(x, y + h - preferredScrollWeight);
		}
	}

	private void scrollsSizeUpdate() {
		if (scrollV != null && scrollH != null) {

			float w = getWidth(), h = getHeight(), preferredScrollWeight = getPreferredScrollWeight();

			scrollV.setSize(preferredScrollWeight, h - preferredScrollWeight);
			scrollH.setSize(w, preferredScrollWeight);
		}
	}

	private void scrollsTransformsUpdate() {
		scrollsPositionUpdate();
		scrollsSizeUpdate();
	}

	private void scrollsValuesUpdate() {
		if (scrollV == null || scrollH == null || items == null) {
			return;
		}

		updateValueForScrollH();
		updateValueForScrollV();
	}

	private void updateValueForScrollH() {
		if (!items.isEmpty()) {
			scrollH.setMaxValue(getCurrentItem().getTextWidth());
		} else {
			scrollH.setValue(scrollH.getMinValue());
		}
	}

	private void updateValueForScrollV() {
		if (items.getTotalHeight() > EditTextOld.this.getHeight()) {
			scrollV.setMinValue(getHeight() - items.getTotalHeight());
		} else {
			scrollV.setMinValue(0);
		}
	}

	private void ctrlV() {
		if (isInClipboardSeveralLines()) {
			insertLines(Clipboard.getAsArray());
		} else {
			insertLine(Clipboard.get());
		}

		scrollsValuesUpdate();
	}

	private void ctrlX() {

		Clipboard.set(selection.getText());

		items.deleteAllSelectedText();

		deleteItemsFromSelectedArea();

		selection.unselect();
		selection.setSelectingState(false);

	}

	private boolean isInClipboardSeveralLines() {
		return Clipboard.get().contains(String.valueOf('\n'));
	}

	private void insertLine(String line) {
		getCurrentItem().insert(line);
		for (int i = 0; i < line.length(); i++) {
			cursor.next();
		}
	}

	private void insertLines(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				getCurrentItem().insert(lines[i]);
			} else {

				if (getCurrentItem().isEmpty()) {
					getCurrentItem().insert(lines[i]);
				} else {
					items.insert(lines[i]);
				}

				cursor.goInEnd();
				if (cursor.getPosY() > EditTextOld.this.getHeight() * .9f) {
					scrollV.appendValue(-items.getTextSize());
				}
			}
		}
	}

	private void keyEnter() {
		items.insert(cursor.getTextOfRightSide());

		scrollH.setValue(scrollH.getMinValue());
		cursor.goInStart();
	}

	private void keyPageUp() {
		getCurrentItem().setEditing(false);
		items.getFirst().setEditing(true);

		cursor.goInStart();

		scrollH.setValue(scrollH.getMinValue());
		scrollV.setValue(0);
	}

	private void keyPageDown() {
		getCurrentItem().setEditing(false);
		items.getLast().setEditing(true);

		cursor.goInEnd();

		scrollsValuesUpdate();

		scrollH.setValue(scrollH.getMinValue());
		scrollV.setValue(scrollV.getMinValue());
	}

	private void keyHome() {
		cursor.setCurrentColumn(0);
		scrollH.setValue(scrollH.getMinValue());
	}

	private void keyEnd() {
		cursor.setCurrentColumn(cursor.getMaxCharsInRow());

		if (getCurrentItem().getTextWidth() > EditTextOld.this.getWidth()) {
			scrollH.setValue(scrollH.getMaxValue() * .5f);
		}
	}

	private void keyTab() {
		getCurrentItem().insert("  ");
		cursor.next();
		cursor.next();
	}

	private void clearAllSelectedText() {
		selection.unselect();
		items.clear();
		while (items.getTotalHeight() < EditTextOld.this.getHeight()) {
			items.add("");
		}
		cursor.setCurrentRow(0);
		cursor.setCurrentColumn(0);
		scrollV.setValue(0);
	}

	private void clearSingleSelectedTextLine() {
		if (!selection.isMultiLinesSelected()) {
			if (selection.isSelectedToRight()) {
				for (int i = 0; i < getCurrentItem().getSelectedText().length(); i++) {
					cursor.back();
				}
			}
		}

		items.deleteAllSelectedText();
		deleteItemsFromSelectedArea();
		selection.unselect();
	}

	private void clearAndJoinLines() {
		final String textFromRightSideToCursor = cursor.getTextOfRightSide();
		cursor.setCurrentRow(cursor.getCurrentRow() - 1);
		cursor.goInEnd();
		getCurrentItem().append(textFromRightSideToCursor);
		removeEmptyItem(cursor.getCurrentRow() + 1);
		scrollsValuesUpdate();
	}

	private void clearEmptyLine() {
		if (cursor.getCurrentRow() == 0) {
			return;
		}

		removeEmptyItem(cursor.getCurrentRow());
		cursor.goInEnd();
		scrollV.appendValue(items.getFirst().getItemHeight());
		scrollsValuesUpdate();
	}

	private void keyBackspace() {
		if (selection.isSelectedAllText()) {
			clearAllSelectedText();
			return;
		}

		if (selection.isSelecting()) {
			clearSingleSelectedTextLine();
			return;
		}

		if (!getCurrentItem().isEmpty() && cursor.getCurrentColumn() == 0 && cursor.getCurrentRow() != 0) {
			clearAndJoinLines();
			return;
		}

		if (getCurrentItem().isEmpty()) {
			clearEmptyLine();
			return;
		}

		getCurrentItem().deleteChar();
		cursor.back();
	}

	private void deleteItemsFromSelectedArea() {
		for (int i = selection.getLastRow(); i > selection.getFirstRow(); i--) {
			removeEmptyItem(i);
		}
	}

	private void checkDimensions() {
		if (graphics.width != (int) getWidth() || graphics.height != (int) getHeight()) {
			createGraphics();
		}
	}

	public static final class CharValidate {
		static final String ADDITIONAL_CHARS = " ,.<>[]{}()+-*/\\\'\";:?!@#$%^&|_`~=";

		private CharValidate() {
		}

		public static boolean isValidChar(char ch) {
			return ADDITIONAL_CHARS.contains("" + ch) || Character.isDigit(ch) || Character.isAlphabetic(ch);
		}
	}

	private final class Items {
		private final AbstractColor color, selectColor;
		private final List<Item> list;
		private float totalHeight;
		private int textSize;

		private Items() {
			color = getTheme().getEditableTextColor();
			selectColor = getTheme().getSelectColor();
			textSize = 32;
			list = new ArrayList<Item>();
			for (int i = 0; i < getHeight() / textSize; i++) {
				list.add(new Item(i * textSize, ""));
				totalHeight += textSize;
			}

			scrollV.setMinMaxValue(getHeight() - totalHeight, 0);
			scrollV.setValue(0);
		}

		private void draw(final PGraphics pg) {
			color.apply(pg);
			if (font != null) {
				pg.textFont(font);
			}
			pg.textSize(textSize);
			pg.textAlign(CORNER, CENTER);

			for (int i = list.size() - 1; i >= 0; i--) {
				Item item = list.get(i);
				if (itemInside(item)) {
					item.draw(pg);
					if (item.isEditing()) {
						cursor.setCurrentRow(i);
					}
				} else {
					item.setEditing(false);
				}
			}
		}

		private boolean isEmpty() {
			return list.isEmpty();
		}

		private boolean itemInside(Item item) {
			return item.getItemY() > getY() - item.getItemHeight() && item.getItemY() < getY() + getHeight();
		}

		private void deleteAllSelectedText() {
			for (Item item : list) {
				item.removeSelectedText();
			}
		}

		private void selectAllText() {
			for (Item item : list) {
				item.setFullSelectEnabled(true);
			}
		}

		private int count() {
			return list.size();
		}

		private void setTextSize(float textSize) {
			if (textSize <= 2) {
				return;
			}
			this.textSize = (int) textSize;
			totalHeight = 0;
			final float tmpScrollVerticalValue = scrollV.getValue();

			for (int i = 0; i < list.size(); i++) {
				Item item = list.get(i);
				item.setShiftY(i * textSize);
				totalHeight += textSize;
			}

			scrollsValuesUpdate();
			scrollV.setValue(tmpScrollVerticalValue + textSize);
		}

		private int getTextSize() {
			return textSize;
		}

		private float getTotalHeight() {
			return totalHeight;
		}

		private void appendTotalHeight(int value) {
			totalHeight += value;
		}

		private void add(String text) {
			list.add(new Item(list.size() * textSize, text));
			totalHeight += textSize;
		}

		private void clear() {
			list.clear();
			totalHeight = 0;
			scrollsValuesUpdate();
		}

		private float getMaxTextWidthFromItems() {
			float maxWidth = 0;
			for (int i = 0; i < list.size(); i++) {
				if (maxWidth < list.get(i).getTextWidth()) {
					maxWidth = list.get(i).getTextWidth();
				}
			}
			return maxWidth;
		}

		private void goUpToEditing() {
			int id = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isEditing()) {
					if (i == 0) {
						return;
					}
					id = i - 1;
					list.get(i).setEditing(false);
				}
			}

			list.get(id).setEditing(true);

			if (totalHeight > getHeight() && list.get(id).getInsideY() < getHeight() * .1f) {
				scrollV.appendValue(textSize);
			}
		}

		private void goDownToEditing() {
			int id = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isEditing()) {
					if (i == list.size() - 1) {
						return;
					}
					id = i + 1;
					list.get(i).setEditing(false);
				}
			}

			list.get(id).setEditing(true);

			if (totalHeight > getHeight() && list.get(id).getInsideY() > getHeight() * .9f) {
				scrollV.appendValue(-textSize);
			}
		}

		private void insert(String partOfText) {
			int id = 0;

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isEditing()) {
					id = i;
				}
			}

			list.add(id + 1, new Item(list.get(id).getShiftY(), partOfText));

			for (int i = id + 1; i < list.size(); i++) {
				list.get(i).formDown();
			}

			list.get(id).setEditing(false);
			list.get(id + 1).setEditing(true);

			totalHeight += textSize;

			if (items.getTotalHeight() > getHeight()) {
				float tempValueOfScrollV = scrollV.getValue();
				scrollV.setMinMaxValue(getHeight() - totalHeight, 0);
				scrollV.setValue(tempValueOfScrollV);
			}

			if (cursor.getPosY() > getHeight() * .8f) {
				scrollV.appendValue(-textSize);
			}
		}

		private Item get(int index) {
			return list.get(constrain(index, 0, list.size() - 1));
		}

		private Item getFirst() {
			return list.get(0);
		}

		private Item getLast() {
			return list.get(list.size() - 1);
		}

		private final class Item {
			private final StringBuilder sb;
			private final Event event;
			private final SpatialView listeningView;
			private float shiftY, textWidth;
			private int firstSelectedColumn, lastSelectedColumn;
			private boolean isEditing, isFullSelected, isPartSelected;

			private Item(final float shiftY, String text) {
				sb = new StringBuilder(text);
				this.shiftY = shiftY;

				listeningView = new SpatialView() {
					@Override
					protected void render() {
					}
				};

				event = new Event(listeningView);

			}

			private void draw(final PGraphics pg) {
				listeningView.setBounds(EditTextOld.this.getX(), getItemY(), EditTextOld.this.getWidth(), getItemHeight());

				if (!Objects.isNull(pg)) {
					textOnDraw(pg);
					selectedTextAreaOnDraw(pg);
				}

				if (isCursorDraggable()) {
					isEditing = true;
					cursorStateUpdate();
				}

				if (ctx.mousePressed && event.isLeave()) {
					isEditing = false;
				}

			}

			private boolean isCursorDraggable() {
				return event.isPressed() && !scrollH.isDragging() && !scrollV.isDragging();
			}

			private void cursorStateUpdate() {
				final float localMouseX = ctx.mouseX - getX() + scrollH.getValue();

				for (int i = 0; i < sb.length(); i++) {
					if (localMouseX > getTextWidth(cursor.getCurrentColumn())
							+ getCharWidth(cursor.getCurrentColumn()) / 2) {
						cursor.justNext();
					} else {
						cursor.justBack();
					}
				}

				if (localMouseX > getTextWidth(cursor.getCurrentColumn())
						+ getCharWidth(cursor.getCurrentColumn()) / 2) {
					cursor.justNext();
				}

				cursor.resetTimer();

				scrollsValuesUpdate();
			}

			private void textOnDraw(PGraphics pg) {
				if (ctx.keyPressed) {
					textWidth = pg.textWidth(sb.toString());
				}
				pg.text(sb.toString(), -scrollH.getValue(), getInsideY() + textSize / 2);
			}

			private void selectedTextAreaOnDraw(PGraphics pg) {
				if (!isSelecting()) {
					return;
				}
				textWidth = pg.textWidth(sb.toString());

				pg.pushStyle();
				pg.noStroke();
				selectColor.apply(pg);
				if (isFullSelected) {
					pg.rect(-scrollH.getValue(), getInsideY(), getTextWidth(), textSize);
				}
				if (isPartSelected) {
					pg.rect(-scrollH.getValue() + getTextWidth(min(firstSelectedColumn, lastSelectedColumn)),
							getInsideY(), getTextWidth(firstSelectedColumn, lastSelectedColumn), textSize);
				}
				pg.popStyle();

			}

			private void setFullSelectEnabled(boolean isFullSelected) {
				this.isFullSelected = isFullSelected;

				if (isFullSelected) {
					firstSelectedColumn = 0;
					lastSelectedColumn = sb.length();
				}

				isPartSelected = false;
			}

			private void unselect() {
				isFullSelected = isPartSelected = false;
				firstSelectedColumn = lastSelectedColumn = 0;
			}

			private void setPartSelected(int firstRow, int lastRow) {
				isPartSelected = true;
				isFullSelected = false;
				firstSelectedColumn = firstRow;
				lastSelectedColumn = lastRow;
			}

			private void setRightSideSelected() {
				isPartSelected = true;
				isFullSelected = false;
				lastSelectedColumn = sb.length();
			}

			private void setRightSideSelected(int index) {
				isPartSelected = true;
				isFullSelected = false;
				firstSelectedColumn = index;
				lastSelectedColumn = sb.length();

			}

			private void setLeftSideSelected(int endRow) {
				isPartSelected = true;
				isFullSelected = false;
				firstSelectedColumn = 0;
				this.lastSelectedColumn = endRow;
			}

			private String getSelectedText() {
				return sb.toString().substring(min(firstSelectedColumn, lastSelectedColumn),
						max(firstSelectedColumn, lastSelectedColumn));
			}

			private void removeSelectedText() {
				if (sb.length() > 0) {
					sb.delete(min(firstSelectedColumn, lastSelectedColumn),
							max(firstSelectedColumn, lastSelectedColumn));
				}
				unselect();
			}

			private String getText() {
				return sb.toString();
			}

			private StringBuilder getStringBuilder() {
				return sb;
			}

			private void setEditing(boolean e) {
				isEditing = e;
			}

			public boolean isEditing() {
				return isEditing;
			}

			private float getItemY() {
				return shiftY + scrollV.getValue() + getY();
			}

			private float getInsideY() {
				return shiftY + scrollV.getValue();
			}

			private float getItemHeight() {
				return textSize;
			}

			private void setShiftY(float shiftY) {
				this.shiftY = shiftY;
			}

			private float getShiftY() {
				return shiftY;
			}

			private void formDown() {
				shiftY += textSize;
			}

			private void formUp() {
				shiftY -= textSize;
			}

			private float getTextWidth() {
				return textWidth;
			}

			private float getTextWidth(int indexEnd) {
				return getTextWidth(0, indexEnd);
			}

			private float getTextWidth(int indexStart, int indexEnd) {
				float width = 0;

				if (indexStart < 0 || indexStart > sb.length()) {
					return width;
				}
				if (indexEnd < 0 || indexEnd > sb.length()) {
					return width;
				}

				width = graphics
						.textWidth(sb.toString().substring(min(indexStart, indexEnd), max(indexStart, indexEnd)));

				return width;
			}

			private float getCharWidth(int indexEnd) {
				float width = 0;

				if (indexEnd > sb.length()) {
					return width;
				}

				width = graphics.textWidth(sb.toString().substring(max(0, indexEnd - 1), indexEnd));

				return width;
			}

			private int getCharsCount() {
				return sb.length();
			}

			private void deleteChar() {
				if (sb.toString().isEmpty()) {
					return;
				}
				if (cursor.getCurrentColumn() - 1 >= 0) {
					sb.delete(cursor.getCurrentColumn() - 1, cursor.getCurrentColumn());
				}
			}

			private boolean isEmpty() {
				return sb.toString().isEmpty();
			}

			private void insert(String txt) {
				if (isEmpty()) {
					sb.append(txt);
				} else {
					sb.insert(cursor.getCurrentColumn(), txt);
				}

			}

			private void append(String txt) {
				sb.append(txt);
			}

			private boolean isSelecting() {
				return firstSelectedColumn != lastSelectedColumn;
			}
		}

	}

	private final class Cursor {
		private static final int MAX_DURATION = 60;
		private final AbstractColor color;
		private float posX, posY;
		private int column, row, duration;

		private Cursor() {
			color = getTheme().getCursorColor();
		}

		private void draw(final PGraphics pg) {
			if (!isFocused) {
				return;
			}

			posX = -scrollH.getValue()
					+ pg.textWidth(items.list.get(getCurrentRow()).sb.toString().substring(0, getCurrentColumn()));
			posY = items.list.get(getCurrentRow()).getInsideY();

			if (duration < MAX_DURATION) {
				duration++;
			} else {
				duration = 0;
			}

			if (duration < MAX_DURATION / 2) {
				pg.pushStyle();
				color.applyStroke(pg);
				pg.strokeWeight(2);
				pg.line(posX, posY, posX, posY + items.getTextSize());
				pg.popStyle();
			}

		}

		private float getPosX() {
			return posX;
		}

		private float getPosY() {
			return posY;
		}

		private int getCurrentColumn() {
			return constrain(column, 0, getMaxCharsInRow());
		}

		private void setCurrentColumn(int column) {
			this.column = column;
		}

		private int getCurrentRow() {
			return constrain(row, 0, items.count() - 1);
		}

		private void setCurrentRow(int row) {
			this.row = constrain(row, 0, items.count() - 1);
		}

		private int getRows() {
			return items.list.size();
		}

		private void back() {
			if (getCurrentColumn() > 0) {
				column--;
			}
			if (posX < getWidth() / 2) {
				scrollH.appendValue(-items.getTextSize() / 2);
			}
		}

		private void next() {
			if (getCurrentColumn() < getMaxCharsInRow()) {
				column++;
			}
			if (posX > getWidth() * .8f) {
				scrollH.appendValue(items.getTextSize());
			}
		}

		private void justNext() {
			if (getCurrentColumn() < getMaxCharsInRow()) {
				column++;
			}
		}

		private void justBack() {
			if (getCurrentColumn() > 0) {
				column--;
			}
		}

		private void goInStart() {
			column = 0;
		}

		private void goInEnd() {
			column = getMaxCharsInRow();
		}

		private void resetTimer() {
			duration = 0;
		}

		private int getMaxCharsInRow() {
			return getCurrentItem().getCharsCount();
		}

		private String getTextOfRightSide() {
			final Items.Item item = getCurrentItem();
			String txt = item.getText().substring(getCurrentColumn(), item.getText().length());
			item.getStringBuilder().delete(getCurrentColumn(), item.getText().length());

			return txt;
		}

	}

	private final class Selection {
		private int startColumn, endColumn, startRow, endRow;
		private boolean isSelecting, selectingWasStoped, isSelectedAllText;

		private Selection() {
			startColumn = endColumn = startRow = endRow = -1;
		}

		private void update() {

			if (isAllowedStateToStartSelecting()) {

				if (selectingWasStoped) {
					unselect();
					selectingWasStoped = false;
				}

				if (startRow == -1) {
					startRow = cursor.getCurrentRow();
				}
				endRow = cursor.getCurrentRow();
				if (startColumn == -1) {
					startColumn = cursor.getCurrentColumn();
				}
				endColumn = cursor.getCurrentColumn();

				if (items.getTotalHeight() > EditTextOld.this.getHeight()) {

					if (cursor.getPosY() < getHeight() * .2f) {
						scrollV.appendValue(getHeight() * .02f);
					}

					if (cursor.getPosY() > getHeight() * .8f) {
						scrollV.appendValue(-getHeight() * .02f);
					}
				}

				if (cursor.getPosX() < getWidth() * .2f) {
					scrollH.appendValue(-getWidth() * .02f);
				}

				if (cursor.getPosX() > getWidth() * .8f) {
					scrollH.appendValue(getWidth() * .02f);
				}

			} else {
				if (!selectingWasStoped) {
					selectingWasStoped = true;
				}
			}

			isSelecting = (startRow != endRow) || (startColumn != endColumn);

			if (isSelecting) {

				for (int i = 0; i < items.count(); i++) {

					if (i == startRow) {
						if (isMultiLinesSelected()) {
							if (isDown()) {
								items.get(startRow).setRightSideSelected();
							}

							if (isUp()) {
								items.get(endRow).setRightSideSelected(endColumn);
							}

						} else {
							items.get(i).setPartSelected(startColumn, endColumn);
						}
					}

					if (i == endRow && isMultiLinesSelected()) {
						if (isDown()) {
							items.get(i).setLeftSideSelected(endColumn);
						}
						if (isUp()) {
							items.get(startRow).setLeftSideSelected(startColumn);
						}
					}

					if (i > min(startRow, endRow) && i < max(startRow, endRow)) {
						items.get(i).setFullSelectEnabled(true);
					} else {
						if (i < min(startRow, endRow) || i > max(startRow, endRow)) {
							items.get(i).setFullSelectEnabled(false);
						}
					}

				}

			}

		}

		private String getText() {
			final StringBuilder sb = new StringBuilder();

			for (Items.Item item : items.list) {
				if (!item.getSelectedText().isEmpty()) {
					sb.append(isMultiLinesSelected() || getCurrentItem().isFullSelected ? item.getSelectedText() + '\n'
							: item.getSelectedText());
				}
			}

			return sb.toString();
		}

		private boolean isAllowedStateToStartSelecting() {
			return isDragging();
		}

		private boolean isSelecting() {
			return isSelecting;
		}

		private void unselect() {
			startRow = endRow = startColumn = endColumn = -1;

			items.list.forEach(item -> item.unselect());

			isSelecting = false;
			selectingWasStoped = true;
			setSelectedAllText(false);
		}

		private boolean isSelectedAllText() {
			return isSelectedAllText;
		}

		private int getFirstRow() {
			return min(startRow, endRow);
		}

		private int getLastRow() {
			return max(startRow, endRow);
		}

		private void setSelectedAllText(boolean isSelectedAllText) {
			this.isSelectedAllText = isSelectedAllText;
			if (isSelectedAllText) {
				isSelecting = true;
			}
		}

		private boolean isMultiLinesSelected() {
			return startRow != endRow;
		}

		private boolean isUp() {
			return endRow < startRow;
		}

		private boolean isDown() {
			return endRow > startRow;
		}

		private void setSelectingState(boolean isSelecting) {
			this.isSelecting = isSelecting;
		}

		private boolean isSelectedToRight() {
			return startColumn < endColumn;
		}

	}

}