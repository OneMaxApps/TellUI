package tellui;

import static java.util.Objects.requireNonNull;

import processing.core.PApplet;
import processing.core.PImage;
import tellui.core.base.Container;
import tellui.core.base.UIHost;
import tellui.core.effect.Transition;
import tellui.core.interfaces.Listener;
import tellui.layout.LayoutManager;
import tellui.util.Debugger;

/**
 * Core facade for the TellUI library.
 * <p>
 * This class provides a simplified API for initializing and controlling the UI system.
 * It follows the singleton pattern and must be initialized via {@link #init(PApplet)}
 * before any other methods are called.
 * </p>
 */
public final class TellUI {
	private static TellUI instance;
	private static PApplet context;

	private static final int MAJOR = 1;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	private static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	private final UIHost uiHost;
	
	
	private TellUI(PApplet pApplet) {
		requireNonNull(pApplet, "pApplet");
		context = pApplet;
		uiHost = UIHost.getInstance();
	}
	
	
	// == PUBLIC API == //

	/**
	 * Returns the Processing sketch context used by TellUI.
	 *
	 * @return the PApplet instance
	 * @throws IllegalStateException if TellUI has not been initialized
	 *                               (i.e. {@link #init(PApplet)} was not called)
	 */
	public static PApplet getContext() {
		if (context == null) {
			throw new IllegalStateException("Context (PApplet) for TellUI is not initialized");
		}

		return context;
	}

	/**
	 * Returns the version of the TellUI library.
	 *
	 * @return the version string in the format "MAJOR.MINOR.PATCH"
	 */
	public static String getVersion() {
		return VERSION;
	}
	
	/**
	 * Initializes the TellUI library with the given Processing sketch context.
	 * <p>
	 * This method must be called exactly once before using any other functionality.
	 * Subsequent calls will throw an exception.
	 * </p>
	 *
	 * @param pApplet the Processing sketch (must not be {@code null})
	 * @return the singleton TellUI instance
	 * @throws NullPointerException     if {@code pApplet} is {@code null}
	 * @throws IllegalStateException    if TellUI has already been initialized
	 */
	public static TellUI init(PApplet pApplet) {
		if (instance != null) {
			throw new IllegalStateException("TellUI already initialized");
		}
		
		instance = new TellUI(pApplet);
		
		return instance;
	}

	/**
	 * Shows a toast message for the specified duration.
	 *
	 * @param text the toast message (must not be {@code null} or blank)
	 * @param ms   the duration in milliseconds (must be positive)
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setToast(String text, long ms) {
		uiHost.setToast(text, ms);
		return this;
	}

	/**
	 * Shows a toast message for the default duration (1000 ms).
	 *
	 * @param text the toast message (must not be {@code null} or blank)
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setToast(String text) {
		uiHost.setToast(text);
		return this;
	}

	/**
	 * Enables or disables debug output for the UI system.
	 *
	 * @param enabled {@code true} to enable debug, {@code false} to disable
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setDebugEnabled(boolean enabled) {
		Debugger.setEnabled(enabled);
		
		return this;
	}
	
	
	// == CONTAINER MANAGER API == //

	/**
	 * Adds a container to the UI system.
	 *
	 * @param container the container to add (must not be {@code null})
	 * @return the added container
	 */
	public Container addContainer(Container container) {
		return uiHost.addContainer(container);
	}
	
	/**
	 * Creates and adds a new container with the specified layout manager.
	 *
	 * @param layoutManager the layout manager for the new container (must not be {@code null})
	 * @return the newly created container
	 */
	public Container addContainer(LayoutManager layoutManager) {
		return uiHost.addContainer(layoutManager);
	}
	
	/**
	 * Creates and adds a new container with the specified layout manager and text identifier.
	 *
	 * @param layoutManager the layout manager for the new container (must not be {@code null})
	 * @param textId        the text identifier for the container
	 * @return the newly created container with the given {@code textId}
	 */
	public Container addContainer(LayoutManager layoutManager, String textId) {
		return uiHost.addContainer(layoutManager, textId);
	}

	/**
	 * Creates and adds a new container with the specified layout manager and numeric identifier.
	 *
	 * @param layoutManager the layout manager for the new container (must not be {@code null})
	 * @param id            the numeric identifier for the container
	 * @return the newly created container with the given {@code id}
	 */
	public Container addContainer(LayoutManager layoutManager, int id) {
		return uiHost.addContainer(layoutManager, id);
	}
	
	/**
	 * Removes the specified container from the UI system.
	 *
	 * @param container the container to remove (must not be {@code null})
	 */
	public void removeContainer(Container container) {
		uiHost.removeContainer(container);
	}
	
	/**
	 * Removes the container with the specified text identifier.
	 *
	 * @param textId the text identifier of the container to remove (must not be {@code null})
	 */
	public void removeContainer(String textId) {
		uiHost.removeContainer(textId);
	}
	
	/**
	 * Removes the container with the specified numeric identifier.
	 *
	 * @param id the numeric identifier of the container to remove
	 */
	public void removeContainer(int id) {
		uiHost.removeContainer(id);
	}
	
	/**
	 * Retrieves the container with the specified text identifier.
	 *
	 * @param textId the text identifier of the container (must not be {@code null} or blank)
	 * @return the container with the given {@code textId}
	 */
	public Container getContainer(String textId) {
		return uiHost.getContainer(textId);
	}
	
