package microui;

import microui.component.TextArea;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import processing.core.PApplet;

public class TextAreaLauncher extends PApplet {
	private TextArea textArea;
	
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
		
		textArea = new TextArea();
		textArea.setText(loadStrings("C:\\Users\\002\\Desktop\\example_of_text.txt"));
		textArea.setTextSize(24);
		
		cm.add(new Container(new GridLayout(10,10)).add(textArea, new GridLayoutParams(1,1,8,8)));
		
	}
	
	@Override
	public void draw() {
		background(128);
		
		if (mouseButton == RIGHT) {
			textArea.setTextSize((int) constrain(map(mouseX,0,width,4,100),4,100));
		}
	}
}