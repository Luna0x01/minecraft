package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.LlamaEntityRenderer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.util.Identifier;

public class LlamaDecorFeatureRenderer implements FeatureRenderer<LlamaEntity> {
	private static final Identifier[] TEXTURES = new Identifier[]{
		new Identifier("textures/entity/llama/decor/white.png"),
		new Identifier("textures/entity/llama/decor/orange.png"),
		new Identifier("textures/entity/llama/decor/magenta.png"),
		new Identifier("textures/entity/llama/decor/light_blue.png"),
		new Identifier("textures/entity/llama/decor/yellow.png"),
		new Identifier("textures/entity/llama/decor/lime.png"),
		new Identifier("textures/entity/llama/decor/pink.png"),
		new Identifier("textures/entity/llama/decor/gray.png"),
		new Identifier("textures/entity/llama/decor/light_gray.png"),
		new Identifier("textures/entity/llama/decor/cyan.png"),
		new Identifier("textures/entity/llama/decor/purple.png"),
		new Identifier("textures/entity/llama/decor/blue.png"),
		new Identifier("textures/entity/llama/decor/brown.png"),
		new Identifier("textures/entity/llama/decor/green.png"),
		new Identifier("textures/entity/llama/decor/red.png"),
		new Identifier("textures/entity/llama/decor/black.png")
	};
	private final LlamaEntityRenderer field_15318;
	private final LlamaEntityModel model = new LlamaEntityModel(0.5F);

	public LlamaDecorFeatureRenderer(LlamaEntityRenderer llamaEntityRenderer) {
		this.field_15318 = llamaEntityRenderer;
	}

	public void render(LlamaEntity llamaEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (llamaEntity.method_14026()) {
			this.field_15318.bindTexture(TEXTURES[llamaEntity.method_14027().getId()]);
			this.model.copy(this.field_15318.getModel());
			this.model.render(llamaEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
