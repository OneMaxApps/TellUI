package microui.component;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import microui.core.ImageBuffer;
import microui.core.base.ContentView;
import microui.core.base.SpatialView;
import microui.core.base.View;
import microui.core.effect.AbstractShadow;
import microui.core.effect.ReactiveShadow;
import microui.core.effect.SpatialAnimator;
import microui.core.exception.DuplicateItemException;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.core.style.LerpedLoopColor;
import microui.core.style.Stroke;
import microui.event.Listener;
import microui.util.Debugger;
import microui.util.SpatialState;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

/**
 * A hierarchical menu button component that can contain both plain items and sub-menus.
 * The MenuButton extends Button to provide interactive menu functionality with support
 * for nested menus, icons, scrollable items, and directional placement.
 * 
 * <p><strong>Note:</strong> If a menu is the root menu, it displays as a vertical list.
 * Non-root menus add horizontal shifting to create nested menu hierarchies.</p>
 * 
 * <p><strong>Status:</strong> STABLE - Do not modify</p>
 * <p><strong>Last Reviewed:</strong> 29.10.2025</p>
 * 
 * @see Button
 * @see Scrollable
 */
public final class MenuButton extends Button implements Scrollable {
    private static final int DEFAULT_MAX_WIDTH = 100;
    private static final int DEFAULT_MAX_HEIGHT = 24;
    private static final int DEFAULT_ITEM_WIDTH = 200;
    private static final int DEFAULT_ITEM_HEIGHT = 22;
    
    private final Items items;
    private final ItemDimensions itemDimensions;
    private final OpenStateIndicator indicator;
    private final Arrow arrow;
    private MenuButton root, activeSubMenu, parent;
    private List<MenuButton> renderOrderList;
    private DirectionMode directionMode;
    private boolean open, rootMode;

    /**
     * Constructs a MenuButton with specified text, position, and dimensions.
     * The menu is initialized with default styling and item dimensions.
     *
     * @param title the text to display on the menu button
     * @param x the x-coordinate of the menu's top-left corner
     * @param y the y-coordinate of the menu's top-left corner
     * @param w the width of the menu button
     * @param h the height of the menu button
     */
    public MenuButton(String title, float x, float y, float w, float h) {
        super(title, x, y, w, h);
        setMaxSize(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);

        items = new Items(this);
        indicator = new OpenStateIndicator(this);
        arrow = new Arrow(this);
        onClick(() -> setOpen(!isOpen()));
        setRoot(this);
        itemDimensions = new ItemDimensions(items, DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT);
        setRootMode(true);
        setDirectionMode(DirectionMode.AUTO);
    }

    /**
     * Constructs a MenuButton with specified text, centered on the screen.
     *
     * @param title the text to display on the menu button
     */
    public MenuButton(String title) {
        this(title, 1, 1, 1, 1);
        setSize(getMaxWidth(), getMaxHeight());
        setPosition(ctx.width / 2 - getWidth() / 2, ctx.height / 2 - getHeight() / 2);
    }

    /**
     * Constructs a default MenuButton with the text "Menu Button".
     * The menu is centered on the screen.
     */
    public MenuButton() {
        this("Menu Button");
    }

    /**
     * Handles mouse wheel scrolling for the menu hierarchy.
     * If this is the root menu and there's an active sub-menu, delegates to it.
     * Otherwise, scrolls the items of this menu.
     *
     * @param mouseEvent the mouse wheel event
     */
    @Override
    public void mouseWheel(MouseEvent mouseEvent) {
        if (isRoot() && getActiveSubMenu() != null) {
            getActiveSubMenu().mouseWheel(mouseEvent);
        } else {
            items.mouseWheel(mouseEvent);
        }
    }

    /**
     * Checks if the menu contains any items.
     *
     * @return true if the menu has no items, false otherwise
     */
    public boolean isEmpty() {
        return items.list.isEmpty();
    }

    /**
     * Gets the number of items in this menu.
     *
     * @return the count of items in this menu
     */
    public int getItemsCount() {
        return items.list.size();
    }

    /**
     * Gets the shadow effect applied to the menu items.
     *
     * @return the current items shadow
     */
    public AbstractShadow getItemsShadow() {
        return items.shadow.getShadow();
    }

    /**
     * Sets the shadow effect for the menu items.
     *
     * @param shadow the shadow to apply to menu items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemsShadow(AbstractShadow shadow) {
        items.shadow.setShadow(shadow);
        return this;
    }

    /**
     * Gets the direction mode for sub-menu placement.
     *
     * @return the current direction mode
     */
    public DirectionMode getDirectionMode() {
        return directionMode;
    }

