package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ChestOpeningRenderHelper {
	public void render(Block block, float light) {
		GlStateManager.color(light, light, light, 1.0F);
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		BlockEntityItemStackRenderHelper.INSTANCE.renderItem(new ItemStack(block));
	}
}
