package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class PigSaddleFeatureRenderer extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
	private final PigEntityModel<PigEntity> model = new PigEntityModel<>(0.5F);

	public PigSaddleFeatureRenderer(FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4196(PigEntity pigEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (pigEntity.isSaddled()) {
			this.bindTexture(SKIN);
			this.getModel().copyStateTo(this.model);
			this.model.render(pigEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
