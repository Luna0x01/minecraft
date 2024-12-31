package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3821<F extends class_3845, D extends class_3830> extends class_3844<class_3871> {
	protected final class_3844<F> field_19074;
	protected final F field_19075;
	protected final class_3945<D> field_19076;
	protected final D field_19077;

	public class_3821(class_3844<F> arg, F arg2, class_3945<D> arg3, D arg4) {
		this.field_19075 = arg2;
		this.field_19077 = arg4;
		this.field_19076 = arg3;
		this.field_19074 = arg;
	}

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		return this.field_19076.method_17536(iWorld, chunkGenerator, random, blockPos, this.field_19077, this.field_19074, this.field_19075);
	}

	public String toString() {
		return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), this.field_19076, this.field_19074);
	}

	public class_3844<F> method_17312() {
		return this.field_19074;
	}
}
