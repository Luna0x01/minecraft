package net.minecraft.client.render.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.class_2876;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.Direction;

public class WeightedBakedModel implements BakedModel {
	private final int totalWeight;
	private final List<WeightedBakedModel.WeightedRandomItemEntry> modelItems;
	private final BakedModel model;

	public WeightedBakedModel(List<WeightedBakedModel.WeightedRandomItemEntry> list) {
		this.modelItems = list;
		this.totalWeight = Weighting.getWeightSum(list);
		this.model = ((WeightedBakedModel.WeightedRandomItemEntry)list.get(0)).model;
	}

	private BakedModel method_12519(long l) {
		return Weighting.getAt(this.modelItems, Math.abs((int)l >> 16) % this.totalWeight).model;
	}

	@Override
	public List<BakedQuad> method_12502(@Nullable BlockState blockState, @Nullable Direction direction, long l) {
		return this.method_12519(l).method_12502(blockState, direction, l);
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
		private final List<WeightedBakedModel.WeightedRandomItemEntry> models = Lists.newArrayList();

		public WeightedBakedModel.Builder add(BakedModel model, int weight) {
			this.models.add(new WeightedBakedModel.WeightedRandomItemEntry(model, weight));
			return this;
		}

		public WeightedBakedModel build() {
			Collections.sort(this.models);
			return new WeightedBakedModel(this.models);
		}

		public BakedModel getFirst() {
			return ((WeightedBakedModel.WeightedRandomItemEntry)this.models.get(0)).model;
		}
	}

	static class WeightedRandomItemEntry extends Weighting.Weight implements Comparable<WeightedBakedModel.WeightedRandomItemEntry> {
		protected final BakedModel model;

		public WeightedRandomItemEntry(BakedModel bakedModel, int i) {
			super(i);
			this.model = bakedModel;
		}

		public int compareTo(WeightedBakedModel.WeightedRandomItemEntry weightedRandomItemEntry) {
			return ComparisonChain.start().compare(weightedRandomItemEntry.weight, this.weight).result();
		}

		public String toString() {
			return "MyWeighedRandomItem{weight=" + this.weight + ", model=" + this.model + '}';
		}
	}
}