    /**
     * Sets the direction mode for sub-menu placement.
     *
     * @param directionMode the direction mode for sub-menu placement
     * @return this MenuButton instance for method chaining
     * @throws NullPointerException if directionMode is null
     */
    public MenuButton setDirectionMode(DirectionMode directionMode) {
        this.directionMode = requireNonNull(directionMode, "directionMode");
        return this;
    }

    /**
     * Checks if the menu is currently open.
     *
     * @return true if the menu is open, false otherwise
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Opens or closes the menu.
     *
     * @param isOpen true to open the menu, false to close it
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setOpen(boolean isOpen) {
        if (this.open == isOpen) {
            return this;
        }

        this.open = isOpen;

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

    /**
     * Opens the menu.
     *
     * @return this MenuButton instance for method chaining
     */
    public MenuButton open() {
        setOpen(true);
        return this;
    }

    /**
     * Closes the menu.
     *
     * @return this MenuButton instance for method chaining
     */
    public MenuButton close() {
        setOpen(false);
        return this;
    }

    /**
     * Adds plain items (non-menu buttons) to the menu.
     *
     * @param title the titles of the items to add
     * @return this MenuButton instance for method chaining
     */
    public MenuButton add(String... title) {
        items.addPlainItemInternal(title);
        return this;
    }

    /**
     * Adds plain items with numeric titles to the menu.
     *
     * @param number the numeric titles of the items to add
     * @return this MenuButton instance for method chaining
     */
    public MenuButton add(int... number) {
        items.addPlainItemInternal(number);
        return this;
    }

    /**
     * Adds a plain item with a click listener to the menu.
     *
     * @param title the title of the item
     * @param onClickListener the listener to call when the item is clicked
     * @return this MenuButton instance for method chaining
     */
    public MenuButton add(String title, Listener onClickListener) {
        add(title);
        get(title, false).onClick(onClickListener);
        return this;
    }

    /**
     * Adds a plain item with an icon and click listener to the menu.
     *
     * @param title the title of the item
     * @param icon the icon to display with the item
     * @param onClickListener the listener to call when the item is clicked
     * @return this MenuButton instance for method chaining
     */
    public MenuButton add(String title, PImage icon, Listener onClickListener) {
        add(title, onClickListener);
        setIcon(icon, title);
        return this;
    }

    /**
     * Adds a sub-menu to the menu with the specified items.
     *
     * @param menuTitle the title of the sub-menu
     * @param itemTitle the items to add to the sub-menu
     * @return this MenuButton instance for method chaining
     */
    public MenuButton addMenu(String menuTitle, String... itemTitle) {
        items.addMenuItemInternal(menuTitle);
        getMenu(menuTitle).add(itemTitle);
        return this;
    }

    /**
     * Adds a sub-menu with an icon and items to the menu.
     *
     * @param menuTitle the title of the sub-menu
     * @param icon the icon to display with the sub-menu
     * @param itemTitles the items to add to the sub-menu
     * @return this MenuButton instance for method chaining
     */
    public MenuButton addMenu(String menuTitle, PImage icon, String... itemTitles) {
        addMenu(menuTitle, itemTitles);
        setIcon(icon, menuTitle);
        return this;
    }

    /**
     * Finds a button by its title, optionally searching recursively through sub-menus.
     *
     * @param title the title to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found button, or null if not found
     */
    public Button find(String title, boolean recursive) {
        return items.findInternal(title, recursive);
    }

    /**
     * Finds a button by its title, searching recursively through sub-menus.
     *
     * @param title the title to search for
     * @return the found button, or null if not found
     */
    public Button find(String title) {
        return find(title, true);
    }

    /**
     * Finds a menu button by its title, optionally searching recursively.
     *
     * @param title the title to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found menu button, or null if not found or not a menu
     */
    public MenuButton findMenu(String title, boolean recursive) {
        if (find(title, recursive) instanceof MenuButton m) {
            return m;
        }
        return null;
    }

    /**
     * Finds a menu button by its title, searching recursively.
     *
     * @param title the title to search for
     * @return the found menu button, or null if not found or not a menu
     */
    public MenuButton findMenu(String title) {
        return findMenu(title, true);
    }

    /**
     * Finds a button by its ID, optionally searching recursively.
     *
     * @param id the ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found button, or null if not found
     */
    public Button findById(int id, boolean recursive) {
        return items.findByIdInternal(id, recursive);
    }

    /**
     * Finds a button by its ID, searching recursively.
     *
     * @param id the ID to search for
     * @return the found button, or null if not found
     */
    public Button findById(int id) {
        return findById(id, true);
    }

