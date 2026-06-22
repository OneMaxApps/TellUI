package tellui.core.base;

import static java.util.Objects.requireNonNull;

import tellui.core.effect.AbstractShadow;
import tellui.util.Debugger;

/**
 * Abstract base class for all GUI components that can be displayed and
 * interacted with. Provides padding, margin, shadow effects, and debugging
 * capabilities.
 * <p>
 * ContentView extends SpatialView to add visual styling and layout spacing
 * features. It manages internal padding (space inside the component) and
 * external margin (space outside the component), and supports shadow effects.
 * </p>
 * 
 * @see SpatialView
 * @see AbstractShadow
 */
public abstract class ContentView extends SpatialView {
	private final Padding padding;
	private final Margin margin;
	private AbstractShadow shadow;

	/**
	 * Constructs a ContentView with specified position and dimensions.
	 * 
	 * @param x      the x-coordinate of the component
	 * @param y      the y-coordinate of the component
	 * @param width  the width of the component
	 * @param height the height of the component
	 */
	public ContentView(float x, float y, float width, float height) {
		super(x, y, width, height);
		setVisible(true);
		setConstrainDimensionsEnabled(true);
		setNegativeDimensionsEnabled(false);

		padding = new Padding();
		margin = new Margin();

		setPaddingEnabled(true);
		setMarginEnabled(true);

	}

	/**
	 * Constructs a ContentView at origin (0,0) with zero dimensions.
	 */
	public ContentView() {
		this(0, 0, 0, 0);
	}

	/**
	 * Draws the component with shadow effects and debug information. Overrides the
	 * parent draw method to add shadow rendering and debug visualization.
	 */
	@Override
	public void draw() {
		if (!isVisible()) {
			return;
		}

		if (shadow != null) {
			shadow.draw();
		}

		super.draw();

		debugOnDraw();
	}

	/**
	 * Returns the shadow effect applied to this component.
	 * 
	 * @return the shadow effect, or null if no shadow is set
	 */
	public final AbstractShadow getShadow() {
		return shadow;
	}

	/**
	 * Sets a shadow effect for this component.
	 * 
	 * @param shadow the shadow effect to apply (cannot be null)
	 * @return this ContentView for method chaining
	 * @throws NullPointerException if shadow is null
	 */
	public final ContentView setShadow(AbstractShadow shadow) {
		this.shadow = requireNonNull(shadow, "shadow");

		shadow.setTarget(this);

		return this;
	}

	/**
	 * Sets the left padding value.
	 * 
	 * @param left the left padding value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPaddingLeft(float left) {
		padding.setLeft(left);

		return this;
	}

	/**
	 * Sets the right padding value.
	 * 
	 * @param right the right padding value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPaddingRight(float right) {
		padding.setRight(right);

		return this;
	}

	/**
	 * Sets the top padding value.
	 * 
	 * @param top the top padding value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPaddingTop(float top) {
		padding.setTop(top);

		return this;
	}

	/**
	 * Sets the bottom padding value.
	 * 
	 * @param bottom the bottom padding value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPaddingBottom(float bottom) {
		padding.setBottom(bottom);

		return this;
	}

	/**
	 * Sets all four padding values individually.
	 * 
	 * @param left   the left padding value
	 * @param right  the right padding value
	 * @param top    the top padding value
	 * @param bottom the bottom padding value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPadding(float left, float right, float top, float bottom) {
		padding.setLeft(left);
		padding.setRight(right);
		padding.setTop(top);
		padding.setBottom(bottom);
		return this;
	}

	/**
	 * Sets horizontal and vertical padding symmetrically.
	 * 
	 * @param paddingHorizontal the horizontal padding (applied to left and right)
	 * @param paddingVertical   the vertical padding (applied to top and bottom)
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPadding(float paddingHorizontal, float paddingVertical) {
		setPadding(paddingHorizontal, paddingHorizontal, paddingVertical, paddingVertical);
		return this;
	}

	/**
	 * Sets uniform padding on all sides.
	 * 
	 * @param padding the padding value for all sides
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPadding(float padding) {
		setPadding(padding, padding);
		return this;
	}

	/**
	 * Copies padding values from another ContentView.
	 * 
	 * @param otherContentView the ContentView to copy padding from (cannot be null)
	 * @return this ContentView for method chaining
	 * @throws NullPointerException if otherContentView is null
	 */
	public final ContentView copyPaddingFrom(ContentView otherContentView) {
		requireNonNull(otherContentView, "otherContentView");

		setPadding(otherContentView.getPaddingLeft(), otherContentView.getPaddingRight(),
				otherContentView.getPaddingTop(), otherContentView.getPaddingBottom());
		return this;
	}

