package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class ArmorFeatureRenderer<T extends EntityModel> implements FeatureRenderer<LivingEntity> {
	protected static final Identifier GLINT_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");
	protected T secondLayer;
	protected T firstLayer;
	private final LivingEntityRenderer<?> renderer;
	private float alpha = 1.0F;
	private float red = 1.0F;
	private float blue = 1.0F;
	private float green = 1.0F;
	private boolean ignoreGlint;
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();

	public ArmorFeatureRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.renderer = livingEntityRenderer;
		this.init();
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		this.method_10278(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, EquipmentSlot.CHEST);
		this.method_10278(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, EquipmentSlot.LEGS);
		this.method_10278(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, EquipmentSlot.FEET);
		this.method_10278(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, EquipmentSlot.HEAD);
	}

	@Override
	public boolean combineTextures() {
		return false;
	}

	private void method_10278(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, EquipmentSlot equipmentSlot) {
		ItemStack itemStack = this.method_10279(livingEntity, equipmentSlot);
		if (itemStack != null && itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.method_11352() == equipmentSlot) {
				T entityModel = this.method_12480(equipmentSlot);
				entityModel.copy(this.renderer.getModel());
				entityModel.animateModel(livingEntity, f, g, h);
				this.method_10277(entityModel, equipmentSlot);
				boolean bl = this.method_12481(equipmentSlot);
				this.renderer.bindTexture(this.getArmorTexture(armorItem, bl));
				switch (armorItem.getMaterial()) {
					case LEATHER:
						int m = armorItem.getColor(itemStack);
						float n = (float)(m >> 16 & 0xFF) / 255.0F;
						float o = (float)(m >> 8 & 0xFF) / 255.0F;
						float p = (float)(m & 0xFF) / 255.0F;
						GlStateManager.color(this.red * n, this.blue * o, this.green * p, this.alpha);
						entityModel.render(livingEntity, f, g, i, j, k, l);
						this.renderer.bindTexture(this.getArmorTexture(armorItem, bl, "overlay"));
					case CHAIN:
					case IRON:
					case GOLD:
					case DIAMOND:
						GlStateManager.color(this.red, this.blue, this.green, this.alpha);
						entityModel.render(livingEntity, f, g, i, j, k, l);
					default:
						if (!this.ignoreGlint && itemStack.hasEnchantments()) {
							method_12479(this.renderer, livingEntity, entityModel, f, g, h, i, j, k, l);
						}
				}
			}
		}
	}

	@Nullable
	public ItemStack method_10279(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
		return livingEntity.getStack(equipmentSlot);
	}

	public T method_12480(EquipmentSlot equipmentSlot) {
		return this.method_12481(equipmentSlot) ? this.secondLayer : this.firstLayer;
	}

	private boolean method_12481(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.LEGS;
	}

	public static void method_12479(
		LivingEntityRenderer<?> livingEntityRenderer,
		LivingEntity livingEntity,
		EntityModel entityModel,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		float l
	) {
		float m = (float)livingEntity.ticksAlive + h;
		livingEntityRenderer.bindTexture(GLINT_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.depthFunc(514);
		GlStateManager.depthMask(false);
		float n = 0.5F;
		GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

		for (int o = 0; o < 2; o++) {
			GlStateManager.disableLighting();
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_COLOR, GlStateManager.class_2866.ONE);
			float p = 0.76F;
			GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float q = 0.33333334F;
			GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.rotate(30.0F - (float)o * 60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(0.0F, m * (0.001F + (float)o * 0.003F) * 20.0F, 0.0F);
			GlStateManager.matrixMode(5888);
			entityModel.render(livingEntity, f, g, i, j, k, l);
		}

		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
		GlStateManager.disableBlend();
	}

	private Identifier getArmorTexture(ArmorItem armor, boolean secondLayer) {
		return this.getArmorTexture(armor, secondLayer, null);
	}

	private Identifier getArmorTexture(ArmorItem item, boolean secondLayer, String overlay) {
		String string = String.format(
			"textures/models/armor/%s_layer_%d%s.png", item.getMaterial().getName(), secondLayer ? 2 : 1, overlay == null ? "" : String.format("_%s", overlay)
		);
		Identifier identifier = (Identifier)ARMOR_TEXTURE_CACHE.get(string);
		if (identifier == null) {
			identifier = new Identifier(string);
			ARMOR_TEXTURE_CACHE.put(string, identifier);
		}

		return identifier;
	}

	protected abstract void init();

	protected abstract void method_10277(T entityModel, EquipmentSlot equipmentSlot);
}
