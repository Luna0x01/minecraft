package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class class_3945<T extends class_3830> {
	public abstract <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, T arg, class_3844<C> arg2, C arg3
	);

	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
	}
}
