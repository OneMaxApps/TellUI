package microui.service;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;

import microui.component.TextView;
import microui.core.base.View;
import microui.core.interfaces.ValuePreviewSource;
import microui.core.style.Color;
import microui.core.style.LerpedColor;

public final class ValueOverlayManager extends View {
	private static ValueOverlayManager instance;
	private final TextView text;
	private static ValuePreviewSource source;
	
	private ValueOverlayManager() {
		super();
		setVisible(true);
		
		final BooleanSupplier s = () -> {
			return source != null && source.isContentPrepared();
		};
		
		text = new TextView();
		text.setPosition(0, 0);
		text.setBackgroundColor(new LerpedColor(Color.TRANSPARENT, new Color(0,128), s).setSpeed(.1f));
		text.setTextColor(new LerpedColor(Color.TRANSPARENT, Color.WHITE, s).setSpeed(.1f));
		
	}

	@Override
	protected void render() {
		if (source != null && source.hasSource() && source.isContentPrepared()) {
			text.setText(source.getSource());
			
		}
		
		text.draw();
	}
	
	public static ValuePreviewSource getSource() {
		return source;
	}

	public static void setSource(ValuePreviewSource source) {
		ValueOverlayManager.source = requireNonNull(source,"source");
	}

	public static ValueOverlayManager getInstance() {
		if (instance == null) {
			instance = new ValueOverlayManager();
		}
		
		return instance;
	}
}