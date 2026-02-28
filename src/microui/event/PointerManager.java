package microui.event;

import static java.util.Objects.requireNonNull;
import static microui.core.base.View.IGNORE_INTERNAL_COMPONENT_ID;

import microui.MicroUI;
import microui.core.base.Component;
import microui.core.base.SpatialView;
import processing.core.PApplet;

public final class PointerManager {
	private static SpatialView owner;
	
	private PointerManager() {
	}
	
	public static boolean request(SpatialView spatialView) {
		requireNonNull(spatialView,"spatialView");
		
		if (!(spatialView instanceof Component)) {
			return false;
		}
		
		if (spatialView.getId() == IGNORE_INTERNAL_COMPONENT_ID) {
			return false;
		}
		
		final PApplet p = MicroUI.getContext();
		final boolean pressed = p.mousePressed;
		
		if (owner == spatialView) {
			return true;
		}
		
		final float mx = p.mouseX;
		final float my = p.mouseY;
		
		final float x = spatialView.getX();
		final float y = spatialView.getY();
		final float w = spatialView.getWidth();
		final float h = spatialView.getHeight();
		
		if (pressed) {
			if (mx > x && mx <  x+w && my > y && my < y + h) {
				if (owner == null) {
					owner = spatialView;
				}
			}
		}
		
		return owner == spatialView;
	}
	
	public static boolean hasOwner() {
		return owner != null;
	}
	
	public static boolean isOwner(SpatialView spatialView) {
		return owner == requireNonNull(spatialView,"spatialView");
	}
	
	public static void release() {
		owner = null;
	}
}