	/**
	 * Returns the left padding value.
	 * 
	 * @return the left padding value
	 */
	public final float getPaddingLeft() {
		return padding.getLeft();
	}

	/**
	 * Returns the right padding value.
	 * 
	 * @return the right padding value
	 */
	public final float getPaddingRight() {
		return padding.getRight();
	}

	/**
	 * Returns the top padding value.
	 * 
	 * @return the top padding value
	 */
	public final float getPaddingTop() {
		return padding.getTop();
	}

	/**
	 * Returns the bottom padding value.
	 * 
	 * @return the bottom padding value
	 */
	public final float getPaddingBottom() {
		return padding.getBottom();
	}

	/**
	 * Returns the x-coordinate including left padding.
	 * 
	 * @return the x-coordinate minus left padding
	 */
	public final float getPadX() {
		return getX() - getPaddingLeft();
	}

	/**
	 * Returns the y-coordinate including top padding.
	 * 
	 * @return the y-coordinate minus top padding
	 */
	public final float getPadY() {
		return getY() - getPaddingTop();
	}

	/**
	 * Returns the width including left and right padding.
	 * 
	 * @return the width plus left and right padding
	 */
	public final float getPadWidth() {
		return getWidth() + getPaddingRight() + getPaddingLeft();
	}

	/**
	 * Returns the height including top and bottom padding.
	 * 
	 * @return the height plus top and bottom padding
	 */
	public final float getPadHeight() {
		return getHeight() + getPaddingBottom() + getPaddingTop();
	}

	/**
	 * Checks if padding is enabled.
	 * 
	 * @return true if padding is enabled, false otherwise
	 */
	public final boolean isPaddingEnabled() {
		return padding.isEnabled();
	}

	/**
	 * Enables or disables padding.
	 * 
	 * @param enabled true to enable padding, false to disable
	 * @return this ContentView for method chaining
	 */
	public final ContentView setPaddingEnabled(boolean enabled) {
		padding.setEnabled(enabled);
		return this;
	}

	/**
	 * Resets all padding values to zero.
	 * 
	 * @return this ContentView for method chaining
	 */
	public final ContentView resetPadding() {
		setPadding(0);
		return this;
	}

	/**
	 * Checks if any padding is applied (non-zero values).
	 * 
	 * @return true if any padding value is greater than 0, false otherwise
	 */
	public final boolean hasPadding() {
		return padding.isEnabled()
				&& (padding.getLeft() > 0 || padding.getRight() > 0 || padding.getTop() > 0 || padding.getBottom() > 0);
	}

	/**
	 * Returns the left margin value.
	 * 
	 * @return the left margin value
	 */
	public final float getMarginLeft() {
		return margin.getLeft();
	}

	/**
	 * Returns the right margin value.
	 * 
	 * @return the right margin value
	 */
	public final float getMarginRight() {
		return margin.getRight();
	}

	/**
	 * Returns the top margin value.
	 * 
	 * @return the top margin value
	 */
	public final float getMarginTop() {
		return margin.getTop();
	}

	/**
	 * Returns the bottom margin value.
	 * 
	 * @return the bottom margin value
	 */
	public final float getMarginBottom() {
		return margin.getBottom();
	}

