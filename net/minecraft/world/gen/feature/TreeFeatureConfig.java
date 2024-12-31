package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class TreeFeatureConfig implements FeatureConfig {
	public final StateProvider trunkProvider;
	public final StateProvider leavesProvider;
	public final List<TreeDecorator> decorators;
	public final int baseHeight;
	public transient boolean field_21593;

	protected TreeFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, List<TreeDecorator> list, int i) {
		this.trunkProvider = stateProvider;
		this.leavesProvider = stateProvider2;
		this.decorators = list;
		this.baseHeight = i;
	}

	public void method_23916() {
		this.field_21593 = true;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		com.google.common.collect.ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("trunk_provider"), this.trunkProvider.serialize(dynamicOps))
			.put(dynamicOps.createString("leaves_provider"), this.leavesProvider.serialize(dynamicOps))
			.put(dynamicOps.createString("decorators"), dynamicOps.createList(this.decorators.stream().map(treeDecorator -> treeDecorator.serialize(dynamicOps))))
			.put(dynamicOps.createString("base_height"), dynamicOps.createInt(this.baseHeight));
		return new Dynamic(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	public static <T> TreeFeatureConfig deserialize(Dynamic<T> dynamic) {
		StateProviderType<?> stateProviderType = Registry.field_21445
			.get(new Identifier((String)dynamic.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		StateProviderType<?> stateProviderType2 = Registry.field_21445
			.get(new Identifier((String)dynamic.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		return new TreeFeatureConfig(
			stateProviderType.deserialize(dynamic.get("trunk_provider").orElseEmptyMap()),
			stateProviderType2.deserialize(dynamic.get("leaves_provider").orElseEmptyMap()),
			dynamic.get("decorators")
				.asList(
					dynamicx -> Registry.field_21448.get(new Identifier((String)dynamicx.get("type").asString().orElseThrow(RuntimeException::new))).method_23472(dynamicx)
				),
			dynamic.get("base_height").asInt(0)
		);
	}

	public static class Builder {
		public final StateProvider trunkProvider;
		public final StateProvider leavesProvider;
		private List<TreeDecorator> decorators = Lists.newArrayList();
		private int baseHeight = 0;

		public Builder(StateProvider stateProvider, StateProvider stateProvider2) {
			this.trunkProvider = stateProvider;
			this.leavesProvider = stateProvider2;
		}

		public TreeFeatureConfig.Builder baseHeight(int i) {
			this.baseHeight = i;
			return this;
		}

		public TreeFeatureConfig build() {
			return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.decorators, this.baseHeight);
		}
	}
}
