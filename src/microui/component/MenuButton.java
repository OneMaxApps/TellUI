package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import microui.core.base.SpatialView;
import microui.core.base.View;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.GradientLoopColor;
import microui.core.style.Stroke;
import microui.event.Listener;
import processing.event.MouseEvent;

// if menu is root - it's vertical list, else add horizontal shifting

public final class MenuButton extends Button implements Scrollable {
	private static final int DEFAULT_MAX_WIDTH = 100;
	private static final int DEFAULT_MAX_HEIGHT = 24;
	private static final int DEFAULT_ITEM_WIDTH = 200;
	private static final int DEFAULT_ITEM_HEIGHT = 22;
	private final Items items;
	private final Mark mark;
	private MenuButton root;
	private ItemDimensions itemDimensions;
	private DirectionMode directionMode;
	private boolean isOpen, isRootModeEnabled;

	public MenuButton(String title, float x, float y, float w, float h) {
		super(title, x, y, w, h);
		setMaxSize(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);

		items = new Items(this);
		mark = new Mark(this);
		onClick(() -> setOpen(!isOpen()));
		onEnterLong(() -> {
			if(this != getRoot()) {
				setOpen(true);
			}
		});
		setRoot(this);
		setItemDimensions(new ItemDimensions(DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT));
		setRootModeEnabled(true);
		setDirectionMode(DirectionMode.RIGHT);
	}

