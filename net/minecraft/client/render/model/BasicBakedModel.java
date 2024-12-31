package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BasicBakedModel implements BakedModel {
	protected final List<BakedQuad> quads;
	protected final Map<Direction, List<BakedQuad>> faceQuads;
	protected final boolean usesAo;
	protected final boolean hasDepth;
	protected final boolean isSideLit;
	protected final Sprite sprite;
	protected final ModelTransformation transformation;
	protected final ModelItemPropertyOverrideList itemPropertyOverrides;

	public BasicBakedModel(
		List<BakedQuad> list,
		Map<Direction, List<BakedQuad>> map,
		boolean bl,
		boolean bl2,
		boolean bl3,
		Sprite sprite,
		ModelTransformation modelTransformation,
		ModelItemPropertyOverrideList modelItemPropertyOverrideList
	) {
		this.quads = list;
		this.faceQuads = map;
		this.usesAo = bl;
		this.hasDepth = bl3;
		this.isSideLit = bl2;
		this.sprite = sprite;
		this.transformation = modelTransformation;
		this.itemPropertyOverrides = modelItemPropertyOverrideList;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return direction == null ? this.quads : (List)this.faceQuads.get(direction);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.usesAo;
	}

	@Override
	public boolean hasDepth() {
		return this.hasDepth;
	}

	@Override
	public boolean isSideLit() {
		return this.isSideLit;
	}

	@Override
	public boolean isBuiltin() {
		return false;
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

	public static class Builder {
		private final List<BakedQuad> quads = Lists.newArrayList();
		private final Map<Direction, List<BakedQuad>> faceQuads = Maps.newEnumMap(Direction.class);
		private final ModelItemPropertyOverrideList itemPropertyOverrides;
		private final boolean usesAo;
		private Sprite particleTexture;
		private final boolean isSideLit;
		private final boolean hasDepth;
		private final ModelTransformation transformation;

		public Builder(JsonUnbakedModel jsonUnbakedModel, ModelItemPropertyOverrideList modelItemPropertyOverrideList, boolean bl) {
			this(
				jsonUnbakedModel.useAmbientOcclusion(), jsonUnbakedModel.getGuiLight().isSide(), bl, jsonUnbakedModel.getTransformations(), modelItemPropertyOverrideList
			);
		}

		private Builder(boolean bl, boolean bl2, boolean bl3, ModelTransformation modelTransformation, ModelItemPropertyOverrideList modelItemPropertyOverrideList) {
			for (Direction direction : Direction.values()) {
				this.faceQuads.put(direction, Lists.newArrayList());
			}

			this.itemPropertyOverrides = modelItemPropertyOverrideList;
			this.usesAo = bl;
			this.isSideLit = bl2;
			this.hasDepth = bl3;
			this.transformation = modelTransformation;
		}

		public BasicBakedModel.Builder addQuad(Direction direction, BakedQuad bakedQuad) {
			((List)this.faceQuads.get(direction)).add(bakedQuad);
			return this;
		}

		public BasicBakedModel.Builder addQuad(BakedQuad bakedQuad) {
			this.quads.add(bakedQuad);
			return this;
		}

		public BasicBakedModel.Builder setParticle(Sprite sprite) {
			this.particleTexture = sprite;
			return this;
		}

		public BakedModel build() {
			if (this.particleTexture == null) {
				throw new RuntimeException("Missing particle!");
			} else {
				return new BasicBakedModel(
					this.quads, this.faceQuads, this.usesAo, this.isSideLit, this.hasDepth, this.particleTexture, this.transformation, this.itemPropertyOverrides
				);
			}
		}
	}
}
