import microui.MicroUI;
import microui.component.*;
import microui.layout.*;

void setup() {
  size(320,240);
  
  MicroUI ui = MicroUI.init(this);
  
  ui.addContainer(new GridLayout(1,1)).add(new Slider(), new GridLayoutParams(0,0));
}

void draw() {
 background(200);
}
