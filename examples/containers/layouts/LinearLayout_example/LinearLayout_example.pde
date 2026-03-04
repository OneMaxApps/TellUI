import microui.MicroUI;
import microui.component.*;
import microui.core.base.*;
import microui.layout.*;

void setup() {
  size(600,500);
  MicroUI ui = MicroUI.init(this);
  
  var linearLayout = new LinearLayout();
  var container = new Container(linearLayout);
 
  container.onClick(() -> linearLayout.setVerticalMode(!linearLayout.isVerticalMode()));
 
  container.ignoreConstraints();
 
  container.add(new Button("First Button"), new LinearLayoutParams(.1f)); // .1f = weight in space into container
  container.add(new Button("Second Button"), new LinearLayoutParams(.2f));

  ui.addContainer(container);
}

void draw() {
  background(200);
}
