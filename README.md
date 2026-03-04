## MicroUI: A Graphical User Interface (GUI) Library for Processing 4

The MicroUI library (version 1.0.0) was developed by Islam Ahmedhanov for creating rich user interfaces in the Processing 4 environment.

### I. Architectural Overview

The fundamental principle of the MicroUI library is to organize all elements into Containers. These containers are managed using the singleton pattern, ContainerManager. To get started, you need to set the Processing context using the static method MicroUI.setContext(PApplet context).

### II. Layout Systems

The library provides four built-in layout systems that allow flexible placement of components within containers:

1. GridLayout;
2. LinearLayout;
3. RowLayout;
4. ColumnLayout;

### III. Control Components (9 Elements)

MicroUI includes ten main components, each with specific capabilities:

1. **Button** A basic control that inherits from AbstractButton. It contains a TextView.
2. **CheckBox** Also inherits from AbstractButton. It is used to control a Boolean state (isChecked).
3. **TextView** (Text View): A component for displaying text. It supports various AutoResizeMode modes, including FULL, BIG, MIDDLE, SMALL, and TINY.
4. **TextField** (Input Field): A component for single-line text input that implements the KeyPressable interface. It manages the cursor position and selection, and supports horizontal scrolling for long text. It supports standard keyboard shortcuts such as CTRL+C, CTRL+V, CTRL+X, and CTRL+A.
5. **TextArea** (Multi-Line Text Editor): A more complex component for multi-line editing that implements the Scrollable and KeyPressable interfaces.  It includes built-in vertical and horizontal scrollbars, as well as complex internal classes for managing text, cursor, and selection.
6. **Slider** (`Slider`): A linear range control (`LinearRangeControl`). It allows the value to change based on the mouse position while holding the component down.
7. **Scroll** (`Scrollbar`): Also a linear range control (`LinearRangeControl`). Includes a movable thumb, which is implemented as a separate `Button` component and is used to navigate through the content.
8. **Knob** (`Rotary`): A range control (`RangeControl`) that changes its value when rotated with the mouse. It uses an arc to visualize the current value.
 9. **MenuButton** (`Menu Button`): Extends the functionality of a standard button by allowing it to display a drop-down list of other buttons or nested submenus (`subMenu`). The component implements the `Scrollable` interface, allowing scrolling of menu items if there are many.

### IV. Initialization Example

Below is the standard code for initializing MicroUI and adding a button to a container using a grid layout (`GridLayout`):

```java
void setup() {
fullScreen();
MicroUI ui = MicroUi.inits(this);

// Adding a new container with the ID "main" using GridLayout
ui.addContainer(new GridLayout(3,3),"main");

// Getting the container by ID and adding a Button component to it
// The component is placed in cell (1,1) using GridLayoutParams
ui.getContainer("main").add(new Button(), new GridLayoutParams(1,1));

}

void draw() {
/* In this method, you don't need to manually call container rendering.
ContainerManager will draw all the necessary content automatically.

It's important to understand that this method should still be written, since Processing won't trigger a rendering update.
*/
}
```