	/**
	 * Sets all four margin values individually.
	 * 
	 * @param left   the left margin value
	 * @param right  the right margin value
	 * @param top    the top margin value
	 * @param bottom the bottom margin value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMargin(float left, float right, float top, float bottom) {
		margin.setLeft(left);
		margin.setRight(right);
		margin.setTop(top);
		margin.setBottom(bottom);
		return this;
	}

	/**
	 * Sets the left margin value.
	 * 
	 * @param left the left margin value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMarginLeft(float left) {
		margin.setLeft(left);
		return this;
	}

	/**
	 * Sets the right margin value.
	 * 
	 * @param right the right margin value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMarginRight(float right) {
		margin.setRight(right);
		return this;
	}

	/**
	 * Sets the top margin value.
	 * 
	 * @param top the top margin value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMarginTop(float top) {
		margin.setTop(top);
		return this;
	}

	/**
	 * Sets the bottom margin value.
	 * 
	 * @param bottom the bottom margin value
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMarginBottom(float bottom) {
		margin.setBottom(bottom);
		return this;
	}

	/**
	 * Sets horizontal and vertical margin symmetrically.
	 * 
	 * @param marginHorizontal the horizontal margin (applied to left and right)
	 * @param marginVertical   the vertical margin (applied to top and bottom)
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMargin(float marginHorizontal, float marginVertical) {
		setMargin(marginHorizontal, marginHorizontal, marginVertical, marginVertical);
		return this;
	}

	/**
	 * Sets uniform margin on all sides.
	 * 
	 * @param margin the margin value for all sides
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMargin(float margin) {
		setMargin(margin, margin);
		return this;
	}

	/**
	 * Copies margin values from another ContentView.
	 * 
	 * @param otherContentView the ContentView to copy margin from (cannot be null)
	 * @return this ContentView for method chaining
	 * @throws NullPointerException if otherContentView is null
	 */
	public final ContentView copyMarginFrom(ContentView otherContentView) {
		requireNonNull(otherContentView, "otherContentView");

		setMargin(otherContentView.getMarginLeft(), otherContentView.getMarginRight(), otherContentView.getMarginTop(),
				otherContentView.getMarginBottom());

		return this;
	}

	/**
	 * Resets all margin values to zero.
	 * 
	 * @return this ContentView for method chaining
	 */
	public final ContentView resetMargin() {
		setMargin(0);
		return this;
	}

	/**
	 * Checks if margin is enabled.
	 * 
	 * @return true if margin is enabled, false otherwise
	 */
	public final boolean isMarginEnabled() {
		return margin.isEnabled();
	}

	/**
	 * Enables or disables margin.
	 * 
	 * @param enabled true to enable margin, false to disable
	 * @return this ContentView for method chaining
	 */
	public final ContentView setMarginEnabled(boolean enabled) {
		margin.setEnabled(enabled);
		return this;
	}

	/**
	 * Checks if any margin is applied (non-zero values).
	 * 
	 * @return true if any margin value is greater than 0, false otherwise
	 */
	public final boolean hasMargin() {
		return margin.isEnabled()
				&& (margin.getLeft() > 0 || margin.getRight() > 0 || margin.getTop() > 0 || margin.getBottom() > 0);
	}

	/**
	 * Returns the absolute x-coordinate including margin.
	 * 
	 * @return the x-coordinate minus left padding and left margin
	 */
	public final float getAbsoluteX() {
		return getPadX() - getMarginLeft();
	}

	/**
	 * Returns the absolute y-coordinate including margin.
	 * 
	 * @return the y-coordinate minus top padding and top margin
	 */
	public final float getAbsoluteY() {
		return getPadY() - getMarginTop();
	}

	/**
	 * Returns the absolute width including padding and margin.
	 * 
	 * @return the width plus padding and margin on both sides
	 */
	public final float getAbsoluteWidth() {
		return getPadWidth() + getMarginLeft() + getMarginRight();
	}

	/**
	 * Returns the absolute height including padding and margin.
	 * 
	 * @return the height plus padding and margin on both sides
	 */
	public final float getAbsoluteHeight() {
		return getPadHeight() + getMarginTop() + getMarginBottom();
	}

