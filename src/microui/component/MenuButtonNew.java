package microui.component;

import java.util.ArrayList;
import java.util.List;

import microui.core.base.SpatialView;
import microui.core.base.View;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.Stroke;
import processing.event.MouseEvent;

// if menu is root - vertical list else horizontal swing
public final class MenuButtonNew extends Button implements Scrollable {
	private final Items items;
	private final Mark mark;
	private boolean isOpen, isRoot;

	public MenuButtonNew(String text, float x, float y, float w, float h) {
		super(text, x, y, w, h);
		items = new Items(this);
		mark = new Mark(this);
		onClick(() -> setOpen(!isOpen()));
		setRoot(true);
	}

	public MenuButtonNew(float x, float y, float w, float h) {
		this("Menu Button", x, y, w, h);
	}

	public MenuButtonNew(String text) {
		this(1, 1, 1, 1);
		setSize(getMinWidth(), getMinHeight());
		setPosition(ctx.width / 2 - getWidth() / 2, ctx.height / 2 - getHeight() / 2);
		setText(text);
	}

	public MenuButtonNew() {
		this("Menu Button");
	}

	public boolean isRoot() {
		return isRoot;
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		items.mouseWheel(mouseEvent);
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void add(String title) {
		items.addInternal(new Button(title));
	}
	
	public void setMarkColor(AbstractColor color) {
		mark.setColor(color);
	}
	
	public AbstractColor getMarkColor() {
		return mark.getColor();
	}

	@Override
	protected void render() {
		super.render();

		if (isOpen()) {
			items.draw();
		}

		mark.draw();
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		
		items.recalculatePosition();
		
		mark.setBoundsFrom(this);
	}

	private void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	private static final class Items extends View implements Scrollable {
		private final MenuButtonNew parent;
		private final List<Button> list;

		public Items(MenuButtonNew parent) {
			super();
			setVisible(true);
			this.parent = parent;
			list = new ArrayList<Button>();

		}

		@Override
		public void mouseWheel(MouseEvent mouseEvent) {
			if (list.size() <= 1) {
				return;
			}

			final boolean isDown = mouseEvent.getCount() > 0;

			final float firstItemY = list.get(0).getY();
			final float lastItemY = list.get(list.size()-1).getY();
			
			for (int i = 0; i < list.size(); i++) {
				Button b = list.get(i);
				b.setY(isDown ? b.getY() + b.getAbsoluteHeight() : b.getY() - b.getAbsoluteHeight());
			}
			
			if(isDown) {
				list.add(0,list.remove(list.size()-1));
				list.get(0).setY(firstItemY);
			} else {
				list.add(list.size()-1,list.remove(0));
				list.get(list.size()-1).setY(lastItemY);
			}
		}

		public void recalculatePosition() {
			float totalHeight = 0;
			for (int i = 0; i < list.size(); i++) {
				Button b = list.get(i);

				// vertical list under the root menu
				if (parent.isRoot()) {
					b.setX(parent.getX());
					b.setY(parent.getY() + parent.getHeight() + totalHeight);
					b.setWidth(b.getWidth());
					b.setHeight(parent.getHeight());
				} else {
					// swing on horizontal
				}

				totalHeight += b.getAbsoluteHeight();
			}
		}

		@Override
		protected void render() {
			itemsOnDraw();
		}

		private void itemsOnDraw() {
			if (list.isEmpty()) {
				return;
			}

			for (int i = 0; i < list.size(); i++) {
				list.get(i).draw();
			}
		}

		private void addInternal(Button button) {
			if (button == null) {
				throw new NullPointerException("Button cannot be null");
			}

			if (list.contains(button)) {
				throw new IllegalArgumentException("Button cannot be added twice in MenuButton");
			}

			if (button instanceof MenuButtonNew subMenu) {
				subMenu.setRoot(false);
			}

			list.add(button);

			recalculatePosition();
		}

	}
	
	private static class Mark extends SpatialView {
		private final Stroke stroke;
		
		public Mark(MenuButtonNew menu) {
			setVisible(true);
			stroke = new Stroke();
			setColor(new GradientColor(Color.TRANSPARENT, Color.GREEN, () -> menu.isOpen()));
		}

		public AbstractColor getColor() {
			return stroke.getColor();
		}

		public void setColor(AbstractColor color) {
			stroke.setColor(color);
		}

		@Override
		protected void render() {
			ctx.noFill();
			stroke.apply();
			ctx.rect(getX(),getY(),getWidth(),getHeight());
		}
		
	}
}