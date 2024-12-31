package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.recipe.RecipeType;

public class class_3284 extends class_3257 {
	private final ItemGroup field_16071;
	private float field_16072;

	public class_3284(int i, ItemGroup itemGroup) {
		super(i, 0, 0, 35, 27, false);
		this.field_16071 = itemGroup;
		this.method_14477(153, 2, 35, 0, RecipeBookScreen.TEXTURE);
	}

	public void method_14614(MinecraftClient minecraftClient) {
		class_3355 lv = minecraftClient.player.method_14675();

		for (class_3286 lv2 : (List)class_3320.field_16242.get(this.field_16071)) {
			for (RecipeType recipeType : lv2.method_14629(lv.method_14986())) {
				if (lv.method_14991(recipeType)) {
					this.field_16072 = 15.0F;
					return;
				}
			}
		}
	}

	@Override
	public void method_891(MinecraftClient client, int i, int j, float f) {
		if (this.visible) {
			if (this.field_16072 > 0.0F) {
				float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_16072 / 15.0F * (float) Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(this.x + 8), (float)(this.y + 12), 0.0F);
				GlStateManager.scale(1.0F, g, 1.0F);
				GlStateManager.translate((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
			}

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

			int m = this.x;
			if (this.field_15896) {
				m -= 2;
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(m, this.y, k, l, this.width, this.height);
			GlStateManager.enableDepthTest();
			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			this.method_14615(client.getItemRenderer());
			GlStateManager.enableLighting();
			DiffuseLighting.disable();
			if (this.field_16072 > 0.0F) {
				GlStateManager.popMatrix();
				this.field_16072 -= f;
			}
		}
	}

	private void method_14615(ItemRenderer itemRenderer) {
		ItemStack itemStack = this.field_16071.getIcon();
		if (this.field_16071 == ItemGroup.TOOLS) {
			itemRenderer.method_12461(itemStack, this.x + 3, this.y + 5);
			itemRenderer.method_12461(ItemGroup.COMBAT.getIcon(), this.x + 14, this.y + 5);
		} else if (this.field_16071 == ItemGroup.MISC) {
			itemRenderer.method_12461(itemStack, this.x + 3, this.y + 5);
			itemRenderer.method_12461(ItemGroup.FOOD.getIcon(), this.x + 14, this.y + 5);
		} else {
			itemRenderer.method_12461(itemStack, this.x + 9, this.y + 5);
		}
	}

	public ItemGroup method_14616() {
		return this.field_16071;
	}

	public boolean method_14617() {
		List<class_3286> list = (List<class_3286>)class_3320.field_16242.get(this.field_16071);
		this.visible = false;

		for (class_3286 lv : list) {
			if (lv.method_14625() && lv.method_14633()) {
				this.visible = true;
				break;
			}
		}

		return this.visible;
	}
}
