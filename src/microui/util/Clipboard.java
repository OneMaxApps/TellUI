package microui.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Utility class for clipboard operations with support for both system and local buffers.
 * 
 * <p>Provides methods to copy and paste text to/from the clipboard. Supports switching
 * between the operating system's clipboard and a local buffer for testing or special cases.</p>
 */
public final class Clipboard {
    /** Local buffer used when not using the system clipboard. */
    private static String localBuffer;
    
    /** Flag indicating whether to use the local buffer instead of the system clipboard. */
    private static boolean usingLocalBuffer;
    
    /** Reference to the system clipboard. */
    private static java.awt.datatransfer.Clipboard clip;

    // Static initialization block
    static {
        usingLocalBuffer = false;
        clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Copies text to the clipboard.
     * 
     * <p>Depending on the buffer mode, copies to either the system clipboard or local buffer.</p>
     * 
     * @param txt the text to copy to the clipboard
     */
    public static final void set(final String txt) {
        if (usingLocalBuffer) {
            Clipboard.localBuffer = txt;
        } else {
            clip.setContents(new StringSelection(txt), null);
        }
    }

    /**
     * Copies an array of strings to the clipboard as newline-separated text.
     * 
     * <p>Each string in the array becomes a separate line in the clipboard content.</p>
     * 
     * @param LINES the array of strings to copy to the clipboard
     */
    public static final void set(final String[] LINES) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < LINES.length; i++) {
            sb.append(LINES[i] + '\n');
        }

        set(sb.toString());
    }

    /**
     * Retrieves text from the clipboard.
     * 
     * <p>Depending on the buffer mode, retrieves from either the system clipboard or local buffer.
     * Returns an empty string if an error occurs while accessing the system clipboard.</p>
     * 
     * @return the text from the clipboard, or empty string if unavailable
     */
    public static final String get() {
        if (usingLocalBuffer) {
            return localBuffer;
        } else {
            try {
                return (String) clip.getData(DataFlavor.stringFlavor);
            } catch (IOException | UnsupportedFlavorException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * Retrieves text from the clipboard and splits it into an array of lines.
     * 
     * <p>Splits the clipboard content by newline characters to create an array of strings.</p>
     * 
     * @return an array of lines from the clipboard content
     */
    public static final String[] getAsArray() {
        return get().split("\n");
    }

    /**
     * Switches to using the local buffer instead of the system clipboard.
     * 
     * <p>All subsequent clipboard operations will use an in-memory local buffer
     * instead of the operating system's clipboard.</p>
     */
    public static final void usingLocalBuffer() {
        usingLocalBuffer = true;
    }

    /**
     * Switches to using the system clipboard instead of the local buffer.
     * 
     * <p>All subsequent clipboard operations will use the operating system's clipboard.</p>
     */
    public static final void usingSystemBuffer() {
        usingLocalBuffer = false;
    }

    /**
     * Returns a string describing which buffer type is currently being used.
     * 
     * @return "Local Buffer" if using local buffer, "OS Buffer" if using system clipboard
     */
    public static final String usingTypeOfBuffer() {
        return usingLocalBuffer ? "Local Buffer" : "OS Buffer";
    }

    /**
     * Checks if the clipboard is empty.
     * 
     * @return true if the clipboard contains no text, false otherwise
     */
    public static final boolean isEmpty() {
        return get().isEmpty();
    }
}