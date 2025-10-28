package microui;

import static java.util.Objects.requireNonNull;
import static microui.Render.Mode.STRICT;

//Status: STABLE - Do not modify
//Last Reviewed: 28.10.2025

/**
 *  Provides modes for render control
 *  <p>
 *  Default mode of rendering is STRICT
 *  </p>
 *  
 */
public final class Render {
	private static Mode mode;
	
	static {
		setMode(STRICT);
	}
	
	private Render() {
		
	}
	
	public static Mode getMode() {
		return mode;
	}

	public static void setMode(Mode mode) {
		Render.mode = requireNonNull(mode,"mode");
	}

	public static enum Mode {
		FLEXIBLE,
		STRICT;
	}
	
}