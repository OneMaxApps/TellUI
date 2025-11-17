package microui;

import microui.component.CheckBox;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.core.style.Color;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import processing.core.PApplet;

public class CheckBoxLauncher extends PApplet {

	public static void main(String[] args) {
		PApplet.main("microui.CheckBoxLauncher");
	}
	
	@Override
	public void settings() {
		size(720,360);
	}
	
	@Override
	public void setup() {
		MicroUI.setContext(this);
		var cm = ContainerManager.getInstance();
		final int COLUMNS = 20;
		final int ROWS = 20;
		
		Container c = new Container(new GridLayout(COLUMNS,ROWS));
		cm.add(c);
		
		for (int i = 0; i < COLUMNS; i++) {
			for (int j = 0; j < ROWS; j++) {
				c.add(new CheckBox(), new GridLayoutParams(i,j));
			}
		}
		
		c.setBackgroundColor(Color.GRAY_200L);
	}
	
	@Override
	public void draw() {
		System.out.println(frameRate);
	}
}