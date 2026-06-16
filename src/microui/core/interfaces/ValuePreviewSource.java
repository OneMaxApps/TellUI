package microui.core.interfaces;

/**
 * An interface for components that have data to display on top of everything on the screen
 */
public interface ValuePreviewSource {
	
	/**
	 * Returns the text to display as a string.
	 * @return the current text to display as a string
	 */
	String getSource();
	
	/**
	 * Checks if the content is ready for display. 
	 * @return true if the content is ready, false otherwise
	 */
	boolean isContentPrepared();
	
}