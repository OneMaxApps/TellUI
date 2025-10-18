package microui;

import microui.component.MenuButton;
import microui.component.MenuButton.ItemDimensions;
import microui.core.base.ContainerManager;
import microui.core.style.theme.ThemeGray;
import microui.core.style.theme.ThemeManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class FileMenuButton extends PApplet {

    private MenuButton fileMenu;

    public static void main(String[] args) {
        PApplet.main("microui.FileMenuButton");
    }

    @Override
    public void settings() {
    	size(800,480);
    }

    @Override
    public void setup() {
        
        MicroUI.setContext(this);
        
        MicroUI.setFlexibleRenderModeEnabled(true);
        
        ThemeManager.setTheme(new ThemeGray());
        
        ContainerManager.getInstance();
        
        fileMenu = new MenuButton("File", 0, 0, 64, 20);
        
        fileMenu.addMenu("New", "Project", "Package", "Class", "Interface", "Enum", "Annotation", "Source Folder", "File", "Folder");
        fileMenu.add("Open File...", "Open Projects from File System...");
        fileMenu.add("Close", "Close All");
        fileMenu.add("Save", "Save As...", "Save All");
        fileMenu.add("Move...", "Rename...");
        fileMenu.add("Refresh");
        fileMenu.addMenu("Convert Line Delimiters To", "Windows (CRLF)", "Unix (LF)", "Mac OS 9 (CR)", "Old Mac (CR)");
        fileMenu.add("Print...");
        fileMenu.addMenu("Switch Workspace", "Other...", "workspace1", "workspace2", "workspace3");
        fileMenu.add("Restart");
        fileMenu.addMenu("Import", "Run/Debug", "Team", "Other");
        fileMenu.addMenu("Export", "Plug-in Development", "Run/Debug", "Team", "Other");
        fileMenu.addMenu("Properties", "Resource", "Builders", "Java Build Path", "Java Code Style", "Java Compiler");
        fileMenu.add("Exit");

        fileMenu.getMenu("New").addMenu("Other", "Java Project", "Maven Project", "Plug-in Project", "Project...");
        
        fileMenu.getMenu("Import").addMenu("General", "Archive File", "File System", "Preferences");
        fileMenu.getMenu("Import").addMenu("Git", "Projects from Git", "Repositories");
        fileMenu.getMenu("Import").addMenu("Maven", "Existing Maven Projects", "Local Maven Project");
        
        fileMenu.getMenu("Export").addMenu("General", "Archive File", "File System", "Preferences");
        fileMenu.getMenu("Export").addMenu("Java", "JAR file", "Runnable JAR file", "Javadoc");
        
        fileMenu.getMenu("New").getMenu("Other").addMenu("Example...", "Plug-in Examples", "Maven Examples");
        fileMenu.getMenu("New").getMenu("Other").getMenu("Example...").addMenu("Java Examples","JavaFX", "Web", "Database");
        fileMenu.getMenu("New").getMenu("Other").getMenu("Example...").getMenu("Java Examples").addMenu("Swing", "JFrame", "JDialog", "JPanel", "Button", "TextField");
       
        fileMenu.getMenu("Properties").addMenu("Run/Debug Settings","Edit...", "Delete", "Duplicate");
        fileMenu.getMenu("Properties").getMenu("Run/Debug Settings").addMenu("New...", "Java Application", "JUnit Test", "Eclipse Application");

        fileMenu.get("JFrame").setTooltip("JFrame it's the base component from Swing, but we're in MicroUI MenuButton item...");
        
        fileMenu.setItemDimensions(new ItemDimensions(140,20));

    }
    
    @Override
    public void draw() {
        background(164);
        fileMenu.draw();
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);
        fileMenu.mouseWheel(event);
    }
}