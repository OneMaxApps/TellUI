package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayList;
import java.util.List;

import microui.util.MathUtils;

public class MultiLineTextController {
	private final List<SingleLineTextController> list;
	private final Selection selection;
	private final Cursor cursor;
	
	public MultiLineTextController() {
		super();
		list = new ArrayList<SingleLineTextController>();
		list.add(new SingleLineTextController());
		
		selection = new Selection(this);
		cursor = new Cursor(this);
	}
	
	public static void main(String[] args) {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.cursor.setRow(1);
		controller.addLine("World");
		System.out.println(controller.getText());
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int getLinesCount() {
		return list.size();
	}
	
	public void addLine(String text) {
		list.add(cursor.getRow(), new SingleLineTextController(text));
	}
	
	public void removeLine(int index) {
		list.remove((int) constrain(index, 0, getLinesCount()-1));
	}
	
	public String getText() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i).getAsString() + "\n");
		}
		
		return sb.toString();
	}
	
	private static final class Selection {
		private final MultiLineTextController controller;
		
		private int startColumn, endColumn, startRow, endRow;

		public Selection(MultiLineTextController controller) {
			super();
			this.controller = requireNonNull(controller,"controller");
		}

		public int getStartColumn() {
			return startColumn;
		}

		public void setStartColumn(int startColumn) {
			final int maxColumn = controller.list.get(getStartRow()).length();
			this.startColumn = (int) MathUtils.constrain(startColumn, 0, maxColumn);
		}

		public int getEndColumn() {
			return endColumn;
		}

		public void setEndColumn(int endColumn) {
			final int maxColumn = controller.list.get(getEndRow()).length();
			this.endColumn = (int) constrain(endColumn, 0, maxColumn);
		}

		public int getStartRow() {
			return (int) constrain(startRow, 0, controller.getLinesCount()-1);
		}

		public void setStartRow(int startRow) {
			this.startRow = (int) constrain(startRow, 0, controller.getLinesCount()-1);
		}

		public int getEndRow() {
			return (int) constrain(endRow, 0, controller.getLinesCount()-1);
		}

		public void setEndRow(int endRow) {
			this.endRow = (int) constrain(endRow, 0, controller.getLinesCount()-1);
		}
		
		public int getEffectiveStartColumn() {
			return Math.min(startColumn, endColumn);
		}
		
		public int getEffectiveEndColumn() {
			return Math.max(startColumn, endColumn);
		}
		
		public int getEffectiveStartRow() {
			return Math.min(startRow, endRow);
		}
		
		public int getEffectiveEndRow() {
			return Math.max(startRow, endRow);
		}
	}
	
	private static final class Cursor {
		private final MultiLineTextController controller;
		
		private int column, row;

		public Cursor(MultiLineTextController controller) {
			super();
			this.controller = requireNonNull(controller,"controller");
		}

		public int getColumn() {
			final int maxColumn = controller.list.get(getRow()).length();
			return (int) constrain(column, 0, maxColumn);
		}

		public void setColumn(int column) {
			final int maxColumn = controller.list.get(getRow()).length();
			this.column = (int) constrain(column, 0, maxColumn-1);
		}

		public int getRow() {
			return (int) constrain(row, 0, controller.getLinesCount()-1);
		}

		public void setRow(int row) {
			this.row = (int) constrain(row, 0, controller.getLinesCount()-1);
		}
		
	}
}