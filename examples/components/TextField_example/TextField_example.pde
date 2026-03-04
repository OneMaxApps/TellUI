import microui.MicroUI;
import microui.component.*;
import microui.layout.*;

void setup() {
  size(480,360);
  
  MicroUI ui = MicroUI.init(this);
  
  ui.addContainer(new GridLayout(1,1)).add(new TextField(), new GridLayoutParams(0,0));
}

void draw() {
 background(200);
}
