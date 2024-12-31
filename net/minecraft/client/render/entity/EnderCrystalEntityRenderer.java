package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EnderCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnderCrystalEntityRenderer extends EntityRenderer<EndCrystalEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/endercrystal/endercrystal.png");
	private EntityModel model = new EnderCrystalEntityModel(0.0F, true);

	public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(EndCrystalEntity endCrystalEntity, double d, double e, double f, float g, float h) {
		float i = (float)endCrystalEntity.endCrystalAge + h;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e, (float)f);
		this.bindTexture(TEXTURE);
		float j = MathHelper.sin(i * 0.2F) / 2.0F + 0.5F;
		j = j * j + j;
		this.model.render(endCrystalEntity, 0.0F, i * 3.0F, j * 0.2F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		super.render(endCrystalEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(EndCrystalEntity endCrystalEntity) {
		return TEXTURE;
	}
}
