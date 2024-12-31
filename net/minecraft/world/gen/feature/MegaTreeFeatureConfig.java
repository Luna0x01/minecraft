package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.stateprovider.StateProvider;

public class MegaTreeFeatureConfig extends TreeFeatureConfig {
	public final int heightInterval;
	public final int crownHeight;

	protected MegaTreeFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, List<TreeDecorator> list, int i, int j, int k) {
		super(stateProvider, stateProvider2, list, i);
		this.heightInterval = j;
		this.crownHeight = k;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Dynamic<T> dynamic = new Dynamic(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("height_interval"),
					dynamicOps.createInt(this.heightInterval),
					dynamicOps.createString("crown_height"),
					dynamicOps.createInt(this.crownHeight)
				)
			)
		);
		return dynamic.merge(super.serialize(dynamicOps));
	}

	public static <T> MegaTreeFeatureConfig deserialize(Dynamic<T> dynamic) {
		TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
		return new MegaTreeFeatureConfig(
			treeFeatureConfig.trunkProvider,
			treeFeatureConfig.leavesProvider,
			treeFeatureConfig.decorators,
			treeFeatureConfig.baseHeight,
			dynamic.get("height_interval").asInt(0),
			dynamic.get("crown_height").asInt(0)
		);
	}

	public static class Builder extends TreeFeatureConfig.Builder {
		private List<TreeDecorator> field_21234 = ImmutableList.of();
		private int field_21235;
		private int heightInterval;
		private int crownHeight;

		public Builder(StateProvider stateProvider, StateProvider stateProvider2) {
			super(stateProvider, stateProvider2);
		}

		public MegaTreeFeatureConfig.Builder treeDecorators(List<TreeDecorator> list) {
			this.field_21234 = list;
			return this;
		}

		public MegaTreeFeatureConfig.Builder baseHeight(int i) {
			this.field_21235 = i;
			return this;
		}

		public MegaTreeFeatureConfig.Builder heightInterval(int i) {
			this.heightInterval = i;
			return this;
		}

		public MegaTreeFeatureConfig.Builder crownHeight(int i) {
			this.crownHeight = i;
			return this;
		}

		public MegaTreeFeatureConfig build() {
			return new MegaTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_21234, this.field_21235, this.heightInterval, this.crownHeight);
		}
	}
}
