package microui;

import microui.component.Button;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.core.effect.SpatialAnimator;
import microui.core.style.theme.ThemeBlack;
import microui.core.style.theme.ThemeManager;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import microui.util.SpatialState;
import processing.core.PApplet;

public class ComponentLauncher extends PApplet {
	
	private Button button;
	
	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}

	@Override
	public void settings() {
		size(640,480);
//		fullScreen();
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
//		MicroUI.setDebugModeEnabled(true);
		ThemeManager.setTheme(new ThemeBlack());
		ContainerManager cm = ContainerManager.getInstance();
		cm.add(new Container(new GridLayout(5,5)).addComponent(button = new Button("INFO"), new GridLayoutParams(2,2,1,1)));
		
		button.setTooltipText("if you can see this, it's means that it's working");
		button.setConstrainDimensionsEnabled(false);
		
		button.setSpatialAnimator(new SpatialAnimator(
				new SpatialState(button),
				new SpatialState(button.getX()-100,button.getY(),300,100), () -> button.isHover()));
	}

	@Override
	public void draw() {
		background(164);
	}
	
}