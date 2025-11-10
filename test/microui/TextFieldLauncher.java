package microui;

import microui.component.TextField;
import microui.component.TextView;
import microui.core.TextController.ValidationMode;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.core.style.Color;
import microui.core.style.theme.ThemeBlack;
import microui.core.style.theme.ThemeManager;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import microui.layout.RowLayout;
import microui.layout.RowLayoutParams;
import processing.core.PApplet;

public class TextFieldLauncher extends PApplet {
	private ContainerManager cm;
	
	public static void main(String[] args) {
		PApplet.main("microui.TextFieldLauncher");
	}
	
	@Override
	public void settings() {
//		fullScreen(P2D,0);
//		fullScreen();
		size(800,400);
	}
	
	@Override
	public void setup() {
//		frameRate(2);
		MicroUI.setContext(this);
		cm = ContainerManager.getInstance();
		ThemeManager.setTheme(new ThemeBlack());
		
		cm.add(new Container(new RowLayout()), "main_container");
		
		var container = cm.getByTextId("main_container");
		
		container.add(new Container(new GridLayout(3,3)), new RowLayoutParams(.8f), "view_container");
		container.add(new Container(new GridLayout(1,30)), new RowLayoutParams(.2f), "tools_container");

		Container viewContainer = (Container) container.getByTextId("view_container");
		Container toolsContainer = (Container) container.getByTextId("tools_container");
		
		prepareViewContainer(viewContainer);
		prepareToolsContainer(toolsContainer);
		
	}
	
	@Override
	public void draw() {
		background(164);
	}
	
	private void prepareViewContainer(Container viewContainer) {
		viewContainer.add(new TextField(), new GridLayoutParams(1,1), "text_field");
		
	}
	
	private void prepareToolsContainer(Container c) {
		c.setBackgroundColor(new Color(32));

		c.add(new TextView("Bounds"), new GridLayoutParams(0,0));
		
		TextField tfX,tfY,tfW,tfH;
		
		c.add(tfX = new TextField(), new GridLayoutParams(0,1));
		c.add(tfY = new TextField(), new GridLayoutParams(0,2));
		c.add(tfW = new TextField(), new GridLayoutParams(0,3));
		c.add(tfH = new TextField(), new GridLayoutParams(0,4));
		
		tfX.setMargin(0, 4);
		tfY.setMargin(0, 4);
		tfW.setMargin(0, 4);
		tfH.setMargin(0, 4);
		
		tfX.setTextSize(tfX.getHeight()/2);
		tfY.setTextSize(tfY.getHeight()/2);
		tfW.setTextSize(tfW.getHeight()/2);
		tfH.setTextSize(tfH.getHeight()/2);
		
		tfX.setValidationMode(ValidationMode.ONLY_DIGITS);
		tfY.setValidationMode(ValidationMode.ONLY_DIGITS);
		tfW.setValidationMode(ValidationMode.ONLY_DIGITS);
		tfH.setValidationMode(ValidationMode.ONLY_DIGITS);
		
		tfX.setTextConstrainEnabled(true);
		tfY.setTextConstrainEnabled(true);
		tfW.setTextConstrainEnabled(true);
		tfH.setTextConstrainEnabled(true);
		
		tfX.setMaxChars(4);
		tfY.setMaxChars(4);
		tfW.setMaxChars(4);
		tfH.setMaxChars(4);
		
		tfX.setHint("Pos: X");
		tfY.setHint("Pos: Y");
		tfW.setHint("Width:");
		tfH.setHint("Height:");
		
		tfX.setTooltip("Position X");
		tfY.setTooltip("Position Y");
		tfW.setTooltip("Width");
		tfH.setTooltip("Height");
		
		Container mc = cm.getByTextId("main_container");
		Container vc = (Container) mc.getByTextId("view_container");
		TextField tfView = (TextField) vc.getByTextId("text_field");
		
		tfX.setOnTextChangedListener(() -> {
			tfView.setX(tfX.getDigits());
		});
		
		tfY.setOnTextChangedListener(() -> {
			tfView.setY(tfY.getDigits());
		});
		
		tfW.setOnTextChangedListener(() -> {
			tfView.setWidth(tfW.getDigits());
		});
		
		tfH.setOnTextChangedListener(() -> {
			tfView.setHeight(tfH.getDigits());
		});
		
		
//		tfView.setHint("Example");
//		tfView.setFont(createFont("C:\\Windows\\Fonts\\consola.ttf",24));
		//tfView.setTextConstrainEnabled(true);
		
		c.add(new TextView("Colors"), new GridLayoutParams(0,5));
		c.add(new TextView("Background"), new GridLayoutParams(0,6));
		
		
	}
}