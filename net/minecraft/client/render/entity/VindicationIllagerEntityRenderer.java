package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3087;
import net.minecraft.class_3091;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VindicationIllagerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;

public class VindicationIllagerEntityRenderer extends MobEntityRenderer<HostileEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vindicator.png");

	public VindicationIllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_3091(0.0F), 0.5F);
		this.addFeature(new HeldItemRenderer(this) {
			@Override
			public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
				if (((VindicationIllagerEntity)entity).method_13600()) {
					super.render(entity, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale);
				}
			}

			@Override
			protected void method_13883(HandOption handOption) {
				((class_3087)this.entityRenderer.getModel()).method_13840(handOption).preRender(0.0625F);
			}
		});
	}

	public void render(HostileEntity hostileEntity, double d, double e, double f, float g, float h) {
		super.render(hostileEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(HostileEntity hostileEntity) {
		return TEXTURE;
	}

	protected void scale(HostileEntity hostileEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
