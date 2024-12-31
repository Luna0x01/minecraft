package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
	protected final A modelLeggings;
	protected final A modelBody;
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();

	protected ArmorFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2) {
		super(featureRendererContext);
		this.modelLeggings = bipedEntityModel;
		this.modelBody = bipedEntityModel2;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.field_6174, i);
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.field_6172, i);
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.field_6166, i);
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.field_6169, i);
	}

	private void renderArmor(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		T livingEntity,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		EquipmentSlot equipmentSlot,
		int l
	) {
		ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
		if (itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.getSlotType() == equipmentSlot) {
				A bipedEntityModel = this.getArmor(equipmentSlot);
				this.getContextModel().setAttributes(bipedEntityModel);
				bipedEntityModel.animateModel(livingEntity, f, g, h);
				this.setVisible(bipedEntityModel, equipmentSlot);
				bipedEntityModel.setAngles(livingEntity, f, g, i, j, k);
				boolean bl = this.isLegs(equipmentSlot);
				boolean bl2 = itemStack.hasEnchantmentGlint();
				if (armorItem instanceof DyeableArmorItem) {
					int m = ((DyeableArmorItem)armorItem).getColor(itemStack);
					float n = (float)(m >> 16 & 0xFF) / 255.0F;
					float o = (float)(m >> 8 & 0xFF) / 255.0F;
					float p = (float)(m & 0xFF) / 255.0F;
					this.renderArmorParts(matrixStack, vertexConsumerProvider, l, armorItem, bl2, bipedEntityModel, bl, n, o, p, null);
					this.renderArmorParts(matrixStack, vertexConsumerProvider, l, armorItem, bl2, bipedEntityModel, bl, 1.0F, 1.0F, 1.0F, "overlay");
				} else {
					this.renderArmorParts(matrixStack, vertexConsumerProvider, l, armorItem, bl2, bipedEntityModel, bl, 1.0F, 1.0F, 1.0F, null);
				}
			}
		}
	}

	private void renderArmorParts(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		ArmorItem armorItem,
		boolean bl,
		A bipedEntityModel,
		boolean bl2,
		float f,
		float g,
		float h,
		@Nullable String string
	) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(
			vertexConsumerProvider, RenderLayer.getEntityCutoutNoCull(this.getArmorTexture(armorItem, bl2, string)), false, bl
		);
		bipedEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
	}

	public A getArmor(EquipmentSlot equipmentSlot) {
		return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
	}

	private boolean isLegs(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.field_6172;
	}

	private Identifier getArmorTexture(ArmorItem armorItem, boolean bl, @Nullable String string) {
		String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
	}

	protected abstract void setVisible(A bipedEntityModel, EquipmentSlot equipmentSlot);

	protected abstract void setInvisible(A bipedEntityModel);
}
