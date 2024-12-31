package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;

public class OptionSliderWidget extends ButtonWidget {
	private double field_20084 = 1.0;
	public boolean dragging;
	private final GameOptions.Option option;
	private final double field_20085;
	private final double field_20086;

	public OptionSliderWidget(int i, int j, int k, GameOptions.Option option) {
		this(i, j, k, option, 0.0, 1.0);
	}

	public OptionSliderWidget(int i, int j, int k, GameOptions.Option option, double d, double e) {
		this(i, j, k, 150, 20, option, d, e);
	}

	public OptionSliderWidget(int i, int j, int k, int l, int m, GameOptions.Option option, double d, double e) {
		super(i, j, k, l, m, "");
		this.option = option;
		this.field_20085 = d;
		this.field_20086 = e;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.field_20084 = option.method_18261(minecraftClient.options.method_18256(option));
		this.message = minecraftClient.options.method_18260(option);
	}

	@Override
	protected int getYImage(boolean isHovered) {
		return 0;
	}

	@Override
	protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
		if (this.visible) {
			if (this.dragging) {
				this.field_20084 = (double)((float)(mouseX - (this.x + 4)) / (float)(this.width - 8));
				this.field_20084 = MathHelper.clamp(this.field_20084, 0.0, 1.0);
			}

			if (this.dragging || this.option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
				double d = this.option.method_18263(this.field_20084);
				client.options.method_18257(this.option, d);
				this.field_20084 = this.option.method_18261(d);
				this.message = client.options.method_18260(this.option);
			}

			client.getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(this.x + (int)(this.field_20084 * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexture(this.x + (int)(this.field_20084 * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	@Override
	public final void method_18374(double d, double e) {
		this.field_20084 = (d - (double)(this.x + 4)) / (double)(this.width - 8);
		this.field_20084 = MathHelper.clamp(this.field_20084, 0.0, 1.0);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.options.method_18257(this.option, this.option.method_18263(this.field_20084));
		this.message = minecraftClient.options.method_18260(this.option);
		this.dragging = true;
	}

	@Override
	public void method_18376(double d, double e) {
		this.dragging = false;
	}
}
