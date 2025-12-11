package microui.component;

import static java.awt.event.KeyEvent.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.util.MathUtils.constrain;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

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

/**
 * A multi-line text editing component with support for selection, scrolling,
 * copy/paste, undo/redo, and keyboard navigation.
 * 
 * <p>TextArea provides a full-featured text editor with:
 * <ul>
 *   <li>Multi-line text editing with line wrapping</li>
 *   <li>Text selection with mouse and keyboard</li>
 *   <li>Horizontal and vertical scrolling</li>
 *   <li>Copy, cut, and paste operations</li>
 *   <li>Undo and redo functionality</li>
 *   <li>Tab support with configurable tab size</li>
 *   <li>Configurable text styling (font, size, color)</li>
 *   <li>Cursor blinking and visual selection highlighting</li>
 *   <li>Keyboard navigation (arrow keys, home, end, page up/down)</li>
 * </ul></p>
 * 
 * <p>The component uses a buffered rendering approach for efficient text display
 * and includes sophisticated cursor positioning and scrolling behavior.</p>
 */
public final class TextArea extends Component implements KeyPressable, Scrollable {
    /** Minimum size for the text area. */
    private static final int MIN_SIZE = 100;
    
    /** Maximum size for the text area. */
    private static final int MAX_SIZE = 1000;
    
    private final TextEditorModel textEditorModel;
    private final TextMetricsPool textMetricsPool;
    private final TextStyle textStyle;
    private final TabConfig styleConfig;
    private final GraphicsBuffer graphics;
    private final CursorRenderer cursorRenderer;
    private final SelectionRenderer selectionRenderer;
    private final ScrollManager scrollManager;
    private final KeyController keyController;
    private final HandleDraggingConfig handleDraggingConfig;
    private final CursorSearch cursorSearch;
    private final StateConfig stateConfig;
    private Listener onTextChangedListener, onStyleChangedListener;
    
    /**
     * Constructs a TextArea with specified position and dimensions.
     * The text area is initialized with default styling and editing capabilities.
     *
     * @param x the x-coordinate of the text area's top-left corner
     * @param y the y-coordinate of the text area's top-left corner
     * @param width the width of the text area
     * @param height the height of the text area
     */
    public TextArea(float x, float y, float width, float height) {
        super(x, y, width, height);
        setMinMaxSize(MIN_SIZE, MAX_SIZE);
        setBackgroundColor(getTheme().getEditableBackgroundColor());

        textEditorModel = new TextEditorModel();
        textMetricsPool = new TextMetricsPool(this);
        textStyle = new TextStyle(this);
        styleConfig = new TabConfig();
        graphics = new GraphicsBuffer();
        graphics.setAutoClearBufferEnabled(true);
        graphics.addBufferedView(new TextRenderer(this));
        graphics.addBufferedView(cursorRenderer = new CursorRenderer(this));
        graphics.addBufferedView(selectionRenderer = new SelectionRenderer(this));
        scrollManager = new ScrollManager(this);
        keyController = new KeyController(this);
        handleDraggingConfig = new HandleDraggingConfig();
        cursorSearch = new CursorSearch(this);
        stateConfig = new StateConfig();
        
        // Set up text change listener
        textEditorModel.setOnTextChangedListener(() -> {
            textMetricsPool.clearCache();
            scrollManager.recalculateRanges();
            
            if (onTextChangedListener != null) {
                onTextChangedListener.action();
            }
        });

        // Set up style change listener
        textStyle.setOnStyleChangedListener(() -> {
            textMetricsPool.clearCache();
            scrollManager.recalculateRanges();
            
            if (onStyleChangedListener != null) {
                onStyleChangedListener.action();
            }
        });
        
        // Mouse press handler
        onPress(() -> {
            if (ctx.mouseButton == LEFT) {
                setFocused(true);
                setCursorPositionMappedFromMouse();
                
                final TextEditorModel m = textEditorModel;
                final int cr = m.getCursorRow();
                final int cc = m.getCursorColumn();
                textEditorModel.setSelection(cr, cr, cc, cc);
            }
        });

        // Mouse dragging handler
        onDragging(() -> {
            if (ctx.mouseButton == LEFT) {
                scrollOnDragging();
                
                final TextEditorModel m = textEditorModel;
                final int cr = m.getCursorRow();
                final int cc = m.getCursorColumn();
                m.setSelectionEnd(cr, cc);
            }
        });

        // Double-click handler
        onDoubleClick(() -> {
            if (ctx.mouseButton == LEFT) {
                selectWordUnderCursor();
            }
        });
    }

    /**
     * Constructs a TextArea with default size and position.
     * This is a convenience constructor for creating a text area.
     */
    public TextArea() {
        this(0, 0, 1, 1);
    }
    
    /**
     * Sets the color of the scrollbar thumbs.
     *
     * @param color the color for the scrollbar thumbs
     */
    public void setScrollsThumbColor(AbstractColor color) {
        scrollManager.scrollH.setThumbColor(color);
        scrollManager.scrollV.setThumbColor(color);
    }
    
    /**
     * Sets the background color of the scrollbars.
     *
     * @param color the background color for the scrollbars
     */
    public void setScrollsBackgroundColor(AbstractColor color) {
        scrollManager.scrollH.setBackgroundColor(color);
        scrollManager.scrollV.setBackgroundColor(color);
    }
    
    /**
     * Sets the stroke color of the scrollbar thumbs.
     *
     * @param color the stroke color for the scrollbar thumbs
     */
    public void setScrollsThumbStrokeColor(AbstractColor color) {
        scrollManager.scrollH.setThumbStrokeColor(color);
        scrollManager.scrollV.setThumbStrokeColor(color);
    }
    
    /**
     * Sets the listener to be called when the text content changes.
     *
     * @param onTextChangedListener the listener for text changes
     * @throws NullPointerException if the listener is null
     */
    public void setOnTextChangedListener(Listener onTextChangedListener) {
        this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
    }

    /**
     * Sets the listener to be called when the text style changes.
     *
     * @param onStyleChangedListener the listener for style changes
     * @throws NullPointerException if the listener is null
     */
    public void setOnStyleChangedListener(Listener onStyleChangedListener) {
        this.onStyleChangedListener = requireNonNull(onStyleChangedListener, "onStyleChangedListener");
    }

    /**
     * Gets the current tab size (number of spaces per tab).
     *
     * @return the current tab size
     */
    public int getTabSize() {
        return styleConfig.getTabSize();
    }

    /**
     * Sets the tab size (number of spaces per tab).
     * Valid values are between 1 and 8 inclusive.
     *
     * @param tabSize the tab size to set
     * @throws IllegalArgumentException if tabSize is not between 1 and 8
     */
    public void setTabSize(int tabSize) {
        styleConfig.setTabSize(tabSize);
    }

    /**
     * Gets the dragging speed for automatic scrolling when cursor is near edges.
     *
     * @return the current dragging speed
     */
    public float getHandleDraggingSpeed() {
        return handleDraggingConfig.getDraggingSpeed();
    }