	/**
	 * Sets the absolute x-coordinate (including margin).
	 * 
	 * @param x the absolute x-coordinate
	 */
	public final void setAbsoluteX(float x) {
		setX(x + getMarginLeft() + getPaddingLeft());
	}

	/**
	 * Sets the absolute y-coordinate (including margin).
	 * 
	 * @param y the absolute y-coordinate
	 */
	public final void setAbsoluteY(float y) {
		setY(y + getMarginTop() + getPaddingTop());
	}

	/**
	 * Sets the absolute width (including padding and margin).
	 * 
	 * @param width the absolute width
	 */
	public final void setAbsoluteWidth(float width) {
		setWidth(width - (getMarginLeft() + getMarginRight()) - (getPaddingLeft() + getPaddingRight()));
	}

	/**
	 * Sets the absolute height (including padding and margin).
	 * 
	 * @param height the absolute height
	 */
	public final void setAbsoluteHeight(float height) {
		setHeight(height - (getMarginTop() + getMarginBottom()) - (getPaddingTop() + getPaddingBottom()));
	}

	/**
	 * Sets the absolute position (including margin).
	 * 
	 * @param x the absolute x-coordinate
	 * @param y the absolute y-coordinate
	 */
	public final void setAbsolutePosition(float x, float y) {
		setAbsoluteX(x);
		setAbsoluteY(y);
	}

	/**
	 * Resizes the component using absolute dimensions, taking margin into account.
	 * This method adjust the real width and height but does not bypass the constrain system and does not modify margin parameters.
	 * 
	 * @param width  change real width with using margin
	 * @param height change real height with using margin
	 * 
	 */
	public final void setAbsoluteSize(float width, float height) {
		setAbsoluteWidth(width);
		setAbsoluteHeight(height);
	}

	/**
	 * Changes the bounds using absolute values, taking margin into account.
	 * This method adjust the real bounds but does not bypass the constrain system and does not modify margin parameters. 
	 * 
	 * @param x      change position with margin left
	 * @param y      change position with margin top
	 * @param width  change real width with using margin
	 * @param height change real height with using margin
	 * 
	 */
	public final void setAbsoluteBounds(float x, float y, float width, float height) {
		setAbsoluteSize(width, height);
		setAbsolutePosition(x, y);
	}

	/**
	 * Copies margin values from another ContentView.
	 * 
	 * @param other the ContentView to copy margin from (cannot be null)
	 * @throws NullPointerException if other is null
	 */
	public final void setMarginFrom(ContentView other) {
		requireNonNull(other, "other");

		setMargin(other.getMarginLeft(), other.getMarginRight(), other.getMarginTop(), other.getMarginBottom());
	}

	/**
	 * Copies padding values from another ContentView.
	 * 
	 * @param other the ContentView to copy padding from (cannot be null)
	 * @throws NullPointerException if other is null
	 */
	public final void setPaddingFrom(ContentView other) {
		requireNonNull(other, "other");

		setPadding(other.getPaddingLeft(), other.getPaddingRight(), other.getPaddingTop(), other.getPaddingBottom());
	}

	/**
	 * Copies absolute position from another ContentView.
	 * 
	 * @param other the ContentView to copy position from (cannot be null)
	 * @throws NullPointerException if other is null
	 */
	public final void setAbsolutePositionFrom(ContentView other) {
		requireNonNull(other, "other");

		setAbsolutePosition(other.getAbsoluteX(), other.getAbsoluteY());
	}

	/**
	 * Copies absolute dimensions from another ContentView.
	 * 
	 * @param other the ContentView to copy dimensions from (cannot be null)
	 * @throws NullPointerException if other is null
	 */
	public final void setAbsoluteDimensionsFrom(ContentView other) {
		requireNonNull(other, "other");

		setAbsoluteSize(other.getAbsoluteWidth(), other.getAbsoluteHeight());

	}

