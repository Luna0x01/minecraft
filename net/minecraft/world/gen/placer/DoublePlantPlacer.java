package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class DoublePlantPlacer extends BlockPlacer {
	public DoublePlantPlacer() {
		super(BlockPlacerType.field_21224);
	}

	public <T> DoublePlantPlacer(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		((TallPlantBlock)blockState.getBlock()).placeAt(iWorld, blockPos, 2);
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return (T)new Dynamic(
				dynamicOps,
				dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString(Registry.field_21446.getId(this.type).toString())))
			)
			.getValue();
	}
}
