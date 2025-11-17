package microui;

import microui.component.TextField;
import microui.component.TextView;
import microui.core.SingleLineTextController.ValidationMode;
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
		fullScreen();
//		size(800,400);
	}

	@Override
	public void setup() {
		MicroUI.setContext(this);
		cm = ContainerManager.getInstance();
		ThemeManager.setTheme(new ThemeBlack());

		cm.add(new Container(new RowLayout()), "main_container");

		var container = cm.getByTextId("main_container");

		container.add(new Container(new GridLayout(3, 3)), new RowLayoutParams(.8f), "view_container");
		container.add(new Container(new GridLayout(1, 30)), new RowLayoutParams(.2f), "tools_container");

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
		viewContainer.add(new TextField(), new GridLayoutParams(1, 1), "text_field");

	}

	private void prepareToolsContainer(Container c) {
		Container mc = cm.getByTextId("main_container");
		Container vc = (Container) mc.getByTextId("view_container");
		TextField tfView = (TextField) vc.getByTextId("text_field");

		c.setBackgroundColor(new Color(32));

		c.add(new TextView("Bounds"), new GridLayoutParams(0, 0));

		TextField tfX, tfY, tfW, tfH;

		c.add(tfX = getPreparedInBoundsControlStyleTextField(), new GridLayoutParams(0, 1));
		c.add(tfY = getPreparedInBoundsControlStyleTextField(), new GridLayoutParams(0, 2));
		c.add(tfW = getPreparedInBoundsControlStyleTextField(), new GridLayoutParams(0, 3));
		c.add(tfH = getPreparedInBoundsControlStyleTextField(), new GridLayoutParams(0, 4));

		tfX.setHint("Pos: X");
		tfY.setHint("Pos: Y");
		tfW.setHint("Width:");
		tfH.setHint("Height:");

		tfY.setTooltip("Position X");
		tfY.setTooltip("Position Y");
		tfW.setTooltip("Width");
		tfH.setTooltip("Height");

		tfX.setOnTextChangedListener(() -> {
			tfView.setX(tfX.getDigitsOrDefault(0));
		});

		tfY.setOnTextChangedListener(() -> {
			tfView.setY(tfY.getDigitsOrDefault(0));
		});

		tfW.setOnTextChangedListener(() -> {
			tfView.setWidth(tfW.getDigitsOrDefault(0));
		});

		tfH.setOnTextChangedListener(() -> {
			tfView.setHeight(tfH.getDigitsOrDefault(0));
		});

		c.add(new TextView("Params"), new GridLayoutParams(0, 5));

		TextField tfHint;
		c.add(tfHint = getPreparedInGeneralStyleTextField(), new GridLayoutParams(0, 6));

		tfHint.setHint("Hint:");
		tfHint.setOnTextChangedListener(() -> {
			tfView.setHint(tfHint.getText());
		});

		tfView.setHint("Example");
		tfView.setFont(createFont("C:\\Windows\\Fonts\\consola.ttf", 24));
		tfView.setMaxSize(400, 100);

//		c.add(new TextView("Colors"), new GridLayoutParams(0,5));
//		c.add(new TextView("Background"), new GridLayoutParams(0,6));

	}

	private static TextField getPreparedInGeneralStyleTextField() {
		final TextField textField = new TextField();
		textField.setMargin(0, 4);
		textField.setTextSize(10);

		return textField;
	}

	private static TextField getPreparedInBoundsControlStyleTextField() {
		final TextField textField = new TextField();
		textField.setMargin(0, 4);
		textField.setTextSize(10);
		textField.setValidationMode(ValidationMode.ONLY_DIGITS);
		textField.setTextConstrainEnabled(true);
		textField.setMaxChars(4);

		return textField;
	}

}