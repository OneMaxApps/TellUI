package microui.component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import microui.core.AbstractButton;
import microui.core.base.SpatialView;
import microui.core.effect.Hover;
import microui.core.effect.Ripples;
import microui.core.effect.SpatialAnimator;
import microui.core.exception.DuplicateItemException;
import microui.core.interfaces.Listener;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.util.SpatialState;

/**
 * A toggle button component that can be switched between two states (active/inactive).
 * The button visually indicates its state through a sliding thumb and a background color
 * that changes based on the active state. Supports ripple effects, hover effects,
 * and state change listeners.
 */
public final class ToggleButton extends AbstractButton {
	private static final int DEFAULT_MIN_WIDTH = 20;
	private static final int DEFAULT_MIN_HEIGHT = 10;
	private static final int DEFAULT_MAX_WIDTH = 50;
	private static final int DEFAULT_MAX_HEIGHT = 25;
	private final Ripples ripples;
	private final Hover hover;
	private final SpatialView thumb;
	private final List<Listener> listenerList;
	private AbstractColor currentStateColor, activeStateColor, thumbColor;
	private boolean active;
	
	/**
	 * Constructs a toggle button with the specified position and size.
	 *
	 * @param x the x-coordinate of the button's top-left corner
	 * @param y the y-coordinate of the button's top-left corner
	 * @param w the width of the button
	 * @param h the height of the button
	 */
	public ToggleButton(float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinMaxSize(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
		setActiveStateColor(Color.GREEN);
		currentStateColor = new  LerpedColor(() -> getBackgroundColor(), () -> getActiveStateColor(), () -> isActive()).setSpeed(.1f);
		setThumbColor(Color.GRAY_164L);
		
		ripples = new Ripples(this);
		hover = new Hover(this);
		thumb = createThumb();
		
		onClick(() -> toggle());
		
		listenerList = new ArrayList<Listener>();
		
	}
	
	/**
	 * Constructs a toggle button with default size and positions it at the center of the screen.
	 */
	public ToggleButton() {
		this(0,0,0,0);
		initPositionInCenter();
	}

	/**
	 * Adds a listener to be notified when the active state of the button changes.
	 *
	 * @param listener the listener to add (must not be {@code null})
	 * @throws NullPointerException if {@code listener} is {@code null}
	 * @throws DuplicateItemException if the listener has already been added
	 */
	public void addOnStateChangedListener(Listener listener) {
		Objects.requireNonNull(listener, "listener");
		
		if (listenerList.contains(listener)) {
			throw new DuplicateItemException("Listener already added");
		}
		
		listenerList.add(listener);
	}
	
	/**
	 * Removes a previously added state change listener.
	 *
	 * @param listener the listener to remove (must not be {@code null})
	 * @throws NullPointerException if {@code listener} is {@code null}
	 * @throws NoSuchElementException if the listener is not found
	 */
	public void removeOnStateChangedListener(Listener listener) {
		Objects.requireNonNull(listener, "listener");
		
		if (!listenerList.contains(listener)) {
			throw new NoSuchElementException("Listener not found");
		}
		
		listenerList.remove(listener);
	}
	
	/**
	 * Returns the current color of the sliding thumb.
	 *
	 * @return the thumb color (never {@code null})
	 */
	public AbstractColor getThumbColor() {
		return thumbColor;
	}

	/**
	 * Sets the color of the sliding thumb.
	 *
	 * @param thumbColor the new thumb color (must not be {@code null})
	 * @throws NullPointerException if {@code thumbColor} is {@code null}
	 */
	public void setThumbColor(AbstractColor thumbColor) {
		this.thumbColor = Objects.requireNonNull(thumbColor, "thumbColor");
	}

	/**
	 * Returns the color used for the background when the button is in the active state.
	 *
	 * @return the active state color (never {@code null})
	 */
	public AbstractColor getActiveStateColor() {
		return activeStateColor;
	}

	/**
	 * Sets the color used for the background when the button becomes active.
	 *
	 * @param activeStateColor the new active state color (must not be {@code null})
	 * @throws NullPointerException if {@code activeStateColor} is {@code null}
	 */
	public void setActiveStateColor(AbstractColor activeStateColor) {
		this.activeStateColor = Objects.requireNonNull(activeStateColor, "activeStateColor");
	}

	/**
	 * Checks whether the button is currently in the active state.
	 *
	 * @return {@code true} if active, {@code false} otherwise
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active state of the button. If the new state differs from the current one,
	 * all registered state change listeners are notified.
	 *
	 * @param active {@code true} to set active, {@code false} to set inactive
	 */
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		
		this.active = active;
		
		notifyListenersOnActiveStateChange();
	}
	
	/**
	 * Toggles the active state of the button. Equivalent to {@code setActive(!isActive())}.
	 */
	public void toggle() {
		setActive(!isActive());
	}

	@Override
	protected void render() {
		getStrokeInternal().apply();
		currentStateColor.apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ripples.draw();
		hover.draw();
		thumb.draw();
	}

	private void initPositionInCenter() {
		setPosition(ctx.width / 2 - getWidth() / 2, ctx.height / 2 - getHeight() / 2);
	}
	
	private SpatialView createThumb() {
		final var thumb = new SpatialView() {
			@Override
			protected void render() {
				getThumbColor().apply();
				ctx.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
			}
		};
		
		thumb.setVisible(true);
		
		final var startState = new SpatialState();
		startState.setSupplierX(() -> getX());
		startState.setSupplierY(() -> getY());
		startState.setSupplierWidth(() -> getWidth() / 2);
		startState.setSupplierHeight(() -> getHeight());
		
		final var endState = new SpatialState();
		endState.setSupplierX(() -> getX() + getWidth() / 2);
		endState.setSupplierY(() -> getY());
		endState.setSupplierWidth(() -> getWidth() / 2);
		endState.setSupplierHeight(() -> getHeight());
		
		final var animator = new SpatialAnimator(startState, endState, () -> isActive());
		
		thumb.setSpatialAnimator(animator);
		
		return thumb;
	}
	
	private void notifyListenersOnActiveStateChange() {
		if (listenerList.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < listenerList.size(); i++) {
			listenerList.get(i).action();
		}
	}
}