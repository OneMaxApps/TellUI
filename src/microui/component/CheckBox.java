package microui.component;

import static processing.core.PConstants.PROJECT;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import microui.core.AbstractButton;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedLoopColor;
import microui.event.Listener;

//Status: STABLE - Do not modify
//Last Reviewed: 01.03.2026

/**
 * Component CheckBox.
 */
public final class CheckBox extends AbstractButton {
	private float cachedCenterX, cachedCenterY, cachedSize;
	private boolean checked;
	private List<Listener> onCheckedListenerList;
	private AbstractColor markColor;
	private Style style; 
	
	/**
	 * Constructs for CheckBox bounds
	 * @param x current position X
	 * @param y current position Y
	 * @param w current width
	 * @param h current height
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
	 * Constructs default CheckBox bounds in to center of screen
	 */
	public CheckBox() {
		this(0,0,0,0);
		
		setInCenter();
	}
	
	/**
	 * @return style of mark 
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * @param style current style for mark
	 */
	public void setStyle(Style style) {
		this.style = Objects.requireNonNull(style,"style");
	}

	/**
	 * @return color of mark
	 */
	public AbstractColor getMarkColor() {
		return markColor;
	}

	/**
	 * @param markColor current color of mark
	 */
	public void setMarkColor(AbstractColor markColor) {
		this.markColor = Objects.requireNonNull(markColor,"markColor");
	}

	/**
	 * Provides setting for listeners
	 * Listeners calling when state of CheckBox changing
	 * @param listener current listener of change state of CheckBox
	 */
	public void addOnCheckedListener(Listener listener) {
		Objects.requireNonNull(listener,"listener");
		
		if (onCheckedListenerList.contains(listener)) {
			throw new IllegalArgumentException("Listener already added");
		}
		
		onCheckedListenerList.add(listener);
	}
	
	/**
	 * Provides removing listeners
	 * @param listener current listener of change state of CheckBox
	 */
	public void removeOnCheckedListener(Listener listener) {
		Objects.requireNonNull(listener,"listener");
		
		if (!onCheckedListenerList.contains(listener)) {
			throw new IllegalArgumentException("Listener not found");
		}
		
		onCheckedListenerList.remove(listener);
	}

	/**
	 * @return true if checked, false if isn't
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked state of checked
	 */
	public void setChecked(boolean checked) {
		if (this.checked == checked) {
			return;
		}
		
		this.checked = checked;
		
		notifyOnCheckListeners();
	}
	
	/**
	 * change state of checked
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
	 * Provides styles for mark
	 */
	public static enum Style {
		/**
		 * Default form of mark style
		 */
		MARK,
		
		/**
		 * Dot form of mark style
		 */
		DOT,
		
		/**
		 * Rectangle form of mark style
		 */
		RECT;
	}
}