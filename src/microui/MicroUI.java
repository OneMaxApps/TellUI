package microui;

import static java.util.Objects.requireNonNull;

import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.util.Debugger;
import processing.core.PApplet;

// Status: STABLE - Do not modify
// Last Reviewed: 04.03.2026

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

	private final ContainerManager containerManager;
	
	private MicroUI(PApplet pApplet) {
		setContext(pApplet);
		
		containerManager = ContainerManager.getInstance();
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
		containerManager.add(container);
		
		return container;
	}
	
	/**
	 * Adds a container with a string identifier to the UI management system.
	 *
	 * @param container the container to add.
	 * @param textId    the string identifier for the container.
	 * @return the added container for chaining.
	 */
	public Container addContainer(Container container, String textId) {
		containerManager.add(container,textId);
		
		return container;
	}
	
	/**
	 * Adds a container with a numeric identifier to the UI management system.
	 *
	 * @param container the container to add.
	 * @param id        the numeric identifier for the container.
	 * @return the added container for chaining.
	 */
	public Container addContainer(Container container, int id) {
		containerManager.add(container,id);
		
		return container;
	}
	
	/**
	 * Removes a container from the UI management system.
	 *
	 * @param container the container to remove.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI removeContainer(Container container) {
		containerManager.remove(container);
		
		return this;
	}
	
	/**
	 * Removes a container identified by its string identifier.
	 *
	 * @param textId the string identifier of the container to remove.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI removeContainer(String textId) {
		containerManager.removeByTextId(textId);
		
		return this;
	}
	
	/**
	 * Removes a container identified by its numeric identifier.
	 *
	 * @param id the numeric identifier of the container to remove.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI removeContainer(int id) {
		containerManager.removeById(id);
		
		return this;
	}
	
	/**
	 * Switches the active container to the specified one.
	 *
	 * @param container the container to activate.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI switchContainer(Container container) {
		containerManager.switchOn(container);
		
		return this;
	}
	
	/**
	 * Switches to the container identified by its string identifier.
	 *
	 * @param textId the string identifier of the container to activate.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI switchContainer(String textId) {
		containerManager.switchOn(textId);
		
		return this;
	}
	
	/**
	 * Switches to the container identified by its numeric identifier.
	 *
	 * @param id the numeric identifier of the container to activate.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI switchContainer(int id) {
		containerManager.switchOn(id);
		
		return this;
	}
	
	/**
	 * Enables or disables the container animator.
	 *
	 * @param enabled true to enable animations, false to disable.
	 * @return this MicroUI instance for chaining.
	 */
	public MicroUI setAnimatorEnabled(boolean enabled) {
		containerManager.setAnimatorEnabled(enabled);
		
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
	
	// == ADVANCED API == //
	
	/**
	 * Returns the container manager instance for advanced container operations.
	 *
	 * @return the container manager.
	 */
	public ContainerManager getContainerManager() {
		return containerManager;
	}

	// == INTERNAL API == //
	private void setContext(PApplet context) {
		requireNonNull(context, "context");

		if (MicroUI.ctx == null) {
			MicroUI.ctx = context;
		}
	}
}