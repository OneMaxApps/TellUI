package microui;

import microui.component.MenuButton;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class ComponentLauncher extends PApplet {

	private MenuButton component;

	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}

	@Override
	public void settings() {
		size(640, 480);
//		fullScreen();
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
		MicroUI.setFlexibleRenderModeEnabled(true);
		
		component = new MenuButton("File",0,0,100,24);
		
		component.addMenu("New", "Java Project,Maven Project,Project...,Package,Class,Interface,Enum,Record,Annotation".split(","));
		
		component.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		component.add("Close Editor,Close All Editors,Save,Save As...,Save All,Revert File".split(","));
		
		component.setTextId("mb");
	}
	
	@Override
	public void draw() {
		background(164);
		
		component.draw();
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		super.mouseWheel(event);
		component.mouseWheel(event);
	}

	
	
}