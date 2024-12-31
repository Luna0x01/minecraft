package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.Direction;

public class HeadFeatureRenderer implements FeatureRenderer<LivingEntity> {
	private final ModelPart modelPart;

	public HeadFeatureRenderer(ModelPart modelPart) {
		this.modelPart = modelPart;
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		ItemStack itemStack = entity.getStack(EquipmentSlot.HEAD);
		if (itemStack != null && itemStack.getItem() != null) {
			Item item = itemStack.getItem();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			GlStateManager.pushMatrix();
			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			boolean bl = entity instanceof VillagerEntity || entity instanceof ZombieEntity && ((ZombieEntity)entity).isVillager();
			if (entity.isBaby() && !(entity instanceof VillagerEntity)) {
				float f = 2.0F;
				float g = 1.4F;
				GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
				GlStateManager.scale(0.7F, 0.7F, 0.7F);
				GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
			}

			this.modelPart.preRender(0.0625F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			if (item == Items.SKULL) {
				float h = 1.1875F;
				GlStateManager.scale(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					GlStateManager.translate(0.0F, 0.0625F, 0.0F);
				}

				GameProfile gameProfile = null;
				if (itemStack.hasNbt()) {
					NbtCompound nbtCompound = itemStack.getNbt();
					if (nbtCompound.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
					} else if (nbtCompound.contains("SkullOwner", 8)) {
						String string = nbtCompound.getString("SkullOwner");
						if (!ChatUtil.isEmpty(string)) {
							gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
							nbtCompound.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
						}
					}
				}

				SkullBlockEntityRenderer.instance.method_10108(-0.5F, 0.0F, -0.5F, Direction.UP, 180.0F, itemStack.getData(), gameProfile, -1, handSwing);
			} else if (!(item instanceof ArmorItem) || ((ArmorItem)item).method_11352() != EquipmentSlot.HEAD) {
				float i = 0.625F;
				GlStateManager.translate(0.0F, -0.25F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(0.625F, -0.625F, -0.625F);
				if (bl) {
					GlStateManager.translate(0.0F, 0.1875F, 0.0F);
				}

				minecraftClient.getHeldItemRenderer().renderItem(entity, itemStack, ModelTransformation.Mode.HEAD);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
