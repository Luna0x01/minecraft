package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class class_3257 extends ButtonWidget {
	protected Identifier field_15895;
	protected boolean field_15896;
	protected int field_15897;
	protected int field_15898;
	protected int field_15899;
	protected int field_15900;

	public class_3257(int i, int j, int k, int l, int m, boolean bl) {
		super(i, j, k, l, m, "");
		this.field_15896 = bl;
	}

	public void method_14477(int i, int j, int k, int l, Identifier identifier) {
		this.field_15897 = i;
		this.field_15898 = j;
		this.field_15899 = k;
		this.field_15900 = l;
		this.field_15895 = identifier;
	}

	public void method_14478(boolean bl) {
		this.field_15896 = bl;
	}

	public boolean method_14479() {
		return this.field_15896;
	}

	public void method_14480(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void method_891(MinecraftClient client, int i, int j, float f) {
		if (this.visible) {
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			client.getTextureManager().bindTexture(this.field_15895);
			GlStateManager.disableDepthTest();
			int k = this.field_15897;
			int l = this.field_15898;
			if (this.field_15896) {
				k += this.field_15899;
			}

			if (this.hovered) {
				l += this.field_15900;
			}

			this.drawTexture(this.x, this.y, k, l, this.width, this.height);
			GlStateManager.enableDepthTest();
		}
	}
}