	/**
	 * Copies absolute bounds from another ContentView.
	 * 
	 * @param other the ContentView to copy bounds from (cannot be null)
	 * @throws NullPointerException if other is null
	 */
	public final void setAbsoluteBoundsFrom(ContentView other) {
		requireNonNull(other, "other");

		setAbsolutePositionFrom(other);
		setAbsoluteDimensionsFrom(other);
	}

	private void debugOnDraw() {
		if (Debugger.isEnabled()) {
			ctx.pushStyle();
			ctx.noFill();
			ctx.strokeWeight(4);

			if (hasMargin() && Debugger.isShowMarginBoundsEnabled()) {
				// for showing margin area (Red rectangle)
				ctx.stroke(200, 0, 0, 100);
				ctx.rect(getAbsoluteX(), getAbsoluteY(), getAbsoluteWidth(), getAbsoluteHeight());
			}

			if (hasPadding() && Debugger.isShowPaddingBoundsEnabled()) {
				// for showing pad area (Green rectangle)
				ctx.stroke(0, 200, 0, 100);
				ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
			}

			if (Debugger.isShowContentBoundsEnabled()) {
			// for showing content area (Blue rectangle)
			ctx.stroke(0, 0, 200, 100);
			ctx.rect(getX(), getY(), getWidth(), getHeight());
			}
			
			ctx.popStyle();
		}
	}

	private final class Padding {
		float left, right, top, bottom;
		boolean enabled;

		boolean isEnabled() {
			return enabled;
		}

		void setEnabled(boolean enabled) {
			this.enabled = enabled;
			if (enabled) {
				checkCorrectState();
			}
		}

		float getLeft() {
			return enabled ? left : 0;
		}

		float getRight() {
			return enabled ? right : 0;
		}

		float getTop() {
			return enabled ? top : 0;
		}

		float getBottom() {
			return enabled ? bottom : 0;
		}

		void setLeft(float left) {
			if (!isCorrectNewValue(this.left, left)) {
				return;
			}
			this.left = left;
		}

		void setRight(float right) {
			if (!isCorrectNewValue(this.right, right)) {
				return;
			}
			this.right = right;
		}

		void setTop(float top) {
			if (!isCorrectNewValue(this.top, top)) {
				return;
			}
			this.top = top;
		}

		void setBottom(float bottom) {
			if (!isCorrectNewValue(this.bottom, bottom)) {
				return;
			}
			this.bottom = bottom;
		}

		boolean isCorrectNewValue(float currentValue, float newValue) {
			if (newValue < 0) {
				throw new IllegalArgumentException("Padding cannot be less than zero");
			}

			if (newValue == currentValue) {
				return false;
			}

			return true;
		}

		void checkCorrectState() {
			if (isNegativeDimensionsEnabled()) {
				throw new IllegalStateException("Negative dimensions must be disabled for using Padding system");
			}
		}

	}

	private final class Margin {
		private float left, right, top, bottom;
		private boolean enabled;

		float getLeft() {
			return enabled ? left : 0;
		}

		float getRight() {
			return enabled ? right : 0;
		}

		float getTop() {
			return enabled ? top : 0;
		}

		float getBottom() {
			return enabled ? bottom : 0;
		}

		void setLeft(float left) {
			checkValue(left);
			this.left = left;
		}

		void setRight(float right) {
			checkValue(right);
			this.right = right;
		}

		void setTop(float top) {
			checkValue(top);
			this.top = top;
		}

		void setBottom(float bottom) {
			checkValue(bottom);
			this.bottom = bottom;
		}

		boolean isEnabled() {
			return enabled;
		}

		void setEnabled(boolean enabled) {
			this.enabled = enabled;

			if (enabled) {
				checkCorrectState();
			}
		}

		void checkValue(float value) {
			if (value < 0) {
				throw new IllegalArgumentException("Margin cannot be less than zero");
			}
		}

		void checkCorrectState() {
			if (isNegativeDimensionsEnabled()) {
				throw new IllegalStateException("Negative dimensions must be disabled for using Margin system");
			}
		}
	}
}