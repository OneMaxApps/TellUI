package microui.component;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.util.MathUtils.constrain;
import static processing.core.PConstants.BACKSPACE;
import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.ENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.TOP;
import static processing.core.PConstants.UP;

import java.util.HashMap;
import java.util.Map;

import microui.constants.Direction;
import microui.core.BufferedView;
import microui.core.GraphicsBuffer;
import microui.core.TextEditorModel;
import microui.core.base.Component;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.event.Listener;
import microui.util.Clipboard;
import microui.util.MathUtils;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public final class TextArea extends Component implements KeyPressable, Scrollable {
	private static final int MIN_SIZE = 100;
	private static final int MAX_SIZE = 1000;

	private final TextEditorModel textEditorModel;
	private final TextMetricsPool textMetricsPool;
	private final TextStyle textStyle;
	private final GraphicsBuffer graphics;
	private final CursorRenderer cursorRenderer;
	private final SelectionRenderer selectionRenderer;
	private final ScrollManager scrollManager;
	private final KeyController keyController;
	private final HandleDraggingConfig handleDraggingConfig;
	private boolean focused;

	public TextArea(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(MIN_SIZE, MAX_SIZE);
		setBackgroundColor(getTheme().getEditableBackgroundColor());

		textEditorModel = new TextEditorModel();
		textMetricsPool = new TextMetricsPool(this);
		textStyle = new TextStyle(this);
		graphics = new GraphicsBuffer();
		graphics.setAutoClearBufferEnabled(true);
		graphics.addBufferedView(new TextRenderer(this));
		graphics.addBufferedView(cursorRenderer = new CursorRenderer(this));
		graphics.addBufferedView(selectionRenderer = new SelectionRenderer(this));
		scrollManager = new ScrollManager(this);
		keyController = new KeyController(this);
		handleDraggingConfig = new HandleDraggingConfig();

		textEditorModel.setOnTextChangedListener(() -> {
			textMetricsPool.clearCache();
			scrollManager.recalculateRanges();
		});

		textStyle.setOnStyleChangedListener(() -> {
			textMetricsPool.clearCache();
			scrollManager.recalculateRanges();
		});

		onPress(() -> setFocused(true));

		onDragging(this::hookOnDragging);
		onPress(this::setCursorPositionMappedFromMouse);

		onPress(() -> {
			textEditorModel.resetSelection();
			textEditorModel.setSelectStart(textEditorModel.getCursorRow(), textEditorModel.getCursorColumn());
			textEditorModel.setSelectEnd(textEditorModel.getCursorRow(), textEditorModel.getCursorColumn());
		});

		onDragging(() -> {
			final TextEditorModel m = textEditorModel;
			final int cr = m.getCursorRow();
			final int cc = m.getCursorColumn();
			
			m.setSelectEnd(cr, cc);
		});

		onDoubleClick(this::selectWordUnderCursor);
		
	}

	public TextArea() {
		this(0, 0, 1, 1);
	}
	
	public float getHandleDraggingSpeed() {
		return handleDraggingConfig.getDraggingSpeed();
	}

	public void setHandleDraggingSpeed(float draggingSpeed) {
		handleDraggingConfig.setDraggingSpeed(draggingSpeed);
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public void setText(String... strings) {
		textEditorModel.setText(strings);
	}

	public AbstractColor getTextColor() {
		return textStyle.getTextColor();
	}

	public void setTextColor(AbstractColor textColor) {
		textStyle.setTextColor(textColor);
	}

	public PFont getFont() {
		return textStyle.getFont();
	}

	public void setFont(PFont font) {
		textStyle.setFont(font);
	}

	public int getTextSize() {
		return textStyle.getTextSize();
	}

	public void setTextSize(int textSize) {
		textStyle.setTextSize(textSize);
	}

	public int getLinesCount() {
		return textEditorModel.getLinesCount();
	}

	public AbstractColor getCursorColor() {
		return cursorRenderer.getColor();
	}

	public void setCursorColor(AbstractColor color) {
		cursorRenderer.setColor(color);
	}

	public float getCursorWeight() {
		return cursorRenderer.getWeight();
	}

	public void setCursorWeight(float weight) {
		cursorRenderer.setWeight(weight);
	}

	public AbstractColor getSelectionColor() {
		return selectionRenderer.getColor();
	}

	public void setSelectionColor(AbstractColor color) {
		selectionRenderer.setColor(color);
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (!isFocused()) {
			return;
		}

		scrollManager.mouseWheel(mouseEvent);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isFocused()) {
			return;
		}

		keyController.keyInput(e);
	}

	@Override
	protected void render() {
		backgroundOnDraw();
		graphics.draw();
		scrollManager.onDraw();

		if (mustLoseFocus()) {
			setFocused(false);
		}

	}

	@Override
	protected void onChangeBounds() {
		textMetricsPool.clearCache();
		scrollManager.recalculateRanges();
		scrollManager.recalculateBounds();

		graphics.setBoundsFrom(this);
	}

	private void selectWordUnderCursor() {
		final int row = textEditorModel.getCursorRow();
		final int column = textEditorModel.getCursorColumn();

		final String word = textEditorModel.getLineText(row);

		int startOfWord = column;
		int endOfWord = column;

		final int clampedColumnFromCursor = (int) MathUtils.constrain(column, 0, word.length() - 1);

		if (word.charAt(clampedColumnFromCursor) == ' ') {
			textEditorModel.setSelect(row, row, 0, word.length());
			return;
		}

		while (startOfWord > 0) {
			final int clampedIndex = (int) MathUtils.constrain(startOfWord, 0, word.length() - 1);

			if (word.charAt(clampedIndex) == ' ') {
				startOfWord++;
				break;
			} else {
				startOfWord--;
			}

		}

		while (endOfWord < word.length()) {
			final int clampedIndex = (int) MathUtils.constrain(endOfWord, 0, word.length() - 1);

			if (word.charAt(clampedIndex) == ' ') {
				break;
			}

			endOfWord++;
		}

		textEditorModel.setSelect(row, row, startOfWord, endOfWord);
	}

	private void setCursorPositionMappedFromMouse() {
		final TextEditorModel m = textEditorModel;
		
		final float scrollH = scrollManager.getHorizontalScrollValue();
		final float scrollV = scrollManager.getVerticalScrollValue();
		
		final float mouseX = (ctx.mouseX + scrollH) - getX();
		final float mouseY = (ctx.mouseY - scrollV) - getY();
		
		final int nearlyRow = (int) MathUtils.convert(mouseY, 0, getTextSize() * m.getLinesCount(), 0, m.getLinesCount());
		
		int nearlyColumn = 0;

		for (int i = 0; i <= m.getLineLength(nearlyRow); i++) {
			if (mouseX > textMetricsPool.getTextWidth(nearlyRow, 0, i)) {
				nearlyColumn = i;
			}
		}
		
		if (m.getCursorRow() == nearlyRow && m.getCursorColumn() == nearlyColumn) {
			return;
		}

		m.setCursorColumn(nearlyColumn);
		m.setCursorRow(nearlyRow);
	}

	private void hookOnDragging() {
		if (scrollManager.isScrolling()) {
			return;
		}

		setCursorPositionMappedFromMouse();
		findCursorSoftly();
	}
	
	private void findCursorSoftly() {
		final Scroll sH = scrollManager.scrollH;
		final Scroll sV = scrollManager.scrollV;
		
		final float cx = cursorRenderer.getX();
		final float cy = cursorRenderer.getY();
		
		final float draggingSpeed = handleDraggingConfig.getDraggingSpeed();
		
		if (cx < getWidth() * .2f) {
			final float speed = MathUtils.convert(cx, 0, getWidth() * .2f, -draggingSpeed, 0);
			sH.appendValue(speed);
		}
		
		if (cx > getWidth() * .8f) {
			final float speed = MathUtils.convert(cx, getWidth() * .8f, getWidth(), 0, draggingSpeed);
			sH.appendValue(speed);
		}
		
		if (cy < getHeight() * .2f) {
			final float speed = MathUtils.convert(cy, 0, getHeight() * .2f, 0, draggingSpeed);
			sV.appendValue(speed);
		}
		
		if (cy > getHeight() * .8f) {
			final float speed = MathUtils.convert(cy, getHeight() * .8f, getHeight(), 0, -draggingSpeed);
			sV.appendValue(speed);
		}
		
	}

	private void findCursor() {
		final TextEditorModel m = textEditorModel;
		final TextMetricsPool tmp = textMetricsPool;

		final float currentLineTextWidthUntilCursor = tmp.getTextWidth(m.getCursorRow(), 0, m.getCursorColumn());

		scrollManager.scrollH.setValue(currentLineTextWidthUntilCursor - getWidth() / 2);

		scrollManager.scrollV.setValue(-(m.getCursorRow() * getTextSize() - getHeight() / 2));
	}

	private boolean mustLoseFocus() {
		return !isDragging() && ctx.mousePressed && !isHover();
	}

	private void backgroundOnDraw() {
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
	}

	private static final class ScrollManager {
		private static final int WEIGHT = 10;
		private final TextArea textArea;
		private final Scroll scrollH, scrollV;
		private boolean scrolling;

		public ScrollManager(TextArea textArea) {
			super();
			this.textArea = requireNonNull(textArea, "textArea");

			scrollH = new Scroll();
			scrollV = new Scroll();

			prepareStyle();
			recalculateBounds();
		}

		public void onDraw() {
			if (textArea.isFocused() && textArea.textEditorModel.isSelectEmpty()) {
				scrollH.draw();
				scrollV.draw();

				scrolling = scrollH.isDragging() || scrollV.isDragging();
			}
		}
		
		

		public boolean isScrolling() {
			return scrolling;
		}

		public float getHorizontalScrollValue() {
			return scrollH.getValue();
		}

		public float getVerticalScrollValue() {
			return scrollV.getValue();
		}

		public void recalculateBounds() {
			final TextArea t = textArea;

			scrollH.setSize(t.getWidth() - WEIGHT, WEIGHT);
			scrollH.setPosition(t.getX(), t.getY() + t.getHeight() - WEIGHT);

			scrollV.setSize(WEIGHT, t.getHeight() - WEIGHT);
			scrollV.setPosition(t.getX() + t.getWidth() - WEIGHT, t.getY());

		}

		public void recalculateRanges() {
			final float calculatedScrollHMax = textArea.textMetricsPool.getMaxWidth() - textArea.getWidth() * .8f;
			scrollH.setMaxValue(Math.max(scrollH.getMinValue(), calculatedScrollHMax));

			scrollH.setVisible(calculatedScrollHMax > textArea.getWidth());

			final float calculatedScrollVMin = textArea.getHeight() - textArea.textMetricsPool.getTotalHeight();
			scrollV.setMinValue(Math.min(scrollV.getMaxValue(), calculatedScrollVMin));

			scrollV.setVisible(calculatedScrollVMin < scrollV.getMaxValue());

		}

		public void mouseWheel(MouseEvent mouseEvent) {
			scrollH.mouseWheel(mouseEvent);
			scrollV.mouseWheel(mouseEvent);

			if (textArea.isHover() && !scrollH.isHover()) {
				final float speed = -mouseEvent.getCount() * textArea.textStyle.getTextSize();
				scrollV.appendValue(speed);
			}
		}

		private void prepareStyle() {
			scrollH.setConstrainDimensionsEnabled(false);
			scrollH.setValue(0);
			scrollH.setBackgroundColor(Color.TRANSPARENT);
			scrollH.setStrokeColor(Color.TRANSPARENT);
			scrollH.setThumbColor(new Color(12, 64));

			scrollV.setConstrainDimensionsEnabled(false);
			scrollV.setBackgroundColor(Color.TRANSPARENT);
			scrollV.setStrokeColor(Color.TRANSPARENT);
			scrollV.setThumbColor(new Color(12, 64));

			scrollV.setValue(0);
			scrollV.setMaxValue(0);
			scrollV.swapOrientation();
		}

	}

	private static final class TextMetricsPool {
		private static final PGraphics GRAPHICS = ctx.createGraphics(1, 1, ctx.sketchRenderer());
		private final TextArea textArea;
		private final Map<Key, Float> map = new HashMap<Key, Float>();
		private float maxWidth, totalHeight;

		public TextMetricsPool(TextArea textArea) {
			super();
			this.textArea = requireNonNull(textArea, "textArea");
			maxWidth = totalHeight = -1;
		}

		public float getTextWidth(int row, int columnStart, int columnEnd) {
			final Key key = new Key(row, columnStart, columnEnd);

			if (map.containsKey(key)) {
				return map.get(key);
			} else {
				final float newValue = getCalculatedTextWidth(row, columnStart, columnEnd);
				map.put(key, newValue);
				return newValue;
			}

		}

		public float getTextWidth(int row) {
			return getTextWidth(row, 0, textArea.textEditorModel.getLineLength(row));
		}

		public float getMaxWidth() {
			if (maxWidth == -1) {
				final int linesCount = textArea.getLinesCount();

				for (int i = 0; i < linesCount; i++) {
					maxWidth = Math.max(maxWidth, getCalculatedTextWidth(i));
				}

			}

			return maxWidth;
		}

		public float getTotalHeight() {
			if (totalHeight == -1) {
				totalHeight = textArea.getLinesCount() * textArea.getTextSize();
			}

			return totalHeight;
		}

		public void clearCache() {
			map.clear();
			maxWidth = totalHeight = -1;
		}

		private float getCalculatedTextWidth(int row, int startColumn, int endColumn) {
			final TextEditorModel model = textArea.textEditorModel;
			final String text = model.getLineText(row);
			final int textLength = model.getLineLength(row);

			startColumn = (int) constrain(startColumn, 0, textLength);
			endColumn = (int) constrain(endColumn, 0, textLength);

			final int effectiveStartColumn = min(startColumn, endColumn);
			final int effectiveEndColumn = max(startColumn, endColumn);

			float textWidth = 0;

			GRAPHICS.beginDraw();
			GRAPHICS.pushStyle();
			textArea.textStyle.apply(GRAPHICS);
			textWidth = GRAPHICS.textWidth(text.substring(effectiveStartColumn, effectiveEndColumn));
			GRAPHICS.popStyle();
			GRAPHICS.endDraw();

			return textWidth;
		}

		public float getCalculatedTextWidth(int row) {
			return getCalculatedTextWidth(row, 0, textArea.textEditorModel.getLineLength(row));
		}

		private static final record Key(int row, int startColumn, int endColumn) {
		}
	}

	private static final class TextRenderer extends BufferedView {
		private final TextArea textArea;

		public TextRenderer(TextArea textArea) {
			super();
			setVisible(true);

			this.textArea = requireNonNull(textArea, "textArea");
		}

		@Override
		protected void render(PGraphics p) {
			final int linesCount = textArea.textEditorModel.getLinesCount();

			final float textSize = textArea.textStyle.getTextSize();

			final float scrollH = textArea.scrollManager.scrollH.getValue();
			final float scrollV = textArea.scrollManager.scrollV.getValue();

			p.pushStyle();
			textArea.textStyle.apply(p);

			for (int i = 0; i < linesCount; i++) {
				final float posX = 0 - scrollH;
				final float posY = 0 + textSize * i + scrollV;
				final String text = textArea.textEditorModel.getLineText(i);
				if (mustBeRendered(posY)) {
					p.text(text, posX, posY);
				}
			}

			p.popStyle();
		}

		private boolean mustBeRendered(float positionY) {
			final float textSize = textArea.getTextSize();

			return positionY > -textSize && positionY < textArea.getHeight();
		}
	}

	private static final class CursorRenderer extends BufferedView {
		private static final byte MIN_WEIGHT = 1;
		private static final byte DEFAULT_WEIGHT = 2;
		private final TextArea textArea;
		private final BlinkTimer blinkTimer;
		private AbstractColor color;
		private float weight;

		public CursorRenderer(TextArea textArea) {
			super();
			setVisible(true);

			this.textArea = requireNonNull(textArea, "textArea");
			blinkTimer = new BlinkTimer();

			setColor(Color.BLACK);
			setWeight(DEFAULT_WEIGHT);
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
			if (weight < MIN_WEIGHT) {
				throw new IllegalArgumentException("Cursor weight cannot be less than " + MIN_WEIGHT);
			}
			this.weight = weight;
		}

		@Override
		protected void render(PGraphics pGraphics) {
			if (!textArea.isFocused()) {
				return;
			}

			if (blinkTimer.isMustBlink()) {
				return;
			}

			color.applyStroke(pGraphics);
			pGraphics.strokeWeight(weight);

			final float posX = getX();
			final float posY = getY();
			final float height = textArea.getTextSize();

			pGraphics.line(posX, posY, posX, posY + height);
		}

		private float getX() {
			final TextEditorModel model = textArea.textEditorModel;

			final int cursorRow = model.getCursorRow();
			final int cursorColumn = model.getCursorColumn();

			final TextMetricsPool tmp = textArea.textMetricsPool;

			final float scrollH = textArea.scrollManager.getHorizontalScrollValue();

			final float posX = tmp.getTextWidth(cursorRow, 0, cursorColumn) - scrollH;

			return posX;
		}

		private float getY() {
			final TextEditorModel model = textArea.textEditorModel;

			final int cursorRow = model.getCursorRow();

			final float scrollV = textArea.scrollManager.getVerticalScrollValue();

			final float posY = cursorRow * textArea.getTextSize() + scrollV;

			return posY;
		}

		private final class BlinkTimer {
			private static short ONE_SECOND_IN_MS = 1000;
			private static short HALF_SECOND_IN_MS = 500;
			private long lastBlinkTime, delta;

			public BlinkTimer() {
				super();
				lastBlinkTime = System.currentTimeMillis();
			}

			public boolean isMustBlink() {
				final long now = System.currentTimeMillis();

				delta = now - lastBlinkTime;

				if (delta > ONE_SECOND_IN_MS) {
					lastBlinkTime = now;
				}

				if (textArea.isPressed() || ctx.keyPressed) {
					delta = 0;
					lastBlinkTime = now;
				}

				if (delta > HALF_SECOND_IN_MS && !textArea.isPressed()) {
					return true;
				}

				return false;
			}
		}
	}

	private static final class SelectionRenderer extends BufferedView {
		private final TextArea textArea;
		private AbstractColor color;

		public SelectionRenderer(TextArea textArea) {
			super();
			setVisible(true);

			this.textArea = requireNonNull(textArea, "textArea");

			setColor(getTheme().getSelectColor());

		}

		public AbstractColor getColor() {
			return color;
		}

		public void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		@Override
		protected void render(PGraphics pGraphics) {
			final TextEditorModel m = textArea.textEditorModel;
			final TextMetricsPool tmp = textArea.textMetricsPool;

			if (m.isSelectEmpty()) {
				return;
			}

			pGraphics.pushStyle();
			pGraphics.noStroke();
			color.apply(pGraphics);

			final int esr = m.getSelectEffectiveStartRow();
			final int eer = m.getSelectEffectiveEndRow();
			final int esc = m.getSelectEffectiveStartColumn();
			final int eec = m.getSelectEffectiveEndColumn();

			float posX = 0;
			float posY = 0;
			float width = 0;
			final float height = textArea.getTextSize();

			final float scH = textArea.scrollManager.getHorizontalScrollValue();
			final float scV = textArea.scrollManager.getVerticalScrollValue();

			final float textSize = textArea.getTextSize();

			if (m.isMultiLineSelected()) {
				for (int i = esr; i <= eer; i++) {
					if (i == esr) {

						posY = textSize * esr;

						posX = tmp.getTextWidth(esr, 0, esc);
						width = tmp.getTextWidth(esr, esc, m.getLineLength(esr));
					}

					if (i != esr && i != eer) {
						posX = 0;
						posY = textSize * i;
						width = tmp.getTextWidth(i);
					}

					if (i == eer) {
						posY = textSize * eer;

						posX = 0;
						width = tmp.getTextWidth(eer, 0, eec);
					}

					posX -= scH;
					posY += scV;

					pGraphics.rect(posX, posY, width, height);
				}

			} else {

				posX = tmp.getTextWidth(esr, 0, min(esc, eec)) - scH;

				posY = scV + textSize * esr;
				width = tmp.getTextWidth(esr, esc, eec);

				pGraphics.rect(posX, posY, width, height);
			}

			pGraphics.popStyle();
		}

	}

	private static final class TextStyle {
		private static final int DEFAULT_TEXT_SIZE = 12;
		private static final int MIN_TEXT_SIZE = 4;
		private AbstractColor textColor;
		private PFont font;
		private Listener onTextStyleChangedListener;
		private int textSize;

		public TextStyle(TextArea textArea) {
			super();
			setTextColor(getTheme().getEditableTextColor());
			setTextSize(DEFAULT_TEXT_SIZE);
		}

		public void apply(PGraphics pGraphics) {
			requireNonNull(pGraphics, "pGraphics");
			textColor.apply(pGraphics);
			if (font == null) {
				pGraphics.textSize(textSize);
			} else {
				pGraphics.textFont(font, textSize);
			}
			pGraphics.textAlign(LEFT, TOP);
		}

		public AbstractColor getTextColor() {
			return textColor;
		}

		public void setTextColor(AbstractColor textColor) {
			this.textColor = requireNonNull(textColor, "textColor");
		}

		public PFont getFont() {
			return font;
		}

		public void setFont(PFont font) {
			if (this.font == font) {
				return;
			}

			this.font = requireNonNull(font, "font");

			onTextStyleChanged();
		}

		public int getTextSize() {
			return textSize;
		}

		public void setTextSize(int textSize) {
			if (textSize < MIN_TEXT_SIZE) {
				throw new IllegalArgumentException("Text size must be greater or equal: " + MIN_TEXT_SIZE);
			}

			if (this.textSize == textSize) {
				return;
			}

			this.textSize = textSize;

			onTextStyleChanged();
		}

		public void onTextStyleChanged() {
			if (onTextStyleChangedListener != null) {
				onTextStyleChangedListener.action();
			}
		}

		public void setOnStyleChangedListener(Listener onTextSizeChangedListener) {
			this.onTextStyleChangedListener = onTextSizeChangedListener;
		}

	}

	private static final class KeyController {
		private final TextArea textArea;

		public KeyController(TextArea textArea) {
			super();
			this.textArea = requireNonNull(textArea, "textArea");
		}

		public void keyInput(KeyEvent keyEvent) {

			if (keyEvent.isShiftDown()) {
				final TextEditorModel m = textArea.textEditorModel;
				
				if (m.isSelectEmpty()) {
					m.setSelectStart(m.getCursorRow(), m.getCursorColumn());
					m.setSelectEnd(m.getCursorRow(), m.getCursorColumn());
				}

				switch (keyEvent.getKeyCode()) {
				case LEFT:
					onShiftDownAndLeftPressed();
					break;

				case RIGHT:
					onShiftDownAndRightPressed();
					break;

				case UP:
					onShiftDownAndUpPressed();
					break;

				case DOWN:
					onShiftDownAndDownPressed();
					break;
					
				case VK_HOME:
					onShiftDownAndHomePressed();
					break;
					
				case VK_END:
					onShiftDownAndEndPressed();
					break;
					
				case VK_PAGE_UP:
					onShiftDownAndPageUpPressed();
					break;
					
				case VK_PAGE_DOWN:
					onShiftDownAndPageDownPressed();
					break;
				}

				return;
			}

			if (keyEvent.isControlDown()) {

				switch (keyEvent.getKeyCode()) {
				case VK_Z:
					textArea.textEditorModel.undo();
					break;

				case VK_Y:
					textArea.textEditorModel.redo();
					break;

				case VK_V:
					onControlDownAndVPressed();
					break;

				case VK_A:
					onControlDownAndAPressed();
					break;

				case VK_C:
					onControlDownAndCPressed();
					break;

				case VK_X:
					onControlDownAndXPressed();
					break;

				case VK_MINUS:
					onControlDownAndMinusPressed();
					break;

				case VK_EQUALS:
					onControlDownAndPlusPressed();
					break;
				}

				return;
			}

			switch (keyEvent.getKeyCode()) {
			case LEFT:
				onKeyLeftPressed();
				break;

			case RIGHT:
				onKeyRightPressed();
				break;

			case UP:
				onKeyUpPressed();
				break;

			case DOWN:
				onKeyDownPressed();
				break;

			case ENTER:
				onKeyEnterPressed();
				break;

			case BACKSPACE:
				onKeyBackspacePressed();
				break;

			case VK_PAGE_UP:
				onKeyPageUpPressed();
				break;

			case VK_PAGE_DOWN:
				onKeyPageDownPressed();
				break;

			case VK_HOME:
				onKeyHomePressed();
				break;

			case VK_END:
				onKeyEndPressed();
				break;

			case VK_DELETE:
				onKeyDeletePressed();
				break;

			default:
				onRegularKey();
				break;
			}

		}
		
		private void onKeyLeftPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			m.moveCursorTo(Direction.LEFT);

			textArea.findCursor();
		}

		private void onKeyRightPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			m.moveCursorTo(Direction.RIGHT);

			textArea.findCursor();
		}

		private void onKeyUpPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			m.moveCursorTo(Direction.UP);

			textArea.findCursor();
		}

		private void onKeyDownPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			m.moveCursorTo(Direction.DOWN);

			textArea.findCursor();
		}

		private void onKeyEnterPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.removeSelectedText();
			}

			m.splitLine();
			m.moveCursorTo(Direction.DOWN);
			m.moveCursorToStartOfLine();

			textArea.findCursor();
		}

		private void onKeyBackspacePressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.removeSelectedText();
				return;
			}

			if (m.getCursorColumn() == 0 && m.getCursorRow() == 0) {
				return;
			}

			if (m.getCursorColumn() == 0 && m.getCursorRow() != 0) {
				final int charCountOnDownLine = m.getLineLength(m.getCursorRow());

				m.moveCursorTo(Direction.UP);
				m.mergeLines();
				m.moveCursorToEndOfLine();
				m.moveCursorTo(Direction.LEFT, charCountOnDownLine);

				textArea.findCursor();

				return;
			}

			m.moveCursorTo(Direction.LEFT);
			m.removeChar();

			textArea.findCursor();

		}

		private void onKeyPageUpPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			textArea.textEditorModel.moveCursorToStart();
			textArea.findCursor();
		}

		private void onKeyPageDownPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			textArea.textEditorModel.moveCursorToEnd();
			textArea.findCursor();
		}

		private void onKeyHomePressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			textArea.textEditorModel.moveCursorToStartOfLine();
			textArea.findCursor();
		}

		private void onKeyEndPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.resetSelection();
			}

			textArea.textEditorModel.moveCursorToEndOfLine();
			textArea.findCursor();
		}

		private void onKeyDeletePressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.removeSelectedText();
				return;
			}

			if (m.getLineLength(m.getCursorRow()) > m.getCursorColumn()) {
				m.removeChar();
			}
		}

		private void onRegularKey() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.removeSelectedText();
			}

			textArea.textEditorModel.insertChar(ctx.key);
			textArea.textEditorModel.moveCursorTo(Direction.RIGHT);
			textArea.findCursor();
		}

		private void onControlDownAndVPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			if (!m.isSelectEmpty()) {
				m.removeSelectedText();
			}

			final String[] lines = Clipboard.getAsArray();
			final boolean multiLine = lines.length > 1;

			if (multiLine) {
				for (int i = lines.length - 1; i >= 0; i--) {
					m.insertString(lines[i]);
					if (i != 0) {
						m.splitLine();
					}
				}
				m.moveCursorTo(Direction.DOWN, lines.length - 1);

				m.moveCursorTo(Direction.RIGHT, lines[lines.length - 1].length());
			} else {
				m.insertString(lines[0]);
				m.moveCursorTo(Direction.RIGHT, lines[0].length());
			}

			textArea.findCursor();
		}

		private void onControlDownAndAPressed() {
			textArea.textEditorModel.selectAll();
		}

		private void onControlDownAndCPressed() {
			Clipboard.set(textArea.textEditorModel.getSelectedText());
		}

		private void onControlDownAndXPressed() {
			final String selectedText = textArea.textEditorModel.getSelectedText();
			Clipboard.set(selectedText);
			textArea.textEditorModel.removeSelectedText();
		}

		private void onControlDownAndMinusPressed() {
			textArea.setTextSize(Math.max(TextStyle.MIN_TEXT_SIZE, textArea.getTextSize() - 1));
			textArea.findCursor();
		}

		private void onControlDownAndPlusPressed() {
			textArea.setTextSize(textArea.getTextSize() + 1);
			textArea.findCursor();
		}

		private void onShiftDownAndLeftPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorTo(Direction.LEFT);
			m.setSelectEndColumn(m.getCursorColumn());
			textArea.findCursor();
		}

		private void onShiftDownAndRightPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorTo(Direction.RIGHT);
			m.setSelectEndColumn(m.getCursorColumn());
			textArea.findCursor();
		}

		private void onShiftDownAndUpPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorTo(Direction.UP);
			m.setSelectEndRow(m.getCursorRow());
			textArea.findCursor();
		}

		private void onShiftDownAndDownPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorTo(Direction.DOWN);
			m.setSelectEndRow(m.getCursorRow());
			textArea.findCursor();
		}
		
		private void onShiftDownAndHomePressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorToStartOfLine();
			m.setSelectEndColumn(m.getCursorColumn());
			textArea.findCursor();
		}
		
		private void onShiftDownAndEndPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorToEndOfLine();
			m.setSelectEndColumn(m.getCursorColumn());
			textArea.findCursor();
		}
		
		private void onShiftDownAndPageUpPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorToStart();
			m.setSelectEnd(0,0);
			textArea.findCursor();
		}
		
		private void onShiftDownAndPageDownPressed() {
			final TextEditorModel m = textArea.textEditorModel;

			m.moveCursorToEnd();
			m.setSelectEnd(m.getCursorRow(),m.getCursorColumn());
			textArea.findCursor();
		}
	}

	private static final class HandleDraggingConfig {
		private static final int DEFAULT_DRAGGING_SPEED = 10;
		private static final int MIN_DRAGGING_SPEED = 1;
		private float draggingSpeed;
		
		public HandleDraggingConfig() {
			super();
			setDraggingSpeed(DEFAULT_DRAGGING_SPEED);
		}

		public float getDraggingSpeed() {
			return draggingSpeed;
		}

		public void setDraggingSpeed(float draggingSpeed) {
			if (draggingSpeed < MIN_DRAGGING_SPEED) {
				throw new IllegalArgumentException("Speed for dragging must be greater than: " + MIN_DRAGGING_SPEED);
			}
			this.draggingSpeed = draggingSpeed;
		}
	}
}