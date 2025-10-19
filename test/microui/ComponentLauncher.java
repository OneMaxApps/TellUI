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
		fullScreen(); // 1680:1050
	}

	@Override
	public void setup() {
		System.out.println(width+":"+height);
		MicroUI.setContext(this);
		var cm = ContainerManager.getInstance();
		
		var container = new Container(new GridLayout(10,20));
		
		var mFile = new MenuButton("File");
		
		mFile.addMenu("New", "Java Project,Maven Project,Project...".split(","));
		mFile.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		mFile.getMenu("New").addMenu("New 1", "1,2,3,4,5,6,7,8".split(","));

		for(int i = 1; i <= 10; i++) {
			mFile.getMenu("New "+i).addMenu("New "+ (int) (i+1), "1,2,3,4,5,6,7,8".split(","));
		}
		
		container.add(mFile, new GridLayoutParams(0,0,1,1,-1,-1));
		
		cm.add(container);
	}

	@Override
	public void draw() {
		background(200);
		
	}
	
}