package net.minecraft.client.render.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.class_2876;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public interface BakedModel {
	List<BakedQuad> method_12502(@Nullable BlockState blockState, @Nullable Direction direction, long l);

	boolean useAmbientOcclusion();

	boolean hasDepth();

	boolean isBuiltin();

	Sprite getParticleSprite();

	ModelTransformation getTransformation();

	class_2876 method_12503();
}
