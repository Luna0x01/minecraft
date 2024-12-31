package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

public class class_3284 extends class_3257 {
	private final class_4113 field_20454;
	private float field_16072;

	public class_3284(int i, class_4113 arg) {
		super(i, 0, 0, 35, 27, false);
		this.field_20454 = arg;
		this.method_14477(153, 2, 35, 0, RecipeBookScreen.TEXTURE);
	}

	public void method_14614(MinecraftClient minecraftClient) {
		class_3320 lv = minecraftClient.player.method_14675();
		List<class_3286> list = lv.method_18138(this.field_20454);
		if (minecraftClient.player.openScreenHandler instanceof class_3536) {
			for (class_3286 lv2 : list) {
				for (RecipeType recipeType : lv2.method_14629(lv.method_21393((class_3536)minecraftClient.player.openScreenHandler))) {
					if (lv.method_21407(recipeType)) {
						this.field_16072 = 15.0F;
						return;
					}
				}
			}
		}
	}

	@Override
	public void method_891(int i, int j, float f) {
		if (this.visible) {
			if (this.field_16072 > 0.0F) {
				float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_16072 / 15.0F * (float) Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(this.x + 8), (float)(this.y + 12), 0.0F);
				GlStateManager.scale(1.0F, g, 1.0F);
				GlStateManager.translate((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
			}

			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.getTextureManager().bindTexture(this.field_15895);
			GlStateManager.disableDepthTest();
			int k = this.field_15897;
			int l = this.field_15898;
			if (this.field_15896) {
				k += this.field_15899;
			}

			if (this.hovered) {
				l += this.field_15900;
			}

			int m = this.x;
			if (this.field_15896) {
				m -= 2;
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(m, this.y, k, l, this.width, this.height);
			GlStateManager.enableDepthTest();
			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			this.method_18802(minecraftClient.getHeldItemRenderer());
			GlStateManager.enableLighting();
			DiffuseLighting.disable();
			if (this.field_16072 > 0.0F) {
				GlStateManager.popMatrix();
				this.field_16072 -= f;
			}
		}
	}

	private void method_18802(HeldItemRenderer heldItemRenderer) {
		List<ItemStack> list = this.field_20454.method_18268();
		int i = this.field_15896 ? -2 : 0;
		if (list.size() == 1) {
			heldItemRenderer.method_19397((ItemStack)list.get(0), this.x + 9 + i, this.y + 5);
		} else if (list.size() == 2) {
			heldItemRenderer.method_19397((ItemStack)list.get(0), this.x + 3 + i, this.y + 5);
			heldItemRenderer.method_19397((ItemStack)list.get(1), this.x + 14 + i, this.y + 5);
		}
	}

	public class_4113 method_14616() {
		return this.field_20454;
	}

	public boolean method_18801(class_3320 arg) {
		List<class_3286> list = arg.method_18138(this.field_20454);
		this.visible = false;
		if (list != null) {
			for (class_3286 lv : list) {
				if (lv.method_14625() && lv.method_14633()) {
					this.visible = true;
					break;
				}
			}
		}

		return this.visible;
	}
}
