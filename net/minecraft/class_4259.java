package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

public class class_4259 extends MobEntityRenderer<PhantomEntity> {
	private static final Identifier field_20937 = new Identifier("textures/entity/phantom.png");

	public class_4259(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4190(), 0.75F);
		this.addFeature(new class_4270(this));
	}

	protected Identifier getTexture(PhantomEntity phantomEntity) {
		return field_20937;
	}

	protected void scale(PhantomEntity phantomEntity, float f) {
		int i = phantomEntity.method_15876();
		float g = 1.0F + 0.15F * (float)i;
		GlStateManager.scale(g, g, g);
		GlStateManager.translate(0.0F, 1.3125F, 0.1875F);
	}

	protected void method_5777(PhantomEntity phantomEntity, float f, float g, float h) {
		super.method_5777(phantomEntity, f, g, h);
		GlStateManager.rotate(phantomEntity.pitch, 1.0F, 0.0F, 0.0F);
	}
}
