package net.minecraft.client.render.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BuiltinBakedModel implements BakedModel {
	private final ModelTransformation transformation;
	private final ModelItemPropertyOverrideList itemPropertyOverrides;
	private final Sprite sprite;
	private final boolean field_21862;

	public BuiltinBakedModel(ModelTransformation modelTransformation, ModelItemPropertyOverrideList modelItemPropertyOverrideList, Sprite sprite, boolean bl) {
		this.transformation = modelTransformation;
		this.itemPropertyOverrides = modelItemPropertyOverrideList;
		this.sprite = sprite;
		this.field_21862 = bl;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
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
	public boolean isSideLit() {
		return this.field_21862;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.transformation;
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return this.itemPropertyOverrides;
	}
}
