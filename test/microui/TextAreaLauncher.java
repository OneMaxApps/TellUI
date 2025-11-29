package microui;

import microui.component.TextArea;
import microui.component.TextField;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.core.controller.FullSingleLineTextController.ValidationMode;
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
		textArea.setFont(createFont("C:\\Windows\\Fonts\\consola.ttf",32));
		
		var container = new Container(new GridLayout(10,10));
		
		cm.add(container.add(textArea, new GridLayoutParams(1,1,8,8)));
		
		var tfTextSize = new TextField();
		tfTextSize.setMargin(10);
		tfTextSize.setTextSize(12);
		tfTextSize.setValidationMode(ValidationMode.ONLY_DIGITS);
		tfTextSize.setOnTextChangedListener(() -> textArea.setTextSize(constrain(tfTextSize.getDigitsOrDefault(4),4,200)));
		
		container.add(tfTextSize, new GridLayoutParams(0, 0, 2, 1));
	}
	
	@Override
	public void draw() {
		background(128);
		
		if (mouseButton == RIGHT) {
			textArea.setTextSize((int) constrain(map(mouseX,0,width,4,100),4,100));
		}
	}
}