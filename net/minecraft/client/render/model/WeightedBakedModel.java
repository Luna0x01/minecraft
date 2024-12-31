package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.Direction;

public class WeightedBakedModel implements BakedModel {
	private final int totalWeight;
	private final List<WeightedBakedModel.Entry> models;
	private final BakedModel defaultModel;

	public WeightedBakedModel(List<WeightedBakedModel.Entry> list) {
		this.models = list;
		this.totalWeight = WeightedPicker.getWeightSum(list);
		this.defaultModel = ((WeightedBakedModel.Entry)list.get(0)).model;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return WeightedPicker.getAt(this.models, Math.abs((int)random.nextLong()) % this.totalWeight).model.getQuads(blockState, direction, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.defaultModel.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return this.defaultModel.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return this.defaultModel.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return this.defaultModel.isBuiltin();
	}

	@Override
	public Sprite getSprite() {
		return this.defaultModel.getSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.defaultModel.getTransformation();
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return this.defaultModel.getItemPropertyOverrides();
	}

	public static class Builder {
		private final List<WeightedBakedModel.Entry> models = Lists.newArrayList();

		public WeightedBakedModel.Builder add(@Nullable BakedModel bakedModel, int i) {
			if (bakedModel != null) {
				this.models.add(new WeightedBakedModel.Entry(bakedModel, i));
			}

			return this;
		}

		@Nullable
		public BakedModel getFirst() {
			if (this.models.isEmpty()) {
				return null;
			} else {
				return (BakedModel)(this.models.size() == 1 ? ((WeightedBakedModel.Entry)this.models.get(0)).model : new WeightedBakedModel(this.models));
			}
		}
	}

	static class Entry extends WeightedPicker.Entry {
		protected final BakedModel model;

		public Entry(BakedModel bakedModel, int i) {
			super(i);
			this.model = bakedModel;
		}
	}
}
