package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class class_3256 extends ButtonWidget {
	private final Identifier field_15891;
	private final int field_15892;
	private final int field_15893;
	private final int field_15894;

	public class_3256(int i, int j, int k, int l, int m, int n, int o, int p, Identifier identifier) {
		super(i, j, k, l, m, "");
		this.field_15892 = n;
		this.field_15893 = o;
		this.field_15894 = p;
		this.field_15891 = identifier;
	}

	public void method_14476(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void method_891(MinecraftClient client, int i, int j, float f) {
		if (this.visible) {
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			client.getTextureManager().bindTexture(this.field_15891);
			GlStateManager.disableDepthTest();
			int k = this.field_15892;
			int l = this.field_15893;
			if (this.hovered) {
				l += this.field_15894;
			}

			this.drawTexture(this.x, this.y, k, l, this.width, this.height);
			GlStateManager.enableDepthTest();
		}
	}
}
