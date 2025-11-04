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
//		fullScreen(P2D,0);
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
//		MicroUI.setDebugModeEnabled(true);
		//Debugger.setDebugModeEnabled(true);

		// ThemeManager.setTheme(new ThemeGray());

		cm = ContainerManager.getInstance();
		
		cm.setAnimatorEnabled(false);
		
		TextField tf = new TextField();
		//tf.setFont(createFont("C:\\Windows\\Fonts\\consolai.ttf",24));

		
		cm.add(getContainerWith(tf), "TextField");
		cm.add(getContainerMain(), "main");
		cm.add(getContainerAllComponents(), "all_components");
		cm.add(getContainerWith(button = new Button()), "Button");
		cm.add(getContainerWith(new CheckBox()), "CheckBox");
		cm.add(getContainerWith(new EditText()), "EditText");
		cm.add(getContainerWith(new Knob()), "Knob");
		cm.add(getContainerWith(new LabeledCheckBox()), "LabeledCheckBox");
		cm.add(getContainerWith(new Scroll()), "Scroll");
		cm.add(getContainerWith(new Slider()), "Slider");
		cm.add(getContainerWith(new TextView()), "TextView");
		cm.add(getContainerWith(new MenuButton()), "MenuButton");

//		button.setImage(loadImage("C:\\Users\\002\\Downloads\\i.jpg"));
		
	}

	@Override
	public void draw() {
		background(32);

		// cm.getContainerByTextId("container_main").getComponentByTextId("edit_text").setSize(mouseX,mouseY);
		// cm.getByTextId("container_main").getByTextId("edit_text").setSize(mouseX,mouseY);
		// Metrics.printAll();
		
	}

	@Override
	public void keyPressed() {
		super.keyPressed();

		if (keyPressed) {
			if (keyCode == 0x70) {
				cm.switchOn("main");
			}

			if (keyCode == TAB) {
				cm.switchOnNext();
			}
		}

	}

	private Container getContainerMain() {
		Container container = new Container(new GridLayout(3, 4));

		Container ContainerMenuItem = new Container(new ColumnLayout());
		// ContainerMenuItem.setMode(IGNORE_CONSTRAINTS);

		ContainerMenuItem.add(new Button("show all components").onClick(() -> cm.switchOn("all_components")),
				new ColumnLayoutParams(.2f));

		MenuButton menuComponents;
		ContainerMenuItem.add(
				menuComponents = new MenuButton(),
				new ColumnLayoutParams(.2f));


		menuComponents.addMenu("Component");
		menuComponents.getMenu("Component").add("Button","CheckBox","EditText","Knob","LabeledCheckBox","MenuButton","Scroll","Slider","TextField","TextView");
		
		menuComponents.get("Button").onClick(() -> cm.switchOn("Button"));
		menuComponents.get("CheckBox").onClick(() -> cm.switchOn("CheckBox"));
		menuComponents.get("EditText").onClick(() -> cm.switchOn("EditText"));
		menuComponents.get("Knob").onClick(() -> cm.switchOn("Knob"));
		menuComponents.get("LabeledCheckBox").onClick(() -> cm.switchOn("LabeledCheckBox"));
		menuComponents.get("MenuButton").onClick(() -> cm.switchOn("MenuButton"));
		menuComponents.get("Scroll").onClick(() -> cm.switchOn("Scroll"));
		menuComponents.get("Slider").onClick(() -> cm.switchOn("Slider"));
		menuComponents.get("TextField").onClick(() -> cm.switchOn("TextField"));
		menuComponents.get("TextView").onClick(() -> cm.switchOn("TextView"));

		container.add(ContainerMenuItem, new GridLayoutParams(1, 1));

		MenuButton menuButton = new MenuButton();
		container.add(menuButton, new GridLayoutParams(0, 0, 1, 1, -1, -1));
		menuButton.setMaxSize(100,24);
		
		menuButton.addMenu("New", "Java Project,Maven Project,Project...,Package,Class,Interface,Enum,Record,Annotation".split(","));
		
		menuButton.add("Open File...,Open Projects from File System...,Recent Files".split(","));
		menuButton.add("Close Editor,Close All Editors,Save,Save As...,Save All,Revert File".split(","));
		
		menuButton.setTextId("mb");
		
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
		container.add((TextField) new TextField().setTextId("text_field"), new GridLayoutParams(2, 1));
		container.add(new TextView("TextView"), new GridLayoutParams(3, 1));
		container.add(new Knob(), new GridLayoutParams(4, 1));

		return container;
	}

	private Container getContainerWith(Component component) {
		Container container = new Container(new GridLayout(11, 11));
		container.setMode(Container.Mode.IGNORE_CONSTRAINTS);

		if (component instanceof EditText) {
			container.add(component, new GridLayoutParams(1, 1, 9, 9), "edit_text");
		} else {
			container.add(component, new GridLayoutParams(4, 5, 3, 1));
		}

		return container;
	}
}