import microui.MicroUI;
import microui.component.*;
import microui.layout.*;

void settings() {
  size(320,240); 
}

void setup() {
  MicroUI ui = MicroUI.init(this);
  
  ui.addContainer(new GridLayout(3,5)).add(new Button(), new GridLayoutParams(1,2));
}

void draw() {
 background(200);
}
