package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public final class TextArea extends Component implements KeyPressable, Scrollable {
	private static final int MIN_SIZE = 100;
	private static final int MAX_SIZE = 1000;
	private final ScrollManager scrollManager;
	
	public TextArea(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(MIN_SIZE, MAX_SIZE);
		setBackgroundColor(getTheme().getEditableBackgroundColor());
		
		scrollManager = new ScrollManager();
	}

	public TextArea() {
		this(0, 0, 1, 1);
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		scrollManager.mouseWheel(mouseEvent);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void render() {
		backgroundOnDraw();
		scrollManager.onDraw();
	}
	
	@Override
	protected void onChangeBounds() {
		scrollManager.recalculateBounds();
	}

	private void backgroundOnDraw() {
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
	}
	
	private final class ScrollManager {
		private static final int WEIGHT = 10;
		private final Scroll scrollH, scrollV;

		public ScrollManager() {
			super();
			scrollH = new Scroll();
			scrollV = new Scroll();
			
			prepareStyle();
			recalculateBounds();
		}
		
		public void onDraw() {
			scrollH.draw();
			scrollV.draw();
		}
		
		public void recalculateBounds() {
			final TextArea t = TextArea.this;
			
			scrollH.setSize(t.getWidth() - WEIGHT, WEIGHT);
			scrollH.setPosition(t.getX(), t.getY() + t.getHeight() - WEIGHT);
			
			scrollV.setSize(WEIGHT, t.getHeight() - WEIGHT);
			scrollV.setPosition(t.getX() + t.getWidth() - WEIGHT, t.getY());
			
		}
		
		public void mouseWheel(MouseEvent mouseEvent) {
			scrollH.mouseWheel(mouseEvent);
			scrollV.mouseWheel(mouseEvent);
		}
		
		private void prepareStyle() {
			scrollH.setConstrainDimensionsEnabled(false);
			
			scrollV.setConstrainDimensionsEnabled(false);
			scrollV.swapOrientation();
		}
		
	}
}