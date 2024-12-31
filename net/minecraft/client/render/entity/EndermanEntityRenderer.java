package net.minecraft.client.render.entity;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanHeldBlockFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;

public class EndermanEntityRenderer extends MobEntityRenderer<EndermanEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/enderman/enderman.png");
	private final Random random = new Random();

	public EndermanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EndermanEntityModel(0.0F), 0.5F);
		this.addFeature(new EndermanEyesFeatureRenderer(this));
		this.addFeature(new EndermanHeldBlockFeatureRenderer(this));
	}

	public EndermanEntityModel getModel() {
		return (EndermanEntityModel)super.getModel();
	}

	public void render(EndermanEntity endermanEntity, double d, double e, double f, float g, float h) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		EndermanEntityModel endermanEntityModel = this.getModel();
		endermanEntityModel.carryingBlock = blockState != null;
		endermanEntityModel.angry = endermanEntity.isAngry();
		if (endermanEntity.isAngry()) {
			double i = 0.02;
			d += this.random.nextGaussian() * 0.02;
			f += this.random.nextGaussian() * 0.02;
		}

		super.render(endermanEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(EndermanEntity endermanEntity) {
		return TEXTURE;
	}
}
