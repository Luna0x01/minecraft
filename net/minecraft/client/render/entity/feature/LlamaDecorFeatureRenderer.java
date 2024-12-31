package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.LlamaEntityRenderer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.util.Identifier;

public class LlamaDecorFeatureRenderer implements FeatureRenderer<LlamaEntity> {
	private static final Identifier[] TEXTURES = new Identifier[]{
		new Identifier("textures/entity/llama/decor/decor_white.png"),
		new Identifier("textures/entity/llama/decor/decor_orange.png"),
		new Identifier("textures/entity/llama/decor/decor_magenta.png"),
		new Identifier("textures/entity/llama/decor/decor_light_blue.png"),
		new Identifier("textures/entity/llama/decor/decor_yellow.png"),
		new Identifier("textures/entity/llama/decor/decor_lime.png"),
		new Identifier("textures/entity/llama/decor/decor_pink.png"),
		new Identifier("textures/entity/llama/decor/decor_gray.png"),
		new Identifier("textures/entity/llama/decor/decor_silver.png"),
		new Identifier("textures/entity/llama/decor/decor_cyan.png"),
		new Identifier("textures/entity/llama/decor/decor_purple.png"),
		new Identifier("textures/entity/llama/decor/decor_blue.png"),
		new Identifier("textures/entity/llama/decor/decor_brown.png"),
		new Identifier("textures/entity/llama/decor/decor_green.png"),
		new Identifier("textures/entity/llama/decor/decor_red.png"),
		new Identifier("textures/entity/llama/decor/decor_black.png")
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