    /**
     * Finds a menu button by its ID, optionally searching recursively.
     *
     * @param id the ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found menu button, or null if not found or not a menu
     */
    public MenuButton findMenuById(int id, boolean recursive) {
        if (findById(id, recursive) instanceof MenuButton m) {
            return m;
        }
        return null;
    }

    /**
     * Finds a menu button by its ID, searching recursively.
     *
     * @param id the ID to search for
     * @return the found menu button, or null if not found or not a menu
     */
    public MenuButton findMenuById(int id) {
        return findMenuById(id, true);
    }

    /**
     * Finds a button by its text ID, optionally searching recursively.
     *
     * @param textId the text ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found button, or null if not found
     */
    public Button findByTextId(String textId, boolean recursive) {
        return items.findByTextIdInternal(textId, recursive);
    }

    /**
     * Finds a button by its text ID, searching recursively.
     *
     * @param textId the text ID to search for
     * @return the found button, or null if not found
     */
    public Button findByTextId(String textId) {
        return findByTextId(textId, true);
    }

    /**
     * Finds a menu button by its text ID, optionally searching recursively.
     *
     * @param title the text ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found menu button, or null if not found or not a menu
     */
    public MenuButton findMenuByTextId(String title, boolean recursive) {
        if (findByTextId(title, recursive) instanceof MenuButton m) {
            return m;
        }
        return null;
    }

    /**
     * Finds a menu button by its text ID, searching recursively.
     *
     * @param title the text ID to search for
     * @return the found menu button, or null if not found or not a menu
     */
    public MenuButton findMenuByTextId(String title) {
        return findMenuByTextId(title, true);
    }

    /**
     * Gets a button by its title, searching recursively.
     * Throws an exception if not found.
     *
     * @param title the title to search for
     * @return the found button
     * @throws NoSuchElementException if no button with the specified title exists
     */
    public Button get(String title) {
        final Button b = find(title);
        if (b == null) {
            throw new NoSuchElementException("Item with title: \"" + title + "\" not found in MenuButton");
        }
        return b;
    }

    /**
     * Gets a button by its title, optionally searching recursively.
     * Throws an exception if not found.
     *
     * @param title the title to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found button
     * @throws NoSuchElementException if no button with the specified title exists
     */
    public Button get(String title, boolean recursive) {
        final Button b = find(title, recursive);
        if (b == null) {
            throw new NoSuchElementException("Item with title: \"" + title + "\" not found in MenuButton");
        }
        return b;
    }

    /**
     * Gets a menu button by its title, searching recursively.
     * Throws an exception if not found or not a menu.
     *
     * @param title the title to search for
     * @return the found menu button
     * @throws NoSuchElementException if no button with the specified title exists
     * @throws ClassCastException if the found button is not a MenuButton
     */
    public MenuButton getMenu(String title) {
        final Button b = get(title);
        if (b instanceof MenuButton m) {
            return m;
        }
        throw new ClassCastException("Item with title: \"" + title + "\" cannot be cast to MenuButton type");
    }

    /**
     * Gets a menu button by its title, optionally searching recursively.
     * Throws an exception if not found or not a menu.
     *
     * @param title the title to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found menu button
     * @throws NoSuchElementException if no button with the specified title exists
     * @throws ClassCastException if the found button is not a MenuButton
     */
    public MenuButton getMenu(String title, boolean recursive) {
        final Button b = get(title, recursive);
        if (b instanceof MenuButton m) {
            return m;
        }
        throw new ClassCastException("Item with title: \"" + title + "\" cannot be cast to MenuButton type");
    }

    /**
     * Gets a button by its ID, searching recursively.
     * Throws an exception if not found.
     *
     * @param id the ID to search for
     * @return the found button
     * @throws NoSuchElementException if no button with the specified ID exists
     */
    public Button getById(int id) {
        final Button b = findById(id);
        if (b == null) {
            throw new NoSuchElementException("Item with id: \"" + id + "\" not found in MenuButton");
        }
        return b;
    }

    /**
     * Gets a button by its ID, optionally searching recursively.
     * Throws an exception if not found.
     *
     * @param id the ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found button
     * @throws NoSuchElementException if no button with the specified ID exists
     */
    public Button getById(int id, boolean recursive) {
        final Button b = findById(id, recursive);
        if (b == null) {
            throw new NoSuchElementException("Item with id: \"" + id + "\" not found in MenuButton");
        }
        return b;
    }

