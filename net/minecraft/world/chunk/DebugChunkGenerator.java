package net.minecraft.world.chunk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.class_3781;
import net.minecraft.class_3782;
import net.minecraft.class_3786;
import net.minecraft.class_3799;
import net.minecraft.class_3801;
import net.minecraft.class_3804;
import net.minecraft.class_4441;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;

public class DebugChunkGenerator extends class_3782<class_3799> {
	private static final List<BlockState> field_10119 = (List<BlockState>)StreamSupport.stream(Registry.BLOCK.spliterator(), false)
		.flatMap(block -> block.getStateManager().getBlockStates().stream())
		.collect(Collectors.toList());
	private static final int field_10120 = MathHelper.ceil(MathHelper.sqrt((float)field_10119.size()));
	private static final int field_10121 = MathHelper.ceil((float)field_10119.size() / (float)field_10120);
	protected static final BlockState field_12956 = Blocks.AIR.getDefaultState();
	protected static final BlockState field_12957 = Blocks.BARRIER.getDefaultState();
	private final class_3799 field_18988;

	public DebugChunkGenerator(IWorld iWorld, SingletonBiomeSource singletonBiomeSource, class_3799 arg) {
		super(iWorld, singletonBiomeSource);
		this.field_18988 = arg;
	}

	@Override
	public void method_17016(class_3781 arg) {
		ChunkPos chunkPos = arg.method_3920();
		int i = chunkPos.x;
		int j = chunkPos.z;
		Biome[] biomes = this.field_18840.method_11540(i * 16, j * 16, 16, 16);
		arg.method_16999(biomes);
		arg.method_17000(class_3804.class_3805.WORLD_SURFACE_WG, class_3804.class_3805.OCEAN_FLOOR_WG);
		arg.method_16990(class_3786.BASE);
	}

	@Override
	public void method_17019(class_4441 arg, class_3801.class_3802 arg2) {
	}

	public class_3799 method_17013() {
		return this.field_18988;
	}

	@Override
	public double[] method_17027(int i, int j) {
		return new double[0];
	}

	@Override
	public int method_17025() {
		return this.field_18838.method_8483() + 1;
	}

	@Override
	public void method_17018(class_4441 arg) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = arg.method_21286();
		int j = arg.method_21288();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				int m = (i << 4) + k;
				int n = (j << 4) + l;
				arg.setBlockState(mutable.setPosition(m, 60, n), field_12957, 2);
				BlockState blockState = method_9190(m, n);
				if (blockState != null) {
					arg.setBlockState(mutable.setPosition(m, 70, n), blockState, 2);
				}
			}
		}
	}

	@Override
	public void method_17023(class_4441 arg) {
	}

	public static BlockState method_9190(int i, int j) {
		BlockState blockState = field_12956;
		if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
			i /= 2;
			j /= 2;
			if (i <= field_10120 && j <= field_10121) {
				int k = MathHelper.abs(i * field_10120 + j);
				if (k < field_10119.size()) {
					blockState = (BlockState)field_10119.get(k);
				}
			}
		}

		return blockState;
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.field_18838.method_8577(pos);
		return biome.getSpawnEntries(category);
	}

	@Override
	public int method_17014(World world, boolean bl, boolean bl2) {
		return 0;
	}
}
