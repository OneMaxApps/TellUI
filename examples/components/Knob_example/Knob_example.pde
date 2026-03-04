import microui.MicroUI;
import microui.component.*;
import microui.layout.*;

void settings() {
  size(320,240); 
}

void setup() {
  MicroUI ui = MicroUI.init(this);
  
  ui.addContainer(new GridLayout(1,1)).add(new Knob(), new GridLayoutParams(0,0));
}

void draw() {
 background(200);
}
