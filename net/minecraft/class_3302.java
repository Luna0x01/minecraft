package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class class_3302 implements FeatureRenderer<PlayerEntity> {
	private final EntityRenderDispatcher field_16150;
	protected LivingEntityRenderer<? extends LivingEntity> field_16148;
	private EntityModel field_16151;
	private Identifier field_16152;
	private UUID field_16153;
	private Class<?> field_16154;
	protected LivingEntityRenderer<? extends LivingEntity> field_16149;
	private EntityModel field_16155;
	private Identifier field_16156;
	private UUID field_16157;
	private Class<?> field_16158;

	public class_3302(EntityRenderDispatcher entityRenderDispatcher) {
		this.field_16150 = entityRenderDispatcher;
	}

	public void render(PlayerEntity playerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (playerEntity.method_14158() != null || playerEntity.method_14159() != null) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			NbtCompound nbtCompound = playerEntity.method_14158();
			if (!nbtCompound.isEmpty()) {
				class_3302.class_3303 lv = this.method_14697(
					playerEntity, this.field_16153, nbtCompound, this.field_16148, this.field_16151, this.field_16152, this.field_16154, f, g, h, i, j, k, l, true
				);
				this.field_16153 = lv.field_16159;
				this.field_16148 = lv.field_16160;
				this.field_16152 = lv.field_16162;
				this.field_16151 = lv.field_16161;
				this.field_16154 = lv.field_16163;
			}

			NbtCompound nbtCompound2 = playerEntity.method_14159();
			if (!nbtCompound2.isEmpty()) {
				class_3302.class_3303 lv2 = this.method_14697(
					playerEntity, this.field_16157, nbtCompound2, this.field_16149, this.field_16155, this.field_16156, this.field_16158, f, g, h, i, j, k, l, false
				);
				this.field_16157 = lv2.field_16159;
				this.field_16149 = lv2.field_16160;
				this.field_16156 = lv2.field_16162;
				this.field_16155 = lv2.field_16161;
				this.field_16158 = lv2.field_16163;
			}

			GlStateManager.disableRescaleNormal();
		}
	}

	private class_3302.class_3303 method_14697(
		PlayerEntity playerEntity,
		@Nullable UUID uUID,
		NbtCompound nbtCompound,
		LivingEntityRenderer<? extends LivingEntity> livingEntityRenderer,
		EntityModel entityModel,
		Identifier identifier,
		Class<?> class_,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		float l,
		boolean bl
	) {
		if (uUID == null || !uUID.equals(nbtCompound.getUuid("UUID"))) {
			uUID = nbtCompound.getUuid("UUID");
			class_ = EntityType.getEntityType(nbtCompound.getString("id"));
			if (class_ == ParrotEntity.class) {
				livingEntityRenderer = new ParrotEntityRenderer(this.field_16150);
				entityModel = new ParrotEntityModel();
				identifier = ParrotEntityRenderer.TEXTURES[nbtCompound.getInt("Variant")];
			}
		}

		livingEntityRenderer.bindTexture(identifier);
		GlStateManager.pushMatrix();
		float m = playerEntity.isSneaking() ? -1.3F : -1.5F;
		float n = bl ? 0.4F : -0.4F;
		GlStateManager.translate(n, m, 0.0F);
		if (class_ == ParrotEntity.class) {
			i = 0.0F;
		}

		entityModel.animateModel(playerEntity, f, g, h);
		entityModel.setAngles(f, g, i, j, k, l, playerEntity);
		entityModel.render(playerEntity, f, g, i, j, k, l);
		GlStateManager.popMatrix();
		return new class_3302.class_3303(uUID, livingEntityRenderer, entityModel, identifier, class_);
	}

	@Override
	public boolean combineTextures() {
		return false;
	}

	class class_3303 {
		public UUID field_16159;
		public LivingEntityRenderer<? extends LivingEntity> field_16160;
		public EntityModel field_16161;
		public Identifier field_16162;
		public Class<?> field_16163;

		public class_3303(
			UUID uUID, LivingEntityRenderer<? extends LivingEntity> livingEntityRenderer, EntityModel entityModel, Identifier identifier, Class<?> class_
		) {
			this.field_16159 = uUID;
			this.field_16160 = livingEntityRenderer;
			this.field_16161 = entityModel;
			this.field_16162 = identifier;
			this.field_16163 = class_;
		}
	}
}
