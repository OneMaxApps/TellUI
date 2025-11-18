package microui.component;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import java.util.ArrayList;
import java.util.List;

import microui.core.GraphicsBuffer;
import microui.core.base.Component;
import microui.core.base.View;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class TextArea extends Component implements Scrollable, KeyPressable {
	private final Items items;
	private GraphicsBuffer graphics;

	public TextArea(float x, float y, float width, float height) {
		super(x, y, width, height);
		setPaddingLeft(10);
		items = new Items(this);

		graphics = new GraphicsBuffer() {
			public void onDraw(PGraphics graphics) {
				items.setGraphics(graphics);
				items.draw();
			}
		};

	}

	public TextArea() {
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
	///////////////////

	@Override
	protected void render() {
		backgroundOnDraw();
		graphics.draw();
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (graphics != null) {
			graphics.setBoundsFrom(this);
		}
	}

	// == PRIVATE API ==
	private void backgroundOnDraw() {
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
	}

	private void initBoundsInCenter() {
		setMinMaxSize(20, 400);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getPadWidth() / 2, ctx.height / 2 - getPadHeight() / 2);
	}
	///////////////////

	private static final class Items extends View {
		private static final int DEFAULT_TEXT_SIZE = 24;
		private static final int MIN_TEXT_SIZE = 4;
		private final TextArea textArea;
		private final ItemManager itemManager;
		private PGraphics graphics;
		private float textSize;

		public Items(TextArea textArea) {
			super();
			setVisible(true);
			
			this.textArea = requireNonNull(textArea, "textArea");
			
			itemManager = new ItemManager();
			
			textSize = DEFAULT_TEXT_SIZE;
			
			itemManager.add();
			itemManager.add();
			itemManager.add();

		}

		public float getTextSize() {
			return textSize;
		}

		public void setTextSize(float textSize) {
			if (this.textSize == textSize) {
				return;
			}

			if (textSize < MIN_TEXT_SIZE) {
				throw new IllegalArgumentException("Text size cannot be less than 1");
			}
			
			this.textSize = textSize;
		}

		public PGraphics getGraphics() {
			return graphics;
		}

		public void setGraphics(PGraphics graphics) {
			this.graphics = requireNonNull(graphics, "graphics");
		}

		@Override
		protected void render() {
			itemsOnDraw();
		}

		private void itemsOnDraw() {
			if (itemManager.isEmpty()) {
				return;
			}

			for (int i = 0; i < itemManager.list.size(); i++) {
				final Item item = itemManager.list.get(i);
				item.draw();
			}
		}

		private final class ItemManager {
			private final List<Item> list;
			private float totalListHeight;
			
			public ItemManager() {
				super();
				list = new ArrayList<Item>();
			}

			public boolean isEmpty() {
				return list.isEmpty();
			}

			public void add() {
				list.add(new Item(totalListHeight));
				
				totalListHeight += getTextSize();
			}

			public void remove(int index) {
				if (index < 0 || index > list.size() - 1) {
					throw new IndexOutOfBoundsException("Index for removing item must be between 0 and list.size()-1");
				}

				list.remove(index);
			}
		}

		private final class Item extends View {
			private AbstractColor textColor;
			private float shiftY;

			public Item(float shiftY) {
				super();
				setVisible(true);
				setTextColor(getTheme().getEditableTextColor());
				
				this.shiftY = shiftY;
			}

			public AbstractColor getTextColor() {
				return textColor;
			}

			public void setTextColor(AbstractColor textColor) {
				this.textColor = requireNonNull(textColor, "textColor");
			}

			@Override
			protected void render() {
				textColor.apply(getGraphics());
				getGraphics().textSize(getTextSize());
				getGraphics().textAlign(LEFT, TOP);
				getGraphics().text("Text", 0, shiftY);
			}

		}
	}
}