    /**
     * Gets a menu button by its ID, searching recursively.
     * Throws an exception if not found or not a menu.
     *
     * @param id the ID to search for
     * @return the found menu button
     * @throws NoSuchElementException if no button with the specified ID exists
     * @throws ClassCastException if the found button is not a MenuButton
     */
    public MenuButton getMenuById(int id) {
        final Button b = getById(id);
        if (b instanceof MenuButton m) {
            return m;
        }
        throw new ClassCastException("Item with id: \"" + id + "\" cannot be cast to MenuButton type");
    }

    /**
     * Gets a menu button by its ID, optionally searching recursively.
     * Throws an exception if not found or not a menu.
     *
     * @param id the ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found menu button
     * @throws NoSuchElementException if no button with the specified ID exists
     * @throws ClassCastException if the found button is not a MenuButton
     */
    public MenuButton getMenuById(int id, boolean recursive) {
        final Button b = getById(id, recursive);
        if (b instanceof MenuButton m) {
            return m;
        }
        throw new ClassCastException("Item with id: \"" + id + "\" cannot be cast to MenuButton type");
    }

    /**
     * Gets a button by its text ID, searching recursively.
     * Throws an exception if not found.
     *
     * @param textId the text ID to search for
     * @return the found button
     * @throws NoSuchElementException if no button with the specified text ID exists
     */
    public Button getByTextId(String textId) {
        final Button b = findByTextId(textId);
        if (b == null) {
            throw new NoSuchElementException("Item with text id: \"" + textId + "\" not found in MenuButton");
        }
        return b;
    }

    /**
     * Gets a button by its text ID, optionally searching recursively.
     * Throws an exception if not found.
     *
     * @param textId the text ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found button
     * @throws NoSuchElementException if no button with the specified text ID exists
     */
    public Button getByTextId(String textId, boolean recursive) {
        final Button b = findByTextId(textId, recursive);
        if (b == null) {
            throw new NoSuchElementException("Item with text id: \"" + textId + "\" not found in MenuButton");
        }
        return b;
    }

    /**
     * Gets a menu button by its text ID, searching recursively.
     * Throws an exception if not found or not a menu.
     *
     * @param textId the text ID to search for
     * @return the found menu button
     * @throws NoSuchElementException if no button with the specified text ID exists
     * @throws ClassCastException if the found button is not a MenuButton
     */
    public MenuButton getMenuByTextId(String textId) {
        final Button b = getByTextId(textId);
        if (b instanceof MenuButton m) {
            return m;
        }
        throw new ClassCastException("Item with text id: \"" + textId + "\" cannot be cast to MenuButton type");
    }

    /**
     * Gets a menu button by its text ID, optionally searching recursively.
     * Throws an exception if not found or not a menu.
     *
     * @param textId the text ID to search for
     * @param recursive true to search in sub-menus, false to search only in this menu
     * @return the found menu button
     * @throws NoSuchElementException if no button with the specified text ID exists
     * @throws ClassCastException if the found button is not a MenuButton
     */
    public MenuButton getMenuByTextId(String textId, boolean recursive) {
        final Button b = getByTextId(textId, recursive);
        if (b instanceof MenuButton m) {
            return m;
        }
        throw new ClassCastException("Item with text id: \"" + textId + "\" cannot be cast to MenuButton type");
    }

    /**
     * Removes items by their titles.
     *
     * @param titles the titles of the items to remove
     * @return this MenuButton instance for method chaining
     */
    public MenuButton remove(String... titles) {
        items.removeInternal(titles);
        return this;
    }

    /**
     * Removes items by their IDs.
     *
     * @param ids the IDs of the items to remove
     * @return this MenuButton instance for method chaining
     */
    public MenuButton removeById(int... ids) {
        items.removeByIdInternal(ids);
        return this;
    }

    /**
     * Removes items by their text IDs.
     *
     * @param textIds the text IDs of the items to remove
     * @return this MenuButton instance for method chaining
     */
    public MenuButton removeByTextId(String... textIds) {
        items.removeByTextIdInternal(textIds);
        return this;
    }

    /**
     * Sets the color of the open state indicator.
     *
     * @param color the color for the open state indicator
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setOpenStateIndicatorColor(AbstractColor color) {
        indicator.setColor(color);
        return this;
    }

    /**
     * Gets the color of the open state indicator.
     *
     * @return the current open state indicator color
     */
    public AbstractColor getOpenStateIndicatorColor() {
        return indicator.getColor();
    }

