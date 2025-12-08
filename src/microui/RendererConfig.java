package microui;

import static java.util.Objects.requireNonNull;
import static microui.RendererConfig.Mode.FLEXIBLE;

//Status: STABLE - Do not modify
//Last Reviewed: 08.12.2025

/**
 *  Provides modes for render control
 *  <p>
 *  Default mode of rendering is FLEXIBLE
 *  </p>
 *  
 */
public final class RendererConfig {
	private static Mode mode;
	
	static {
		setMode(FLEXIBLE);
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