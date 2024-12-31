package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class SimpleStateProvider extends StateProvider {
	private final BlockState state;

	public SimpleStateProvider(BlockState blockState) {
		super(StateProviderType.field_21305);
		this.state = blockState;
	}

	public <T> SimpleStateProvider(Dynamic<T> dynamic) {
		this(BlockState.deserialize(dynamic.get("state").orElseEmptyMap()));
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos blockPos) {
		return this.state;
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.field_21445.getId(this.stateProvider).toString()))
			.put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue());
		return (T)new Dynamic(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
