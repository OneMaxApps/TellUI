package tellui.component;

import static processing.core.PConstants.PROJECT;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import tellui.core.AbstractButton;
import tellui.core.exception.DuplicateItemException;
import tellui.core.interfaces.Listener;
import tellui.core.style.AbstractColor;
import tellui.core.style.Color;
import tellui.core.style.LerpedLoopColor;

/**
 * A clickable CheckBox component that can be toggled between checked and unchecked states.
 * <p>
 * Supports multiple visual styles for the check mark (MARK, DOT, RECT) and notifies
 * registered listeners whenever the state changes (via {@link #setChecked(boolean)}
 * or {@link #toggle()}).
 * </p>
 *
 * @see AbstractButton
 */
public final class CheckBox extends AbstractButton {
	private float cachedCenterX, cachedCenterY, cachedSize;
	private boolean checked;
	private List<Listener> onCheckedListenerList;
	private AbstractColor markColor;
	private Style style; 
	
	/**
	 * Constructs a CheckBox with the specified bounds.
	 *
	 * @param x the x-coordinate of the CheckBox
	 * @param y the y-coordinate of the CheckBox
	 * @param w the width of the CheckBox
	 * @param h the height of the CheckBox
	 */
	public CheckBox(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinMaxSize(20, 20, 50, 50);
		
		onCheckedListenerList = new ArrayList<Listener>();
		
		setMarkColor(new LerpedLoopColor(new Color(0,255), new Color(0,132)).setSpeed(.1f));
		
		onClick(() -> {
			if (isEnterCheckBox()) {
				toggle();
			}
		});
		
		setStyle(Style.MARK);

	}
	
	/**
	 * Constructs a CheckBox with default size, centered on the screen.
	 */
	public CheckBox() {
		this(0,0,0,0);
		
		setInCenter();
	}
	
	/**
	 * Returns the current visual style of the check mark.
	 *
	 * @return the current style (MARK, DOT, or RECT)
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * Sets the visual style of the check mark.
	 *
	 * @param style the new style (must not be null)
	 * @throws NullPointerException if {@code style} is null
	 */
	public void setStyle(Style style) {
		this.style = Objects.requireNonNull(style,"style");
	}

	/**
	 * Returns the color used for the check mark.
	 *
	 * @return the mark color
	 */
	public AbstractColor getMarkColor() {
		return markColor;
	}

	/**
	 * Sets the color of the check mark.
	 *
	 * @param markColor the new mark color (must not be null)
	 * @throws NullPointerException if {@code markColor} is null
	 */
	public void setMarkColor(AbstractColor markColor) {
		this.markColor = Objects.requireNonNull(markColor,"markColor");
	}

	/**
	 * Adds a listener that is called whenever the checked state changes
	 * (via {@link #setChecked(boolean)} or {@link #toggle()}).
	 *
	 * @param listener the listener to add
	 * @throws NullPointerException   if {@code listener} is null
	 * @throws DuplicateItemException if the listener has already been added
	 */
	public void addOnCheckedListener(Listener listener) {
		Objects.requireNonNull(listener,"listener");
		
		if (onCheckedListenerList.contains(listener)) {
			throw new DuplicateItemException("Listener already added");
		}
		
		onCheckedListenerList.add(listener);
	}
	
	/**
	 * Removes a previously added checked-state listener.
	 *
	 * @param listener the listener to remove
	 * @throws NullPointerException   if {@code listener} is null
	 * @throws NoSuchElementException if the listener was not found
	 */
	public void removeOnCheckedListener(Listener listener) {
		Objects.requireNonNull(listener,"listener");
		
		if (!onCheckedListenerList.contains(listener)) {
			throw new NoSuchElementException("Listener not found");
		}
		
		onCheckedListenerList.remove(listener);
	}

	/**
	 * Returns the current checked state.
	 *
	 * @return {@code true} if checked, {@code false} otherwise
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * Sets the checked state. No event is fired if the new state equals the current one.
	 *
	 * @param checked the new checked state
	 */
	public void setChecked(boolean checked) {
		if (this.checked == checked) {
			return;
		}
		
		this.checked = checked;
		
		notifyOnCheckListeners();
	}
	
	/**
	 * Toggles the checked state (switches it to the opposite value).
	 */
	public void toggle() {
		checked = !checked;
		
		notifyOnCheckListeners();
	}

	@Override
	protected void render() {
		if (getShadow() != null) {
			getShadow().setCustomBounds(cachedCenterX, cachedCenterY, cachedSize, cachedSize);
		}
		
		getBackgroundColor().apply();
		getStrokeColor().applyStroke();
		ctx.rect(cachedCenterX, cachedCenterY, cachedSize, cachedSize);
		
		if (checked) {
			markOnRender();
		}
	}
	
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		
		updateCachedBounds();
	}

	private void setInCenter() {
		final float x = ctx.width / 2 - getWidth() / 2;
		final float y = ctx.height / 2 - getHeight() / 2;
		
		setPosition(x, y);
	}
	
	private void updateCachedBounds() {
		cachedSize = Math.min(getWidth(), getHeight());
		cachedCenterX = getX() + getWidth()  / 2 - cachedSize / 2;
		cachedCenterY = getY() + getHeight() / 2 - cachedSize / 2;
	}
	
	private void notifyOnCheckListeners() {
		for (int i = 0; i < onCheckedListenerList.size(); i++) {
			onCheckedListenerList.get(i).action();
		}
	}
	
	private boolean isEnterCheckBox() {
		final float x = cachedCenterX;
		final float y = cachedCenterY;
		final float s = cachedSize;
		
		final float mx = ctx.mouseX;
		final float my = ctx.mouseY;
		
		return mx > x && mx < x + s && my > y && my < y + s;
	}
	
	private void markOnRender() {
		final float x = cachedCenterX;
		final float y = cachedCenterY;
		final float s = cachedSize;
		
		final float offset = s*.12f;
		
		switch(style) {
			case MARK:
				ctx.pushStyle();
				
				markColor.applyStroke();
				ctx.strokeWeight(s*.1f);
				ctx.strokeCap(PROJECT);
				
				
				
				ctx.line(x + offset, y+s/2 + offset, x+s/2, y+s - offset);
				ctx.line(x+s - offset, y + offset, x+s/2, y+s - offset);
				
				ctx.popStyle();
				break;
	
			case DOT:
				ctx.pushStyle();
				markColor.apply();
				markColor.applyStroke();
				ctx.ellipse(x + s / 2, y + s / 2, s*.5f, s*.5f);
				ctx.popStyle();
				break;
				
			case RECT:
				ctx.pushStyle();
				markColor.apply();
				markColor.applyStroke();
				ctx.rect(x + offset, y + offset , s - offset*2 , s - offset*2);
				ctx.popStyle();
				break;	
		}
	}
	
	/**
	 * Defines the visual styles available for the check mark.
	 */
	public static enum Style {
		/**
		 * The default check mark (two crossing lines).
		 */
		MARK,
		
		/**
		 * A filled dot.
		 */
		DOT,
		
		/**
		 * A filled rectangle.
		 */
		RECT;
	}
}