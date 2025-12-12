package microui.layout;

import microui.core.base.Container.Entry;
import microui.core.base.ContentView;

/**
 * Layout manager that arranges child components in a grid with specified columns and rows.
 * Provides flexible grid-based positioning with support for cell spanning and alignment options.
 * <p>
 * The GridLayout divides the container into a grid of equal-sized cells and positions
 * components within those cells. Components can span multiple cells and can be aligned
 * within their allocated space. Supports two modes: IGNORE_CONSTRAINTS (forces exact cell sizing)
 * and RESPECT_CONSTRAINTS (respects component size constraints with alignment).
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see LayoutManager
 * @see GridLayoutParams
 * @see Entry
 * @see ContentView
 */
public final class GridLayout extends LayoutManager {
	/** Number of columns in the grid. */
	private int columns, rows;

	/**
	 * Constructs a GridLayout with specified columns and rows.
	 * 
	 * @param columns the number of columns in the grid (must be ≥ 1)
	 * @param rows the number of rows in the grid (must be ≥ 1)
	 * @throws IllegalArgumentException if columns or rows is less than 1
	 */
	public GridLayout(int columns, int rows) {
		super();
		setColumns(columns);
		setRows(rows);

	}

	/**
	 * Draws debug visualization showing grid cell boundaries.
	 * Useful for development to see the grid structure.
	 */
	@Override
	public void debugOnDraw() {
		ctx.pushStyle();
		ctx.stroke(0, 128);
		ctx.fill(200, 0, 0, 32);
		// Draw all grid cells
		for (int col = 0; col < columns; col++) {
			for (int row = 0; row < rows; row++) {
				ctx.rect(getContainer().getX() + (getContainer().getWidth() / columns) * col,
						getContainer().getY() + (getContainer().getHeight() / rows) * row,
						getContainer().getWidth() / columns, getContainer().getHeight() / rows);
			}
		}
		ctx.popStyle();
	}

