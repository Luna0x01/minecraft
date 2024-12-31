package net.minecraft.client.render.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
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
		this.totalWeight = Weighting.getRate(list);
		this.model = ((WeightedBakedModel.WeightedRandomItemEntry)list.get(0)).model;
	}

	@Override
	public List<BakedQuad> getByDirection(Direction direction) {
		return this.model.getByDirection(direction);
	}

	@Override
	public List<BakedQuad> getQuads() {
		return this.model.getQuads();
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

	public BakedModel method_10425(long l) {
		return Weighting.pick(this.modelItems, Math.abs((int)l >> 16) % this.totalWeight).model;
	}

	public static class Builder {
		private List<WeightedBakedModel.WeightedRandomItemEntry> models = Lists.newArrayList();

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
			return ComparisonChain.start()
				.compare(weightedRandomItemEntry.weight, this.weight)
				.compare(this.method_10429(), weightedRandomItemEntry.method_10429())
				.result();
		}

		protected int method_10429() {
			int i = this.model.getQuads().size();

			for (Direction direction : Direction.values()) {
				i += this.model.getByDirection(direction).size();
			}

			return i;
		}

		public String toString() {
			return "MyWeighedRandomItem{weight=" + this.weight + ", model=" + this.model + '}';
		}
	}
}
