import microui.MicroUI;
import microui.component.*;
import microui.core.base.*;
import microui.layout.*;

void setup() {
  size(600,500);
  MicroUI ui = MicroUI.init(this);
  
  var container = new Container(new RowLayout());
 
  container.ignoreConstraints();
 
  container.add(new Button("First Button"), new RowLayoutParams(.1f)); // .1f = weight in space into container
  container.add(new Button("Second Button"), new RowLayoutParams(.2f));

  ui.addContainer(container);
}

void draw() {
  background(200);
}