	/**
	 * Validates that the provided LayoutParams are of the correct type for GridLayout.
	 * 
	 * @param layoutParams the layout parameters to validate
	 * @throws IllegalArgumentException if layoutParams is not an instance of GridLayoutParams
	 */
	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof GridLayoutParams)) {
			throw new IllegalArgumentException("using not correct layout params for GridLayout");
		}
	}

	/**
	 * Recalculates and positions all child components within the grid.
	 * Handles both IGNORE_CONSTRAINTS and RESPECT_CONSTRAINTS modes.
	 */
	@Override
	public void recalculate() {
		float containerX = getContainer().getX();
		float containerY = getContainer().getY();
		float containerW = getContainer().getWidth();
		float containerH = getContainer().getHeight();

		// Calculate cell dimensions
		float colWidth = containerW / getColumns();
		float rowHeight = containerH / getRows();

		// Position each component
		for (int i = 0; i < getEntryList().size(); i++) {
			ContentView contentView = getEntryList().get(i).contentView();
			GridLayoutParams params = (GridLayoutParams) getEntryList().get(i).layoutParams();

			checkOutOfGrid(params);

			// Calculate cell position and size based on grid coordinates
			float requiredCellX = containerX + colWidth * params.getColumn();
			float requiredCellY = containerY + rowHeight * params.getRow();
			float requiredCellWidth = colWidth * params.getColumnSpan();
			float requiredCellHeight = rowHeight * params.getRowSpan();

			switch (getContainer().getMode()) {
			case IGNORE_CONSTRAINTS:
				// Force exact sizing to fit cell
				contentView.setConstrainDimensionsEnabled(false);
				contentView.setAbsoluteSize(requiredCellWidth, requiredCellHeight);
				contentView.setAbsolutePosition(requiredCellX, requiredCellY);
				break;

			case RESPECT_CONSTRAINTS:
				// Respect component constraints, align within cell
				// Note: sizing may not exactly match cell if constraints prevent it
				contentView.setAbsoluteSize(requiredCellWidth, requiredCellHeight);

				// Calculate alignment positions
				float alignXLeft = requiredCellX;
				float alignXCenter = requiredCellX + requiredCellWidth / 2 - contentView.getAbsoluteWidth() / 2;
				float alignXRight = requiredCellX + requiredCellWidth - contentView.getAbsoluteWidth();

				float alignYTop = requiredCellY;
				float alignYCenter = requiredCellY + requiredCellHeight / 2 - contentView.getAbsoluteHeight() / 2;
				float alignYBottom = requiredCellY + requiredCellHeight - contentView.getAbsoluteHeight();

				// Apply alignment based on parameters
				float correctPosX = params.getAlignX() == -1 ? alignXLeft
						: params.getAlignX() == 1 ? alignXRight : alignXCenter;
				float correctPosY = params.getAlignY() == -1 ? alignYTop
						: params.getAlignY() == 1 ? alignYBottom : alignYCenter;

				contentView.setAbsolutePosition(correctPosX, correctPosY);

				break;
			}

		}
	}

	/**
	 * Called when a new entry is added to the container.
	 * Validates that components don't overlap in the grid.
	 * 
	 * @param contentViewEntry the entry being added
	 */
	@Override
	public void onAdd(Entry contentViewEntry) {
		super.onAdd(contentViewEntry);
		checkContentViewsForOverlap();
	}

	/**
	 * Returns the number of columns in the grid.
	 * 
	 * @return the column count
	 */
	public final int getColumns() {
		return columns;
	}

	/**
	 * Returns the number of rows in the grid.
	 * 
	 * @return the row count
	 */
	public final int getRows() {
		return rows;
	}

	/**
	 * Sets the number of columns in the grid.
	 * 
	 * @param columns the column count (must be ≥ 1)
	 * @throws IllegalArgumentException if columns is less than 1
	 */
	private void setColumns(int columns) {
		if (columns < 1) {
			throw new IllegalArgumentException("columns in grid layout cannot be less than 1");
		}
		this.columns = columns;
	}

	/**
	 * Sets the number of rows in the grid.
	 * 
	 * @param rows the row count (must be ≥ 1)
	 * @throws IllegalArgumentException if rows is less than 1
	 */
	private void setRows(int rows) {
		if (rows < 1) {
			throw new IllegalArgumentException("rows in grid layout cannot be less than 1");
		}
		this.rows = rows;
	}

	/**
	 * Validates that a component's grid placement doesn't extend beyond grid bounds.
	 * 
	 * @param params the grid layout parameters to check
	 * @throws IndexOutOfBoundsException if component extends beyond grid boundaries
	 */
	private void checkOutOfGrid(GridLayoutParams params) {
		if (params.getColumn() + (params.getColumnSpan() - 1) >= getColumns()
				|| (params.getRow() + params.getRowSpan() - 1) >= getRows()) {
			throw new IndexOutOfBoundsException("contentView is out of grid layout");
		}
	}

	/**
	 * Checks that no components overlap in the grid.
	 * 
	 * @throws IllegalArgumentException if multiple components occupy the same grid cells
	 */
	private void checkContentViewsForOverlap() {
		for (Entry entry : getEntryList()) {

			GridLayoutParams params = (GridLayoutParams) entry.layoutParams();

			for (Entry otherEntry : getEntryList()) {

				GridLayoutParams paramsOther = (GridLayoutParams) otherEntry.layoutParams();

				int pc = params.getColumn(), pcs = params.getColumnSpan(), pr = params.getRow(),
						prs = params.getRowSpan();

				int opc = paramsOther.getColumn(), opcs = paramsOther.getColumnSpan(), opr = paramsOther.getRow(),
						oprs = paramsOther.getRowSpan();

				if (params != paramsOther) {
					// Check for overlap in grid coordinates
					if (pc > opc - pcs && pc < opc + opcs && pr > opr - prs && pr < opr + oprs) {
						throw new IllegalArgumentException("several contentViews cannot be in one cell of grid");
					}
				}

			}
		}
	}

}