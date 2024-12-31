package net.minecraft.client.render.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.class_2876;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BuiltinBakedModel implements BakedModel {
	private final ModelTransformation transformation;
	private final class_2876 field_13658;

	public BuiltinBakedModel(ModelTransformation modelTransformation, class_2876 arg) {
		this.transformation = modelTransformation;
		this.field_13658 = arg;
	}

	@Override
	public List<BakedQuad> method_19561(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return Collections.emptyList();
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

	@Override
	public class_2876 method_12503() {
		return this.field_13658;
	}
}
