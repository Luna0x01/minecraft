package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

public class CatCollarFeatureRenderer extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/cat/cat_collar.png");
	private final CatEntityModel<CatEntity> model;

	public CatCollarFeatureRenderer(FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> context, EntityModelLoader loader) {
		super(context);
		this.model = new CatEntityModel<>(loader.getModelPart(EntityModelLayers.CAT_COLLAR));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CatEntity catEntity, float f, float g, float h, float j, float k, float l
	) {
		if (catEntity.isTamed()) {
			float[] fs = catEntity.getCollarColor().getColorComponents();
			render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, catEntity, f, g, j, k, l, h, fs[0], fs[1], fs[2]);
		}
	}
}
