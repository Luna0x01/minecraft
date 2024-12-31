package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.ItemStack;

public class DynamicBlockRenderer {
	public void render(Block block, float f) {
		GlStateManager.color4f(f, f, f, 1.0F);
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		ItemDynamicRenderer.INSTANCE.render(new ItemStack(block));
	}
}
