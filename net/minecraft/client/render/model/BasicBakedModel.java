package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4231;
import net.minecraft.block.BlockState;
import net.minecraft.client.class_2876;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BasicBakedModel implements BakedModel {
	protected final List<BakedQuad> quads;
	protected final Map<Direction, List<BakedQuad>> field_13674;
	protected final boolean ambientOcclusion;
	protected final boolean depth;
	protected final Sprite sprite;
	protected final ModelTransformation transformation;
	protected final class_2876 field_13675;

	public BasicBakedModel(
		List<BakedQuad> list, Map<Direction, List<BakedQuad>> map, boolean bl, boolean bl2, Sprite sprite, ModelTransformation modelTransformation, class_2876 arg
	) {
		this.quads = list;
		this.field_13674 = map;
		this.ambientOcclusion = bl;
		this.depth = bl2;
		this.sprite = sprite;
		this.transformation = modelTransformation;
		this.field_13675 = arg;
	}

	@Override
	public List<BakedQuad> method_19561(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return direction == null ? this.quads : (List)this.field_13674.get(direction);
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

	@Override
	public class_2876 method_12503() {
		return this.field_13675;
	}

	public static class Builder {
		private final List<BakedQuad> quads = Lists.newArrayList();
		private final Map<Direction, List<BakedQuad>> field_13676 = Maps.newEnumMap(Direction.class);
		private final class_2876 field_13677;
		private final boolean ambientOcclusion;
		private Sprite sprite;
		private final boolean depth;
		private final ModelTransformation transformation;

		public Builder(class_4231 arg, class_2876 arg2) {
			this(arg.method_19222(), arg.method_19224(), arg.method_19230(), arg2);
		}

		public Builder(BlockState blockState, BakedModel bakedModel, Sprite sprite, Random random, long l) {
			this(bakedModel.useAmbientOcclusion(), bakedModel.hasDepth(), bakedModel.getTransformation(), bakedModel.method_12503());
			this.sprite = bakedModel.getParticleSprite();

			for (Direction direction : Direction.values()) {
				random.setSeed(l);

				for (BakedQuad bakedQuad : bakedModel.method_19561(blockState, direction, random)) {
					this.addQuad(direction, new TexturedBakedQuad(bakedQuad, sprite));
				}
			}

			random.setSeed(l);

			for (BakedQuad bakedQuad2 : bakedModel.method_19561(blockState, null, random)) {
				this.addQuad(new TexturedBakedQuad(bakedQuad2, sprite));
			}
		}

		private Builder(boolean bl, boolean bl2, ModelTransformation modelTransformation, class_2876 arg) {
			for (Direction direction : Direction.values()) {
				this.field_13676.put(direction, Lists.newArrayList());
			}

			this.field_13677 = arg;
			this.ambientOcclusion = bl;
			this.depth = bl2;
			this.transformation = modelTransformation;
		}

		public BasicBakedModel.Builder addQuad(Direction direction, BakedQuad quad) {
			((List)this.field_13676.get(direction)).add(quad);
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
				return new BasicBakedModel(this.quads, this.field_13676, this.ambientOcclusion, this.depth, this.sprite, this.transformation, this.field_13677);
			}
		}
	}
}
