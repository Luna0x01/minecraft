package net.minecraft.client.render.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public interface BakedModel {
	List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random);

	boolean useAmbientOcclusion();

	boolean hasDepth();

	boolean isSideLit();

	boolean isBuiltin();

	Sprite getSprite();

	ModelTransformation getTransformation();

	ModelOverrideList getOverrides();
}
