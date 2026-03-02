package microui.event;

import static java.util.Objects.requireNonNull;
import static microui.core.base.View.IGNORE_INTERNAL_COMPONENT_ID;

import microui.MicroUI;
import microui.core.base.Component;
import microui.core.base.SpatialView;
import microui.core.base.View;
import microui.core.interfaces.ModalParent;
import processing.core.PApplet;

/**
 * Manages pointer (mouse/touch) ownership for UI components.
 * Ensures that only one component can claim pointer focus at a time.
 */
public final class PointerManager {
	private static SpatialView owner;
	private static boolean modalViewOpen;
	
	private PointerManager() {
	}
	
	/**
	 * Checks is modal component is open.
	 * 
	 * @return true if modal view component is open, false if not.
	 */
	public static boolean isModalViewOpen() {
		return modalViewOpen;
	}

	/**
	 * Setter for components which implements ModalParent interface.
	 * 
	 * @param modalViewOpen true if open, false if not.
	 */
	public static void setModalViewOpen(boolean modalViewOpen) {
		PointerManager.modalViewOpen = modalViewOpen;
	}

	/**
	 * Requests pointer ownership for the given spatial view.
	 * 
	 * @param spatialView the view requesting ownership, cannot be null.
	 * @return true if the view now owns the pointer, false otherwise.
	 * @throws NullPointerException if spatialView is null.
	 */
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
		
		if(modalViewOpen) {
			if(!(spatialView instanceof ModalParent) && spatialView.getId() != View.MODAL_ITEM_VIEW_ID) {
				return false;
			}
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
	
	/**
	 * Checks whether any view currently owns the pointer.
	 *
	 * @return true if an owner exists, false otherwise.
	 */
	public static boolean hasOwner() {
		return owner != null;
	}
	
	/**
	 * Checks whether the given view is the current pointer owner.
	 *
	 * @param spatialView the view to check, cannot be null.
	 * @return true if the view is the owner, false otherwise.
	 * @throws NullPointerException if spatialView is null.
	 */
	public static boolean isOwner(SpatialView spatialView) {
		return owner == requireNonNull(spatialView,"spatialView");
	}
	
	/**
	 * Releases pointer ownership. After calling this, no view owns the pointer.
	 */
	public static void release() {
		owner = null;
	}
}