	/**
	 * Retrieves the container with the specified numeric identifier.
	 *
	 * @param id the numeric identifier of the container
	 * @return the container with the given {@code id}
	 */
	public Container getContainer(int id) {
		return uiHost.getContainer(id);
	}
	
	/**
	 * Finds a container by its text identifier. Returns {@code null} if not found.
	 *
	 * @param textId the text identifier of the container
	 * @return the container with the given {@code textId}, or {@code null} if none found
	 */
	public Container findContainer(String textId) {
		return uiHost.findContainer(textId);
	}
	
	/**
	 * Finds a container by its numeric identifier. Returns {@code null} if not found.
	 *
	 * @param id the numeric identifier of the container
	 * @return the container with the given {@code id}, or {@code null} if none found
	 */
	public Container findContainer(int id) {
		return uiHost.findContainer(id);
	}
	
	/**
	 * Navigates to the specified container with the default transition.
	 *
	 * @param container the target container (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(Container container) {
		uiHost.navigateTo(container);
		
		return this;
	}
	
	/**
	 * Navigates to the container with the specified text identifier using the default transition.
	 *
	 * @param textId the text identifier of the target container (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(String textId) {
		uiHost.navigateTo(textId);
		
		return this;
	}
	
	/**
	 * Navigates to the container with the specified numeric identifier using the default transition.
	 *
	 * @param id the numeric identifier of the target container
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(int id) {
		uiHost.navigateTo(id);
		
		return this;
	}
	
	/**
	 * Navigates to the specified container using the given transition.
	 *
	 * @param container  the target container (must not be {@code null})
	 * @param transition the transition effect to use (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(Container container, Transition transition) {
		uiHost.navigateTo(container,transition);
		
		return this;
	}
	
	/**
	 * Navigates to the specified container using a predefined transition identified by a constant.
	 * The constant must be one of {@code LEFT}, {@code RIGHT}, {@code UP}, or {@code DOWN}
	 * from {@link processing.core.PConstants}.
	 *
	 * @param container  the target container (must not be {@code null})
	 * @param transition the transition constant (LEFT, RIGHT, UP, DOWN)
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(Container container, int transition) {
		uiHost.navigateTo(container,transition);
		
		return this;
	}
	
	/**
	 * Navigates to the container with the specified text identifier using the given transition.
	 *
	 * @param textId the text identifier of the target container (must not be {@code null})
	 * @param transition the transition effect to use (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(String textId, Transition transition) {
		uiHost.navigateTo(textId,transition);
		
		return this;
	}
	
	/**
	 * Navigates to the container with the specified text identifier using a predefined transition constant.
	 *
	 * @param textId the text identifier of the target container (must not be {@code null})
	 * @param transition the transition constant (LEFT, RIGHT, UP, DOWN)
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(String textId, int transition) {
		uiHost.navigateTo(textId,transition);
		
		return this;
	}
	
	/**
	 * Navigates to the container with the specified numeric identifier using the given transition.
	 *
	 * @param id the numeric identifier of the target container
	 * @param transition the transition effect to use (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(int id, Transition transition) {
		uiHost.navigateTo(id,transition);
		
		return this;
	}
	
	/**
	 * Navigates to the container with the specified numeric identifier using a predefined transition constant.
	 *
	 * @param id the numeric identifier of the target container
	 * @param transition the transition constant (LEFT, RIGHT, UP, DOWN)
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI navigateTo(int id, int transition) {
		uiHost.navigateTo(id,transition);
		
		return this;
	}
	
	/**
	 * Returns whether transitions are enabled globally.
	 *
	 * @return {@code true} if transitions are enabled, {@code false} otherwise
	 */
	public boolean isTransitionEnabled() {
		return uiHost.isTransitionEnabled();
	}
	
	/**
	 * Enables or disables transitions globally.
	 *
	 * @param enabled {@code true} to enable transitions, {@code false} to disable
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setTransitionEnabled(boolean enabled) {
		uiHost.setTransitionEnabled(enabled);
		return this;
	}
	
	
	// == SURFACE API == //
	
	/**
	 * Sets the icon for the application surface (window).
	 *
	 * @param icon the icon image (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setSurfaceIcon(PImage icon) {
		uiHost.setIcon(icon);
		return this;
	}

	/**
	 * Sets the title of the application surface (window).
	 *
	 * @param title the window title (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setSurfaceTitle(String title) {
		uiHost.setTitle(title);
		return this;
	}

	/**
	 * Sets the location of the application surface on the screen.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setSurfaceLocation(int x, int y) {
		uiHost.setLocation(x, y);
		return this;
	}

	/**
	 * Sets whether the application surface is resizable by the user.
	 *
	 * @param enabled {@code true} to allow resizing, {@code false} to disallow
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI setSurfaceResizable(boolean enabled) {
		uiHost.setResizable(enabled);
		return this;
	}

	/**
	 * Adds a listener to be notified when the surface is resized.
	 *
	 * @param listener the listener to add (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI addOnSurfaceResizeListener(Listener listener) {
		uiHost.addOnSurfaceResizeListener(listener);
		return this;
	}

	/**
	 * Removes a previously added surface resize listener.
	 *
	 * @param listener the listener to remove (must not be {@code null})
	 * @return this TellUI instance (for chaining)
	 */
	public TellUI removeOnSurfaceResizeListener(Listener listener) {
		uiHost.removeOnSurfaceResizeListener(listener);
		return this;
	}
}