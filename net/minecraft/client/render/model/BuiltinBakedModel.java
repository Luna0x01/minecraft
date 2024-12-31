package net.minecraft.client.render.model;

import java.util.List;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BuiltinBakedModel implements BakedModel {
	private ModelTransformation transformation;

	public BuiltinBakedModel(ModelTransformation modelTransformation) {
		this.transformation = modelTransformation;
	}

	@Override
	public List<BakedQuad> getByDirection(Direction direction) {
		return null;
	}

	@Override
	public List<BakedQuad> getQuads() {
		return null;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean hasDepth() {
		return true;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public Sprite getParticleSprite() {
		return null;
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.transformation;
	}
}
