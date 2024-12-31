package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3087;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.entity.IllusionIllagerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class IllusionIllagerEntityRenderer extends MobEntityRenderer<HostileEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/illusionist.png");

	public IllusionIllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_3087(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(new HeldItemRenderer(this) {
			@Override
			public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
				if (((IllusionIllagerEntity)entity).method_14133() || ((IllusionIllagerEntity)entity).method_14127()) {
					super.render(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale);
				}
			}

			@Override
			protected void method_13883(HandOption handOption) {
				((class_3087)this.entityRenderer.getModel()).method_13840(handOption).preRender(0.0625F);
			}
		});
		((class_3087)this.getModel()).field_16093.visible = true;
	}

	protected Identifier getTexture(HostileEntity hostileEntity) {
		return TEXTURE;
	}

	protected void scale(HostileEntity hostileEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
	}

	public void render(HostileEntity hostileEntity, double d, double e, double f, float g, float h) {
		if (hostileEntity.isInvisible()) {
			Vec3d[] vec3ds = ((IllusionIllagerEntity)hostileEntity).method_14126(h);
			float i = this.method_5783(hostileEntity, h);

			for (int j = 0; j < vec3ds.length; j++) {
				super.render(
					hostileEntity,
					d + vec3ds[j].x + (double)MathHelper.cos((float)j + i * 0.5F) * 0.025,
					e + vec3ds[j].y + (double)MathHelper.cos((float)j + i * 0.75F) * 0.0125,
					f + vec3ds[j].z + (double)MathHelper.cos((float)j + i * 0.7F) * 0.025,
					g,
					h
				);
			}
		} else {
			super.render(hostileEntity, d, e, f, g, h);
		}
	}

	public void method_12462(HostileEntity hostileEntity, double d, double e, double f) {
		super.method_10208(hostileEntity, d, e, f);
	}

	protected boolean method_14691(HostileEntity hostileEntity) {
		return true;
	}
}
