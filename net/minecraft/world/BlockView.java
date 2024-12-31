package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelGeneratorType;

public interface BlockView {
	BlockEntity getBlockEntity(BlockPos pos);

	int getLight(BlockPos pos, int minBlockLight);

	BlockState getBlockState(BlockPos pos);

	boolean isAir(BlockPos pos);

	Biome getBiome(BlockPos pos);

	boolean isEmpty();

	int getStrongRedstonePower(BlockPos pos, Direction direction);

	LevelGeneratorType getGeneratorType();
}
