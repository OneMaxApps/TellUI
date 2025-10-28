package microui;

import static microui.Render.Mode.FLEXIBLE;

import microui.component.MenuButton;
import microui.core.base.ContainerManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class ComponentLauncher extends PApplet {

	MenuButton menuFile;

	public static void main(String[] args) {
		PApplet.main("microui.ComponentLauncher");
	}

	@Override
	public void settings() {
//		fullScreen(P2D,0); // 1680:1050
		fullScreen();
//		size(640,360,P2D);
	}

	@Override
	public void setup() {
		Render.setMode(FLEXIBLE);
		MicroUI.setContext(this);
		ContainerManager.getInstance();

		menuFile = new MenuButton("File",0,0,100,32);

		menuFile
		    .addMenu("New", 
		        "Project...",
		        "Package",
		        "Class", 
		        "Interface",
		        "Enum",
		        "Annotation", 
		        "Source Folder",
		        "File",
		        "Folder",
		        "Untitled Text File",
		        "JUnit Test Case", 
		        "Task"
		    )
		    .add("Open File...")
		    .add("Open Projects from File System...")
		    .add("Close")
		    .add("Close All")
		    .add("Save") 
		    .add("Save As...")
		    .add("Save All")
		    .add("Revert")
		    .add("Move...")
		    .add("Rename...")
		    .add("Refresh")
		    .addMenu("Convert Line Delimiters To",
		        "Windows (CRLF)",
		        "Unix (LF)", 
		        "Mac OS 9 (CR)"
		    )
		    .add("Print...")
		    .add("Import...")
		    .add("Export...")
		    .add("Properties")
		    .add("Exit");
		
		menuFile.setIcon(loadImage("C:\\Users\\002\\Desktop\\icon.png"), "Open File...");
		
	}

	@Override
	public void draw() {
		background(200);
		menuFile.draw();
		
	}

	@Override
	public void keyPressed() {
		if (keyCode == RIGHT) {
			menuFile.appendX(10);
		}
		if (keyCode == LEFT) {
			menuFile.appendX(-10);
		}
		if (keyCode == UP) {
			menuFile.appendY(-10);
		}
		if (keyCode == DOWN) {
			menuFile.appendY(10);
		}

	}

	@Override
	public void mouseWheel(MouseEvent event) {
		menuFile.mouseWheel(event);
	}

}