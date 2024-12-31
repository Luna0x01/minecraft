package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
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
		this.renderFeature(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, 4);
		this.renderFeature(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, 3);
		this.renderFeature(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, 2);
		this.renderFeature(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale, 1);
	}

	@Override
	public boolean combineTextures() {
		return false;
	}

	private void renderFeature(
		LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale, int slot
	) {
		ItemStack itemStack = this.getSlot(entity, slot);
		if (itemStack != null && itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			T entityModel = this.getLayer(slot);
			entityModel.copy(this.renderer.getModel());
			entityModel.animateModel(entity, handSwing, handSwingAmount, tickDelta);
			this.setVisible(entityModel, slot);
			boolean bl = this.usesSecondLayer(slot);
			this.renderer.bindTexture(this.getArmorTexture(armorItem, bl));
			switch (armorItem.getMaterial()) {
				case LEATHER:
					int i = armorItem.getColor(itemStack);
					float f = (float)(i >> 16 & 0xFF) / 255.0F;
					float g = (float)(i >> 8 & 0xFF) / 255.0F;
					float h = (float)(i & 0xFF) / 255.0F;
					GlStateManager.color(this.red * f, this.blue * g, this.green * h, this.alpha);
					entityModel.render(entity, handSwing, handSwingAmount, age, headYaw, headPitch, scale);
					this.renderer.bindTexture(this.getArmorTexture(armorItem, bl, "overlay"));
				case CHAIN:
				case IRON:
				case GOLD:
				case DIAMOND:
					GlStateManager.color(this.red, this.blue, this.green, this.alpha);
					entityModel.render(entity, handSwing, handSwingAmount, age, headYaw, headPitch, scale);
				default:
					if (!this.ignoreGlint && itemStack.hasEnchantments()) {
						this.renderGlint(entity, entityModel, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale);
					}
			}
		}
	}

	public ItemStack getSlot(LivingEntity entity, int slot) {
		return entity.getArmorSlot(slot - 1);
	}

	public T getLayer(int slot) {
		return this.usesSecondLayer(slot) ? this.secondLayer : this.firstLayer;
	}

	private boolean usesSecondLayer(int slot) {
		return slot == 2;
	}

	private void renderGlint(
		LivingEntity entity, T model, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale
	) {
		float f = (float)entity.ticksAlive + tickDelta;
		this.renderer.bindTexture(GLINT_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.depthFunc(514);
		GlStateManager.depthMask(false);
		float g = 0.5F;
		GlStateManager.color(g, g, g, 1.0F);

		for (int i = 0; i < 2; i++) {
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(768, 1);
			float h = 0.76F;
			GlStateManager.color(0.5F * h, 0.25F * h, 0.8F * h, 1.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float j = 0.33333334F;
			GlStateManager.scale(j, j, j);
			GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
			GlStateManager.matrixMode(5888);
			model.render(entity, handSwing, handSwingAmount, age, headYaw, headPitch, scale);
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

	protected abstract void setVisible(T bipedModel, int slot);
}
