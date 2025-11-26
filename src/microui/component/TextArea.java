package microui.component;

import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import java.util.HashMap;
import java.util.Map;

import microui.core.BufferedView;
import microui.core.GraphicsBuffer;
import microui.core.TextEditorModel;
import microui.core.base.Component;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.event.Listener;
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
	private final ScrollManager scrollManager;
	
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
		scrollManager = new ScrollManager(this);
		
		textEditorModel.setOnTextChangedListener(() -> {
			textMetricsPool.clearCache();
			scrollManager.recalculateRanges();
		});
		
		textStyle.setOnStyleChangedListener(() -> {
			textMetricsPool.clearCache();
			scrollManager.recalculateRanges();
		});
	}

	public TextArea() {
		this(0, 0, 1, 1);
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

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		scrollManager.mouseWheel(mouseEvent);
	}

	@Override
	public void keyPressed(KeyEvent e) {
//		 TODO Auto-generated method stub

	}

	@Override
	protected void render() {
		backgroundOnDraw();
		graphics.draw();
		scrollManager.onDraw();
	}
	
	@Override
	protected void onChangeBounds() {
		scrollManager.recalculateBounds();
		
		graphics.setBoundsFrom(this);
	}

	private void backgroundOnDraw() {
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
	}
	
	private static final class ScrollManager {
		private final TextArea textArea;
		private static final int WEIGHT = 10;
		private final Scroll scrollH, scrollV;

		public ScrollManager(TextArea textArea) {
			super();
			this.textArea = requireNonNull(textArea,"textArea");
			
			scrollH = new Scroll();
			scrollV = new Scroll();
			
			prepareStyle();
			recalculateBounds();
		}
		
		public void onDraw() {
			scrollH.draw();
			scrollV.draw();
		}
		
		public void recalculateBounds() {
			final TextArea t = textArea;
			
			scrollH.setSize(t.getWidth() - WEIGHT, WEIGHT);
			scrollH.setPosition(t.getX(), t.getY() + t.getHeight() - WEIGHT);
			
			scrollV.setSize(WEIGHT, t.getHeight() - WEIGHT);
			scrollV.setPosition(t.getX() + t.getWidth() - WEIGHT, t.getY());
			
		}
		
		public void recalculateRanges() {
			final float calculatedScrollHMax = textArea.textMetricsPool.getMaxWidth();
			scrollH.setMaxValue(Math.max(scrollH.getMinValue(), calculatedScrollHMax));
		
			final float calculatedScrollVMin = -(textArea.textMetricsPool.getTotalHeight() - textArea.getHeight());
			scrollV.setMinValue(Math.min(scrollV.getMaxValue(), calculatedScrollVMin));
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
			
			scrollV.setConstrainDimensionsEnabled(false);
			scrollV.setValue(0);
			scrollV.setMaxValue(0);
			scrollV.swapOrientation();
		}
		
	}
	
	private static final class TextMetricsPool {
		private final PGraphics graphics;
		private final TextArea textArea;
		private final Map <Key, Float> map = new HashMap<Key, Float>();
		private float maxWidth = -1, totalHeight = -1;
		
		public TextMetricsPool(TextArea textArea) {
			super();
			graphics = ctx.createGraphics(1, 1, ctx.sketchRenderer());
			this.textArea = requireNonNull(textArea,"textArea");
		}

		public float getTextWidth(int row, int columnStart, int columnEnd) {
			final Key key = new Key(row, columnStart, columnEnd);
			
			if(map.containsKey(key)) {
				return map.get(key);
			} else {
				return map.put(key, getCalculatedTextWidth(row,columnStart,columnEnd));
			}
			
		}
		
		public float getMaxWidth() {
			if (maxWidth == -1) {
				final int linesCount = textArea.getLinesCount();
				
				for(int i = 0; i < linesCount; i++) {
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
		
		public float getCalculatedTextWidth(int row) {
			return getCalculatedTextWidth(row,0, textArea.textEditorModel.getLineLength(row));
		}
		
		private float getCalculatedTextWidth(int row, int startColumn, int endColumn) {
			float textWidth = 0;
			
			final String text = textArea.textEditorModel.getLineText(row);
			final int textLength = textArea.textEditorModel.getLineLength(row);
			startColumn = (int) MathUtils.constrain(startColumn, 0, textLength);
			endColumn = (int) MathUtils.constrain(endColumn, 0, textLength);
			final int effectiveStartColumn = Math.min(startColumn, endColumn);
			final int effectiveEndColumn = Math.max(startColumn, endColumn);
			
			graphics.beginDraw();
			graphics.pushStyle();
			graphics.textAlign(LEFT,TOP);
			graphics.textSize(24);
			textWidth = graphics.textWidth(text.substring(effectiveStartColumn,effectiveEndColumn));
			graphics.popStyle();
			graphics.endDraw();
			
			return textWidth;
		}
		
		private static final record Key(int row, int startColumn, int endColumn) {}
	}

	private static final class TextRenderer extends BufferedView {
		private final TextArea textArea;
		
		public TextRenderer(TextArea textArea) {
			super();
			setVisible(true);
			
			this.textArea = requireNonNull(textArea,"textArea");
		}

		@Override
		protected void render(PGraphics p) {

			final int linesCount = textArea.textEditorModel.getLinesCount();
			
			final float textSize = textArea.textStyle.getTextSize();
			
			p.pushStyle();
			textArea.textStyle.getTextColor().apply(p);
			p.textSize(textSize);
			p.textAlign(LEFT,TOP);
			
			for(int i = 0; i < linesCount; i++) {
				final float posX = 0 - textArea.scrollManager.scrollH.getValue();
				final float posY = 0 + textSize * i + textArea.scrollManager.scrollV.getValue();
				final String text = textArea.textEditorModel.getLineText(i);
				
				p.text(text,posX,posY);
			}
			
			p.popStyle();
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
}