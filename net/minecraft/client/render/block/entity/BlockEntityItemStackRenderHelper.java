package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2871;
import net.minecraft.client.render.entity.model.ShieldModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Direction;

public class BlockEntityItemStackRenderHelper {
	public static BlockEntityItemStackRenderHelper INSTANCE = new BlockEntityItemStackRenderHelper();
	private ChestBlockEntity chest = new ChestBlockEntity(ChestBlock.Type.BASIC);
	private ChestBlockEntity trappedChest = new ChestBlockEntity(ChestBlock.Type.TRAP);
	private EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
	private BannerBlockEntity banner = new BannerBlockEntity();
	private SkullBlockEntity skull = new SkullBlockEntity();
	private ShieldModel shield = new ShieldModel();

	public void renderItem(ItemStack stack) {
		if (stack.getItem() == Items.BANNER) {
			this.banner.fromItemStack(stack);
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.banner, 0.0, 0.0, 0.0, 0.0F);
		} else if (stack.getItem() == Items.SHIELD) {
			if (stack.getSubNbt("BlockEntityTag", false) != null) {
				this.banner.fromItemStack(stack);
				MinecraftClient.getInstance()
					.getTextureManager()
					.bindTexture(class_2871.field_13541.method_12344(this.banner.getTextureIdentifier(), this.banner.getPatterns(), this.banner.getColors()));
			} else {
				MinecraftClient.getInstance().getTextureManager().bindTexture(class_2871.TEXTURE_SHIELD_BASE);
			}

			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			this.shield.render();
			GlStateManager.popMatrix();
		} else if (stack.getItem() == Items.SKULL) {
			GameProfile gameProfile = null;
			if (stack.hasNbt()) {
				NbtCompound nbtCompound = stack.getNbt();
				if (nbtCompound.contains("SkullOwner", 10)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", 8) && !nbtCompound.getString("SkullOwner").isEmpty()) {
					GameProfile var4 = new GameProfile(null, nbtCompound.getString("SkullOwner"));
					gameProfile = SkullBlockEntity.loadProperties(var4);
					nbtCompound.remove("SkullOwner");
					nbtCompound.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
				}
			}

			if (SkullBlockEntityRenderer.instance != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				SkullBlockEntityRenderer.instance.method_10108(0.0F, 0.0F, 0.0F, Direction.UP, 0.0F, stack.getData(), gameProfile, -1, 0.0F);
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
