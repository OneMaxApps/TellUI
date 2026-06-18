## MicroUI: A Graphical User Interface (GUI) Library for Processing 4

The MicroUI library (version 1.0.0) was developed by Islam Akhmedkhanov for creating rich user interfaces in the Processing 4 environment.

### I. Architectural Overview

The fundamental principle of the MicroUI library is to organize all elements into Containers. These containers are managed internally by the UIHost. To get started, you simply initialize MicroUI with the Processing sketch context using `MicroUI.init(this)`.

### II. Layout Systems

The library provides four built-in layout systems that allow flexible placement of components within containers:

1. GridLayout;
2. LinearLayout;
3. RowLayout;
4. ColumnLayout;

### III. Control Components (10 Elements)

MicroUI includes ten main components, each with specific capabilities:

1. **Button** - A basic control that inherits from `AbstractButton`. It contains a `TextView` and supports ripple and hover effects.

2. **CheckBox** - Also inherits from `AbstractButton`. Controls a Boolean state (`isChecked`) and supports multiple visual styles: `MARK`, `DOT`, and `RECT`.

3. **TextView** - A component for displaying text. It supports various `AutoResizeMode` modes, including `FULL`, `BIG`, `MIDDLE`, `SMALL`, and `TINY`.

4. **TextField** - A single?line text input that implements `KeyPressable`. It manages cursor position, selection, horizontal scrolling, and standard keyboard shortcuts (`CTRL+C`, `CTRL+V`, `CTRL+X`, `CTRL+A`).

5. **TextArea** - A multi?line text editor that implements both `Scrollable` and `KeyPressable`. It includes built?in vertical and horizontal scrollbars, undo/redo support, and complex internal classes for text, cursor, and selection.

6. **Slider** - A linear range control (`LinearRangeControl`) that changes its value when the user clicks or drags anywhere on the track.

7. **Scroll** - Also a linear range control (`LinearRangeControl`). Includes a movable thumb (implemented as a separate `Button`) used to navigate through content.

8. **Knob** - A rotary range control (`RangeControl`) that changes its value when rotated with the mouse. An arc visualizes the current value.

9. **MenuButton** - Extends a standard button by displaying a drop-down list of other buttons or nested sub-menus. Implements `Scrollable`, allowing scrolling of menu items when many are present.

10. **ToggleButton** - A toggle switch that inherits `AbstractButton`. It provides a sliding thumb with active/inactive states and supports ripple and hover effects.

### IV. Initialization Example

Below is the standard code for initializing MicroUI and adding a button to a container using a grid layout (`GridLayout`):

```java
void setup() {
  fullScreen();
  MicroUI ui = MicroUI.init(this);

  // Adding a new container with the ID "main" using GridLayout
  ui.addContainer(new GridLayout(3, 3), "main");

  // Getting the container by ID and adding a Button component to it
  // The component is placed in cell (1,1) using GridLayoutParams
  ui.getContainer("main").add(new Button(), new GridLayoutParams(1, 1));
}

void draw() {
  /* In this method, you don't need to manually call container rendering.
     UIHost draws all the necessary content automatically.

     However, you must still declare draw() so Processing continuously updates the frame.
  */
}