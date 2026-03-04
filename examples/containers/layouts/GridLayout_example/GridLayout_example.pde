import microui.MicroUI;
import microui.component.*;
import microui.core.base.*;
import microui.layout.*;

void setup() {
  size(600,500);
  MicroUI ui = MicroUI.init(this);
  
  var container = new Container(new GridLayout(10,10));
  container.ignoreConstraints();
  
  container.add(new Button(), new GridLayoutParams(0,0,2,1));
  container.add(new Button(), new GridLayoutParams(3,0,4,2));
  container.add(new Button(), new GridLayoutParams(8,0,2,1));
  container.add(new Scroll(), new GridLayoutParams(0,2,10,1));

   ui.addContainer(container);
}

void draw() {
  
}
