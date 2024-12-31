package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.class_3685;
import net.minecraft.class_3741;
import net.minecraft.class_3746;
import net.minecraft.class_4197;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2871;
import net.minecraft.client.render.entity.model.ShieldModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityItemStackRenderHelper {
	private static final ShulkerBoxBlockEntity[] field_15273 = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(ShulkerBoxBlockEntity::new)
		.toArray(ShulkerBoxBlockEntity[]::new);
	private static final ShulkerBoxBlockEntity field_20643 = new ShulkerBoxBlockEntity(null);
	public static BlockEntityItemStackRenderHelper INSTANCE = new BlockEntityItemStackRenderHelper();
	private final ChestBlockEntity chest = new ChestBlockEntity();
	private final ChestBlockEntity trappedChest = new class_3746();
	private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
	private final BannerBlockEntity banner = new BannerBlockEntity();
	private final BedBlockEntity bed = new BedBlockEntity();
	private final SkullBlockEntity skull = new SkullBlockEntity();
	private final class_3741 field_20644 = new class_3741();
	private final ShieldModel field_20645 = new ShieldModel();
	private final class_4197 field_20646 = new class_4197();

	public void renderItem(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof BannerItem) {
			this.banner.method_16774(stack, ((BannerItem)item).method_16011());
			BlockEntityRenderDispatcher.INSTANCE.method_19324(this.banner);
		} else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof BedBlock) {
			this.bed.setColor(((BedBlock)((BlockItem)item).getBlock()).getColor());
			BlockEntityRenderDispatcher.INSTANCE.method_19324(this.bed);
		} else if (item == Items.SHIELD) {
			if (stack.getNbtCompound("BlockEntityTag") != null) {
				this.banner.method_16774(stack, ShieldItem.method_16122(stack));
				MinecraftClient.getInstance()
					.getTextureManager()
					.bindTexture(class_2871.field_13541.method_12344(this.banner.getTextureIdentifier(), this.banner.getPatterns(), this.banner.getColors()));
			} else {
				MinecraftClient.getInstance().getTextureManager().bindTexture(class_2871.TEXTURE_SHIELD_BASE);
			}

			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			this.field_20645.render();
			if (stack.hasEnchantmentGlint()) {
				this.method_19050(this.field_20645::render);
			}

			GlStateManager.popMatrix();
		} else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof class_3685) {
			GameProfile gameProfile = null;
			if (stack.hasNbt()) {
				NbtCompound nbtCompound = stack.getNbt();
				if (nbtCompound.contains("SkullOwner", 10)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
					GameProfile var6 = new GameProfile(null, nbtCompound.getString("SkullOwner"));
					gameProfile = SkullBlockEntity.loadProperties(var6);
					nbtCompound.remove("SkullOwner");
					nbtCompound.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
				}
			}

			if (SkullBlockEntityRenderer.instance != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				SkullBlockEntityRenderer.instance
					.method_10108(0.0F, 0.0F, 0.0F, null, 180.0F, ((class_3685)((BlockItem)item).getBlock()).method_16548(), gameProfile, -1, 0.0F);
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
			}
		} else if (item == Items.TRIDENT) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(class_4197.field_20592);
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			this.field_20646.method_18943();
			if (stack.hasEnchantmentGlint()) {
				this.method_19050(this.field_20646::method_18943);
			}

			GlStateManager.popMatrix();
		} else if (item instanceof BlockItem && ((BlockItem)item).getBlock() == Blocks.CONDUIT) {
			BlockEntityRenderDispatcher.INSTANCE.method_19324(this.field_20644);
		} else if (item == Blocks.ENDERCHEST.getItem()) {
			BlockEntityRenderDispatcher.INSTANCE.method_19324(this.enderChest);
		} else if (item == Blocks.TRAPPED_CHEST.getItem()) {
			BlockEntityRenderDispatcher.INSTANCE.method_19324(this.trappedChest);
		} else if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
			DyeColor dyeColor = ShulkerBoxBlock.colorOf(item);
			if (dyeColor == null) {
				BlockEntityRenderDispatcher.INSTANCE.method_19324(field_20643);
			} else {
				BlockEntityRenderDispatcher.INSTANCE.method_19324(field_15273[dyeColor.getId()]);
			}
		} else {
			BlockEntityRenderDispatcher.INSTANCE.method_19324(this.chest);
		}
	}

	private void method_19050(Runnable runnable) {
		GlStateManager.color(0.5019608F, 0.2509804F, 0.8F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(HeldItemRenderer.field_20931);
		HeldItemRenderer.method_19390(MinecraftClient.getInstance().getTextureManager(), runnable, 1);
	}
}
