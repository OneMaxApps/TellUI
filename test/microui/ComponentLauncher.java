package microui;

import microui.component.MenuButton;
import microui.core.base.ContainerManager;
import microui.core.effect.PlainShadow;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class ComponentLauncher extends PApplet {

	MenuButton mFile;

	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}

	@Override
	public void settings() {
		// fullScreen(); // 1680:1050
		
		size(640,360);
	}

	@Override
	public void setup() {
		MicroUI.setFlexibleRenderModeEnabled(true);
		MicroUI.setContext(this);
		ContainerManager.getInstance();

		mFile = new MenuButton("File", 100, 100, 64, 20);

		mFile.addMenu("New", "Java Project,Maven Project,Project...".split(","));
		mFile.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		mFile.getMenu("New").addMenu("New 1", "1,2,3,4,5,6,7,8".split(","));
		mFile.setShadow(new PlainShadow());
		
	}

	@Override
	public void draw() {
		background(132);
		mFile.draw();

	}

	@Override
	public void keyPressed() {
		if (keyCode == RIGHT) {
			mFile.appendX(10);
		}
		if (keyCode == LEFT) {
			mFile.appendX(-10);
		}
		if (keyCode == UP) {
			mFile.appendY(-10);
		}
		if (keyCode == DOWN) {
			mFile.appendY(10);
		}

	}

	@Override
	public void mouseWheel(MouseEvent event) {
		mFile.mouseWheel(event);
	}

}