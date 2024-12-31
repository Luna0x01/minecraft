package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class SliderWidget extends ButtonWidget {
	private float progress = 1.0F;
	public boolean focused;
	private String label;
	private final float min;
	private final float max;
	private final PagedEntryListWidget.Listener listener;
	private SliderWidget.LabelSupplier labelSupplier;

	public SliderWidget(
		PagedEntryListWidget.Listener listener, int i, int j, int k, String string, float f, float g, float h, SliderWidget.LabelSupplier labelSupplier
	) {
		super(i, j, k, 150, 20, "");
		this.label = string;
		this.min = f;
		this.max = g;
		this.progress = (h - f) / (g - f);
		this.labelSupplier = labelSupplier;
		this.listener = listener;
		this.message = this.getMessage();
	}

	public float getSliderValue() {
		return this.min + (this.max - this.min) * this.progress;
	}

	public void setSliderValue(float value, boolean updateListener) {
		this.progress = (value - this.min) / (this.max - this.min);
		this.message = this.getMessage();
		if (updateListener) {
			this.listener.setFloatValue(this.id, this.getSliderValue());
		}
	}

	public float getProgress() {
		return this.progress;
	}

	private String getMessage() {
		return this.labelSupplier == null
			? I18n.translate(this.label) + ": " + this.getSliderValue()
			: this.labelSupplier.getLabel(this.id, I18n.translate(this.label), this.getSliderValue());
	}

	@Override
	protected int getYImage(boolean isHovered) {
		return 0;
	}

	@Override
	protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
		if (this.visible) {
			if (this.focused) {
				this.progress = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
				if (this.progress < 0.0F) {
					this.progress = 0.0F;
				}

				if (this.progress > 1.0F) {
					this.progress = 1.0F;
				}

				this.message = this.getMessage();
				this.listener.setFloatValue(this.id, this.getSliderValue());
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(this.x + (int)(this.progress * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexture(this.x + (int)(this.progress * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	public void setSliderProgress(float progress) {
		this.progress = progress;
		this.message = this.getMessage();
		this.listener.setFloatValue(this.id, this.getSliderValue());
	}

	@Override
	public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
		if (super.isMouseOver(client, mouseX, mouseY)) {
			this.progress = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
			if (this.progress < 0.0F) {
				this.progress = 0.0F;
			}

			if (this.progress > 1.0F) {
				this.progress = 1.0F;
			}

			this.message = this.getMessage();
			this.listener.setFloatValue(this.id, this.getSliderValue());
			this.focused = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.focused = false;
	}

	public interface LabelSupplier {
		String getLabel(int id, String label, float sliderValue);
	}
}
