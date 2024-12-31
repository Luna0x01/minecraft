package net.minecraft.client.render.model;

import net.minecraft.client.render.block.BlockModelShapes;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.registry.Registry;

public class BakedModelManager implements ResourceReloadListener {
	private Registry<ModelIdentifier, BakedModel> bakedModels;
	private final SpriteAtlasTexture atlas;
	private final BlockModelShapes shapes;
	private BakedModel bakedModel;

	public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture) {
		this.atlas = spriteAtlasTexture;
		this.shapes = new BlockModelShapes(this);
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		ModelLoader modelLoader = new ModelLoader(resourceManager, this.atlas, this.shapes);
		this.bakedModels = modelLoader.method_10383();
		this.bakedModel = this.bakedModels.get(ModelLoader.MISSING_ID);
		this.shapes.reload();
	}

	public BakedModel getByIdentifier(ModelIdentifier identifier) {
		if (identifier == null) {
			return this.bakedModel;
		} else {
			BakedModel bakedModel = this.bakedModels.get(identifier);
			return bakedModel == null ? this.bakedModel : bakedModel;
		}
	}

	public BakedModel getBakedModel() {
		return this.bakedModel;
	}

	public SpriteAtlasTexture getAtlas() {
		return this.atlas;
	}

	public BlockModelShapes getModelShapes() {
		return this.shapes;
	}
}
