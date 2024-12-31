package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3016 implements class_3999 {
	private final float field_14873;
	private final Random field_14874;

	public class_3016(BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.field_14873 = structurePlacementData.method_13389();
		this.field_14874 = structurePlacementData.method_13386(blockPos);
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo method_13390(BlockView blockView, BlockPos blockPos, Structure.StructureBlockInfo structureBlockInfo) {
		return !(this.field_14873 >= 1.0F) && !(this.field_14874.nextFloat() <= this.field_14873) ? null : structureBlockInfo;
	}
}
