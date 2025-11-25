package microui;

import microui.component.TextArea;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import processing.core.PApplet;

public class TextAreaLauncher extends PApplet {

	public static void main(String[] args) {
		PApplet.main("microui.TextAreaLauncher");

	}
	
	@Override
	public void settings() {
		size(720,480);
	}
	
	@Override
	public void setup() {
		MicroUI.setContext(this);
		var cm = ContainerManager.getInstance();
		
		TextArea textArea = new TextArea();
		
		cm.add(new Container(new GridLayout(10,10)).add(textArea, new GridLayoutParams(1,1,8,8)));
		
	}
	
	@Override
	public void draw() {
		background(128);
	}
}