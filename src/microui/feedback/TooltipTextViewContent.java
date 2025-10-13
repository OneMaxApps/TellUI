package microui.feedback;

import static java.lang.Math.max;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import microui.component.TextView;
import microui.core.style.GradientColor;

public final class TooltipTextViewContent extends TooltipContent {
	private static final int DEFAULT_TEXT_SIZE = 12;
	private final TextView textView;
	private boolean isSizeDirty;

	public TooltipTextViewContent(String text) {
		super();
		setVisible(true);
		setConstrainDimensionsEnabled(true);
		setMinMaxSize(8, 8, ctx.width, ctx.height);
		setMarginLeft(10);

		textView = new TextView(text);
		textView.setConstrainDimensionsEnabled(false);
		textView.setBackgroundColor(getTheme().getTooltipBackgroundColor());
		textView.setTextColor(getTheme().getTooltipTextColor());
		textView.setAutoResizeModeEnabled(false);
		textView.setTextSize(DEFAULT_TEXT_SIZE);
		textView.setPadding(4, 5);
		textView.setClipModeEnabled(false);
		textView.setAlignX(LEFT);
		textView.setAlignY(TOP);

		isSizeDirty = true;
	}

	@Override
	public boolean isPreparedShow() {
		if (textView.getText().isEmpty()) {
			return false;
		}
		prepareSize();

		return true;
	}

	@Override
	public boolean isPreparedClose() {
		if (textView.getBackgroundColor() instanceof GradientColor gradientColor) {
			gradientColor.resetAnimationProgress();
		}

		if (textView.getTextColor() instanceof GradientColor gradientColor) {
			gradientColor.resetAnimationProgress();
		}

		return true;
	}

	public String getText() {
		return textView.getText();
	}

	public void setText(String text) {
		textView.setText(text);
		isSizeDirty = true;
	}

	public float getTextSize() {
		return textView.getTextSize();
	}

	public void setTextSize(float textSize) {
		textView.setTextSize(textSize);
		isSizeDirty = true;
	}

	@Override
	protected void render() {
		textView.draw();
	}

	@Override
	protected void onChangePosition() {
		super.onChangePosition();

		textView.setAbsolutePositionFrom(this);
	}

	private void prepareSize() {
		if (!isSizeDirty) {
			return;
		}

		final String[] lines = textView.getText().split("\n");

		float maxTextWidth = 0;

		ctx.pushStyle();
		ctx.textSize(textView.getTextSize());
		if (textView.getFont() != null) {
			ctx.textFont(textView.getFont());
		}
		for (int i = 0; i < lines.length; i++) {
			maxTextWidth = max(maxTextWidth, ctx.textWidth(lines[i]));
		}

		float totalTextHeight = (ctx.textAscent() + ctx.textDescent()) * (lines.length);

		if (lines.length > 1) {
			for (int i = 0; i < lines.length; i++) {
				totalTextHeight += ctx.textDescent();
			}
		}

		ctx.popStyle();

		textView.setSize(maxTextWidth, totalTextHeight);
		textView.setMarginFrom(this);

		setSize(textView.getPadWidth(), textView.getPadHeight());

		isSizeDirty = false;

	}
}