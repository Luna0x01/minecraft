package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.util.Identifier;

public class LlamaEntityRenderer extends MobEntityRenderer<LlamaEntity> {
	private static final Identifier[] TEXTURES = new Identifier[]{
		new Identifier("textures/entity/llama/llama_creamy.png"),
		new Identifier("textures/entity/llama/llama_white.png"),
		new Identifier("textures/entity/llama/llama_brown.png"),
		new Identifier("textures/entity/llama/llama_gray.png")
	};

	public LlamaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new LlamaEntityModel(0.0F), 0.7F);
		this.addFeature(new LlamaDecorFeatureRenderer(this));
	}

	protected Identifier getTexture(LlamaEntity llamaEntity) {
		return TEXTURES[llamaEntity.getVariant()];
	}
}
