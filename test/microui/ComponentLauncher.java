package microui;

import static microui.Render.Mode.FLEXIBLE;

import microui.component.MenuButton;
import microui.core.base.ContainerManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class ComponentLauncher extends PApplet {

	MenuButton mFile;

	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}

	@Override
	public void settings() {
//		fullScreen(P2D,0); // 1680:1050
		fullScreen();
//		size(640,360,P2D);
	}

	@Override
	public void setup() {
		Render.setMode(FLEXIBLE);
		MicroUI.setContext(this);
		ContainerManager.getInstance();

		mFile = new MenuButton("File",0,0,100,32);

		mFile.addMenu("menu 0","1,2,3,4,5".split(","));
		mFile.getMenu("menu 0").addMenu("menu new", "1,2,3,4,5".split(","));

		mFile.addMenu("menu 1","1,2,3,4,5".split(","));
		for(int i = 1; i < 10; i++) {
			
			if(i == 9) {
				mFile.getMenu("menu " + i).addMenu("spacial","re");
			} else {
				mFile.getMenu("menu " + i).addMenu("menu "+(i+1),"1,2,3,4,5".split(","));
			}
			
		}
		
		mFile.getMenu("menu 5").setTextId("m");
		
		mFile.removeByTextId("m");
		
	}

	@Override
	public void draw() {
		background(200);
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