import microui.MicroUI;
import microui.component.*;
import microui.core.base.*;
import microui.layout.*;
import microui.core.style.Color;

void setup() {
  size(600,500);
  
 MicroUI ui = MicroUI.init(this);
  
 var cFirst = new Container(new GridLayout(3,5));
 var cSecond = new Container(new GridLayout(3,5));
 
 cFirst.setTextId("first");
 cSecond.setTextId("second");
 
 cFirst.setBackgroundColor(new Color(200,200,232));
 cSecond.setBackgroundColor(new Color(200,232,200));
 
 cFirst.add(new Button("go next").onClick(() -> ui.switchContainer("second")), new GridLayoutParams(1,2));
 cSecond.add(new Button("go prev").onClick(() -> ui.switchContainer("first")), new GridLayoutParams(1,2));
 
 // you can disable animator
 // ui.setAnimatorEnabled(false);
 
 ui.addContainer(cFirst);
 ui.addContainer(cSecond);
 
}

void draw() {
  
}
