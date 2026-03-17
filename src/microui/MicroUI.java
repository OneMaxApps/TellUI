package microui;

import static java.util.Objects.requireNonNull;

import microui.core.base.Container;
import microui.core.base.UIHost;
import microui.core.effect.Transition;
import microui.event.Listener;
import microui.layout.LayoutManager;
import microui.util.Debugger;
import processing.core.PApplet;
import processing.core.PImage;

public final class MicroUI {
	private static MicroUI instance;
	private static PApplet context;

	private static final int MAJOR = 1;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	private static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	private final UIHost uiHost;
	
	
	private MicroUI(PApplet pApplet) {
		requireNonNull(pApplet, "pApplet");
		context = pApplet;
		uiHost = UIHost.getInstance();
	}
	
	
	// == PUBLIC API == //
	public static PApplet getContext() {
		if (context == null) {
			throw new IllegalStateException("Context (PApplet) for MicroUI is not initialized");
		}

		return context;
	}

	public static String getVersion() {
		return VERSION;
	}
	
	public static MicroUI init(PApplet pApplet) {
		if (instance != null) {
			throw new IllegalStateException("MicroUI already initialized");
		}
		
		instance = new MicroUI(pApplet);
		
		return instance;
	}

	public MicroUI setToast(String text, long ms) {
		uiHost.setToast(text, ms);
		return this;
	}

	public MicroUI setToast(String text) {
		uiHost.setToast(text);
		return this;
	}

	public MicroUI setDebugEnabled(boolean enabled) {
		Debugger.setEnabled(enabled);
		
		return this;
	}
	
	
	// == CONTAINER MANAGER API == //
	public Container addContainer(Container container) {
		return uiHost.addContainer(container);
	}
	
	public Container addContainer(LayoutManager layoutManager) {
		return uiHost.addContainer(layoutManager);
	}
	
	public Container addContainer(LayoutManager layoutManager, String textId) {
		return uiHost.addContainer(layoutManager, textId);
	}

	public Container addContainer(LayoutManager layoutManager, int id) {
		return uiHost.addContainer(layoutManager, id);
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
	
	public Container getContainer(String textId) {
		return uiHost.getContainer(textId);
	}
	
	public Container getContainer(int id) {
		return uiHost.getContainer(id);
	}
	
	public Container findContainer(String textId) {
		return uiHost.findContainer(textId);
	}
	
	public Container findContainer(int id) {
		return uiHost.findContainer(id);
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
	
	public MicroUI navigateTo(Container container, int transition) {
		uiHost.navigateTo(container,transition);
		
		return this;
	}
	
	public MicroUI navigateTo(String textId, Transition transition) {
		uiHost.navigateTo(textId,transition);
		
		return this;
	}
	
	public MicroUI navigateTo(String textId, int transition) {
		uiHost.navigateTo(textId,transition);
		
		return this;
	}
	
	public MicroUI navigateTo(int id, Transition transition) {
		uiHost.navigateTo(id,transition);
		
		return this;
	}
	
	public MicroUI navigateTo(int id, int transition) {
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
	
	
	// == SURFACE API == //
	
	public MicroUI setSurfaceIcon(PImage icon) {
		uiHost.setIcon(icon);
		return this;
	}

	public MicroUI setSurfaceTitle(String title) {
		uiHost.setTitle(title);
		return this;
	}

	public MicroUI setSurfaceLocation(int x, int y) {
		uiHost.setLocation(x, y);
		return this;
	}

	public MicroUI setSurfaceResizable(boolean enabled) {
		uiHost.setResizable(enabled);
		return this;
	}

	public MicroUI addOnSurfaceResizeListener(Listener listener) {
		uiHost.addOnSurfaceResizeListener(listener);
		return this;
	}

	public MicroUI removeOnSurfaceResizeListener(Listener listener) {
		uiHost.removeOnSurfaceResizeListener(listener);
		return this;
	}
}