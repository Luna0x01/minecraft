package net.minecraft.client.render.model;

import java.util.Map;
import net.minecraft.class_4288;
import net.minecraft.class_4290;
import net.minecraft.client.render.block.BlockModelShapes;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;

public class BakedModelManager implements ResourceReloadListener {
	private Map<class_4290, BakedModel> field_21090;
	private final SpriteAtlasTexture atlas;
	private final BlockModelShapes shapes;
	private BakedModel bakedModel;

	public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture) {
		this.atlas = spriteAtlasTexture;
		this.shapes = new BlockModelShapes(this);
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		class_4288 lv = new class_4288(resourceManager, this.atlas);
		this.field_21090 = lv.method_19570();
		this.bakedModel = (BakedModel)this.field_21090.get(class_4288.field_21079);
		this.shapes.reload();
	}

	public BakedModel method_19594(class_4290 arg) {
		return (BakedModel)this.field_21090.getOrDefault(arg, this.bakedModel);
	}

	public BakedModel getBakedModel() {
		return this.bakedModel;
	}

	public BlockModelShapes getModelShapes() {
		return this.shapes;
	}
}