    /**
     * Sets the dragging speed for automatic scrolling when cursor is near edges.
     * Minimum speed is 1.
     *
     * @param draggingSpeed the dragging speed to set
     * @throws IllegalArgumentException if draggingSpeed is less than 1
     */
    public void setHandleDraggingSpeed(float draggingSpeed) {
        handleDraggingConfig.setDraggingSpeed(draggingSpeed);
    }

    /**
     * Checks if the text area currently has keyboard focus.
     *
     * @return true if focused, false otherwise
     */
    public boolean isFocused() {
        return stateConfig.isFocused();
    }

    /**
     * Sets whether the text area has keyboard focus.
     *
     * @param focused true to give focus, false to remove focus
     */
    public void setFocused(boolean focused) {
        stateConfig.setFocused(focused);
    }

    /**
     * Checks if the text area is editable.
     *
     * @return true if editable, false if read-only
     */
    public boolean isEditable() {
        return stateConfig.isEditable();
    }

    /**
     * Sets whether the text area is editable.
     *
     * @param editable true to enable editing, false for read-only mode
     */
    public void setEditable(boolean editable) {
        stateConfig.setEditable(editable);
    }

    /**
     * Sets the text content of the text area.
     * Each string in the array becomes a separate line.
     *
     * @param strings the lines of text to set
     */
    public void setText(String... strings) {
        textEditorModel.setText(strings);
    }

    /**
     * Gets the text color.
     *
     * @return the current text color
     */
    public AbstractColor getTextColor() {
        return textStyle.getTextColor();
    }

    /**
     * Sets the text color.
     *
     * @param textColor the color for text rendering
     */
    public void setTextColor(AbstractColor textColor) {
        textStyle.setTextColor(textColor);
    }

    /**
     * Gets the current font.
     *
     * @return the current font, or null if using default
     */
    public PFont getFont() {
        return textStyle.getFont();
    }

    /**
     * Sets the font for text rendering.
     *
     * @param font the font to use for text
     * @throws NullPointerException if font is null
     */
    public void setFont(PFont font) {
        textStyle.setFont(font);
    }

    /**
     * Gets the text size in pixels.
     *
     * @return the current text size
     */
    public int getTextSize() {
        return textStyle.getTextSize();
    }

    /**
     * Sets the text size in pixels.
     * Minimum size is 4 pixels.
     *
     * @param textSize the text size to set
     * @throws IllegalArgumentException if textSize is less than 4
     */
    public void setTextSize(int textSize) {
        textStyle.setTextSize(textSize);
    }

    /**
     * Gets the number of lines in the text area.
     *
     * @return the count of lines
     */
    public int getLinesCount() {
        return textEditorModel.getLineCount();
    }

    /**
     * Gets the cursor color.
     *
     * @return the current cursor color
     */
    public AbstractColor getCursorColor() {
        return cursorRenderer.getColor();
    }

    /**
     * Sets the cursor color.
     *
     * @param color the color for the cursor
     */
    public void setCursorColor(AbstractColor color) {
        cursorRenderer.setColor(color);
    }

    /**
     * Gets the cursor stroke weight (thickness).
     *
     * @return the current cursor weight
     */
    public float getCursorWeight() {
        return cursorRenderer.getWeight();
    }

    /**
     * Sets the cursor stroke weight (thickness).
     * Minimum weight is 1 pixel.
     *
     * @param weight the cursor weight to set
     * @throws IllegalArgumentException if weight is less than 1
     */
    public void setCursorWeight(float weight) {
        cursorRenderer.setWeight(weight);
    }

    /**
     * Gets the selection highlight color.
     *
     * @return the current selection color
     */
    public AbstractColor getSelectionColor() {
        return selectionRenderer.getColor();
    }

    /**
     * Sets the selection highlight color.
     *
     * @param color the color for text selection highlighting
     */
    public void setSelectionColor(AbstractColor color) {
        selectionRenderer.setColor(color);
    }

    /**
     * Handles mouse wheel scrolling.
     * Scrolls the text area vertically when hovered, or scrolls scrollbars when they are hovered.
     *
     * @param mouseEvent the mouse wheel event
     */
    @Override
    public void mouseWheel(MouseEvent mouseEvent) {
        if (!isFocused()) {
            return;
        }

        scrollManager.mouseWheel(mouseEvent);
    }

    /**
     * Handles key press events for text editing and navigation.
     * Only processes keys when the text area is focused.
     *
     * @param e the key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!isFocused()) {
            return;
        }

        keyController.keyInput(e);
    }

    /**
     * Renders the text area, including background, text, cursor, selection, and scrollbars.
     * Also handles focus loss when clicking outside the component.
     */
    @Override
    protected void render() {
        backgroundOnDraw();
        graphics.draw();
        scrollManager.onDraw();

        if (mustLoseFocus()) {
            setFocused(false);
        }
        
        cursorSearch.updateState();
    }

    /**
     * Called when the text area's bounds change.
     * Updates text metrics, scroll ranges, and graphics buffer bounds.
     */
    @Override
    protected void onChangeBounds() {
        textMetricsPool.clearCache();
        scrollManager.recalculateRanges();
        scrollManager.recalculateBounds();

        graphics.setBoundsFrom(this);
    }

