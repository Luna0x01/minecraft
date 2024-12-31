package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;

public class class_3885 implements class_3845 {
	public final Predicate<BlockState> field_19244;
	public final BlockState field_19245;

	public class_3885(Predicate<BlockState> predicate, BlockState blockState) {
		this.field_19244 = predicate;
		this.field_19245 = blockState;
	}
}
