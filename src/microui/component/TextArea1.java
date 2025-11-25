package microui.component;

import static java.util.Objects.requireNonNull;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import microui.core.GraphicsBuffer;
import microui.core.TextEditorModel;
import microui.core.base.Component;
import microui.core.base.View;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class TextArea1 extends Component implements Scrollable, KeyPressable {
	private static final byte MIN_SIZE = 100;
	private static final short MAX_SIZE = 1000;
	private static final byte SCROLL_WEIGHT = 10;
	private static final byte SCROLL_BUFFER_AREA = 10;
	private static final byte VERTICAL_SCROLL_MAX = 10;
	private static final byte ADDITIONAL_VERTICAL_SCROLL_MIN = 10;
	
	private final TextEditorModel textEditorModel;
	private final Items items;
	private final Scroll scrollH, scrollV;
	private final TextWidthPool textWidthPool;
	private GraphicsBuffer graphics;
	
	public TextArea1(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(MIN_SIZE, MAX_SIZE);
		setConstrainDimensionsEnabled(true);
		
		textEditorModel = new TextEditorModel();
		
		items = new Items(this);

		scrollH = new Scroll();
		scrollV = new Scroll();
		
		textWidthPool = new TextWidthPool(this);
		
		scrollH.setConstrainDimensionsEnabled(false);
		scrollH.setMinMaxValue(-10, 0);
		scrollH.setValue(-10);
		
		scrollV.setConstrainDimensionsEnabled(false);
		scrollV.swapOrientation();
		scrollV.setMaxValue(VERTICAL_SCROLL_MAX);
		scrollV.setValue(VERTICAL_SCROLL_MAX);
		
		recalculateScrollsBounds();
		
		graphics = new GraphicsBuffer() {
			public void onDraw(PGraphics graphics) {
				items.setGraphics(graphics);
				items.draw();
			}
		};
		
		textEditorModel.setOnTextChangedListener(() -> {
			recalculateScrollsMinMaxValue();
			textWidthPool.clearCache();
		});
		
		textEditorModel.setText(ctx.loadStrings("C:\\Users\\002\\Desktop\\example_of_text.txt"));
		
	}

	public TextArea1() {
		this(0, 0, 1, 1);
		initBoundsInCenter();
	}

	// == PUBLIC API ==
	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {

	}
	
	public float getTextSize() {
		return items.getTextSize();
	}

	public void setTextSize(float textSize) {
		items.setTextSize(textSize);
	}
	
	public PFont getFont() {
		return null;
	}
	
	public void setFont() {
		
	}

	///////////////////

	@Override
	protected void render() {
		backgroundOnDraw();
		graphics.draw();
		scrollH.draw();
		scrollV.draw();
		
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (graphics != null) {
			graphics.setBoundsFrom(this);
		}
		
		if(scrollH != null && scrollV != null) {
			recalculateScrollsBounds();
			
			recalculateScrollsMinMaxValue();
		}
	}

	// == PRIVATE API ==
	private void recalculateScrollsMinMaxValue() {
		scrollH.setMaxValue(textWidthPool.getMaxWidth() - getWidth());
		
		final float minScrollValue = -textEditorModel.getLinesCount() * items.getTextSize() + getHeight() - ADDITIONAL_VERTICAL_SCROLL_MIN;
		if (scrollV.getMaxValue() < minScrollValue) {
			scrollV.setMaxValue(minScrollValue + 1);
		}
		
		scrollV.setMinValue(minScrollValue);
		
	}
	
	private void recalculateScrollsBounds() {
		scrollH.setSize(getWidth(), SCROLL_WEIGHT);
		scrollH.setPosition(getX(), getY() + getHeight() - SCROLL_WEIGHT);
		
		scrollV.setSize(SCROLL_WEIGHT, getHeight() - SCROLL_BUFFER_AREA);
		scrollV.setPaddingBottom(SCROLL_BUFFER_AREA);
		scrollV.setPosition(getX() + getWidth() - SCROLL_WEIGHT, getY());
	}
	
	private void backgroundOnDraw() {
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
	}

	private void initBoundsInCenter() {
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getPadWidth() / 2, ctx.height / 2 - getPadHeight() / 2);
	}
	///////////////////

	private static final class Items extends View {
		private static final int DEFAULT_TEXT_SIZE = 24;
		private static final int MIN_TEXT_SIZE = 4;
		private final TextArea1 textArea;
		private PGraphics graphics;
		private float textSize;

		public Items(TextArea1 textArea) {
			super();
			setVisible(true);
			
			this.textArea = requireNonNull(textArea, "textArea");
			
			textSize = DEFAULT_TEXT_SIZE;
			

		}

		public float getTextSize() {
			return textSize;
		}

		public void setTextSize(float textSize) {
			if (this.textSize == textSize) {
				return;
			}

			if (textSize < MIN_TEXT_SIZE) {
				throw new IllegalArgumentException("Text size cannot be less than "+ MIN_TEXT_SIZE);
			}
			
			this.textSize = textSize;
			
			textArea.textWidthPool.clearCache();
			textArea.recalculateScrollsMinMaxValue();
		}

		public PGraphics getGraphics() {
			return graphics;
		}

		public void setGraphics(PGraphics graphics) {
			this.graphics = requireNonNull(graphics, "graphics");
		}

		@Override
		protected void render() {
			getGraphics().clear();
			getGraphics().fill(0);
			getGraphics().textSize(textSize);
			getGraphics().textAlign(LEFT,TOP);
			itemsOnDraw();
		}

		private void itemsOnDraw() {
			
			for(int i = 0; i < textArea.textEditorModel.getLinesCount(); i++) {
				
				final float posX = 0 - textArea.scrollH.getValue();
				
				final float posY = 0 + textArea.scrollV.getValue() + getTextSize() * i;
				
				final String text = textArea.textEditorModel.getLineText(i);
				
				getGraphics().text(text, posX, posY);
			}
			
		}
	}
	
	private static final class TextWidthPool {
		private static final byte EMPTY_CACHE = -1;
		private static final PGraphics CONTEXT = ctx.createGraphics(1, 1, ctx.sketchRenderer());
		private final TextArea1 textArea;
		private float cachedMaxWidth;
		
		public TextWidthPool(TextArea1 textArea) {
			super();
			this.textArea = requireNonNull(textArea,"textArea");
			
			cachedMaxWidth = EMPTY_CACHE;
		}

		public float getMaxWidth() {
			if (cachedMaxWidth == EMPTY_CACHE) {
				final int linesCount = textArea.textEditorModel.getLinesCount();
				
				for(int i = 0; i < linesCount; i++) {
					final String text = textArea.textEditorModel.getLineText(i);
					final float textWidth = getTextWidth(text);
					cachedMaxWidth = Math.max(cachedMaxWidth, textWidth);
				}
			}			
			
			return cachedMaxWidth;
		}
		
		// must be called only when:
		// 1. text was changed; [1]
		// 2. text size was changed; [1]
		// 3. TODO text font was changed; [0]
		public void clearCache() {
			cachedMaxWidth = EMPTY_CACHE;
		}
		
		private float getTextWidth(String text) {
			requireNonNull(text,"text");
			final float textSize = textArea.items.getTextSize();
			float textWidth = 0;
			
			CONTEXT.beginDraw();
			CONTEXT.pushStyle();
			CONTEXT.textSize(textSize);
			CONTEXT.textAlign(LEFT,TOP);
			textWidth = CONTEXT.textWidth(text);
			CONTEXT.popStyle();
			CONTEXT.endDraw();
			
			return textWidth;
		}
	}
}