    /**
     * Selects the word under the current cursor position.
     * Used for double-click selection behavior.
     */
    private void selectWordUnderCursor() {
        final int row = textEditorModel.getCursorRow();
        final int column = textEditorModel.getCursorColumn();

        final String word = textEditorModel.getLineText(row);

        if (word.isBlank()) {
            return;
        }

        final boolean cursorColumnOnWhitespace = Character
                .isWhitespace(word.charAt((int) constrain(column, 0, word.length() - 1)));

        if (cursorColumnOnWhitespace) {
            return;
        }

        int startOfWord = column;
        int endOfWord = column;

        final int clampedColumnFromCursor = (int) MathUtils.constrain(column, 0, word.length() - 1);

        if (word.charAt(clampedColumnFromCursor) == ' ') {
            textEditorModel.setSelection(row, row, 0, word.length());
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

        textEditorModel.setSelection(row, row, startOfWord, endOfWord);
    }

    /**
     * Sets the cursor position based on current mouse coordinates.
     * Maps mouse position to the nearest character position in the text.
     */
    private void setCursorPositionMappedFromMouse() {
        final TextEditorModel m = textEditorModel;

        final float scrollH = scrollManager.getHorizontalScrollValue();
        final float scrollV = scrollManager.getVerticalScrollValue();

        final float mouseX = (ctx.mouseX + scrollH) - getX();
        final float mouseY = (ctx.mouseY - scrollV) - getY();

        final int nearlyRow = (int) MathUtils.convert(mouseY, 0, getTextSize() * m.getLineCount(), 0,
                m.getLineCount());

        int nearlyColumn = 0;

        for (int i = 0; i <= m.getLineLength(nearlyRow); i++) {
            if (mouseX > textMetricsPool.getTextWidth(nearlyRow, 0, i)) {
                nearlyColumn = i;
            }
        }

        if (m.getCursorRow() == nearlyRow && m.getCursorColumn() == nearlyColumn) {
            return;
        }

        m.setCursorRow(nearlyRow);
        m.setCursorColumn(nearlyColumn);
    }

    /**
     * Handles scrolling behavior during mouse dragging.
     * Updates cursor position and performs soft cursor search.
     */
    private void scrollOnDragging() {
        if (scrollManager.isScrolling()) {
            return;
        }

        setCursorPositionMappedFromMouse();
        cursorSearch.findSoftly();
    }

    /**
     * Determines if the text area should lose focus.
     * Loses focus when mouse is pressed outside the component while not dragging.
     *
     * @return true if focus should be lost, false otherwise
     */
    private boolean mustLoseFocus() {
        return !isDragging() && ctx.mousePressed && !isHover();
    }

    /**
     * Draws the text area background.
     */
    private void backgroundOnDraw() {
        getBackgroundColor().apply();
        ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
    }

    /**
     * Internal class for managing horizontal and vertical scrolling.
     */
    private static final class ScrollManager {
        /** Scrollbar thickness. */
        private static final int WEIGHT = 10;
        
        private final TextArea textArea;
        private final Scroll scrollH, scrollV;
        private boolean scrolling;

        /**
         * Constructs a ScrollManager for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public ScrollManager(TextArea textArea) {
            super();
            this.textArea = requireNonNull(textArea, "textArea");

            scrollH = new Scroll();
            scrollV = new Scroll();

            prepareStyle();
            recalculateBounds();
        }

        /**
         * Draws the scrollbars if they are visible and text area is focused.
         * Terminates soft cursor searching while scrolling.
         */
        public void onDraw() {
            if (textArea.isFocused() && !textArea.textEditorModel.hasSelection()) {
                scrollH.draw();
                scrollV.draw();

                scrolling = scrollH.isDragging() || scrollV.isDragging();

                if (scrolling) {
                    textArea.cursorSearch.terminateSoftlySearching();
                }
            }
        }

        /**
         * Checks if scrolling is currently active.
         *
         * @return true if either scrollbar is being dragged, false otherwise
         */
        public boolean isScrolling() {
            return scrolling;
        }

        /**
         * Gets the current horizontal scroll value.
         *
         * @return the horizontal scroll position
         */
        public float getHorizontalScrollValue() {
            return scrollH.getValue();
        }

        /**
         * Gets the current vertical scroll value.
         *
         * @return the vertical scroll position
         */
        public float getVerticalScrollValue() {
            return scrollV.getValue();
        }

        /**
         * Recalculates scrollbar bounds based on text area dimensions.
         */
        public void recalculateBounds() {
            final TextArea t = textArea;

            scrollH.setSize(t.getWidth() - WEIGHT, WEIGHT);
            scrollH.setPosition(t.getX(), t.getY() + t.getHeight() - WEIGHT);

            scrollV.setSize(WEIGHT, t.getHeight() - WEIGHT);
            scrollV.setPosition(t.getX() + t.getWidth() - WEIGHT, t.getY());
        }

        /**
         * Recalculates scrollbar ranges based on text content dimensions.
         * Hides scrollbars when they are not needed.
         */
        public void recalculateRanges() {
            final float calculatedScrollHMax = textArea.textMetricsPool.getMaxWidth() - textArea.getWidth() * .8f;
            scrollH.setMaxValue(Math.max(scrollH.getMinValue(), calculatedScrollHMax));

            scrollH.setVisible(calculatedScrollHMax > textArea.getWidth());

            final float calculatedScrollVMin = textArea.getHeight() - textArea.textMetricsPool.getTotalHeight();
            scrollV.setMinValue(Math.min(scrollV.getMaxValue(), calculatedScrollVMin));

            scrollV.setVisible(calculatedScrollVMin < scrollV.getMaxValue());
        }

        /**
         * Handles mouse wheel events for scrolling.
         * Scrolls vertically when text area is hovered, scrolls scrollbars when they are hovered.
         *
         * @param mouseEvent the mouse wheel event
         */
        public void mouseWheel(MouseEvent mouseEvent) {
            scrollH.mouseWheel(mouseEvent);
            scrollV.mouseWheel(mouseEvent);

            if (textArea.isHover() && !scrollH.isHover()) {
                textArea.cursorSearch.terminateSoftlySearching();

                final float speed = -mouseEvent.getCount() * textArea.textStyle.getTextSize();
                scrollV.appendValue(speed);
            }
        }

        /**
         * Prepares default scrollbar styling.
         */
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

    /**
     * Internal cache for text width measurements to improve performance.
     */
    private static final class TextMetricsPool {
        /** Graphics context for text measurement. */
        private static final PGraphics GRAPHICS = ctx.createGraphics(1, 1, ctx.sketchRenderer());
        
        private final TextArea textArea;
        private final Map<Long, Float> map = new HashMap<Long, Float>();

        private float maxWidth, totalHeight;

        /**
         * Constructs a TextMetricsPool for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public TextMetricsPool(TextArea textArea) {
            super();
            this.textArea = requireNonNull(textArea, "textArea");
            maxWidth = totalHeight = -1;
        }

        /**
         * Gets the width of a text segment within a line.
         * Results are cached for performance.
         *
         * @param row the line index
         * @param columnStart the starting column
         * @param columnEnd the ending column
         * @return the width of the text segment in pixels
         */
        public float getTextWidth(int row, int columnStart, int columnEnd) {
            final Long key = getGeneratedKey(row, columnStart, columnEnd);

            if (map.containsKey(key)) {
                return map.get(key);
            } else {
                final float newValue = getCalculatedTextWidth(row, columnStart, columnEnd);
                map.put(key, newValue);
                return newValue;
            }
        }

        /**
         * Gets the width of an entire line.
         *
         * @param row the line index
         * @return the width of the entire line in pixels
         */
        public float getTextWidth(int row) {
            return getTextWidth(row, 0, textArea.textEditorModel.getLineLength(row));
        }

        /**
         * Gets the maximum width among all lines.
         *
         * @return the maximum line width in pixels
         */
        public float getMaxWidth() {
            if (maxWidth == -1) {
                final int linesCount = textArea.getLinesCount();

                for (int i = 0; i < linesCount; i++) {
                    maxWidth = Math.max(maxWidth, getCalculatedTextWidth(i));
                }
            }

            return maxWidth;
        }

        /**
         * Gets the total height of all lines.
         *
         * @return the total height in pixels
         */
        public float getTotalHeight() {
            if (totalHeight == -1) {
                totalHeight = textArea.getLinesCount() * textArea.getTextSize();
            }

            return totalHeight;
        }

        /**
         * Clears the text measurement cache.
         * Called when text content or styling changes.
         */
        public void clearCache() {
            map.clear();
            maxWidth = totalHeight = -1;
        }

        /**
         * Calculates the width of a text segment.
         *
         * @param row the line index
         * @param startColumn the starting column
         * @param endColumn the ending column
         * @return the calculated width in pixels
         */
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

        /**
         * Calculates the width of an entire line.
         *
         * @param row the line index
         * @return the calculated line width in pixels
         */
        public float getCalculatedTextWidth(int row) {
            return getCalculatedTextWidth(row, 0, textArea.textEditorModel.getLineLength(row));
        }

        /**
         * Generates a cache key from row and column indices.
         * Uses bit packing to combine three integers into one long.
         *
         * @param row the line index
         * @param startColumn the starting column
         * @param endColumn the ending column
         * @return a unique key for the cache
         */
        private static long getGeneratedKey(int row, int startColumn, int endColumn) {
            return ((long) (row & 0xFFFFF) << 40) | ((long) (startColumn & 0xFFFFF) << 20) | (endColumn & 0xFFFFF);
        }
    }

    /**
     * Internal class for rendering text content.
     */
    private static final class TextRenderer extends BufferedView {
        private final TextArea textArea;

        /**
         * Constructs a TextRenderer for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public TextRenderer(TextArea textArea) {
            super();
            setVisible(true);
            this.textArea = requireNonNull(textArea, "textArea");
        }

        /**
         * Renders text lines with proper positioning and scrolling.
         * Only renders lines that are visible in the current viewport.
         *
         * @param p the graphics context to render to
         */
        @Override
        protected void render(PGraphics p) {
            final int linesCount = textArea.textEditorModel.getLineCount();

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

        /**
         * Checks if a line at the given Y position should be rendered.
         * Optimizes rendering by only drawing lines within the visible area.
         *
         * @param positionY the Y position of the line
         * @return true if the line should be rendered, false otherwise
         */
        private boolean mustBeRendered(float positionY) {
            final float textSize = textArea.getTextSize();
            return positionY > -textSize && positionY < textArea.getHeight();
        }
    }

    /**
     * Internal class for rendering the text cursor.
     */
    private static final class CursorRenderer extends BufferedView {
        /** Minimum cursor weight. */
        private static final byte MIN_WEIGHT = 1;
        
        /** Default cursor weight. */
        private static final byte DEFAULT_WEIGHT = 2;
        
        private final TextArea textArea;
        private final BlinkTimer blinkTimer;
        private AbstractColor color;
        private float weight;

        /**
         * Constructs a CursorRenderer for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public CursorRenderer(TextArea textArea) {
            super();
            setVisible(true);

            this.textArea = requireNonNull(textArea, "textArea");
            blinkTimer = new BlinkTimer();

            setColor(Color.BLACK);
            setWeight(DEFAULT_WEIGHT);
        }

        /**
         * Gets the cursor color.
         *
         * @return the current cursor color
         */
        public AbstractColor getColor() {
            return color;
        }

        /**
         * Sets the cursor color.
         *
         * @param color the cursor color
         * @throws NullPointerException if color is null
         */
        public void setColor(AbstractColor color) {
            this.color = requireNonNull(color, "color");
        }

        /**
         * Gets the cursor stroke weight.
         *
         * @return the current cursor weight
         */
        public float getWeight() {
            return weight;
        }

        /**
         * Sets the cursor stroke weight.
         *
         * @param weight the cursor weight
         * @throws IllegalArgumentException if weight is less than MIN_WEIGHT
         */
        public void setWeight(float weight) {
            if (weight < MIN_WEIGHT) {
                throw new IllegalArgumentException("Cursor weight cannot be less than " + MIN_WEIGHT);
            }
            this.weight = weight;
        }

        /**
         * Renders the cursor if it should be visible.
         * The cursor blinks when inactive and becomes solid during interaction.
         *
         * @param pGraphics the graphics context to render to
         */
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

        /**
         * Calculates the X position of the cursor.
         * Accounts for horizontal scrolling and text width up to the cursor.
         *
         * @return the X coordinate for cursor rendering
         */
        private float getX() {
            final TextEditorModel model = textArea.textEditorModel;

            final int cursorRow = model.getCursorRow();
            final int cursorColumn = model.getCursorColumn();

            final TextMetricsPool tmp = textArea.textMetricsPool;

            final float scrollH = textArea.scrollManager.getHorizontalScrollValue();

            final float posX = tmp.getTextWidth(cursorRow, 0, cursorColumn) - scrollH;

            return posX;
        }

        /**
         * Calculates the Y position of the cursor.
         * Accounts for vertical scrolling and line height.
         *
         * @return the Y coordinate for cursor rendering
         */
        private float getY() {
            final TextEditorModel model = textArea.textEditorModel;

            final int cursorRow = model.getCursorRow();

            final float scrollV = textArea.scrollManager.getVerticalScrollValue();

            final float posY = cursorRow * textArea.getTextSize() + scrollV;

            return posY;
        }

        /**
         * Internal timer for cursor blinking behavior.
         */
        private final class BlinkTimer {
            /** Blink cycle duration in milliseconds. */
            private static short ONE_SECOND_IN_MS = 1000;
            
            /** Time cursor stays visible/invisible during blink cycle. */
            private static short HALF_SECOND_IN_MS = 500;
            
            private long lastBlinkTime, delta;

            /**
             * Constructs a BlinkTimer.
             */
            public BlinkTimer() {
                super();
                lastBlinkTime = System.currentTimeMillis();
            }

            /**
             * Determines if the cursor should blink (be invisible).
             * Resets the timer during user interaction.
             *
             * @return true if the cursor should be invisible, false if visible
             */
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

    /**
     * Internal class for rendering text selection highlights.
     */
    private static final class SelectionRenderer extends BufferedView {
        private final TextArea textArea;
        private AbstractColor color;

        /**
         * Constructs a SelectionRenderer for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public SelectionRenderer(TextArea textArea) {
            super();
            setVisible(true);

            this.textArea = requireNonNull(textArea, "textArea");

            setColor(getTheme().getSelectColor());
        }

        /**
         * Gets the selection highlight color.
         *
         * @return the current selection color
         */
        public AbstractColor getColor() {
            return color;
        }

        /**
         * Sets the selection highlight color.
         *
         * @param color the selection color
         * @throws NullPointerException if color is null
         */
        public void setColor(AbstractColor color) {
            this.color = requireNonNull(color, "color");
        }

        /**
         * Renders text selection highlights.
         * Handles both single-line and multi-line selections.
         *
         * @param pGraphics the graphics context to render to
         */
        @Override
        protected void render(PGraphics pGraphics) {
            final TextEditorModel m = textArea.textEditorModel;
            final TextMetricsPool tmp = textArea.textMetricsPool;

            if (!m.hasSelection()) {
                return;
            }

            pGraphics.pushStyle();
            pGraphics.noStroke();
            color.apply(pGraphics);

            final int esr = m.getSelectionEffectiveStartRow();
            final int eer = m.getSelectionEffectiveEndRow();
            final int esc = m.getSelectionEffectiveStartColumn();
            final int eec = m.getSelectionEffectiveEndColumn();

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

    /**
     * Internal class for managing text styling properties.
     */
    private static final class TextStyle {
        /** Default text size. */
        private static final int DEFAULT_TEXT_SIZE = 12;
        
        /** Minimum text size. */
        private static final int MIN_TEXT_SIZE = 4;
        
        private AbstractColor textColor;
        private PFont font;
        private Listener onTextStyleChangedListener;
        private int textSize;

        /**
         * Constructs a TextStyle with default values.
         */
        public TextStyle(TextArea textArea) {
            super();
            setTextColor(getTheme().getEditableTextColor());
            setTextSize(DEFAULT_TEXT_SIZE);
        }

        /**
         * Applies text style properties to a graphics context.
         *
         * @param pGraphics the graphics context to style
         * @throws NullPointerException if pGraphics is null
         */
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

        /**
         * Gets the text color.
         *
         * @return the current text color
         */
        public AbstractColor getTextColor() {
            return textColor;
        }

        /**
         * Sets the text color.
         *
         * @param textColor the text color
         * @throws NullPointerException if textColor is null
         */
        public void setTextColor(AbstractColor textColor) {
            this.textColor = requireNonNull(textColor, "textColor");
        }

        /**
         * Gets the current font.
         *
         * @return the current font, or null if using default
         */
        public PFont getFont() {
            return font;
        }

        /**
         * Sets the font for text rendering.
         *
         * @param font the font to use
         * @throws NullPointerException if font is null
         */
        public void setFont(PFont font) {
            if (this.font == font) {
                return;
            }

            this.font = requireNonNull(font, "font");
            onTextStyleChanged();
        }

        /**
         * Gets the text size in pixels.
         *
         * @return the current text size
         */
        public int getTextSize() {
            return textSize;
        }

        /**
         * Sets the text size in pixels.
         *
         * @param textSize the text size
         * @throws IllegalArgumentException if textSize is less than MIN_TEXT_SIZE
         */
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

        /**
         * Notifies listeners when text style changes.
         */
        public void onTextStyleChanged() {
            if (onTextStyleChangedListener != null) {
                onTextStyleChangedListener.action();
            }
        }

        /**
         * Sets the listener for style changes.
         *
         * @param onTextSizeChangedListener the style change listener
         */
        public void setOnStyleChangedListener(Listener onTextSizeChangedListener) {
            this.onTextStyleChangedListener = onTextSizeChangedListener;
        }
    }

    /**
     * Internal class for handling keyboard input and navigation.
     */
    private static final class KeyController {
        private final TextArea textArea;
        private final ShiftController shiftController;
        private final ControlController controlController;
        
        /**
         * Constructs a KeyController for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public KeyController(TextArea textArea) {
            super();
            this.textArea = requireNonNull(textArea, "textArea");

            shiftController = new ShiftController(textArea);
            controlController = new ControlController(textArea);
        }
        
        /**
         * Routes key input to appropriate handlers based on modifier keys.
         *
         * @param keyEvent the key event
         * @throws NullPointerException if keyEvent is null
         */
        public void keyInput(KeyEvent keyEvent) {
            requireNonNull(keyEvent, "keyEvent");

            if (keyEvent.isShiftDown()) {
                shiftController.keyInput(keyEvent);
                return;
            }
            
            if (keyEvent.isControlDown()) {
                controlController.keyInput(keyEvent);
                return;
            }

            switch (keyEvent.getKeyCode()) {
            case VK_LEFT:
                onKeyLeftPressed();
                break;
            case VK_RIGHT:
                onKeyRightPressed();
                break;
            case VK_UP:
                onKeyUpPressed();
                break;
            case VK_DOWN:
                onKeyDownPressed();
                break;
            case VK_HOME:
                onKeyHomePressed();
                break;
            case VK_END:
                onKeyEndPressed();
                break;
            case VK_PAGE_UP:
                onKeyPageUpPressed();
                break;
            case VK_PAGE_DOWN:
                onKeyPageDownPressed();
                break;
            case VK_BACK_SPACE:
                onKeyBackspacePressed();
                break;
            case VK_DELETE:
                onKeyDeletePressed();
                break;
            case VK_ENTER:
                onKeyEnterPressed();
                break;
            case VK_TAB:
                onKeyTabPressed();
                break;
            default:
                onPrintableKeyPressed(keyEvent);
                break;
            }
        }

        /**
         * Handles left arrow key press (move cursor left).
         */
        private void onKeyLeftPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            final boolean mustMoveUp = m.cursorAtStartOfLine() && !m.cursorAtStartOfLines();

            if (mustMoveUp) {
                m.moveCursorTo(Direction.UP);
                m.moveCursorToEndOfLine();
                cs.startSoftlySearching();
                return;
            }

            m.moveCursorTo(Direction.LEFT);
            cs.startSoftlySearching();
        }

        /**
         * Handles right arrow key press (move cursor right).
         */
        private void onKeyRightPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            final boolean mustMoveDown = m.cursorAtEndOfLine() && !m.cursorAtEndOfLines();

            if (mustMoveDown) {
                m.moveCursorTo(Direction.DOWN);
                m.moveCursorToStartOfLine();
                cs.startSoftlySearching();
                return;
            }

            m.moveCursorTo(Direction.RIGHT);
            cs.startSoftlySearching();
        }

        /**
         * Handles up arrow key press (move cursor up).
         */
        private void onKeyUpPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            if (!m.cursorAtStartOfLines()) {
                m.moveCursorTo(Direction.UP);
                cs.startSoftlySearching();
            }
        }

        /**
         * Handles down arrow key press (move cursor down).
         */
        private void onKeyDownPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            if (!m.cursorAtEndOfLines()) {
                m.moveCursorTo(Direction.DOWN);
                cs.startSoftlySearching();
            }
        }

        /**
         * Handles home key press (move cursor to start of line).
         */
        private void onKeyHomePressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            if (m.cursorAtStartOfLine()) {
                return;
            }

            m.moveCursorToStartOfLine();
            cs.startSoftlySearching();
        }

        /**
         * Handles end key press (move cursor to end of line).
         */
        private void onKeyEndPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            if (m.cursorAtEndOfLine()) {
                return;
            }

            m.moveCursorToEndOfLine();
            cs.startSoftlySearching();
        }

        /**
         * Handles page up key press (move cursor to start of text).
         */
        private void onKeyPageUpPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            if (!m.cursorAtStartOfText()) {
                m.moveCursorToStartOfText();
                cs.startSoftlySearching();
            }
        }

        /**
         * Handles page down key press (move cursor to end of text).
         */
        private void onKeyPageDownPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.resetSelection();
            }

