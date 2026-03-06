package microui;

import static java.util.Objects.requireNonNull;

import microui.core.base.Container;
import microui.core.base.UIHost;
import microui.core.effect.Transition;
import microui.layout.LayoutManager;
import microui.util.Debugger;
import processing.core.PApplet;

/**
 * Core initialization and metadata class for the MicroUI library.
 * <p>
 * This singleton-style class provides the foundational setup for MicroUI
 * components by establishing a connection to the Processing sketch context.
 * <b>Must be initialized</b> before using any MicroUI functionality.
 * </p>
 * 
 * @see #setContext(PApplet)
 * @see #getContext()
 */
public final class MicroUI {
	private static MicroUI instance;
	private static PApplet ctx;

	private static final int MAJOR = 1;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	private static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	private final UIHost uiHost;
	
	
	private MicroUI(PApplet pApplet) {
		setContext(pApplet);
		
		uiHost = UIHost.getInstance();
	}
	
	/**
	 * Returns the current version of the MicroUI library.
	 * 
	 * @return the version string in format "MAJOR.MINOR.PATCH"
	 */
	public static String getVersion() {
		return VERSION;
	}
	
	/**
	 * Returns the Processing sketch context for MicroUI.
	 * <p>
	 * This method provides access to the initialized PApplet instance, which is
	 * required by MicroUI components for rendering and Processing API access.
	 * </p>
	 * 
	 * @return the initialized PApplet instance
	 * @throws IllegalStateException if {@link #setContext(PApplet)} has not been
	 *                               called
	 * 
	 * @see #setContext(PApplet)
	 */
	public static PApplet getContext() {
		if (ctx == null) {
			throw new IllegalStateException("Context (PApplet) for MicroUI is not initialized");
		}

		return ctx;
	}
	
	/**
	 * Initializes the MicroUI library with the given Processing sketch context.
	 * <p>
	 * This method must be called once before using any MicroUI functionality.
	 * Subsequent calls return the existing instance.
	 * </p>
	 *
	 * @param pApplet the Processing sketch context, cannot be null.
	 * @return the singleton MicroUI instance.
	 * @throws NullPointerException if pApplet is null.
	 */
	public static MicroUI init(PApplet pApplet) {
		if (instance == null) {
			instance = new MicroUI(pApplet);
		}
		
		return instance;
	}
	
	// == PUBLIC FACADE API == //
	
	/**
	 * Adds a container to the UI management system.
	 *
	 * @param container the container to add.
	 * @return the added container for chaining.
	 */
	public Container addContainer(Container container) {
		uiHost.addContainer(container);
		
		return container;
	}
	
	/**
	 * Creates a new container with the given layout manager and adds it to the UI system.
	 *
	 * @param layoutManager the layout manager for the new container.
	 * @return the newly created container.
	 */
	public Container addContainer(LayoutManager layoutManager) {
		final var c = new Container(layoutManager);
		uiHost.addContainer(c);
		return c;
	}
	
	/**
	 * Creates a new container with the given layout manager and string identifier,
	 * and adds it to the UI system.
	 *
	 * @param layoutManager the layout manager for the new container.
	 * @param textId        the string identifier for the container.
	 * @return the newly created container.
	 */
	public Container addContainer(LayoutManager layoutManager, String textId) {
		final var c = new Container(layoutManager);
		c.setTextId(textId);
		uiHost.addContainer(c);
		return c;
	}

	public Container addContainer(LayoutManager layoutManager, int id) {
		final var c = new Container(layoutManager);
		c.setId(id);
		uiHost.addContainer(c);
		return c;
	}
	
	public MicroUI removeContainer(Container container) {
		uiHost.removeContainer(container);
		
		return this;
	}
	

	public MicroUI removeContainer(String textId) {
		uiHost.removeContainer(textId);
		
		return this;
	}
	
	public MicroUI removeContainer(int id) {
		uiHost.removeContainer(id);
		
		return this;
	}
	
	public MicroUI navigateTo(Container container) {
		uiHost.navigateTo(container);
		
		return this;
	}
	
	public MicroUI navigateTo(String textId) {
		uiHost.navigateTo(textId);
		
		return this;
	}
	
	public MicroUI navigateTo(int id) {
		uiHost.navigateTo(id);
		
		return this;
	}
	
	public MicroUI navigateTo(Container container, Transition transition) {
		uiHost.navigateTo(container,transition);
		
		return this;
	}
	
	public MicroUI navigateTo(String textId, Transition transition) {
		uiHost.navigateTo(textId,transition);
		
		return this;
	}
	
	public MicroUI navigateTo(int id, Transition transition) {
		uiHost.navigateTo(id,transition);
		
		return this;
	}
	
	public boolean isTransitionEnabled() {
		return uiHost.isTransitionEnabled();
	}
	
	public MicroUI setTransitionEnabled(boolean enabled) {
		uiHost.setTransitionEnabled(enabled);
		return this;
	}
	
	/**
	 * Enables or disables debug mode for the UI system.
	 *
	 * @param enabled true to enable debug output, false to disable.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI setDebugEnabled(boolean enabled) {
		Debugger.setEnabled(enabled);
		
		return this;
	}

	// == INTERNAL API == //
	private void setContext(PApplet context) {
		requireNonNull(context, "context");

		if (MicroUI.ctx == null) {
			MicroUI.ctx = context;
		}
	}
}