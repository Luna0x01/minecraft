package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;

public class OptionSliderWidget extends ButtonWidget {
	private float value = 1.0F;
	public boolean dragging;
	private final GameOptions.Option option;
	private final float min;
	private final float max;

	public OptionSliderWidget(int i, int j, int k, GameOptions.Option option) {
		this(i, j, k, option, 0.0F, 1.0F);
	}

	public OptionSliderWidget(int i, int j, int k, GameOptions.Option option, float f, float g) {
		super(i, j, k, 150, 20, "");
		this.option = option;
		this.min = f;
		this.max = g;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.value = option.getRatio(minecraftClient.options.getIntValue(option));
		this.message = minecraftClient.options.getValueMessage(option);
	}

	@Override
	protected int getYImage(boolean isHovered) {
		return 0;
	}

	@Override
	protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
		if (this.visible) {
			if (this.dragging) {
				this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
				this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
				float f = this.option.getValue(this.value);
				client.options.setValue(this.option, f);
				this.value = this.option.getRatio(f);
				this.message = client.options.getValueMessage(this.option);
			}

			client.getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	@Override
	public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
		if (super.isMouseOver(client, mouseX, mouseY)) {
			this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
			this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
			client.options.setValue(this.option, this.option.getValue(this.value));
			this.message = client.options.getValueMessage(this.option);
			this.dragging = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.dragging = false;
	}
}
