package microui;

import microui.component.MenuButton;
import microui.core.base.ContainerManager;
import processing.core.PApplet;

public class ComponentLauncher extends PApplet {

	
	MenuButton mFile;
	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}
	
	@Override
	public void settings() {
		fullScreen(); // 1680:1050
	}

	@Override
	public void setup() {
		MicroUI.setFlexibleRenderModeEnabled(true);
		
		MicroUI.setContext(this);
		ContainerManager.getInstance();
		
		mFile = new MenuButton("File");
		
		mFile.addMenu("New", "Java Project,Maven Project,Project...".split(","));
		mFile.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		mFile.getMenu("New").addMenu("New 1", "1,2,3,4,5,6,7,8".split(","));

		for(int i = 1; i <= 100; i++) {
			mFile.getMenu("New "+i).addMenu("New "+ (int) (i+1), "1,2,3,4,5".split(","));
		}

	}
	
	@Override
	public void draw() {
		background(200);
		mFile.draw();
	}
	
}