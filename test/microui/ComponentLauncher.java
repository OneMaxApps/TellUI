package microui;

import microui.component.MenuButton;
import microui.component.MenuButton.ItemDimensions;
import microui.core.base.ContainerManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class ComponentLauncher extends PApplet {

	private MenuButton component;

	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}

	@Override
	public void settings() {
		size(800, 480);
//		fullScreen();
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
		
		MicroUI.setFlexibleRenderModeEnabled(true);
		
		ContainerManager.getInstance();
		
		//ThemeManager.setTheme(new ThemeBlack());
		
		component = new MenuButton("File",400,0,64,20);
		
		component.addMenu("New", "Java Project,Maven Project,Project...,Package,Class,Interface,Enum,Record,Annotation".split(","));
		
		component.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		component.add("Close Editor,Close All Editors,Save,Save As...,Save All,Revert File".split(","));
		component.getMenu("New").addMenu("New 1","Java Project,Maven Project,Project...,Package,Class,Interface,Enum,Record,Annotation".split(","));
		
		component.getMenu("New 1").addMenu("New 2","1,2,3,4,5,6,7,8,9,10".split(","));
		
		component.setItemDimensions(new ItemDimensions(100,20));
		
		component.getMenu("New 2").setTooltip("i'm sun-menu item in MenuButton");
		
		component.get("Package").onClick(() -> exit());
		component.get("1").onClick(() -> System.out.println(1));
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