    /**
     * Sets the background color for all items in this menu.
     *
     * @param color the background color to apply
     * @param recursive true to apply to items in sub-menus, false for only this menu
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemsBackgroundColor(AbstractColor color, boolean recursive) {
        for (int i = 0; i < items.list.size(); i++) {
            final Button b = items.list.get(i);
            b.setBackgroundColor(color);

            if (recursive) {
                if (b instanceof MenuButton m) {
                    m.setItemsBackgroundColor(color, recursive);
                }
            }
        }
        return this;
    }

    /**
     * Sets the text color for all items in this menu.
     *
     * @param color the text color to apply
     * @param recursive true to apply to items in sub-menus, false for only this menu
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemsTextColor(AbstractColor color, boolean recursive) {
        for (int i = 0; i < items.list.size(); i++) {
            final Button b = items.list.get(i);
            b.setTextColor(color);
            if (recursive) {
                if (b instanceof MenuButton m) {
                    m.setItemsTextColor(color, recursive);
                }
            }
        }
        return this;
    }

    /**
     * Gets the color of the arrow indicator for sub-menus.
     *
     * @return the current arrow color
     */
    public AbstractColor getArrowColor() {
        return arrow.getColor();
    }

    /**
     * Sets the color of the arrow indicator for sub-menus.
     *
     * @param color the color for the arrow indicator
     * @param recursive true to apply to arrow indicators in sub-menus
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setArrowColor(AbstractColor color, boolean recursive) {
        arrow.setColor(color);

        if (recursive) {
            for (int i = 0; i < items.list.size(); i++) {
                final Button b = items.list.get(i);
                if (b instanceof MenuButton m) {
                    m.setArrowColor(color, recursive);
                }
            }
        }

        return this;
    }

    /**
     * Sets the size for all items in this menu.
     *
     * @param width the width for items
     * @param height the height for items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemSize(float width, float height) {
        itemDimensions.setSize(width, height);
        return this;
    }

    /**
     * Sets the size for all items in this menu and recursively in sub-menus.
     *
     * @param width the width for items
     * @param height the height for items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemSizeRecursive(float width, float height) {
        setItemSize(width, height);

        for (int i = 0; i < items.list.size(); i++) {
            final Button b = items.list.get(i);
            if (b instanceof MenuButton m) {
                m.setItemSizeRecursive(width, height);
            }
        }

        return this;
    }

    /**
     * Sets the width for all items in this menu.
     *
     * @param width the width for items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemWidth(float width) {
        itemDimensions.setWidth(width);
        return this;
    }

    /**
     * Sets the width for all items in this menu and recursively in sub-menus.
     *
     * @param width the width for items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemWidthRecursive(float width) {
        setItemWidth(width);

        for (int i = 0; i < items.list.size(); i++) {
            final Button b = items.list.get(i);
            if (b instanceof MenuButton m) {
                m.setItemWidthRecursive(width);
            }
        }

        return this;
    }

    /**
     * Sets the height for all items in this menu.
     *
     * @param height the height for items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemHeight(float height) {
        itemDimensions.setHeight(height);
        return this;
    }

    /**
     * Sets the height for all items in this menu and recursively in sub-menus.
     *
     * @param height the height for items
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setItemHeightRecursive(float height) {
        setItemHeight(height);

        for (int i = 0; i < items.list.size(); i++) {
            final Button b = items.list.get(i);
            if (b instanceof MenuButton m) {
                m.setItemHeightRecursive(height);
            }
        }

        return this;
    }

    /**
     * Gets the width of items in this menu.
     *
     * @return the current item width
     */
    public float getItemWidth() {
        return itemDimensions.getWidth();
    }

    /**
     * Gets the height of items in this menu.
     *
     * @return the current item height
     */
    public float getItemHeight() {
        return itemDimensions.getHeight();
    }

    /**
     * Sets an icon for a specific item in the menu.
     *
     * @param icon the icon to set
     * @param title the title of the item to receive the icon
     * @return this MenuButton instance for method chaining
     */
    public MenuButton setIcon(PImage icon, String title) {
        items.setIcon(icon, title);
        return this;
    }

    /**
     * Gets the icon buffer for a specific item in the menu.
     *
     * @param title the title of the item
     * @return the ImageBuffer containing the icon, or null if no icon is set
     */
    public ImageBuffer getIcon(String title) {
        return items.getIcon(title);
    }

    /**
     * Renders the menu button and its hierarchy.
     * If the menu is open, renders all visible items and sub-menus.
     * Handles automatic closing when clicking outside the menu hierarchy.
     */
    @Override
    protected void render() {
        super.render();

        if (isEmpty()) {
            return;
        }

        if (isOpen() && isRoot()) {
            items.draw();

            for (int i = 0; i < getRenderOrderList().size(); i++) {
                final MenuButton m = getRenderOrderList().get(i);
                if (m.isOpen()) {
                    m.items.draw();
                }
            }
        }

        if (mustBeClosed()) {
            setOpen(false);
        }

        indicator.draw();
        arrow.draw();

        if (Debugger.isEnabled()) {
            if (isActiveSubMenu()) {
                ctx.fill(0, 200, 0, 100);
                ctx.rect(getAbsoluteX(), getAbsoluteY(), getAbsoluteWidth(), getAbsoluteHeight());
            }
        }
    }

