package microui.core.base;

import static java.util.Objects.requireNonNull;

import microui.core.effect.AbstractShadow;
import microui.util.Debugger;

//Status: STABLE - Do not modify
//Last Reviewed: 21.10.2025
public abstract class ContentView extends SpatialView {
	private final Padding padding;
	private final Margin margin;
	private AbstractShadow shadow;
	
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

	public ContentView() {
		this(0, 0, 0, 0);
	}

	@Override
	public void draw() {
		if (!isVisible()) {
			return;
		}

		if(shadow != null) {
			shadow.draw();
		}
		
		super.draw();

		debugOnDraw();
	}
	
	public final AbstractShadow getShadow() {
		return shadow;
	}

	public final ContentView setShadow(AbstractShadow shadow) {
		this.shadow = requireNonNull(shadow,"shadow");
		
		shadow.setTarget(this);
		
		return this;
	}

	public final ContentView setPaddingLeft(float left) {
		padding.setLeft(left);

		return this;
	}

	public final ContentView setPaddingRight(float right) {
		padding.setRight(right);

		return this;
	}

	public final ContentView setPaddingTop(float top) {
		padding.setTop(top);

		return this;
	}

	public final ContentView setPaddingBottom(float bottom) {
		padding.setBottom(bottom);

		return this;
	}

	public final ContentView setPadding(float left, float right, float top, float bottom) {
		padding.setLeft(left);
		padding.setRight(right);
		padding.setTop(top);
		padding.setBottom(bottom);
		return this;
	}

	public final ContentView setPadding(float paddingHorizontal, float paddingVertical) {
		setPadding(paddingHorizontal, paddingHorizontal, paddingVertical, paddingVertical);
		return this;
	}

	public final ContentView setPadding(float padding) {
		setPadding(padding, padding);
		return this;
	}

	public final ContentView copyPaddingFrom(ContentView otherContentView) {
		requireNonNull(otherContentView, "otherContentView");
		
		setPadding(otherContentView.getPaddingLeft(),otherContentView.getPaddingRight(), otherContentView.getPaddingTop(),otherContentView.getPaddingBottom());
		return this;
	}

	public final float getPaddingLeft() {
		return padding.getLeft();
	}

	public final float getPaddingRight() {
		return padding.getRight();
	}

	public final float getPaddingTop() {
		return padding.getTop();
	}

	public final float getPaddingBottom() {
		return padding.getBottom();
	}

	public final float getPadX() {
		return getX() - getPaddingLeft();
	}

	public final float getPadY() {
		return getY() - getPaddingTop();
	}

	public final float getPadWidth() {
		return getWidth() + getPaddingRight() + getPaddingLeft();
	}

	public final float getPadHeight() {
		return getHeight() + getPaddingBottom() + getPaddingTop();
	}

	public final boolean isPaddingEnabled() {
		return padding.isEnabled();
	}

	public final ContentView setPaddingEnabled(boolean isEnabled) {
		padding.setEnabled(isEnabled);
		return this;
	}

	public final ContentView resetPadding() {
		setPadding(0);
		return this;
	}

	public final boolean hasPadding() {
		return padding.isEnabled()
				&& (padding.getLeft() > 0 || padding.getRight() > 0 || padding.getTop() > 0 || padding.getBottom() > 0);
	}

	public final float getMarginLeft() {
		return margin.getLeft();
	}

	public final float getMarginRight() {
		return margin.getRight();
	}

	public final float getMarginTop() {
		return margin.getTop();
	}

	public final float getMarginBottom() {
		return margin.getBottom();
	}

	public final ContentView setMargin(float left, float right, float top, float bottom) {
		margin.setLeft(left);
		margin.setRight(right);
		margin.setTop(top);
		margin.setBottom(bottom);
		return this;
	}

	public final ContentView setMarginLeft(float left) {
		margin.setLeft(left);
		return this;
	}

	public final ContentView setMarginRight(float right) {
		margin.setRight(right);
		return this;
	}

	public final ContentView setMarginTop(float top) {
		margin.setTop(top);
		return this;
	}

	public final ContentView setMarginBottom(float bottom) {
		margin.setBottom(bottom);
		return this;
	}

	public final ContentView setMargin(float marginHorizontal, float marginVertical) {
		setMargin(marginHorizontal, marginHorizontal, marginVertical, marginVertical);
		return this;
	}

	public final ContentView setMargin(float margin) {
		setMargin(margin, margin);
		return this;
	}

	public final ContentView copyMarginFrom(ContentView otherContentView) {
		requireNonNull(otherContentView, "otherContentView");
		
		setMargin(otherContentView.getMarginLeft(),otherContentView.getMarginRight(), otherContentView.getMarginTop(), otherContentView.getMarginBottom());
		
		return this;
	}

	public final ContentView resetMargin() {
		setMargin(0);
		return this;
	}

	public final boolean isMarginEnabled() {
		return margin.isEnabled();
	}

	public final ContentView setMarginEnabled(boolean isEnabled) {
		margin.setEnabled(isEnabled);
		return this;
	}

