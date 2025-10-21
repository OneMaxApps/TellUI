package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import microui.core.base.SpatialView;
import microui.core.base.View;
import microui.core.effect.SpatialAnimator;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.GradientLoopColor;
import microui.core.style.Stroke;
import microui.event.Listener;
import microui.util.Debugger;
import microui.util.SpatialState;
import processing.core.PApplet;
import processing.event.MouseEvent;

//Status: STABLE - Do not modify
//Last Reviewed: 21.10.2025

// if menu is root - it's vertical list, else add horizontal shifting
public final class MenuButton extends Button implements Scrollable {
	private static final int DEFAULT_MAX_WIDTH = 100;
	private static final int DEFAULT_MAX_HEIGHT = 24;
	private static final int DEFAULT_ITEM_WIDTH = 200;
	private static final int DEFAULT_ITEM_HEIGHT = 22;
	private final Items items;
	private final OpenStateIndicator indicator;
	private final Arrow arrow;
	private MenuButton root, activeSubMenu, parent;
	private ItemDimensions itemDimensions;
	private List<MenuButton> renderOrderList;
	private DirectionMode directionMode;
	private boolean isOpen, isRootModeEnabled;

	public MenuButton(String title, float x, float y, float w, float h) {
		super(title, x, y, w, h);
		setMaxSize(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);

		items = new Items(this);
		indicator = new OpenStateIndicator(this);
		arrow = new Arrow(this);
		onClick(() -> setOpen(!isOpen()));
		setRoot(this);
		setItemDimensions(new ItemDimensions(DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT));
		setRootModeEnabled(true);
		setDirectionMode(DirectionMode.AUTO);
	}

