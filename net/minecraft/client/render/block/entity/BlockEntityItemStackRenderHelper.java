package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2871;
import net.minecraft.client.render.entity.model.ShieldModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityItemStackRenderHelper {
	private static final ShulkerBoxBlockEntity[] field_15273 = new ShulkerBoxBlockEntity[16];
	public static BlockEntityItemStackRenderHelper INSTANCE;
	private final ChestBlockEntity chest = new ChestBlockEntity(ChestBlock.Type.BASIC);
	private final ChestBlockEntity trappedChest = new ChestBlockEntity(ChestBlock.Type.TRAP);
	private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
	private final BannerBlockEntity banner = new BannerBlockEntity();
	private final SkullBlockEntity skull = new SkullBlockEntity();
	private final ShieldModel shield = new ShieldModel();

	public void renderItem(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.BANNER) {
			this.banner.method_13720(stack, false);
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.banner, 0.0, 0.0, 0.0, 0.0F);
		} else if (item == Items.SHIELD) {
			if (stack.getNbtCompound("BlockEntityTag") != null) {
				this.banner.method_13720(stack, true);
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
		} else if (item == Items.SKULL) {
			GameProfile gameProfile = null;
			if (stack.hasNbt()) {
				NbtCompound nbtCompound = stack.getNbt();
				if (nbtCompound.contains("SkullOwner", 10)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
					GameProfile var5 = new GameProfile(null, nbtCompound.getString("SkullOwner"));
					gameProfile = SkullBlockEntity.loadProperties(var5);
					nbtCompound.remove("SkullOwner");
					nbtCompound.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
				}
			}

			if (SkullBlockEntityRenderer.instance != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				SkullBlockEntityRenderer.instance.method_10108(0.0F, 0.0F, 0.0F, Direction.UP, 180.0F, stack.getData(), gameProfile, -1, 0.0F);
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
			}
		} else if (item == Item.fromBlock(Blocks.ENDERCHEST)) {
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.enderChest, 0.0, 0.0, 0.0, 0.0F);
		} else if (item == Item.fromBlock(Blocks.TRAPPED_CHEST)) {
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.trappedChest, 0.0, 0.0, 0.0, 0.0F);
		} else if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(field_15273[ShulkerBoxBlock.colorOf(item).getId()], 0.0, 0.0, 0.0, 0.0F);
		} else {
			BlockEntityRenderDispatcher.INSTANCE.renderBlockEntity(this.chest, 0.0, 0.0, 0.0, 0.0F);
		}
	}

	static {
		for (DyeColor dyeColor : DyeColor.values()) {
			field_15273[dyeColor.getId()] = new ShulkerBoxBlockEntity(dyeColor);
		}

		INSTANCE = new BlockEntityItemStackRenderHelper();
	}
}
