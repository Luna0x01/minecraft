package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;

public class TropicalFishSomethingFeatureRenderer extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
	private final TropicalFishEntityModelA<TropicalFishEntity> modelA = new TropicalFishEntityModelA<>(0.008F);
	private final TropicalFishEntityModelB<TropicalFishEntity> modelB = new TropicalFishEntityModelB<>(0.008F);

	public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		TropicalFishEntity tropicalFishEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		EntityModel<TropicalFishEntity> entityModel = (EntityModel<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0 ? this.modelA : this.modelB);
		float[] fs = tropicalFishEntity.getPatternColorComponents();
		render(
			this.getContextModel(),
			entityModel,
			tropicalFishEntity.getVarietyId(),
			matrixStack,
			vertexConsumerProvider,
			i,
			tropicalFishEntity,
			f,
			g,
			j,
			k,
			l,
			h,
			fs[0],
			fs[1],
			fs[2]
		);
	}
}
