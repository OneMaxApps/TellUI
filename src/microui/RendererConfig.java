package microui;

import static java.util.Objects.requireNonNull;
import static microui.RendererConfig.Mode.STRICT;

//Status: STABLE - Do not modify
//Last Reviewed: 30.10.2025

/**
 *  Provides modes for render control
 *  <p>
 *  Default mode of rendering is STRICT
 *  </p>
 *  
 */
public final class RendererConfig {
	private static Mode mode;
	
	static {
		setMode(STRICT);
	}
	
	private RendererConfig() {
		
	}
	
	public static Mode getMode() {
		return mode;
	}

	public static void setMode(Mode mode) {
		RendererConfig.mode = requireNonNull(mode,"mode");
	}

	public static enum Mode {
		FLEXIBLE,
		STRICT;
	}
	
}