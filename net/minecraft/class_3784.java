package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.SingletonBiomeSource;

interface class_3784<C extends class_3798, T extends ChunkGenerator<C>> {
	T create(World world, SingletonBiomeSource singletonBiomeSource, C arg);
}