            if (!m.cursorAtEndOfText()) {
                m.moveCursorToEndOfText();
                cs.startSoftlySearching();
            }
        }

        /**
         * Handles backspace key press (delete character before cursor).
         */
        private void onKeyBackspacePressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;
            
            if (!textArea.isEditable()) {
                return;
            }
            
            if (m.isBlank()) {
                return;
            }
            
            if (m.hasSelection()) {
                m.removeSelectedText();
                cs.startSoftlySearching();
                return;
            }

            if (m.cursorAtStartOfText()) {
                return;
            }

            if (m.cursorAtStartOfLine()) {
                m.moveCursorTo(Direction.UP);
                m.moveCursorToEndOfLine();
                final int prevColumn = m.getCursorColumn();
                m.mergeLines();
                m.setCursorColumn(prevColumn);
                cs.startSoftlySearching();
                return;
            }

            m.moveCursorTo(Direction.LEFT);
            m.removeChar();
            cs.startSoftlySearching();
        }

        /**
         * Handles delete key press (delete character after cursor).
         */
        private void onKeyDeletePressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (!textArea.isEditable()) {
                return;
            }
            
            if (m.isBlank()) {
                return;
            }

            if (m.hasSelection()) {
                m.removeSelectedText();
                cs.startSoftlySearching();
                return;
            }

            if (m.cursorAtEndOfText()) {
                return;
            }

            if (m.cursorAtEndOfLine()) {
                m.mergeLines();
                return;
            }

            m.removeChar();
        }

        /**
         * Handles enter key press (insert new line).
         */
        private void onKeyEnterPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;

            if (!textArea.isEditable()) {
                return;
            }
            
            if (m.hasSelection()) {
                m.removeSelectedText();
            }

            m.splitLine();
            m.moveCursorTo(Direction.DOWN);
            m.moveCursorToStartOfLine();
            cs.startSoftlySearching();
        }

        /**
         * Handles printable character key presses.
         *
         * @param keyEvent the key event containing the character
         */
        private void onPrintableKeyPressed(final KeyEvent keyEvent) {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;
            requireNonNull(keyEvent, "keyEvent");

            if (!textArea.isEditable()) {
                return;
            }
            
            final char ch = keyEvent.getKey();

            if (!CharChecker.isValid(ch)) {
                return;
            }

            if (m.hasSelection()) {
                m.removeSelectedText();
            }

            m.insertChar(ch);
            m.moveCursorTo(Direction.RIGHT);
            cs.startSoftlySearching();
        }
        
        /**
         * Handles tab key press (insert spaces or manage indentation).
         */
        private void onKeyTabPressed() {
            final TextEditorModel m = textArea.textEditorModel;
            final CursorSearch cs = textArea.cursorSearch;
            
            if (!textArea.isEditable()) {
                return;
            }
            
            if (m.hasSelection()) {
                final int cr = m.getCursorRow();
                final int cc = m.getCursorColumn();
                
                // Indent all selected lines
                for (int i = m.getSelectionEffectiveStartRow(); i <= m.getSelectionEffectiveEndRow(); i++) {
                    m.setCursorRow(i);
                    m.setCursorColumn(0);
                    
                    for (int j = 0; j < textArea.getTabSize(); j++) {
                        m.insertChar(' ');
                        m.moveCursorTo(Direction.RIGHT);
                    }
                }
                
                m.setSelectionStartColumn(m.getSelectionStartColumn() + textArea.getTabSize());
                m.setSelectionEndColumn(m.getSelectionEndColumn() + textArea.getTabSize());
                
                m.setCursorRow(cr);
                m.setCursorColumn(cc + textArea.getTabSize());
                
                cs.startSoftlySearching();
                return;
            }
            
            // Insert spaces for tab
            for (int i = 0; i < textArea.getTabSize(); i++) {
                m.insertChar(' ');
                m.moveCursorTo(Direction.RIGHT);
            }
            
            cs.startSoftlySearching();
        }

        /**
         * Internal controller for shift-modified key operations (selection).
         */
        private static final class ShiftController {
            private final TextArea textArea;

            /**
             * Constructs a ShiftController for the text area.
             *
             * @param textArea the parent text area
             * @throws NullPointerException if textArea is null
             */
            public ShiftController(TextArea textArea) {
                super();
                this.textArea = requireNonNull(textArea, "textArea");
            }

            /**
             * Handles shift-modified key input for text selection.
             *
             * @param keyEvent the key event
             * @throws NullPointerException if keyEvent is null
             */
            public void keyInput(KeyEvent keyEvent) {
                requireNonNull(keyEvent, "keyEvent");

                final TextEditorModel m = textArea.textEditorModel;
                
                final int cr = m.getCursorRow();
                final int cc = m.getCursorColumn();

                if (!m.hasSelection()) {
                    m.setSelection(cr, cr, cc, cc);
                }

                switch (keyEvent.getKeyCode()) {
                case VK_LEFT:
                    onKeyLeftPressed();
                    break;
                case VK_RIGHT:
                    onKeyRightPressed();
                    break;
                case VK_UP:
                    onKeyUpPressed();
                    break;
                case VK_DOWN:
                    onKeyDownPressed();
                    break;
                case VK_HOME:
                    onKeyHomePressed();
                    break;
                case VK_END:
                    onKeyEndPressed();
                    break;
                case VK_PAGE_UP:
                    onKeyPageUpPressed();
                    break;
                case VK_PAGE_DOWN:
                    onKeyPageDownPressed();
                    break;
                case VK_TAB:
                    onKeyTabPressed();
                    break;
                case VK_BACK_SPACE:
                    textArea.keyController.onKeyBackspacePressed();
                    break;
                case VK_DELETE:
                    textArea.keyController.onKeyDeletePressed();
                    break;
                case VK_ENTER:
                    textArea.keyController.onKeyEnterPressed();
                    break;
                default:
                    textArea.keyController.onPrintableKeyPressed(keyEvent);
                    break;
                }
            }

            /**
             * Handles shift+left arrow (extend selection left).
             */
            private void onKeyLeftPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                final boolean mustMoveUp = m.cursorAtStartOfLine() && !m.cursorAtStartOfLines();

                if (mustMoveUp) {
                    m.moveCursorTo(Direction.UP);
                    m.moveCursorToEndOfLine();

                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    m.setSelectionEnd(cr, cc);
                    cs.startSoftlySearching();
                    return;
                }

                m.moveCursorTo(Direction.LEFT);
                final int cc = m.getCursorColumn();
                m.setSelectionEndColumn(cc);
                cs.startSoftlySearching();
            }

            /**
             * Handles shift+right arrow (extend selection right).
             */
            private void onKeyRightPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                final boolean mustMoveDown = m.cursorAtEndOfLine() && !m.cursorAtEndOfLines();

                if (mustMoveDown) {
                    m.moveCursorTo(Direction.DOWN);
                    m.moveCursorToStartOfLine();

                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    m.setSelectionEnd(cr, cc);
                    cs.startSoftlySearching();
                    return;
                }

                m.moveCursorTo(Direction.RIGHT);
                final int cc = m.getCursorColumn();
                m.setSelectionEndColumn(cc);
                cs.startSoftlySearching();
            }
        
            /**
             * Handles shift+up arrow (extend selection up).
             */
            private void onKeyUpPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                if (!m.cursorAtStartOfLines()) {
                    m.moveCursorTo(Direction.UP);
                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    m.setSelectionEnd(cr, cc);
                    cs.startSoftlySearching();
                }
            }
            
            /**
             * Handles shift+down arrow (extend selection down).
             */
            private void onKeyDownPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                if (!m.cursorAtEndOfLines()) {
                    m.moveCursorTo(Direction.DOWN);
                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    m.setSelectionEnd(cr, cc);
                    cs.startSoftlySearching();
                }
            }
        
            /**
             * Handles shift+home (extend selection to line start).
             */
            private void onKeyHomePressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                if (m.cursorAtStartOfLine()) {
                    return;
                }

                m.moveCursorToStartOfLine();
                final int cc = m.getCursorColumn();
                m.setSelectionEndColumn(cc);
                cs.startSoftlySearching();
            }
            
            /**
             * Handles shift+end (extend selection to line end).
             */
            private void onKeyEndPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                if (m.cursorAtEndOfLine()) {
                    return;
                }
                
                m.moveCursorToEndOfLine();
                final int cc = m.getCursorColumn();
                m.setSelectionEndColumn(cc);
                cs.startSoftlySearching();
            }
        
            /**
             * Handles shift+page up (extend selection to text start).
             */
            private void onKeyPageUpPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                if (!m.cursorAtStartOfText()) {
                    m.moveCursorToStartOfText();
                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    m.setSelectionEnd(cr, cc);
                    cs.startSoftlySearching();
                }
            }
        
            /**
             * Handles shift+page down (extend selection to text end).
             */
            private void onKeyPageDownPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;

                if (!m.cursorAtEndOfText()) {
                    m.moveCursorToEndOfText();
                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    m.setSelectionEnd(cr, cc);
                    cs.startSoftlySearching();
                }
            }
            
            /**
             * Handles shift+tab (outdent/remove indentation).
             */
            private void onKeyTabPressed() {
                if (!textArea.isEditable()) {
                    return;
                }
                
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;
                final int ts = textArea.getTabSize();
                
                if (m.hasSelection()) {
                    boolean canMakeUntab = true;
                    
                    final int sr = m.getSelectionEffectiveStartRow();
                    final int er = m.getSelectionEffectiveEndRow();
                    
                    // Check if all selected lines have enough leading spaces
                    for (int i = sr; i <= er; i++) {
                        final String text = m.getLineText(i);
                        
                        if (text.length() < ts) {
                            canMakeUntab = false;
                            break;
                        }
                        
                        for (int j = 0; j < ts; j++) {
                            if (text.charAt(j) != ' ') {
                                canMakeUntab = false;
                                break;
                            }
                        }
                    }
                    
                    if (canMakeUntab) {
                        final int cr = m.getCursorRow();
                        final int cc = m.getCursorColumn();
                        
                        // Remove leading spaces from all selected lines
                        for (int i = sr; i <= er; i++) {
                            m.setCursorRow(i);
                            m.setCursorColumn(0);
                            for (int j = 0; j < ts; j++) {
                                m.removeChar();
                            }
                        }
                        
                        m.moveCursorTo(cr, cc - ts);
                        m.setSelectionStartColumn(m.getSelectionStartColumn() - ts);
                        m.setSelectionEndColumn(m.getSelectionEndColumn() - ts);
                    }
                    
                } else {
                    final int cr = m.getCursorRow();
                    final int cc = m.getCursorColumn();
                    final String text = m.getLineText(cr);
                    
                    if (text.length() < ts) {
                        return;
                    }
                    
                    for (int j = 0; j < ts; j++) {
                        if (text.charAt(j) != ' ') {
                            return;
                        }
                    }
                    
                    m.setCursorColumn(0);
                    for (int i = 0; i < ts; i++) {
                        m.removeChar();
                    }
                    m.moveCursorTo(cr, cc - ts);
                }
                
                cs.startSoftlySearching();
            }
        }
        
        /**
         * Internal controller for control-modified key operations (commands).
         */
        private static final class ControlController {
            private final TextArea textArea;
            
            /**
             * Constructs a ControlController for the text area.
             *
             * @param textArea the parent text area
             * @throws NullPointerException if textArea is null
             */
            public ControlController(TextArea textArea) {
                super();
                this.textArea = requireNonNull(textArea, "textArea");
            }
            
            /**
             * Handles control-modified key input for commands.
             *
             * @param keyEvent the key event
             * @throws NullPointerException if keyEvent is null
             */
            public void keyInput(KeyEvent keyEvent) {
                requireNonNull(keyEvent, "keyEvent");
                
                switch (keyEvent.getKeyCode()) {
                case VK_A:
                    onKeyAPressed();
                    break;
                case VK_Z:
                    onKeyZPressed();
                    break;
                case VK_X:
                    onKeyXPressed();
                    break;
                case VK_C:
                    onKeyCPressed();
                    break;
                case VK_V:
                    onKeyVPressed();
                    break;
                case VK_Y:
                    onKeyYPressed();
                    break;
                case VK_MINUS:
                    onKeyMinusPressed();
                    break;
                case VK_EQUALS:
                    onKeyPlusPressed();
                }
            }
            
            /**
             * Handles Ctrl+A (select all).
             */
            private void onKeyAPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                m.selectAll();
            }
            
            /**
             * Handles Ctrl+Z (undo).
             */
            private void onKeyZPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;
                
                if (!textArea.isEditable()) {
                    return;
                }
                
                m.undo();
                cs.find();
            }
            
            /**
             * Handles Ctrl+X (cut).
             */
            private void onKeyXPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                
                if (!textArea.isEditable()) {
                    return;
                }
                
                Clipboard.set(m.getSelectedText());
                m.removeSelectedText();
            }
            
            /**
             * Handles Ctrl+C (copy).
             */
            private void onKeyCPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                Clipboard.set(m.getSelectedText());
            }
            
            /**
             * Handles Ctrl+V (paste).
             */
            private void onKeyVPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;
                
                if (!textArea.isEditable()) {
                    return;
                }
                
                if (m.hasSelection()) {
                    m.removeSelectedText();
                }

                final String[] lines = Clipboard.getAsArray();
                                
                final boolean multiLine = lines.length > 1;

                if (multiLine) {
                    final StringBuilder sb = new StringBuilder();
                    
                    sb.append(m.getTextUntilCursor());
                    for (int i = 0; i < lines.length; i++) {
                        sb.append(lines[i]);
                        if (i != lines.length - 1) {
                            sb.append('\n');
                        }
                    }
                    sb.append(m.getTextAfterCursor());
                    
                    final String[] preparedText = sb.toString().split("\n");
                    
                    final int untilCursorRows = m.getTextUntilCursor().split("\n").length;
                    final int afterCursorRows = m.getTextAfterCursor().split("\n").length;
                    final int pastingTextRows = lines.length;
                    
                    final int rows = untilCursorRows + pastingTextRows + afterCursorRows - 1;

                    int newCursorRow = rows - afterCursorRows;
                    final int newCursorColumn = lines[lines.length - 1].length();

                    if (!m.getLineText(m.getCursorRow()).isEmpty()) {
                        newCursorRow--;
                    }
                    
                    m.setText(preparedText);
                    m.moveCursorTo(newCursorRow, newCursorColumn);
                } else {
                    m.insertString(lines[0]);
                    m.moveCursorTo(Direction.RIGHT, lines[0].length());
                }

                cs.startSoftlySearching();
            }
            
            /**
             * Handles Ctrl+Y (redo).
             */
            private void onKeyYPressed() {
                final TextEditorModel m = textArea.textEditorModel;
                final CursorSearch cs = textArea.cursorSearch;
                
                if (!textArea.isEditable()) {
                    return;
                }
                
                m.redo();
                cs.find();
            }
            
            /**
             * Handles Ctrl+- (decrease text size).
             */
            private void onKeyMinusPressed() {
                final CursorSearch cs = textArea.cursorSearch;
                final int textSize = textArea.getTextSize();

                if (textSize == TextStyle.MIN_TEXT_SIZE) {
                    return;
                }
                
                final int newTextSize = max(TextStyle.MIN_TEXT_SIZE, textSize - 1);
                textArea.setTextSize(newTextSize);
                cs.find();
            }
            
            /**
             * Handles Ctrl+= (increase text size).
             */
            private void onKeyPlusPressed() {
                final CursorSearch cs = textArea.cursorSearch;
                final int textSize = textArea.getTextSize();

                textArea.setTextSize(textSize + 1);
                cs.find();
            }
        }
    }

    /**
     * Internal configuration for dragging/scrolling speed.
     */
    private static final class HandleDraggingConfig {
        /** Default dragging speed. */
        private static final int DEFAULT_DRAGGING_SPEED = 10;
        
        /** Minimum dragging speed. */
        private static final int MIN_DRAGGING_SPEED = 1;
        
        private float draggingSpeed;

        /**
         * Constructs a HandleDraggingConfig with default speed.
         */
        public HandleDraggingConfig() {
            super();
            setDraggingSpeed(DEFAULT_DRAGGING_SPEED);
        }

        /**
         * Gets the dragging speed.
         *
         * @return the current dragging speed
         */
        public float getDraggingSpeed() {
            return draggingSpeed;
        }

        /**
         * Sets the dragging speed.
         *
         * @param draggingSpeed the dragging speed to set
         * @throws IllegalArgumentException if draggingSpeed is less than MIN_DRAGGING_SPEED
         */
        public void setDraggingSpeed(float draggingSpeed) {
            if (draggingSpeed < MIN_DRAGGING_SPEED) {
                throw new IllegalArgumentException("Speed for dragging must be greater than: " + MIN_DRAGGING_SPEED);
            }
            this.draggingSpeed = draggingSpeed;
        }
    }

    /**
     * Internal class for managing cursor positioning and scrolling during interaction.
     */
    private static final class CursorSearch {
        private final TextArea textArea;
        private boolean softlySearching;

        /**
         * Constructs a CursorSearch for the text area.
         *
         * @param textArea the parent text area
         * @throws NullPointerException if textArea is null
         */
        public CursorSearch(TextArea textArea) {
            super();
            this.textArea = requireNonNull(textArea, "textArea");
        }

        /**
         * Starts soft cursor searching (automatic scrolling when cursor is near edges).
         */
        public void startSoftlySearching() {
            softlySearching = true;
        }

        /**
         * Stops soft cursor searching.
         */
        public void terminateSoftlySearching() {
            softlySearching = false;
        }

        /**
         * Updates cursor search state, performing soft search if active.
         */
        public void updateState() {
            if (softlySearching) {
                findSoftly();
            }
        }

        /**
         * Performs soft cursor search - automatically scrolls when cursor is near edges.
         */
        private void findSoftly() {
            final Scroll sH = textArea.scrollManager.scrollH;
            final Scroll sV = textArea.scrollManager.scrollV;

            final float cx = textArea.cursorRenderer.getX();
            final float cy = textArea.cursorRenderer.getY();

            final float draggingSpeed = textArea.handleDraggingConfig.getDraggingSpeed();

            // Scroll left if cursor is in left 20% of visible area
            if (cx < textArea.getWidth() * .2f) {
                final float speed = MathUtils.convert(cx, 0, textArea.getWidth() * .2f, -draggingSpeed, 0);
                sH.appendValue(speed);
            }

            // Scroll right if cursor is in right 20% of visible area
            if (cx > textArea.getWidth() * .8f) {
                final float speed = MathUtils.convert(cx, textArea.getWidth() * .8f, textArea.getWidth(), 0,
                        draggingSpeed);
                sH.appendValue(speed);
            }

            // Scroll up if cursor is in top 20% of visible area
            if (cy < textArea.getHeight() * .2f) {
                final float speed = MathUtils.convert(cy, 0, textArea.getHeight() * .2f, draggingSpeed, 0);
                sV.appendValue(speed);
            }

            // Scroll down if cursor is in bottom 20% of visible area
            if (cy > textArea.getHeight() * .8f) {
                final float speed = MathUtils.convert(cy, textArea.getHeight() * .8f, textArea.getHeight(), 0,
                        -draggingSpeed);
                sV.appendValue(speed);
            }
        }

        /**
         * Centers the viewport on the current cursor position.
         */
        private void find() {
            final TextEditorModel m = textArea.textEditorModel;
            final TextMetricsPool tmp = textArea.textMetricsPool;
            final Scroll scH = textArea.scrollManager.scrollH;
            final Scroll scV = textArea.scrollManager.scrollV;

            final float currentLineTextWidthUntilCursor = tmp.getTextWidth(m.getCursorRow(), 0, m.getCursorColumn());

            scH.setValue(currentLineTextWidthUntilCursor - textArea.getWidth() / 2);
            scV.setValue(-(m.getCursorRow() * textArea.getTextSize() - textArea.getHeight() / 2));
        }
    }

    /**
     * Internal utility for validating printable characters.
     */
    private static final class CharChecker {
        /** String containing all valid special characters. */
        private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*";

        /**
         * Checks if a character is valid for text input.
         *
         * @param ch the character to check
         * @return true if the character is valid, false otherwise
         */
        public static boolean isValid(final char ch) {
            return STANDARD_VALIDATION.indexOf(ch) >= 0 || Character.isLetterOrDigit(ch);
        }
    }
    
    /**
     * Internal configuration for tab behavior.
     */
    private static final class TabConfig {
        /** Minimum tab size. */
        private static final byte MIN_TAB_SIZE = 1;
        
        /** Maximum tab size. */
        private static final byte MAX_TAB_SIZE = 8;
        
        /** Default tab size. */
        private static final byte DEFAULT_TAB_SIZE = 2;
        
        private int tabSize;
        
        /**
         * Constructs a TabConfig with default tab size.
         */
        public TabConfig() {
            super();
            setTabSize(DEFAULT_TAB_SIZE);
        }
        
        /**
         * Gets the current tab size.
         *
         * @return the tab size
         */
        public int getTabSize() {
            return tabSize;
        }

        /**
         * Sets the tab size.
         *
         * @param tabSize the tab size to set
         * @throws IllegalArgumentException if tabSize is not between MIN_TAB_SIZE and MAX_TAB_SIZE
         */
        public void setTabSize(int tabSize) {
            if (tabSize < MIN_TAB_SIZE || tabSize > MAX_TAB_SIZE) {
                throw new IllegalArgumentException("Tab size must be between " + MIN_TAB_SIZE + " and " + MAX_TAB_SIZE);
            }
            
            this.tabSize = tabSize;
        }
    }
    
    /**
     * Internal configuration for text area state.
     */
    private static final class StateConfig {
        private boolean focused, editable;

        /**
         * Constructs a StateConfig with default values.
         */
        public StateConfig() {
            super();
            setFocused(false);
            setEditable(true);
        }

        /**
         * Gets the focus state.
         *
         * @return true if focused, false otherwise
         */
        public boolean isFocused() {
            return focused;
        }

        /**
         * Sets the focus state.
         *
         * @param focused the focus state to set
         */
        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        /**
         * Gets the editable state.
         *
         * @return true if editable, false otherwise
         */
        public boolean isEditable() {
            return editable;
        }

        /**
         * Sets the editable state.
         *
         * @param editable the editable state to set
         */
        public void setEditable(boolean editable) {
            this.editable = editable;
        }
    }
}