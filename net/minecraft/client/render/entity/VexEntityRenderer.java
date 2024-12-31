package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.VexEntity;
import net.minecraft.util.Identifier;

public class VexEntityRenderer extends BipedEntityRenderer<VexEntity> {
	private static final Identifier TEXTURE_VEX = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier TEXTURE_CHARGING = new Identifier("textures/entity/illager/vex_charging.png");
	private int field_15306 = ((VexEntityModel)this.model).method_13841();

	public VexEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VexEntityModel(), 0.3F);
	}

	protected Identifier getTexture(VexEntity vexEntity) {
		return vexEntity.isCharging() ? TEXTURE_CHARGING : TEXTURE_VEX;
	}

	public void render(VexEntity vexEntity, double d, double e, double f, float g, float h) {
		int i = ((VexEntityModel)this.model).method_13841();
		if (i != this.field_15306) {
			this.model = new VexEntityModel();
			this.field_15306 = i;
		}

		super.render(vexEntity, d, e, f, g, h);
	}

	protected void scale(VexEntity vexEntity, float f) {
		GlStateManager.scale(0.4F, 0.4F, 0.4F);
	}
}
