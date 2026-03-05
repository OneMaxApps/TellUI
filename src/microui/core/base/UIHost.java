package microui.core.base;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import microui.MicroUI;
import microui.core.exception.DuplicateItemException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.event.PointerManager;
import microui.layout.LayoutManager;
import microui.service.TooltipManager;
import microui.service.ValueOverlayManager;
import microui.util.Debugger;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public final class UIHost extends View {
	private static UIHost instance;
	private final ContainerManager containerManager;
	private final TooltipManager tooltipManager;
	private final ValueOverlayManager valueOverlayManager;
	
	private UIHost() {
		setVisible(true);
		
		containerManager = new ContainerManager();
		tooltipManager = TooltipManager.getInstance();
		valueOverlayManager = ValueOverlayManager.getInstance();
		
		new Renderer(this);
	}	

	public static UIHost getInstance() {
		if (instance == null) {
			instance = new UIHost();
		}

		return instance;
	}
	
	public void addContainer(Container container) {
		containerManager.add(container);
	}
	
	public void addContainer(LayoutManager layoutManager) {
		containerManager.add(new Container(layoutManager));
	}
	
	public void addContainer(LayoutManager layoutManager, String textId) {
		containerManager.add((Container) new Container(layoutManager).setTextId(textId));
	}
	
	public void addContainer(LayoutManager layoutManager, int id) {
		containerManager.add((Container) new Container(layoutManager).setId(id));
	}

	public void removeContainer(Container container) {
		containerManager.remove(container);
	}
	
	public void removeContainer(String textId) {
		containerManager.remove(containerManager.get(textId));
	}
	
	public void removeContainer(int id) {
		containerManager.remove(containerManager.get(id));
	}
	
	public void navigateTo(Container container) {
		containerManager.navigateTo(container);
	}
	
	public void navigateTo(String textId) {
		containerManager.navigateTo(containerManager.get(textId));
	}
	
	public void navigateTo(int id) {
		containerManager.navigateTo(containerManager.get(id));
	}

	@Override
	protected void render() {
		containerManager.draw();
		tooltipManager.draw();
		valueOverlayManager.draw();
	}
	
	public static final class Renderer {
		private final UIHost uiHost;
		
		private Renderer(UIHost uiHost) {
			this.uiHost = uiHost;
			
			MicroUI.getContext().registerMethod("draw", this);
		}
		
		public void draw() {
			uiHost.draw();
			
			if (!ctx.mousePressed) {
				PointerManager.release();
			}
		}
		
	}
	
	public static final class ContainerManager extends View implements Scrollable, KeyPressable {
		private final List<Container> list;
		private Container current, previous;
		
		private ContainerManager() {
			setVisible(true);
			list = new ArrayList<Container>();
			ctx.registerMethod("keyEvent", this);
			ctx.registerMethod("mouseEvent", this);
		}
		
		@Override
		protected void render() {
			if (current != null) {
				current.draw();
			}
		}
		
		public void add(Container container) {
			addInternal(container);
		}
		
		public void remove(Container container) {
			removeInternal(container);
		}
		
		public Container find(String textId) {
			Objects.requireNonNull(textId,"textId");
			
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getTextId().equals(textId)) {
					return c;
				}
			}
			
			return null;
		}
		
		public Container find(int id) {
			for(int i = 0; i < list.size(); i++) {
				final var c = list.get(i);
				if(c.getId() == id) {
					return c;
				}
			}
			
			return null;
		}
		
		public Container get(String textId) {
			final var c = find(textId);
			
			if(c == null) {
				throw new NoSuchElementException("Container with text id: " + textId +" not found");
			}
			
			return c;
		}
		
		public Container get(int id) {
			final var c = find(id);
			
			if(c == null) {
				throw new NoSuchElementException("Container with id: " + id +" not found");
			}
			
			return c;
		}
		
		public void navigateTo(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (!list.contains(container)) {
				throw new NoSuchElementException("Container not found");
			}
			
			previous = current;
			current = container;
		}
		
		@Override
		public void keyPressed(KeyEvent keyEvent) {
			if (current != null) {
				current.keyPressed(keyEvent);
			}
		}
		
		@Override
		public void mouseWheel(MouseEvent mouseEvent) {
			if (current != null) {
				current.mouseWheel(mouseEvent);
			}
		}
		
		public void keyEvent(KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.PRESS) {

				if (Debugger.isHotKeyEnabled()) {
					if (keyEvent.isAltDown()) {
						Debugger.setEnabled(!Debugger.isEnabled());
					}
				}
				
				keyPressed(keyEvent);
			}
		}
		
		public void mouseEvent(MouseEvent mouseEvent) {
			if (mouseEvent.getAction() == MouseEvent.WHEEL) {
				mouseWheel(mouseEvent);
			}
		}
		
		private void addInternal(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (list.contains(container)) {
				throw new DuplicateItemException("Container already added");
			}
			
			if (current == null) {
				current = container;
			} else {
				previous = current;
			}
			
			list.add(container);
		}
		
		private void removeInternal(Container container) {
			Objects.requireNonNull(container,"container");
			
			if (!list.contains(container)) {
				throw new NoSuchElementException("Container not found");
			}
			
			if (current == container) {
				current = null;
			}
			
			if (previous == container) {
				previous = null;
			}
			
			list.remove(container);
		}
	}
}