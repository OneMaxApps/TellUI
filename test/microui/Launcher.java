package microui;

import microui.component.Button;
import microui.component.CheckBox;
import microui.component.EditText;
import microui.component.Knob;
import microui.component.LabeledCheckBox;
import microui.component.MenuButton;
import microui.component.Scroll;
import microui.component.Slider;
import microui.component.TextField;
import microui.component.TextView;
import microui.constants.ContainerMode;
import microui.core.base.Component;
import microui.core.base.Container;
import microui.core.base.ContainerManager;
import microui.layout.ColumnLayout;
import microui.layout.ColumnLayoutParams;
import microui.layout.GridLayout;
import microui.layout.GridLayoutParams;
import processing.core.PApplet;

// NOTE: ///////////////////////////////////////////////////
// getX,Y,Width,Height = content area;
// getPadX,Y,Width,Height = padded area;
// getAbsoluteX,Y,Width,Height = margin area;
////////////////////////////////////////////////////////////

public final class Launcher extends PApplet {
	ContainerManager cm;
	Button button;

	public static void main(String[] args) {
		PApplet.main("microui.Launcher");
	}

	@Override
	public void settings() {
		fullScreen();
//		size(720, 480);
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
//		MicroUI.setDebugModeEnabled(true);
		// ThemeManager.setTheme(new ThemeGlass());

		cm = ContainerManager.getInstance();

		cm.add(getContainerMain(), "main");
		cm.add(getContainerAllComponents(), "all_components");
		cm.add(getContainerWith(button = new Button()), "Button");
		cm.add(getContainerWith(new CheckBox()), "CheckBox");
		cm.add(getContainerWith(new EditText()), "EditText");
		cm.add(getContainerWith(new Knob()), "Knob");
		cm.add(getContainerWith(new LabeledCheckBox()), "LabeledCheckBox");
		cm.add(getContainerWith(new MenuButton()), "MenuButton");
		cm.add(getContainerWith(new Scroll()), "Scroll");
		cm.add(getContainerWith(new Slider()), "Slider");
		cm.add(getContainerWith(new TextField()), "TextField");
		cm.add(getContainerWith(new TextView()), "TextView");

	}

	@Override
	public void draw() {
		background(100);

		// cm.getContainerByTextId("container_main").getComponentByTextId("edit_text").setSize(mouseX,mouseY);
		//Metrics.printAll();
	}

	@Override
	public void keyPressed() {
		super.keyPressed();

		if (keyPressed) {
			if (keyCode == 0x70) {
				cm.switchOn("main");
			}

			if (keyCode == TAB) {
				cm.switchOnNextContainer();
			}
		}

	}

	private Container getContainerMain() {
		Container container = new Container(new GridLayout(3, 3));

		Container ContainerMenuItem = new Container(new ColumnLayout());
		ContainerMenuItem.setContainerMode(ContainerMode.IGNORE_CONSTRAINTS);

		ContainerMenuItem.add(new Button("show all components"), new ColumnLayoutParams(.2f), () -> cm.switchOn("all_components"));

		MenuButton menuButtonOfComponents;
		ContainerMenuItem.add(
				menuButtonOfComponents = new MenuButton("show component", "Button", "CheckBox", "EditText", "Knob",
						"LabeledCheckBox", "MenuButton", "Scroll", "Slider", "TextField", "TextView"),
				new ColumnLayoutParams(.2f));

		menuButtonOfComponents.getItem("Button").onClick(() -> cm.switchOn("Button"));
		menuButtonOfComponents.getItem("CheckBox").onClick(() -> cm.switchOn("CheckBox"));
		menuButtonOfComponents.getItem("EditText").onClick(() -> cm.switchOn("EditText"));
		menuButtonOfComponents.getItem("Knob").onClick(() -> cm.switchOn("Knob"));
		menuButtonOfComponents.getItem("LabeledCheckBox").onClick(() -> cm.switchOn("LabeledCheckBox"));
		menuButtonOfComponents.getItem("MenuButton").onClick(() -> cm.switchOn("MenuButton"));
		menuButtonOfComponents.getItem("Scroll").onClick(() -> cm.switchOn("Scroll"));
		menuButtonOfComponents.getItem("Slider").onClick(() -> cm.switchOn("Slider"));
		menuButtonOfComponents.getItem("TextField").onClick(() -> cm.switchOn("TextField"));
		menuButtonOfComponents.getItem("TextView").onClick(() -> cm.switchOn("TextView"));

		container.add(ContainerMenuItem, new GridLayoutParams(0, 0));

		return container;
	}

	private Container getContainerAllComponents() {
		Container container = new Container(new GridLayout(5, 5));

		container.add(new Button(), new GridLayoutParams(0, 0));
		container.add(new CheckBox(), new GridLayoutParams(1, 0));
		container.add(new EditText(), new GridLayoutParams(2, 0));
		container.add(new LabeledCheckBox("confirm"), new GridLayoutParams(3, 0));
		container.add(new MenuButton().add("one", "two", "three", "four", "five"), new GridLayoutParams(4, 0));
		container.add(new Scroll(), new GridLayoutParams(0, 1), "scroll");
		container.add(new Slider(), new GridLayoutParams(1, 1));
		container.add(new TextField(), new GridLayoutParams(2, 1));
		container.add(new TextView("TextView"), new GridLayoutParams(3, 1));
		container.add(new Knob(), new GridLayoutParams(4, 1));

		return container;
	}

	private Container getContainerWith(Component component) {
		Container container = new Container(new GridLayout(11, 11));
		container.setContainerMode(ContainerMode.IGNORE_CONSTRAINTS);

		if (component instanceof EditText) {
			container.add(component, new GridLayoutParams(1, 1, 9, 9), "edit_text");
		} else {
			container.add(component, new GridLayoutParams(5, 5), "edit_text");
		}

		return container;
	}
}