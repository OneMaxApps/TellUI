package microui.core;

import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.effect.Hover;
import microui.core.effect.Ripples;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;

//Status: STABLE - Do not modify
//Last Reviewed: 21.10.2025
public abstract class AbstractButton extends Component {
	private final Ripples ripples;
	private final Hover hover;
	private final Stroke stroke;

	public AbstractButton(float x, float y, float w, float h) {
		super(x, y, w, h);
		setBackgroundColor(getTheme().getBackgroundColor());
		ripples = new Ripples(this);
		hover = new Hover(this);
		stroke = new Stroke();

	}

	@Override
	protected void render() {
		ctx.pushStyle();
		stroke.apply();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();
	}

	public final AbstractColor getRipplesColor() {
		return ripples.getColor();
	}

	public final AbstractButton setRipplesColor(AbstractColor color) {
		ripples.setColor(color);

		return this;
	}

	public final boolean isRipplesEnabled() {
		return ripples.isEnabled();
	}

	public final AbstractButton setRipplesEnabled(boolean isEnabled) {
		ripples.setEnabled(isEnabled);

		return this;
	}

	public final boolean isHoverEnabled() {
		return hover.isEnabled();
	}

	public final AbstractButton setHoverEnabled(boolean isEnabled) {
		hover.setEnabled(isEnabled);

		return this;
	}

	public AbstractColor getHoverColor() {
		return hover.getColor();
	}

	public AbstractButton setHoverColor(AbstractColor color) {
		hover.setColor(color);

		return this;
	}

	public final float getHoverSpeed() {
		return hover.getSpeed();
	}

	public final AbstractButton setHoverSpeed(float speed) {
		hover.setSpeed(speed);

		return this;
	}

	public final float getStrokeWeight() {
		return stroke.getWeight();
	}

	public final AbstractButton setStrokeWeight(int weight) {
		stroke.setWeight(weight);

		return this;
	}

	public final AbstractColor getStrokeColor() {
		return stroke.getColor();
	}

	public final AbstractButton setStrokeColor(AbstractColor color) {
		stroke.setColor(color);

		return this;
	}

	protected final Ripples getRipplesInternal() {
		return ripples;
	}

	protected final Hover getHoverInternal() {
		return hover;
	}

	protected final Stroke getStrokeInternal() {
		return stroke;
	}
}