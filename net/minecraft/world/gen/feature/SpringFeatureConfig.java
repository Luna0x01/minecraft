package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpringFeatureConfig implements FeatureConfig {
	public final FluidState state;
	public final boolean requiresBlockBelow;
	public final int rockCount;
	public final int holeCount;
	public final Set<Block> validBlocks;

	public SpringFeatureConfig(FluidState fluidState, boolean bl, int i, int j, Set<Block> set) {
		this.state = fluidState;
		this.requiresBlockBelow = bl;
		this.rockCount = i;
		this.holeCount = j;
		this.validBlocks = set;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("state"),
					FluidState.serialize(dynamicOps, this.state).getValue(),
					dynamicOps.createString("requires_block_below"),
					dynamicOps.createBoolean(this.requiresBlockBelow),
					dynamicOps.createString("rock_count"),
					dynamicOps.createInt(this.rockCount),
					dynamicOps.createString("hole_count"),
					dynamicOps.createInt(this.holeCount),
					dynamicOps.createString("valid_blocks"),
					dynamicOps.createList(this.validBlocks.stream().map(Registry.field_11146::getId).map(Identifier::toString).map(dynamicOps::createString))
				)
			)
		);
	}

	public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic) {
		return new SpringFeatureConfig(
			(FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.field_15906.getDefaultState()),
			dynamic.get("requires_block_below").asBoolean(true),
			dynamic.get("rock_count").asInt(4),
			dynamic.get("hole_count").asInt(1),
			ImmutableSet.copyOf(dynamic.get("valid_blocks").asList(dynamicx -> Registry.field_11146.get(new Identifier(dynamicx.asString("minecraft:air")))))
		);
	}
}
