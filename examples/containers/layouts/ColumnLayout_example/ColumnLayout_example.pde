import microui.MicroUI;
import microui.component.*;
import microui.core.base.*;
import microui.layout.*;

void setup() {
  size(600,500);
  MicroUI ui = MicroUI.init(this);
  
   var container = new Container(new ColumnLayout());
 
   container.setMode(Container.Mode.IGNORE_CONSTRAINTS);
 
   container.add(new Button("First Button"), new ColumnLayoutParams(.1f)); // .1f = weight in space into container
   container.add(new Button("Second Button"), new ColumnLayoutParams(.2f));

   ui.addContainer(container);
}

void draw() {
  
}
