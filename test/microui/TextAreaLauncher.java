package microui;

import static java.awt.event.KeyEvent.VK_F1;

import microui.component.TextArea;
import microui.core.base.Container;
import microui.core.base.Container.Mode;
import microui.core.base.ContainerManager;
import microui.core.effect.PlainShadow;
import microui.core.style.Color;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import processing.core.PApplet;
import processing.event.KeyEvent;

public class TextAreaLauncher extends PApplet {
	private TextArea textArea;
	
	public static void main(String[] args) {
		PApplet.main("microui.TextAreaLauncher");

	}
	
	@Override
	public void settings() {
//		size(720,480);
		fullScreen(P2D,0);
	}
	
	@Override
	public void setup() {
		MicroUI.setContext(this);
		var cm = ContainerManager.getInstance();
		
		textArea = new TextArea();
		
//		textArea.setText(loadStrings("C:\\Users\\002\\Desktop\\example_of_text.txt"));
		textArea.setText(loadStrings("C:\\Users\\002\\eclipse-workspace\\MicroUI\\src\\microui\\component\\TextArea.java"));
		textArea.setTextSize(24);
		textArea.setFont(createFont("C:\\Windows\\Fonts\\consola.ttf",32));
		textArea.setMargin(20);
		
		textArea.setBackgroundColor(new Color(0,0,200));
		textArea.setTextColor(Color.WHITE);
		textArea.setCursorColor(Color.WHITE);
		textArea.setSelectionColor(new Color(128,128));
		textArea.setShadow(new PlainShadow());
		
		var container = new Container(new GridLayout(1,1));
		container.setMode(Mode.IGNORE_CONSTRAINTS);
		
		cm.add(container.add(textArea, new GridLayoutParams(0,0)));
		
	}
	
	@Override
	public void draw() {
		background(128);
		
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == VK_F1) {
			textArea.setText(loadStrings("C:\\Users\\002\\Desktop\\example_of_text.txt"));
		}
	}
}