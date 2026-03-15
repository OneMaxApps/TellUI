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
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.event.Listener;
import microui.util.SpatialState;

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
	
	public ToggleButton() {
		this(0,0,0,0);
		initPositionInCenter();
	}

	public void addOnStateChangedListener(Listener listener) {
		Objects.requireNonNull(listener, "listener");
		
		if (listenerList.contains(listener)) {
			throw new DuplicateItemException("Listener already added");
		}
		
		listenerList.add(listener);
	}
	
	public void removeOnStateChangedListener(Listener listener) {
		Objects.requireNonNull(listener, "listener");
		
		if (!listenerList.contains(listener)) {
			throw new NoSuchElementException("Listener not found");
		}
		
		listenerList.remove(listener);
	}
	
	public AbstractColor getThumbColor() {
		return thumbColor;
	}

	public void setThumbColor(AbstractColor thumbColor) {
		this.thumbColor = Objects.requireNonNull(thumbColor, "thumbColor");
	}

	public AbstractColor getActiveStateColor() {
		return activeStateColor;
	}

	public void setActiveStateColor(AbstractColor activeStateColor) {
		this.activeStateColor = Objects.requireNonNull(activeStateColor, "activeStateColor");
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		
		this.active = active;
		
		notifyListenersOnActiveStateChange();
	}
	
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