    /**
     * Called when the menu's bounds change.
     * Updates the positions and dimensions of internal components.
     */
    @Override
    protected void onChangeBounds() {
        super.onChangeBounds();

        items.recalculateDimensions();
        items.recalculatePosition();

        indicator.setPosition(getAbsoluteX(), getAbsoluteY());
        indicator.setSize(getAbsoluteWidth() * .99f, getAbsoluteHeight() * .99f);

        arrow.setPosition(getX() + getWidth(), getY());
        arrow.setSize(getPaddingRight(), getAbsoluteHeight());
    }

    private List<MenuButton> getRenderOrderList() {
        if (getRoot().renderOrderList == null) {
            getRoot().renderOrderList = new ArrayList<MenuButton>();
        }
        return getRoot().renderOrderList;
    }

    private boolean mustBeClosed() {
        return ctx.mousePressed && isOpen() && !isHover() && !items.isHoverDeep() && !getRoot().isHover();
    }

    private MenuButton getRoot() {
        return root;
    }

    private void setRoot(MenuButton root) {
        this.root = requireNonNull(root, "root");
    }

    private boolean isRoot() {
        return rootMode;
    }

    private void setRootMode(boolean rootMode) {
        this.rootMode = rootMode;
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

    /**
     * Enumeration defining the direction modes for sub-menu placement.
     */
    public static enum DirectionMode {
        /** Automatically choose direction based on available space. */
        AUTO,
        
        /** Always place sub-menus to the left. */
        LEFT,
        
        /** Always place sub-menus to the right. */
        RIGHT;
    }

    private static final class ItemDimensions {
        private final Items items;
        private float width, height;

        public ItemDimensions(Items items, float width, float height) {
            super();
            this.items = requireNonNull(items, "items");

            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("Dimensions for items in MenuButton cannot be less than 1");
            }

            this.width = width;
            this.height = height;

            items.recalculateAllRecursive();
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            if (this.width == width) {
                return;
            }

            if (width < 1) {
                throw new IllegalArgumentException("Dimensions for items in MenuButton cannot be less than 1");
            }

            this.width = width;
            items.recalculateAllRecursive();
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            if (this.height == height) {
                return;
            }

            if (height < 1) {
                throw new IllegalArgumentException("Dimensions for items in MenuButton cannot be less than 1");
            }

            this.height = height;
            items.recalculateAllRecursive();
        }

        public void setSize(float width, float height) {
            if (this.width == width && this.height == height) {
                return;
            }

            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("Dimensions for items in MenuButton cannot be less than 1");
            }

            this.width = width;
            this.height = height;
            items.recalculateAllRecursive();
        }
    }

    private static final class Items extends View implements Scrollable {
        private final MenuButton menu;
        private final List<Button> list;
        private final List<ImageBuffer> iconList;
        private final ShadowWrapper shadow;

        public Items(MenuButton menu) {
            super();
            setVisible(true);
            this.menu = menu;
            list = new ArrayList<Button>();
            iconList = new ArrayList<ImageBuffer>();
            shadow = new ShadowWrapper(menu);
        }


        @Override
        public void mouseWheel(MouseEvent mouseEvent) {
            requireNonNull(mouseEvent, "mouseEvent");

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
                Collections.rotate(list, 1);
                Collections.rotate(iconList, 1);
                list.get(0).setY(firstItemY);
            } else {
                Collections.rotate(list, -1);
                Collections.rotate(iconList, -1);
                list.get(list.size() - 1).setY(lastItemY);
            }

            recalculateIconsPositions();
        }

        public void setIcon(PImage icon, String title) {
            iconList.get(list.indexOf(menu.get(title, false))).set(icon);
        }

        public ImageBuffer getIcon(String title) {
            return iconList.get(list.indexOf(menu.get(title, false)));
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

            shadow.recalculatePosition();
            recalculateIconsPositions();
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

            final float newWidth = menu.getItemWidth();
            final float newHeight = menu.getItemHeight();

            for (int i = 0; i < list.size(); i++) {
                list.get(i).setSize(newWidth, newHeight);
            }

            shadow.recalculateDimensions();
            recalculateIconsDimensions();
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

        public void addPlainItemInternal(int... numbers) {
            requireNonNull(numbers, "numbers");

            for (int i = 0; i < numbers.length; i++) {
                addPlainItemInternal(String.valueOf(numbers[i]));
            }
        }

        public void addMenuItemInternal(String... title) {
            checkTitle(title);

            for (int i = 0; i < title.length; i++) {
                addInternal(new MenuButton(title[i]));
            }
        }

        public Button findInternal(String title, boolean recursive) {
            checkTitle(title);

            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                if (b.getText().equals(title)) {
                    return b;
                }
            }

            if (!recursive) {
                return null;
            }

            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                Button finded = null;
                if (b instanceof MenuButton m) {
                    finded = m.find(title);
                    if (finded != null) {
                        return finded;
                    }
                }
            }

            return null;
        }

        public Button findByIdInternal(int id, boolean recursive) {
            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                if (b.getId() == id) {
                    return b;
                }
            }

            if (!recursive) {
                return null;
            }

            for (int i = 0; i < list.size(); i++) {
                Button b = list.get(i);
                Button found = null;
                if (b instanceof MenuButton m) {
                    found = m.items.findByIdInternal(id, recursive);
                    if (found != null) {
                        return found;
                    }
                }
            }

            return null;
        }

        public Button findByTextIdInternal(String textId, boolean recursive) {
            requireNonNull(textId, "textId");

            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                if (b.getTextId().equals(textId)) {
                    return b;
                }
            }

            if (!recursive) {
                return null;
            }

            for (int i = 0; i < list.size(); i++) {
                Button b = list.get(i);
                Button found = null;
                if (b instanceof MenuButton m) {
                    found = m.items.findByTextIdInternal(textId, recursive);
                    if (found != null) {
                        return found;
                    }
                }
            }

            return null;
        }

        public void removeInternal(String... titles) {
            requireNonNull(titles, "titles");

            for (int i = 0; i < titles.length; i++) {
                removeSafe(menu.get(titles[i]));
            }
        }

        public void removeByIdInternal(int... ids) {
            requireNonNull(ids, "ids");

            for (int i = 0; i < ids.length; i++) {
                removeSafe(menu.getById(ids[i]));
            }
        }

        public void removeByTextIdInternal(String... textIds) {
            requireNonNull(textIds, "textIds");

            for (int i = 0; i < textIds.length; i++) {
                removeSafe(menu.getByTextId(textIds[i]));
            }
        }

        @Override
        protected void render() {
            shadow.draw();
            itemsOnDraw();
        }

        private void removeSafe(Button button) {
            final MenuButton m = findMenuWhichContains(button);
            final List<Button> l = m.items.list;
            final int index = l.indexOf(button);
            m.items.iconList.remove(index);
            l.remove(button);

            if (button instanceof MenuButton mb) {
                menu.items.removeMenuFromRenderOrderList(mb);
            }

            recalculatePositionAllRecursive();
            shadow.recalculateDimensions();
            shadow.recalculatePosition();
        }
        
        private void removeMenuFromRenderOrderList(MenuButton menuButton) {
            menu.getRenderOrderList().remove(menuButton);

            for (int i = 0; i < menuButton.items.list.size(); i++) {
                final Button b = menuButton.items.list.get(i);
                if (b instanceof MenuButton m) {
                    m.items.removeMenuFromRenderOrderList(m);
                }
            }
        }

        private void addInternal(Button button) {
            requireNonNull(button, "button");

            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                if (b.getText().equals(button.getText())) {
                    throw new DuplicateItemException("Item with title: \"" + button.getText()
                            + "\" already exists in MenuButton: " + "\"" + menu.getText() + "\"");
                }
            }

            prepareGeneralItemStyle(button);

            if (button instanceof MenuButton m) {
                prepareMenuItem(m);
            } else {
                preparePlainItem(button);
            }

            list.add(button);
            iconList.add(new ImageBuffer());

            recalculateDimensions();
            recalculatePosition();
        }

        private void recalculateIconsPositions() {
            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                iconList.get(i).setPosition(b.getPadX(), b.getPadY());
            }
        }
        
        private void recalculateIconsDimensions() {
            for (int i = 0; i < list.size(); i++) {
                final Button b = list.get(i);
                iconList.get(i).setSize(b.getPaddingLeft(), b.getHeight());
            }
        }

        private void checkTitle(String... titles) {
            requireNonNull(titles, "titles");

            for (int i = 0; i < titles.length; i++) {
                requireNonNull(titles[i], "titles[" + i + "]");

                if (titles[i].isBlank()) {
                    throw new IllegalArgumentException(
                            "Title cannot be blank [Inside MenuButton: " + menu.getText() + "]");
                }
            }
        }

        private static void prepareGeneralItemStyle(Button button) {
            button.setTextAlignX(LEFT);
            button.setPadding(20, 10, 0, 0);
            button.setStrokeColor(Color.TRANSPARENT);
            button.setBackgroundColor(getTheme().getMenuButtonItemColor());
        }

        private void preparePlainItem(Button button) {
            button.onClick(() -> menu.getRoot().setOpen(false));
            button.setTextColor(getTheme().getMenuButtonItemTextColor());
        }

        private void prepareMenuItem(MenuButton menuButton) {
            menuButton.setRootMode(false);
            menuButton.setRoot(menu.getRoot());
            menuButton.setParent(menu);
            menu.getRenderOrderList().add(menuButton);

            final AbstractColor c = getTheme().getMenuButtonItemTextColor();
            final AbstractColor c1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
            menuButton.setTextColor(new LerpedLoopColor(c, c1).setSpeed(.05f));
        }

        private MenuButton findMenuWhichContains(Button button) {
            if (list.contains(button)) {
                return menu;
            }

            for (int i = 0; i < list.size(); i++) {
                Button b = list.get(i);
                MenuButton found = null;

                if (b instanceof MenuButton m) {
                    found = m.items.findMenuWhichContains(button);

                    if (found != null) {
                        return found;
                    }
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
                b.setInteractionHandlerEnabled(shouldReact());
                b.draw();

                if (Debugger.isEnabled()) {
                    if (menu.isActiveSubMenu()) {
                        ctx.pushStyle();
                        ctx.fill(0, 200, 0, 32);
                        ctx.rect(b.getAbsoluteX(), b.getAbsoluteY(), b.getAbsoluteWidth(), b.getAbsoluteHeight());
                        ctx.popStyle();
                    }
                }
            }

            for (int i = 0; i < iconList.size(); i++) {
                final ImageBuffer img = iconList.get(i);
                img.draw();
            }
        }

        private boolean shouldReact() {
            final MenuButton active = menu.getActiveSubMenu();
            final boolean isHasActive = active != null;

            if (!isHasActive) {
                return true;
            }

            if (menu.isActiveSubMenu()) {
                return true;
            }

            if (!menu.isActiveSubMenu() && !active.isHover() && !active.items.isHover()) {
                return true;
            }

            if (menu == active.getParent()) {
                return true;
            }

            return false;
        }

        private static final class ShadowWrapper extends ContentView {
            private final MenuButton menu;

            public ShadowWrapper(MenuButton menu) {
                super();
                setVisible(true);
                setConstrainDimensionsEnabled(false);

                this.menu = menu;
                menu.onClick(() -> {
                    if (getShadow() instanceof ReactiveShadow s) {
                        s.requestUpdateWeights();
                    }
                });
                setShadow(new ReactiveShadow());
            }

            public void recalculatePosition() {
                if (menu.isEmpty()) {
                    return;
                }

                setPosition(menu.items.list.get(0).getAbsoluteX(), menu.items.list.get(0).getAbsoluteY());
            }

            public void recalculateDimensions() {
                if (menu.isEmpty()) {
                    return;
                }

                setSize(menu.items.list.get(0).getAbsoluteWidth(), menu.getItemHeight() * menu.items.list.size());
            }

            
            @Override
            protected void render() {
                getShadow().setVisible(menu.isActiveSubMenu() || (menu.isRoot() && menu.getActiveSubMenu() == null));
            }
        }
    }

    private static class OpenStateIndicator extends SpatialView {
        private final Stroke stroke;

        public OpenStateIndicator(MenuButton menu) {
            setVisible(true);
            stroke = new Stroke();
            stroke.setWeight(2);

            setColor(new LerpedColor(Color.TRANSPARENT,
                    new LerpedLoopColor(Color.GRAY_232L, new Color(0, 0, 232, 64)).setSpeed(.01f),
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
        private static final String CLOSE_SYMBOL = "▶";
        private static final String OPEN_SYMBOL = "▼";
        
        private final MenuButton menu;
        private AbstractColor color;

        public Arrow(MenuButton menu) {
            super();
            setVisible(true);
            this.menu = requireNonNull(menu, "menu");
            setColor(getTheme().getPrimaryColor());
        }

        @Override
        protected void render() {
            if (menu.isRoot()) {
                return;
            }

            color.apply();
            ctx.textAlign(PApplet.CENTER, PApplet.CENTER);
            ctx.textSize(menu.getItemHeight() / 3);
            ctx.text(menu.isOpen() ? OPEN_SYMBOL : CLOSE_SYMBOL, getX(), getY(), getWidth(), getHeight());
        }

        public AbstractColor getColor() {
            return color;
        }

        public void setColor(AbstractColor color) {
            this.color = requireNonNull(color, "color");
        }
    }
}