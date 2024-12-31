package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Direction;

public class BlockEntityItemStackRenderHelper {
	public static BlockEntityItemStackRenderHelper INSTANCE = new BlockEntityItemStackRenderHelper();
	private ChestBlockEntity chest = new ChestBlockEntity(0);
	private ChestBlockEntity trappedChest = new ChestBlockEntity(1);
	private EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
	private BannerBlockEntity banner = new BannerBlockEntity();
	private SkullBlockEntity skull = new SkullBlockEntity();

	public void renderItem(ItemStack stack) {
		if (stack.getItem() == Items.BANNER) {
			this.banner.fromItemStack(stack);
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.banner, 0.0, 0.0, 0.0, 0.0F);
		} else if (stack.getItem() == Items.SKULL) {
			GameProfile gameProfile = null;
			if (stack.hasNbt()) {
				NbtCompound nbtCompound = stack.getNbt();
				if (nbtCompound.contains("SkullOwner", 10)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", 8) && nbtCompound.getString("SkullOwner").length() > 0) {
					GameProfile var4 = new GameProfile(null, nbtCompound.getString("SkullOwner"));
					gameProfile = SkullBlockEntity.loadProperties(var4);
					nbtCompound.remove("SkullOwner");
					nbtCompound.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
				}
			}

			if (SkullBlockEntityRenderer.instance != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.5F, 0.0F, -0.5F);
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				GlStateManager.disableCull();
				SkullBlockEntityRenderer.instance.render(0.0F, 0.0F, 0.0F, Direction.UP, 0.0F, stack.getData(), gameProfile, -1);
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
			}
		} else {
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block == Blocks.ENDERCHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.enderChest, 0.0, 0.0, 0.0, 0.0F);
			} else if (block == Blocks.TRAPPED_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.trappedChest, 0.0, 0.0, 0.0, 0.0F);
			} else {
				BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.chest, 0.0, 0.0, 0.0, 0.0F);
			}
		}
	}
}
