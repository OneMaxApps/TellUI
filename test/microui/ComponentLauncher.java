package microui;

import microui.component.MenuButton;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import processing.core.PApplet;

public class ComponentLauncher extends PApplet {

	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}
	
	@Override
	public void settings() {
		size(640,360,P2D);
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
		var cm = ContainerManager.getInstance();
		
		var container = new Container(new GridLayout(10,20));
		
		var mFile = new MenuButton("File");
		
		mFile.addMenu("New", "Java Project, Maven Project,Project...".split(","));
		mFile.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		
		container.add(mFile, new GridLayoutParams(0,0,1,1,-1,0));
		
		cm.add(container);
	}

	@Override
	public void draw() {
		background(200);
	}
	
}