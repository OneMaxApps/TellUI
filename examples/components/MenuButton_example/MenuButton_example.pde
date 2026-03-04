import microui.MicroUI;
import microui.component.*;
import microui.layout.*;

void setup() {
  size(800,400);
  
  MicroUI ui = MicroUI.init(this);
  
  final var menu = new MenuButton("File");
  
  menu.add("New","Open...");
  menu.addMenu("Open Recent","1","2","3","4");
  menu.add("Sketchbook...","Examples...","Close","Save","Save As...","Export Application...","Export as PDEZ...","Page Setup","Print...","Preferences...","Quit");
  
  ui.addContainer(new GridLayout(8,8)).add(menu, new GridLayoutParams(0,0,2,1,-1,-1));
}

void draw() {
 background(200);
}
