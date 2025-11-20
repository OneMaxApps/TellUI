package microui.core;

import static microui.util.MathUtils.constrain;

import java.util.ArrayList;
import java.util.List;

public class MultiLineTextController {
	private final List<SingleLineTextController> list;
	
	public MultiLineTextController() {
		super();
		list = new ArrayList<SingleLineTextController>();
		
	}
	
	public static void main(String[] args) {
		var controller = new MultiLineTextController();
		controller.addLine("Hello ");
		controller.addLine("World");
		controller.splitLine(1, 3);
		controller.addLine("!");
		controller.mergeLines(1);
		controller.mergeLines(1);
		controller.mergeLines(0);
		
		System.out.println(controller.getText());
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int getLinesCount() {
		return list.size();
	}
	
	public void addLine(String text) {
		list.add(getLinesCount(), new SingleLineTextController(text));
	}
	
	public void insertLine(int index, String text) {
		list.add((int) constrain(index,0,getLinesCount()), new SingleLineTextController(text));
	}
	
	public void removeLine(int index) {
		list.remove((int) constrain(index, 0, getLinesCount()-1));
	}
	
	public void insertCharForLine(int row, int column, char ch) {
		row = (int) constrain(row, 0, getLinesCount()-1);
		list.get(row).insert(column, ch);
	}
	
	public void insertStringForLine(int row, int column, String str) {
		row = (int) constrain(row, 0, getLinesCount()-1);
		list.get(row).insert(column, str);
	}
	
	public void removeCharForLine(int row, int column) {
		row = (int) constrain(row, 0, getLinesCount()-1);
		list.get(row).removeCharAt(column);
	}
	
	public void removeStringForLine(int row, int columnStart, int columnEnd) {
		row = (int) constrain(row, 0, getLinesCount()-1);
		list.get(row).remove(columnStart, columnEnd);
	}
	
	public void splitLine(int row, int column) {
		if (isEmpty()) {
			return;
		}
		
		row = (int) constrain(row, 0, getLinesCount()-1);
		
		insertLine(row+1,getLine(row).getAsString().substring(column));
		getLine(row).remove(column, getLine(row).length());
	}
	
	public void mergeLines(int row) {
		if (getLinesCount() <= 1) {
			return;
		}
		
		row = (int) constrain(row, 0, getLinesCount()-1);
		
		getLine(row).insert(getLine(row).length(), getLine(row+1).getAsString());
		removeLine(row+1);
	}
	
	public SingleLineTextController getLine(int row) {
		row = (int) constrain(row, 0, getLinesCount()-1);
		
		return list.get(row);
	}
	
	public String getText() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i).getAsString() + "\n");
		}
		
		return sb.toString();
	}
}