	public final boolean hasMargin() {
		return margin.isEnabled()
				&& (margin.getLeft() > 0 || margin.getRight() > 0 || margin.getTop() > 0 || margin.getBottom() > 0);
	}

	public final float getAbsoluteX() {
		return getPadX() - getMarginLeft();
	}

	public final float getAbsoluteY() {
		return getPadY() - getMarginTop();
	}

	public final float getAbsoluteWidth() {
		return getPadWidth() + getMarginLeft() + getMarginRight();
	}

	public final float getAbsoluteHeight() {
		return getPadHeight() + getMarginTop() + getMarginBottom();
	}

	public final void setAbsoluteX(float x) {
		setX(x + getMarginLeft() + getPaddingLeft());
	}

	public final void setAbsoluteY(float y) {
		setY(y + getMarginTop() + getPaddingTop());
	}

	public final void setAbsoluteWidth(float width) {
		setWidth(width - (getMarginLeft() + getMarginRight()) - (getPaddingLeft() + getPaddingRight()));
	}

	public final void setAbsoluteHeight(float height) {
		setHeight(height - (getMarginTop() + getMarginBottom()) - (getPaddingTop() + getPaddingBottom()));
	}

	public final void setAbsolutePosition(float x, float y) {
		setAbsoluteX(x);
		setAbsoluteY(y);
	}

	/**
	 * Resize using margin. this method can change real width and height but it's
	 * don't ignore constrain system. Also this method can't modify margin
	 * parameters
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
	 * Change bounds using margin. this method can change real bounds, but it's
	 * don't ignore constrain system. Also this method can't modify margin
	 * parameters
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

	public final void setMarginFrom(ContentView other) {
		requireNonNull(other,"other");

		setMargin(other.getMarginLeft(), other.getMarginRight(), other.getMarginTop(), other.getMarginBottom());
	}

	public final void setPaddingFrom(ContentView other) {
		requireNonNull(other,"other");

		setPadding(other.getPaddingLeft(), other.getPaddingRight(), other.getPaddingTop(), other.getPaddingBottom());
	}

	public final void setAbsolutePositionFrom(ContentView other) {
		requireNonNull(other,"other");
		
		setAbsolutePosition(other.getAbsoluteX(), other.getAbsoluteY());
	}

	public final void setAbsoluteDimensionsFrom(ContentView other) {
		requireNonNull(other,"other");

		setAbsoluteSize(other.getAbsoluteWidth(), other.getAbsoluteHeight());

	}

	public final void setAbsoluteBoundsFrom(ContentView other) {
		requireNonNull(other,"other");

		setAbsolutePositionFrom(other);
		setAbsoluteDimensionsFrom(other);
	}

	private void debugOnDraw() {
		if (Debugger.isDebugModeEnabled()) {
			ctx.pushStyle();
			ctx.noFill();
			ctx.strokeWeight(4);

			if (hasMargin()) {
				// for showing margin area (Red rectangle)
				ctx.stroke(200, 0, 0, 100);
				ctx.rect(getAbsoluteX(), getAbsoluteY(), getAbsoluteWidth(), getAbsoluteHeight());
			}

			if (hasPadding()) {
				// for showing pad area (Green rectangle)
				ctx.stroke(0, 200, 0, 100);
				ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
			}

			// for showing content area (Blue rectangle)
			ctx.stroke(0, 0, 200, 100);
			ctx.rect(getX(), getY(), getWidth(), getHeight());

			ctx.noStroke();
			ctx.popStyle();
		}
	}

	private final class Padding {
		float left, right, top, bottom;
		boolean isEnabled;

		boolean isEnabled() {
			return isEnabled;
		}

		void setEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;
			if (isEnabled) {
				checkCorrectState();
			}
		}

		float getLeft() {
			return isEnabled ? left : 0;
		}

		float getRight() {
			return isEnabled ? right : 0;
		}

		float getTop() {
			return isEnabled ? top : 0;
		}

		float getBottom() {
			return isEnabled ? bottom : 0;
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
				throw new IllegalArgumentException("padding cannot be less than zero");
			}

			if (newValue == currentValue) {
				return false;
			}

			return true;
		}

		void checkCorrectState() {
			if (isNegativeDimensionsEnabled()) {
				throw new IllegalStateException("negative dimensions must be disabled for using Padding system");
			}
		}

	}

	private final class Margin {
		private float left, right, top, bottom;
		private boolean isEnabled;

		float getLeft() {
			return isEnabled ? left : 0;
		}

		float getRight() {
			return isEnabled ? right : 0;
		}

		float getTop() {
			return isEnabled ? top : 0;
		}

		float getBottom() {
			return isEnabled ? bottom : 0;
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
			return isEnabled;
		}

		void setEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;

			if (isEnabled) {
				checkCorrectState();
			}
		}

		void checkValue(float value) {
			if (value < 0) {
				throw new IllegalArgumentException("margin cannot be less than zero");
			}
		}

		void checkCorrectState() {
			if (isNegativeDimensionsEnabled()) {
				throw new IllegalStateException("negative dimensions must be disabled for using Margin system");
			}
		}
	}
}