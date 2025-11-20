package microui.core;

import static microui.util.MathUtils.constrain;

import java.util.ArrayList;
import java.util.List;

public class MultiLineTextController {
	private static final int MAX_CAPACITY_FOR_CLEAR_ADAPTER = 1000;
	private final List<SingleLineTextController> list;
	private StringBuilder adapterSb;
	
	public MultiLineTextController() {
		super();
		list = new ArrayList<SingleLineTextController>();
		
	}
	
	public static void main(String[] args) {
		var controller = new MultiLineTextController();
//		controller.addLine("Hello ");
//		controller.addLine("World");
//		controller.splitLine(1, 3);
//		controller.addLine("!");
//		controller.mergeLines(1);
//		controller.mergeLines(1);
//		controller.mergeLines(0);
		

		
		
		
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
		list.remove(getClampedIndex(index));
	}
	
	public void insertCharForLine(int row, int column, char ch) {
		if (isEmpty()) {
			addLine("");
		}
		
		list.get(getClampedIndex(row)).insert(column, ch);
	}
	
	public void insertStringForLine(int row, int column, String str) {
		if (isEmpty()) {
			addLine("");
		}
		
		list.get(getClampedIndex(row)).insert(column, str);
	}
	
	public void removeCharForLine(int row, int column) {
		if (isEmpty()) {
			addLine("");
		}
		
		list.get(getClampedIndex(row)).removeCharAt(column);
	}
	
	public void removeStringForLine(int row, int columnStart, int columnEnd) {
		if (isEmpty()) {
			addLine("");
		}
		
		list.get(getClampedIndex(row)).remove(columnStart, columnEnd);
	}
	
	public void splitLine(int row, int column) {
		row = getClampedIndex(row);
		
		if (isEmpty()) {
			return;
		}
		
		final String currentRowText = getLine(row).getAsString();
		final int clampedColumn = (int) constrain(column,0,currentRowText.length());
		
		insertLine(row+1,currentRowText.substring(clampedColumn));
		getLine(row).remove(clampedColumn,currentRowText.length());
		
	}
	
	public void mergeLines(int row) {
		row = getClampedIndex(row);
		
		if (getLinesCount() <= 1 || isLastRow(row)) {
			return;
		}
		
		getLine(row).insert(getLine(row).length(), getLine(row+1).getAsString());
		removeLine(row+1);
	}
	
	public SingleLineTextController getLine(int row) {
		if (isEmpty()) {
			addLine("");
		}
		
		return list.get(getClampedIndex(row));
	}
	
	public String getText() {
		if (adapterSb == null) {
			adapterSb = new StringBuilder();
		} else {
			adapterSb.setLength(0);
			if(adapterSb.capacity() > MAX_CAPACITY_FOR_CLEAR_ADAPTER) {
				adapterSb.trimToSize();
			}
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (isLastRow(i)) {
				adapterSb.append(list.get(i).getAsString());
			} else {
				adapterSb.append(list.get(i).getAsString() + "\n");
			}
		}
		
		return adapterSb.toString();
	}
	
	private int getClampedIndex(int index) {
		return (int) constrain(index, 0, getLinesCount()-1);
	}
	
	private boolean isLastRow(int row) {
		return getClampedIndex(row) == getLinesCount()-1;
	}
}