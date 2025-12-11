package microui.core;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import microui.core.base.SpatialView;
import processing.core.PGraphics;

/**
 * A container for managing off-screen graphics buffers with composable buffered views.
 * GraphicsBuffer provides a flexible way to render multiple BufferedView components
 * to an off-screen PGraphics buffer before compositing them onto the main drawing surface.
 * 
 * <p>Features include:
 * <ul>
 *   <li>Automatic buffer creation and management</li>
 *   <li>Composition of multiple BufferedView components</li>
 *   <li>Configurable auto-clearing of buffer between frames</li>
 *   <li>Automatic buffer resizing when dimensions change</li>
 *   <li>Optional custom drawing via override method</li>
 * </ul></p>
 * 
 * @see SpatialView
 * @see BufferedView
 * @see PGraphics
 */
public class GraphicsBuffer extends SpatialView {
    private PGraphics graphics;
    private final List<BufferedView> viewList;
    private boolean autoClearBufferEnabled;
    
    /**
     * Constructs a GraphicsBuffer with specified position and dimensions.
     * Creates an initial graphics buffer and sets up default properties.
     *
     * @param x the x-coordinate of the buffer's top-left corner
     * @param y the y-coordinate of the buffer's top-left corner
     * @param width the width of the buffer
     * @param height the height of the buffer
     */
    public GraphicsBuffer(float x, float y, float width, float height) {
        super(x, y, width, height);
        setVisible(true);
        setConstrainDimensionsEnabled(true);
        setMinMaxSize(1, 1, ctx.width, ctx.height);

        viewList = new ArrayList<BufferedView>();
        
        createGraphics();
    }

    /**
     * Constructs a GraphicsBuffer with position and dimensions from a SpatialView.
     *
     * @param spatialView the SpatialView to copy bounds from
     * @throws NullPointerException if spatialView is null
     */
    public GraphicsBuffer(SpatialView spatialView) {
        this(requireNonNull(spatialView, "spatialView").getX(), spatialView.getY(),
                spatialView.getWidth(), spatialView.getHeight());
    }

    /**
     * Constructs a GraphicsBuffer at position (0,0) with zero dimensions.
     * Useful when bounds will be set later.
     */
    public GraphicsBuffer() {
        this(0, 0, 0, 0);
    }

    /**
     * Optional override method for custom drawing directly to the graphics buffer.
     * Called during rendering before any BufferedView components are drawn.
     *
     * @param graphics the graphics buffer to draw to
     */
    public void onDraw(PGraphics graphics) {
        // for overriding - default implementation does nothing
    }
    
    /**
     * Checks if the buffer is automatically cleared before each render cycle.
     *
     * @return true if auto-clear is enabled, false otherwise
     */
    public boolean isAutoClearBufferEnabled() {
        return autoClearBufferEnabled;
    }

    /**
     * Enables or disables automatic clearing of the buffer before each render cycle.
     * When enabled, the buffer is cleared to transparency before rendering.
     *
     * @param autoClearBufferEnabled true to enable auto-clear, false to disable
     */
    public void setAutoClearBufferEnabled(boolean autoClearBufferEnabled) {
        this.autoClearBufferEnabled = autoClearBufferEnabled;
    }

    /**
     * Gets the underlying PGraphics buffer.
     * Useful for direct manipulation of the graphics context.
     *
     * @return the PGraphics buffer
     */
    public final PGraphics getGraphics() {
        return graphics;
    }
    
    /**
     * Adds a BufferedView to be rendered to this graphics buffer.
     * BufferedViews are drawn in the order they are added.
     *
     * @param bufferedView the BufferedView to add
     * @throws NullPointerException if bufferedView is null
     * @throws IllegalArgumentException if bufferedView is already added
     */
    public final void addBufferedView(BufferedView bufferedView) {
        requireNonNull(bufferedView, "bufferedView");
        
        if (viewList.contains(bufferedView)) {
            throw new IllegalArgumentException("BufferedView cannot be added twice");
        }
        
        viewList.add(bufferedView);
    }
    
    /**
     * Removes a BufferedView from this graphics buffer.
     *
     * @param bufferedView the BufferedView to remove
     * @throws NullPointerException if bufferedView is null
     * @throws IllegalArgumentException if bufferedView is not in the list
     */
    public final void removeBufferedView(BufferedView bufferedView) {
        requireNonNull(bufferedView, "bufferedView");
        
        if (!viewList.contains(bufferedView)) {
            throw new IllegalArgumentException("BufferedView not found");
        }
        
        viewList.remove(bufferedView);
    }

    /**
     * Renders the graphics buffer and all its BufferedView components.
     * The buffer is drawn to the main Processing sketch at the buffer's position.
     */
    @Override
    protected void render() {
        if (graphics != null) {
            graphics.beginDraw();
            if (autoClearBufferEnabled) {
                graphics.clear();
            }
            onDraw(graphics);
            listOnDraw(graphics);
            graphics.endDraw();

            ctx.image(graphics, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
        }
    }

    /**
     * Called when the buffer's dimensions change.
     * Recreates the graphics buffer with the new dimensions.
     */
    @Override
    protected void onChangeDimensions() {
        super.onChangeDimensions();
        createGraphics();
    }

    /**
     * Creates or recreates the PGraphics buffer with current dimensions.
     * Disposes of any existing buffer first to prevent memory leaks.
     */
    private void createGraphics() {
        if (graphics != null) {
            graphics.dispose();            
        }
        
        graphics = ctx.createGraphics((int) max(1, getWidth()), (int) max(1, getHeight()), ctx.sketchRenderer());
    }

    /**
     * Draws all BufferedView components to the graphics buffer.
     *
     * @param pGraphics the graphics buffer to draw to
     */
    private void listOnDraw(PGraphics pGraphics) {
        if (viewList.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < viewList.size(); i++) {
            final BufferedView v = viewList.get(i);
            v.draw(pGraphics);
        }
    }
}