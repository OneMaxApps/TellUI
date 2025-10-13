package microui.layout;

public final class GridLayoutParams implements LayoutParams {
	private final int column, row, columnSpan, rowSpan, alignX, alignY;

	public GridLayoutParams(int column, int row, int columnSpan, int rowSpan, int alignX, int alignY) {
		super();
		if (column < 0) {
			throw new IllegalArgumentException("grid layout param: \"column\" cannot be less than zero");
		}
		if (row < 0) {
			throw new IllegalArgumentException("grid layout param: \"row\" cannot be less than zero");
		}
		if (columnSpan < 1) {
			throw new IllegalArgumentException("grid layout param: \"columnSpan\" cannot be less than 1");
		}
		if (rowSpan < 1) {
			throw new IllegalArgumentException("grid layout param: \"rowSpan\" cannot be less than 1");
		}

		if (alignX < -1) {
			throw new IllegalArgumentException("grid layout param: \"alignX\" cannot be less than -1");
		}
		if (alignX > 1) {
			throw new IllegalArgumentException("grid layout param: \"alignX\" cannot be greater than 1");
		}

		if (alignY < -1) {
			throw new IllegalArgumentException("grid layout param: \"alignY\" cannot be less than -1");
		}
		if (alignY > 1) {
			throw new IllegalArgumentException("grid layout param: \"alignY\" cannot be greater than 1");
		}

		this.column = column;
		this.row = row;
		this.columnSpan = columnSpan;
		this.rowSpan = rowSpan;
		this.alignX = alignX;
		this.alignY = alignY;
	}

	public GridLayoutParams(int column, int row, int columnSpan, int rowSpan) {
		this(column, row, columnSpan, rowSpan, 0, 0);
	}

	public GridLayoutParams(int column, int row) {
		this(column, row, 1, 1);
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int getColumnSpan() {
		return columnSpan;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public int getAlignX() {
		return alignX;
	}

	public int getAlignY() {
		return alignY;
	}

}