	public MenuButton(String title) {
		this(title, 1, 1, 1, 1);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getWidth() / 2, ctx.height / 2 - getHeight() / 2);
	}

	public MenuButton() {
		this("Menu Button");
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (isRoot() && getActiveSubMenu() != null) {
			getActiveSubMenu().mouseWheel(mouseEvent);
		} else {
			items.mouseWheel(mouseEvent);
		}
		
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

			if (getParent() == getRoot()) {
				setActiveSubMenu(null);
			} else {
				setActiveSubMenu(getParent());
			}

		}

		if (!isRoot() && isOpen) {
			setActiveSubMenu(this);
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

	public MenuButton add(int... number) {
		items.addPlainItemInternal(number);
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

	public MenuButton setOpenStateIndicatorColor(AbstractColor color) {
		indicator.setColor(color);

		return this;
	}

	public AbstractColor getOpenStateIndicatorColor() {
		return indicator.getColor();
	}

	public MenuButton setItemsBackgroundColor(AbstractColor color) {
		for (int i = 0; i < items.list.size(); i++) {
			final Button b = items.list.get(i);
			b.setBackgroundColor(color);
		}
		return this;
	}

	public MenuButton setItemsBackgroundColorRecursive(AbstractColor color) {
		setItemsBackgroundColor(color);

		for (int i = 0; i < items.list.size(); i++) {
			final Button b = items.list.get(i);

			if (b instanceof MenuButton m) {
				m.setItemsBackgroundColorRecursive(color);
			}
		}

		return this;
	}

	public MenuButton setItemsTextColor(AbstractColor color) {
		for (int i = 0; i < items.list.size(); i++) {
			final Button b = items.list.get(i);
			b.setTextColor(color);
		}
		return this;
	}

	public MenuButton setItemsTextColorRecursive(AbstractColor color) {
		setItemsTextColor(color);

		for (int i = 0; i < items.list.size(); i++) {
			final Button b = items.list.get(i);

			if (b instanceof MenuButton m) {
				m.setItemsTextColorRecursive(color);
			}
		}

		return this;
	}
	
	public AbstractColor getArrowColor() {
		return arrow.getColor();
	}
	
	public MenuButton setArrowColor(AbstractColor color) {
		arrow.setColor(color);
		
		return this;
	}
	
	public MenuButton setArrowColorRecursive(AbstractColor color) {
		setArrowColor(color);
		
		for(int i = 0; i < items.list.size(); i++) {
			final Button b = items.list.get(i);
			
			if(b instanceof MenuButton m) {
				m.setArrowColorRecursive(color);
			}
			
		}
		
		return this;
	}

	@Override
	protected void render() {
		super.render();

		if (isOpen() && isRoot()) {
			items.draw();

			for (int i = 0; i < getRenderOrderList().size(); i++) {
				final MenuButton m = getRenderOrderList().get(i);
				if (m.isOpen()) {
					m.items.draw();
				}
			}

		}

		if (isMustBeClosed()) {
			setOpen(false);
		}

		indicator.draw();
		arrow.draw();
		
		if (Debugger.isDebugModeEnabled()) {
			if (isActiveSubMenu()) {
				ctx.fill(0, 200, 0, 100);
				ctx.rect(getAbsoluteX(), getAbsoluteY(), getAbsoluteWidth(), getAbsoluteHeight());
			}
		}
		
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		items.recalculateDimensions();
		items.recalculatePosition();

		indicator.setPosition(getAbsoluteX(), getAbsoluteY());
		indicator.setSize(getAbsoluteWidth() * .99f, getAbsoluteHeight() * .99f);
		
		arrow.setPosition(getX()+getWidth(), getY());
		arrow.setSize(getPaddingRight(),getAbsoluteHeight());
	}

	private List<MenuButton> getRenderOrderList() {
		if (getRoot().renderOrderList == null) {
			getRoot().renderOrderList = new ArrayList<MenuButton>();
		}

		return getRoot().renderOrderList;
	}

	private boolean isMustBeClosed() {
		return ctx.mousePressed && isOpen() && !isHover() && !items.isHoverDeep() && !getRoot().isHover();
	}

	private MenuButton getRoot() {
		if (getParent() == null) {
			return root;
		} else {
			return getParent().getRoot();
		}
	}

	private void setRoot(MenuButton root) {
		if (root == null) {
			throw new NullPointerException("Root for MenuButton cannot be null");
		}
		this.root = root;
	}

	private boolean isRoot() {
		return isRootModeEnabled;
	}

	private void setRootModeEnabled(boolean isRoot) {
		this.isRootModeEnabled = isRoot;
	}

	private MenuButton getParent() {
		return parent;
	}

	private void setParent(MenuButton parent) {
		this.parent = parent;
	}

	private MenuButton getActiveSubMenu() {
		return getRoot().activeSubMenu;
	}

	private void setActiveSubMenu(MenuButton activeSubMenu) {
		getRoot().activeSubMenu = activeSubMenu;
	}

	private boolean isActiveSubMenu() {
		return this == getActiveSubMenu();
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
		private final MenuButton menu;
		private final List<Button> list;

		public Items(MenuButton menu) {
			super();
			setVisible(true);
			this.menu = menu;
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
				if (menu.isRoot()) {
					b.setAbsoluteX(menu.getX());
					b.setAbsoluteY(menu.getY() + menu.getHeight() + totalHeight);
				} else {
					// vertical list with horizontal shifting on horizontal

					float newX = 0;

					switch (menu.getDirectionMode()) {
					case AUTO:

						if (isRightSideHasEnoughPlace()) {
							newX = menu.getX() + menu.getAbsoluteWidth();
						} else {
							newX = menu.getX() - getMaxItemWidth();
						}

						break;

					case LEFT:
						newX = menu.getX() - getMaxItemWidth();
						break;

					case RIGHT:
						newX = menu.getX() + menu.getAbsoluteWidth();
						break;
					}

					b.setX(newX);

					b.setY(menu.getY() + totalHeight);
				}

				totalHeight += b.getHeight();
			}
		}

		public void recalculatePositionAllRecursive() {
			recalculatePosition();

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);

				if (b instanceof MenuButton m) {
					m.items.recalculatePositionAllRecursive();
				}
			}
		}

		public void recalculateDimensions() {
			if (list.isEmpty()) {
				return;
			}

			final float newWidth = menu.getItemDimensions().width();
			final float newHeight = menu.getItemDimensions().height();

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

		public void addPlainItemInternal(int... number) {
			if (number == null) {
				throw new NullPointerException("Number array for MenuButton cannot be null");
			}

			final StringBuilder sb = new StringBuilder();

			for (int i = 0; i < number.length; i++) {
				sb.append(number[i] + ",");
			}

			addPlainItemInternal(sb.toString().split(","));
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
			}

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				if (b instanceof MenuButton m) {
					return m.find(title);
				}
			}

			return null;
		}

		public void removeInternal(String... title) {
			checkTitle(title);

			for (int i = 0; i < title.length; i++) {
				final Button b = findInternal(title[i]);

				if (b == null) {
					throw new NoSuchElementException("Item with title: \"" + title + "\" not found in MenuButton");
				}

				if (findListWhichContainsButtonInternal(b).remove(b)) {
					menu.getRenderOrderList().remove(b);
					recalculatePositionAllRecursive();
				}
			}

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

		private void checkTitle(String... titles) {
			if (titles == null) {
				throw new NullPointerException("Titles array for MenuButton items cannot be null");
			}

			for (int i = 0; i < titles.length; i++) {
				if (titles[i] == null) {
					throw new NullPointerException("Title for MenuButton item cannot be null");
				}
				
				if(titles[i].isEmpty()) {
					throw new IllegalArgumentException("Title cannot be empty [Inside MenuButton: "+menu.getText()+"]");
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
			button.onClick(() -> menu.getRoot().setOpen(false));

			button.setTextColor(getTheme().getMenuButtonItemTextColor());
		}

		private void prepareMenuItem(MenuButton menuButton) {
			menuButton.setRootModeEnabled(false);
			menuButton.setRoot(menu.getRoot());
			menuButton.setParent(menu);
			menu.getRenderOrderList().add(menuButton);

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
				if (menu.getAbsoluteX() + menu.getAbsoluteWidth() + b.getAbsoluteWidth() > ctx.width) {
					return false;
				}
			}

			return true;
		}

		private float getMaxItemWidth() {
			float max = 0;
			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				max = Math.max(max, b.getAbsoluteWidth());
			}

			return max;
		}

		private void itemsOnDraw() {
			if (list.isEmpty()) {
				return;
			}

			for (int i = 0; i < list.size(); i++) {
				final Button b = list.get(i);
				b.setInteractionHandlerEnabled(isShouldReact());
				b.draw();

				if (Debugger.isDebugModeEnabled()) {
					if (menu.isActiveSubMenu()) {
						ctx.pushStyle();
						ctx.fill(0, 200, 0, 32);
						ctx.rect(b.getAbsoluteX(), b.getAbsoluteY(), b.getAbsoluteWidth(), b.getAbsoluteHeight());
						ctx.popStyle();
					}
				}
			}
		}

		private boolean isShouldReact() {
			final MenuButton active = menu.getActiveSubMenu();
			final boolean isHasActive = active != null;

			if (!isHasActive) {
				return true;
			}

			if (menu.isActiveSubMenu()) {
				return true;
			}

			if (!menu.isActiveSubMenu() && !active.isHover() && !active.items.isHover() && !active.isHover()) {
				return true;
			}

			if (menu == active.getParent()) {
				return true;
			}

			return false;
		}

	}

	private static class OpenStateIndicator extends SpatialView {
		private final Stroke stroke;

		public OpenStateIndicator(MenuButton menu) {
			setVisible(true);
			stroke = new Stroke();
			stroke.setWeight(2); 

			setColor(new GradientColor(Color.TRANSPARENT,
					new GradientLoopColor(Color.GRAY_232L, new Color(0, 0, 232, 64)).setSpeed(.01f),
					() -> menu.isOpen() && !menu.isRoot()));

			setSpatialAnimator(new SpatialAnimator(new SpatialState(0, 0, 0, 0), menu, () -> menu.isOpen())
					.setPositionEnabled(false));
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
	
	private static final class Arrow extends SpatialView {
		private static final int DEFAULT_SIZE = 8;
		private static final String CLOSE_SYMBOL = "▶";
		private static final String OPEN_SYMBOL = "▼";
		private final MenuButton menu;
		private AbstractColor color;
		
		public Arrow(MenuButton menu) {
			super();
			setVisible(true);
			if(menu == null) {
				throw new NullPointerException("MenuButton for Arrow cannot be null");
			}
			
			setColor(getTheme().getPrimaryColor());
			
			
			this.menu = menu;
		}

		@Override
		protected void render() {
			if(menu.isRoot()) {
				return;
			}
			
			color.apply();
			ctx.textAlign(PApplet.CENTER,PApplet.CENTER);
			ctx.textSize(DEFAULT_SIZE);
			ctx.text(menu.isOpen ? OPEN_SYMBOL : CLOSE_SYMBOL, getX(), getY(), getWidth(), getHeight());
			
		}

		public AbstractColor getColor() {
			return color;
		}

		public void setColor(AbstractColor color) {
			if(color == null) {
				throw new NullPointerException("Color cannot be null");
			}
			this.color = color;
		}

	}
}