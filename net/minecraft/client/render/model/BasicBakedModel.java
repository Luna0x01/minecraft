package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.render.model.json.BlockModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BasicBakedModel implements BakedModel {
	protected final List<BakedQuad> quads;
	protected final List<List<BakedQuad>> directionalQuads;
	protected final boolean ambientOcclusion;
	protected final boolean depth;
	protected final Sprite sprite;
	protected final ModelTransformation transformation;

	public BasicBakedModel(List<BakedQuad> list, List<List<BakedQuad>> list2, boolean bl, boolean bl2, Sprite sprite, ModelTransformation modelTransformation) {
		this.quads = list;
		this.directionalQuads = list2;
		this.ambientOcclusion = bl;
		this.depth = bl2;
		this.sprite = sprite;
		this.transformation = modelTransformation;
	}

	@Override
	public List<BakedQuad> getByDirection(Direction direction) {
		return (List<BakedQuad>)this.directionalQuads.get(direction.ordinal());
	}

	@Override
	public List<BakedQuad> getQuads() {
		return this.quads;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.ambientOcclusion;
	}

	@Override
	public boolean hasDepth() {
		return this.depth;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return this.sprite;
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.transformation;
	}

	public static class Builder {
		private final List<BakedQuad> quads = Lists.newArrayList();
		private final List<List<BakedQuad>> directionalQuads = Lists.newArrayListWithCapacity(6);
		private final boolean ambientOcclusion;
		private Sprite sprite;
		private boolean depth;
		private ModelTransformation transformation;

		public Builder(BlockModel blockModel) {
			this(blockModel.hasAmbientOcclusion(), blockModel.hasDepth(), blockModel.getTransformation());
		}

		public Builder(BakedModel bakedModel, Sprite sprite) {
			this(bakedModel.useAmbientOcclusion(), bakedModel.hasDepth(), bakedModel.getTransformation());
			this.sprite = bakedModel.getParticleSprite();

			for (Direction direction : Direction.values()) {
				this.addTexturedQuad(bakedModel, sprite, direction);
			}

			this.addTexturedQuad(bakedModel, sprite);
		}

		private void addTexturedQuad(BakedModel model, Sprite sprite, Direction direction) {
			for (BakedQuad bakedQuad : model.getByDirection(direction)) {
				this.addQuad(direction, new TexturedBakedQuad(bakedQuad, sprite));
			}
		}

		private void addTexturedQuad(BakedModel model, Sprite sprite) {
			for (BakedQuad bakedQuad : model.getQuads()) {
				this.addQuad(new TexturedBakedQuad(bakedQuad, sprite));
			}
		}

		private Builder(boolean bl, boolean bl2, ModelTransformation modelTransformation) {
			for (Direction direction : Direction.values()) {
				this.directionalQuads.add(Lists.newArrayList());
			}

			this.ambientOcclusion = bl;
			this.depth = bl2;
			this.transformation = modelTransformation;
		}

		public BasicBakedModel.Builder addQuad(Direction direction, BakedQuad quad) {
			((List)this.directionalQuads.get(direction.ordinal())).add(quad);
			return this;
		}

		public BasicBakedModel.Builder addQuad(BakedQuad quad) {
			this.quads.add(quad);
			return this;
		}

		public BasicBakedModel.Builder setParticle(Sprite sprite) {
			this.sprite = sprite;
			return this;
		}

		public BakedModel build() {
			if (this.sprite == null) {
				throw new RuntimeException("Missing particle!");
			} else {
				return new BasicBakedModel(this.quads, this.directionalQuads, this.ambientOcclusion, this.depth, this.sprite, this.transformation);
			}
		}
	}
}
