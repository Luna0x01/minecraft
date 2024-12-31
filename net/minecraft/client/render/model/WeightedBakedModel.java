package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.class_2876;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.Direction;

public class WeightedBakedModel implements BakedModel {
	private final int totalWeight;
	private final List<WeightedBakedModel.class_4292> modelItems;
	private final BakedModel model;

	public WeightedBakedModel(List<WeightedBakedModel.class_4292> list) {
		this.modelItems = list;
		this.totalWeight = Weighting.getWeightSum(list);
		this.model = ((WeightedBakedModel.class_4292)list.get(0)).field_21095;
	}

	@Override
	public List<BakedQuad> method_19561(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return Weighting.getAt(this.modelItems, Math.abs((int)random.nextLong()) % this.totalWeight).field_21095.method_19561(blockState, direction, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.model.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return this.model.hasDepth();
	}

	@Override
	public boolean isBuiltin() {
		return this.model.isBuiltin();
	}

	@Override
	public Sprite getParticleSprite() {
		return this.model.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.model.getTransformation();
	}

	@Override
	public class_2876 method_12503() {
		return this.model.method_12503();
	}

	public static class Builder {
		private final List<WeightedBakedModel.class_4292> models = Lists.newArrayList();

		public WeightedBakedModel.Builder add(@Nullable BakedModel model, int weight) {
			if (model != null) {
				this.models.add(new WeightedBakedModel.class_4292(model, weight));
			}

			return this;
		}

		@Nullable
		public BakedModel getFirst() {
			if (this.models.isEmpty()) {
				return null;
			} else {
				return (BakedModel)(this.models.size() == 1 ? ((WeightedBakedModel.class_4292)this.models.get(0)).field_21095 : new WeightedBakedModel(this.models));
			}
		}
	}

	static class class_4292 extends Weighting.Weight {
		protected final BakedModel field_21095;

		public class_4292(BakedModel bakedModel, int i) {
			super(i);
			this.field_21095 = bakedModel;
		}
	}
}
