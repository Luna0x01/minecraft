package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModelOverrideList {
	public static final ModelOverrideList EMPTY = new ModelOverrideList();
	private final ModelOverrideList.BakedOverride[] overrides;
	private final Identifier[] conditionTypes;

	private ModelOverrideList() {
		this.overrides = new ModelOverrideList.BakedOverride[0];
		this.conditionTypes = new Identifier[0];
	}

	public ModelOverrideList(
		ModelLoader modelLoader, JsonUnbakedModel parent, Function<Identifier, UnbakedModel> unbakedModelGetter, List<ModelOverride> overrides
	) {
		this.conditionTypes = (Identifier[])overrides.stream()
			.flatMap(ModelOverride::streamConditions)
			.map(ModelOverride.Condition::getType)
			.distinct()
			.toArray(Identifier[]::new);
		Object2IntMap<Identifier> object2IntMap = new Object2IntOpenHashMap();

		for (int i = 0; i < this.conditionTypes.length; i++) {
			object2IntMap.put(this.conditionTypes[i], i);
		}

		List<ModelOverrideList.BakedOverride> list = Lists.newArrayList();

		for (int j = overrides.size() - 1; j >= 0; j--) {
			ModelOverride modelOverride = (ModelOverride)overrides.get(j);
			BakedModel bakedModel = this.bakeOverridingModel(modelLoader, parent, unbakedModelGetter, modelOverride);
			ModelOverrideList.InlinedCondition[] inlinedConditions = (ModelOverrideList.InlinedCondition[])modelOverride.streamConditions().map(condition -> {
				int i = object2IntMap.getInt(condition.getType());
				return new ModelOverrideList.InlinedCondition(i, condition.getThreshold());
			}).toArray(ModelOverrideList.InlinedCondition[]::new);
			list.add(new ModelOverrideList.BakedOverride(inlinedConditions, bakedModel));
		}

		this.overrides = (ModelOverrideList.BakedOverride[])list.toArray(new ModelOverrideList.BakedOverride[0]);
	}

	@Nullable
	private BakedModel bakeOverridingModel(
		ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, UnbakedModel> unbakedModelGetter, ModelOverride override
	) {
		UnbakedModel unbakedModel = (UnbakedModel)unbakedModelGetter.apply(override.getModelId());
		return Objects.equals(unbakedModel, parent) ? null : loader.bake(override.getModelId(), net.minecraft.client.render.model.ModelRotation.X0_Y0);
	}

	@Nullable
	public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
		if (this.overrides.length != 0) {
			Item item = stack.getItem();
			int i = this.conditionTypes.length;
			float[] fs = new float[i];

			for (int j = 0; j < i; j++) {
				Identifier identifier = this.conditionTypes[j];
				ModelPredicateProvider modelPredicateProvider = ModelPredicateProviderRegistry.get(item, identifier);
				if (modelPredicateProvider != null) {
					fs[j] = modelPredicateProvider.call(stack, world, entity, seed);
				} else {
					fs[j] = Float.NEGATIVE_INFINITY;
				}
			}

			for (ModelOverrideList.BakedOverride bakedOverride : this.overrides) {
				if (bakedOverride.test(fs)) {
					BakedModel bakedModel = bakedOverride.model;
					if (bakedModel == null) {
						return model;
					}

					return bakedModel;
				}
			}
		}

		return model;
	}

	static class BakedOverride {
		private final ModelOverrideList.InlinedCondition[] conditions;
		@Nullable
		final BakedModel model;

		BakedOverride(ModelOverrideList.InlinedCondition[] conditions, @Nullable BakedModel model) {
			this.conditions = conditions;
			this.model = model;
		}

		boolean test(float[] values) {
			for (ModelOverrideList.InlinedCondition inlinedCondition : this.conditions) {
				float f = values[inlinedCondition.index];
				if (f < inlinedCondition.threshold) {
					return false;
				}
			}

			return true;
		}
	}

	static class InlinedCondition {
		public final int index;
		public final float threshold;

		InlinedCondition(int index, float threshold) {
			this.index = index;
			this.threshold = threshold;
		}
	}
}