	public MenuButton(String title) {
		this(title, 1, 1, 1, 1);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getWidth() / 2, ctx.height / 2 - getHeight() / 2);
		setText(title);
	}

	public MenuButton() {
		this("Menu Button");
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		items.mouseWheel(mouseEvent);
	}

	public DirectionMode getDirectionMode() {
		return directionMode;
	}

	public void setDirectionMode(DirectionMode directionMode) {
		if (directionMode == null) {
			throw new NullPointerException("DirectionMode cannot be null");
		}
		this.directionMode = directionMode;
	}

	public ItemDimensions getItemDimensions() {
		return itemDimensions;
	}

	public MenuButton setItemDimensions(ItemDimensions itemDimensions) {
		if (itemDimensions == null) {
			throw new NullPointerException("ItemDimensions cannot be null");
		}

		if (this.itemDimensions == itemDimensions) {
			return this;
		}

		this.itemDimensions = itemDimensions;

		items.recalculateAllRecursive();

		return this;
	}

	public boolean isOpen() {
		return isOpen;
	}

	
	public MenuButton setOpen(boolean isOpen) {
		if (this.isOpen == isOpen) {
			return this;
		}

		this.isOpen = isOpen;

		if (!isOpen) {
			items.close();
		}

		return this;
	}

	public MenuButton open() {
		setOpen(true);
		return this;
	}

	public MenuButton close() {
		setOpen(false);
		return this;
	}

	
	public MenuButton add(String... title) {
		items.addPlainItemInternal(title);
		return this;
	}

	public MenuButton add(String title, Listener onClickListener) {
		if (onClickListener == null) {
			throw new NullPointerException("OnClickListener cannot be null");
		}
		
		items.addPlainItemInternal(title);
		items.findInternal(title).onClick(onClickListener);
		return this;
	}

	public MenuButton addMenu(String menuTitle, String... itemTitle) {
		items.addMenuItemInternal(menuTitle);
		getMenu(menuTitle).add(itemTitle);
		return this;
	}

	public Button find(String title) {
		return items.findInternal(title);
	}

	public MenuButton findMenu(String title) {
		Button b = find(title);

		if (b instanceof MenuButton m) {
			return m;
		}

		return null;
	}

	public Button get(String title) {
		final Button b = find(title);

		if (b == null) {
			throw new NoSuchElementException("Item with title: \"" + title + "\" not found in MenuButton");
		}

		return b;
	}

	
	public MenuButton getMenu(String title) {
		final Button b = get(title);

		if (b instanceof MenuButton m) {
			return m;
		}

		throw new ClassCastException("Item with title: \"" + title + "\" cannot be cast to MenuButton type");
	}

	public MenuButton remove(String... title) {
		items.removeInternal(title);
		return this;
	}

	public MenuButton setMarkColor(AbstractColor color) {
		mark.setColor(color);

		return this;
	}

	public AbstractColor getMarkColor() {
		return mark.getColor();
	}

	@Override
	protected void render() {
		super.render();

		if (isOpen()) {
			items.draw();
		}

		if (ctx.mousePressed && isOpen() && !items.isHoverDeep() && !getRoot().isHover()) {
			setOpen(false);
		}

		mark.draw();
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		items.recalculatePosition();
		items.recalculateDimensions();

		mark.setPosition(getAbsoluteX(), getAbsoluteY());
		mark.setSize(getAbsoluteWidth()*.99f, getAbsoluteHeight()*.99f);
	}

	private MenuButton getRoot() {
		return root;
	}

	private void setRoot(MenuButton root) {
		if (root == null) {
			throw new NullPointerException("Root for MenuButton cannot be null");
		}
		this.root = root;
	}

	private boolean isRootModeEnabled() {
		return isRootModeEnabled;
	}

	private void setRootModeEnabled(boolean isRoot) {
		this.isRootModeEnabled = isRoot;
	}
	
	public static enum DirectionMode {
		AUTO, LEFT, RIGHT;
	}

	public static final record ItemDimensions(float width, float height) {

		public ItemDimensions {
			if (width <= 0 || height <= 0) {
				throw new IllegalArgumentException("Dimensions for MenuButton item cannot be less or equal zero");
			}
		}
	}

	private static final class Items extends View implements Scrollable {
		private final MenuButton parent;
		private final List<Button> list;

		public Items(MenuButton parent) {
			super();
			setVisible(true);
			this.parent = parent;
			list = new ArrayList<Button>();

		}

		@Override
		public void mouseWheel(MouseEvent mouseEvent) {
			if (mouseEvent == null) {
				throw new NullPointerException("MouseEvent cannot be null");
			}

			if (list.isEmpty()) {
				return;
			}

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) instanceof MenuButton m) {
					m.mouseWheel(mouseEvent);
				}
			}

			if (list.size() == 1) {
				return;
			}

			if (!isHover()) {
				return;
			}

			final boolean isDown = mouseEvent.getCount() > 0;

			final float firstItemY = list.get(0).getY();
			final float lastItemY = list.get(list.size() - 1).getY();

			for (int i = 0; i < list.size(); i++) {
				Button b = list.get(i);
				b.setY(isDown ? b.getY() + b.getHeight() : b.getY() - b.getHeight());
			}

			if (isDown) {
				list.add(0, list.remove(list.size() - 1));
				list.get(0).setY(firstItemY);
			} else {
				list.add(list.size() - 1, list.remove(0));
				list.get(list.size() - 1).setY(lastItemY);
			}
		}

		public void close() {
			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				if (b instanceof MenuButton m) {
					m.setOpen(false);
				}
			}
		}

		public void recalculatePosition() {
			float totalHeight = 0;
			for (int i = 0; i < list.size(); i++) {
				Button b = list.get(i);
				b.setConstrainDimensionsEnabled(false);

				// vertical list under the root menu
				if (parent.isRootModeEnabled()) {
					b.setAbsoluteX(parent.getX());
					b.setAbsoluteY(parent.getY() + parent.getHeight() + totalHeight);
				} else {
					// swing on horizontal

					float newX = 0;

					switch (parent.getDirectionMode()) {
					case AUTO:
						if (isRightSideHasEnoughPlace()) {
							newX = parent.getX() + parent.getAbsoluteWidth();
						} else {
							newX = parent.getX() - parent.getAbsoluteWidth();
						}
						break;

					case LEFT:
						newX = parent.getX() - parent.getAbsoluteWidth();
						break;

					case RIGHT:
						newX = parent.getX() + parent.getAbsoluteWidth();
						break;
					}

					b.setX(newX);

					b.setY(parent.getY() + totalHeight);
				}

				totalHeight += b.getHeight();
			}
		}

		public void recalculateDimensions() {
			if (list.isEmpty()) {
				return;
			}

			final float newWidth = parent.getRoot().getItemDimensions().width();
			final float newHeight = parent.getRoot().getItemDimensions().height();

			for (int i = 0; i < list.size(); i++) {
				list.get(i).setSize(newWidth, newHeight);
			}

		}

		public void recalculateAllRecursive() {
			recalculateDimensions();
			recalculatePosition();

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				if (b instanceof MenuButton m) {
					m.items.recalculateAllRecursive();
				}
			}
		}
		
		public void addPlainItemInternal(String... title) {
			checkTitle(title);
			
			for (int i = 0; i < title.length; i++) {
				addInternal(new Button(title[i]));
			}
		}
		
		public void addMenuItemInternal(String... title) {
			checkTitle(title);
			
			for (int i = 0; i < title.length; i++) {
				addInternal(new MenuButton(title[i]));
			}
		}

		public Button findInternal(String title) {
			checkTitle(title);

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				if (b.getText().equals(title)) {
					return b;
				}

				if (b instanceof MenuButton m) {
					return m.find(title);
				}
			}

			return null;
		}

		public void removeInternal(String... title) {
			checkTitle(title);
			
			for(int i = 0; i < title.length; i++) {
				final Button b = findInternal(title[i]);
		
				if (b == null) {
					throw new NoSuchElementException("Item with title: \"" + title + "\" not found in MenuButton");
				}
		
				findListWhichContainsButtonInternal(b).remove(b);
			}
			
			recalculatePosition();
			recalculateDimensions();
		}

		@Override
		protected void render() {
			itemsOnDraw();
		}
		
		private void addInternal(Button button) {
			if (button == null) {
				throw new NullPointerException("Button cannot be null");
			}

			if (list.contains(button)) {
				throw new IllegalArgumentException("Button cannot be added twice in MenuButton");
			}

			prepareGeneralItemStyle(button);
			
			if (button instanceof MenuButton m) {
				prepareMenuItem(m);
			} else {
				preparePlainItem(button);
			}

			list.add(button);

			recalculateDimensions();
			recalculatePosition();
		}
		
		private static void checkTitle(String... titles) {
			if(titles == null) {
				throw new NullPointerException("Titles array for MenuButton items cannot be null");
			}
			
			for(int i = 0; i < titles.length; i++) {
				if(titles[i] == null) {
					throw new NullPointerException("Title for MenuButton item cannot be null");
				}
			}
		}

		private static void prepareGeneralItemStyle(Button button) {
			button.setTextAlignX(LEFT);
			button.setPadding(10, 0);
			
			button.setStrokeColor(Color.TRANSPARENT);
			button.setBackgroundColor(getTheme().getMenuButtonItemColor());
		}
		
		private void preparePlainItem(Button button) {
			button.onClick(() -> parent.getRoot().setOpen(false));

			button.setTextColor(getTheme().getMenuButtonItemTextColor());
		}
		
		private void prepareMenuItem(MenuButton menuButton) {
			menuButton.setRootModeEnabled(false);
			menuButton.setRoot(parent.getRoot());
			
			final AbstractColor c = getTheme().getMenuButtonItemTextColor();
			final AbstractColor c1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);

			menuButton.setTextColor(new GradientLoopColor(c, c1).setSpeed(.05f));
		}
		
		private List<Button> findListWhichContainsButtonInternal(Button button) {
			if (list.contains(button)) {
				return list;
			}

			for (int i = 0; i < list.size(); i++) {
				Button b = list.get(i);
				if (b instanceof MenuButton m) {
					return m.items.findListWhichContainsButtonInternal(button);
				}
			}

			return null;
		}

		private boolean isHover() {
			if (!parent.isRootModeEnabled() && parent.isHover()) {
				return true;
			}

			if (list.isEmpty()) {
				return false;
			}

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				if (b.isHover()) {
					return true;
				}

			}

			return false;
		}

		private boolean isHoverDeep() {
			if (isHover()) {
				return true;
			}

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);

				if (b instanceof MenuButton m) {

					if (m.items.isHoverDeep() && m.isOpen()) {
						return true;
					}

				}
			}

			return false;
		}

		private boolean isRightSideHasEnoughPlace() {
			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				if (parent.getAbsoluteX() + parent.getAbsoluteWidth() + b.getAbsoluteWidth() > ctx.width) {
					return false;
				}
			}

			return true;
		}

		private void itemsOnDraw() {
			if (list.isEmpty()) {
				return;
			}

			for (int i = 0; i < list.size(); i++) {
				list.get(i).draw();
			}
		}

	}

	private static class Mark extends SpatialView {
		private final Stroke stroke;

		public Mark(MenuButton menu) {
			setVisible(true);
			stroke = new Stroke();
			stroke.setWeight(2);

			setColor(new GradientColor(Color.TRANSPARENT,
					new GradientLoopColor(Color.GRAY_232L, new Color(0, 0, 232, 64)).setSpeed(.01f),
					() -> menu.isOpen() && !menu.isRootModeEnabled()));
		}

		public AbstractColor getColor() {
			return stroke.getColor();
		}

		public void setColor(AbstractColor color) {
			stroke.setColor(color);
		}

		@Override
		protected void render() {
			ctx.noFill();
			stroke.apply();
			ctx.rect(getX(), getY(), getWidth(), getHeight());
		}

	}
}