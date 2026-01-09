package microui.layout;

/**
 * Layout parameters for positioning components within a GridLayout.
 * Defines grid coordinates, spanning, and alignment for components in a grid-based layout.
 * <p>
 * GridLayoutParams specifies where and how a component should be placed within a GridLayout:
 * - Grid coordinates (column, row) determine starting cell
 * - Span values (columnSpan, rowSpan) determine how many cells the component occupies
 * - Alignment values (alignX, alignY) control positioning within the allocated cell space
 * </p>
 * @see LayoutParams
 * @see GridLayout
 */
public final class GridLayoutParams implements LayoutParams {
	private final int column, row, columnSpan, rowSpan, alignX, alignY;

	/**
	 * Constructs GridLayoutParams with all parameters specified.
	 * 
	 * @param column the starting column index (0-based, must be ≥ 0)
	 * @param row the starting row index (0-based, must be ≥ 0)
	 * @param columnSpan the number of columns to span (must be ≥ 1)
	 * @param rowSpan the number of rows to span (must be ≥ 1)
	 * @param alignX horizontal alignment (-1=left, 0=center, 1=right)
	 * @param alignY vertical alignment (-1=top, 0=center, 1=bottom)
	 * @throws IllegalArgumentException if any parameter is out of valid range
	 */
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

	/**
	 * Constructs GridLayoutParams with default center alignment.
	 * 
	 * @param column the starting column index (0-based)
	 * @param row the starting row index (0-based)
	 * @param columnSpan the number of columns to span
	 * @param rowSpan the number of rows to span
	 */
	public GridLayoutParams(int column, int row, int columnSpan, int rowSpan) {
		this(column, row, columnSpan, rowSpan, 0, 0);
	}

	/**
	 * Constructs GridLayoutParams for a single cell with default center alignment.
	 * 
	 * @param column the column index (0-based)
	 * @param row the row index (0-based)
	 */
	public GridLayoutParams(int column, int row) {
		this(column, row, 1, 1);
	}

	/**
	 * Returns the starting column index.
	 * 
	 * @return the column index (0-based)
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the starting row index.
	 * 
	 * @return the row index (0-based)
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns the number of columns spanned.
	 * 
	 * @return the column span (≥ 1)
	 */
	public int getColumnSpan() {
		return columnSpan;
	}

	/**
	 * Returns the number of rows spanned.
	 * 
	 * @return the row span (≥ 1)
	 */
	public int getRowSpan() {
		return rowSpan;
	}

	/**
	 * Returns the horizontal alignment.
	 * 
	 * @return -1 for left, 0 for center, 1 for right
	 */
	public int getAlignX() {
		return alignX;
	}

	/**
	 * Returns the vertical alignment.
	 * 
	 * @return -1 for top, 0 for center, 1 for bottom
	 */
	public int getAlignY() {
		return alignY;
	}

}