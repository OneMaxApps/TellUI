package microui.core.style;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;

//Status: STABLE - Do not modify
//Last Reviewed: 11.10.2025
public final class GradientColor extends AbstractGradientColor {

	private final BooleanSupplier condition;

	public GradientColor(AbstractColor start, AbstractColor end, BooleanSupplier condition) {
		super(start, end);

		this.condition = requireNonNull(condition,"condition");

		getAnimator().setSpeed(.025f);
	}

	public void resetAnimationProgress() {
		getAnimator().setProgress(0);
	}

	@Override
	protected void preApply() {
		super.preApply();
		getAnimator().setStartEnabled(condition.getAsBoolean());
	}
}