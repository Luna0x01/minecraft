package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class PlainsFlowerStateProvider extends StateProvider {
	private static final BlockState[] tulips = new BlockState[]{
		Blocks.field_10048.getDefaultState(), Blocks.field_10270.getDefaultState(), Blocks.field_10315.getDefaultState(), Blocks.field_10156.getDefaultState()
	};
	private static final BlockState[] flowers = new BlockState[]{
		Blocks.field_10449.getDefaultState(), Blocks.field_10573.getDefaultState(), Blocks.field_10554.getDefaultState(), Blocks.field_9995.getDefaultState()
	};

	public PlainsFlowerStateProvider() {
		super(StateProviderType.field_21307);
	}

	public <T> PlainsFlowerStateProvider(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
		if (d < -0.8) {
			return tulips[random.nextInt(tulips.length)];
		} else {
			return random.nextInt(3) > 0 ? flowers[random.nextInt(flowers.length)] : Blocks.field_10182.getDefaultState();
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.field_21445.getId(this.stateProvider).toString()));
		return (T)new Dynamic(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
