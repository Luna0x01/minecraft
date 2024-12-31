package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class RealmsSliderButton extends RealmsButton {
	public float value = 1.0F;
	public boolean sliding;
	private final float minValue;
	private final float maxValue;
	private int steps;

	public RealmsSliderButton(int i, int j, int k, int l, int m, int n) {
		this(i, j, k, l, n, 0, 1.0F, (float)m);
	}

	public RealmsSliderButton(int i, int j, int k, int l, int m, int n, float f, float g) {
		super(i, j, k, l, 20, "");
		this.minValue = f;
		this.maxValue = g;
		this.value = this.toPct((float)n);
		this.getProxy().message = this.getMessage();
	}

	public String getMessage() {
		return "";
	}

	public float toPct(float f) {
		return MathHelper.clamp((this.clamp(f) - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
	}

	public float toValue(float f) {
		return this.clamp(this.minValue + (this.maxValue - this.minValue) * MathHelper.clamp(f, 0.0F, 1.0F));
	}

	public float clamp(float f) {
		f = this.clampSteps(f);
		return MathHelper.clamp(f, this.minValue, this.maxValue);
	}

	protected float clampSteps(float f) {
		if (this.steps > 0) {
			f = (float)(this.steps * Math.round(f / (float)this.steps));
		}

		return f;
	}

	@Override
	public int getYImage(boolean bl) {
		return 0;
	}

	@Override
	public void renderBg(int i, int j) {
		if (this.getProxy().visible) {
			if (this.sliding) {
				this.value = (float)(i - (this.getProxy().x + 4)) / (float)(this.getProxy().getWidth() - 8);
				this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
				float f = this.toValue(this.value);
				this.clicked(f);
				this.value = this.toPct(f);
				this.getProxy().message = this.getMessage();
			}

			MinecraftClient.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(this.getProxy().x + (int)(this.value * (float)(this.getProxy().getWidth() - 8)), this.getProxy().y, 0, 66, 4, 20);
			this.blit(this.getProxy().x + (int)(this.value * (float)(this.getProxy().getWidth() - 8)) + 4, this.getProxy().y, 196, 66, 4, 20);
		}
	}

	@Override
	public void clicked(int i, int j) {
		this.value = (float)(i - (this.getProxy().x + 4)) / (float)(this.getProxy().getWidth() - 8);
		this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
		this.clicked(this.toValue(this.value));
		this.getProxy().message = this.getMessage();
		this.sliding = true;
	}

	public void clicked(float f) {
	}

	@Override
	public void released(int i, int j) {
		this.sliding = false;
	}
}
