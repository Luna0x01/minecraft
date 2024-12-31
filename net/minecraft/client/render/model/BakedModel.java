package net.minecraft.client.render.model;

import java.util.List;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public interface BakedModel {
	List<BakedQuad> getByDirection(Direction direction);

	List<BakedQuad> getQuads();

	boolean useAmbientOcclusion();

	boolean hasDepth();

	boolean isBuiltin();

	Sprite getParticleSprite();

	ModelTransformation getTransformation();
}
