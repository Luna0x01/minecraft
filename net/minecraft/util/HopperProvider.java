package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.World;

public interface HopperProvider extends Inventory {
	VoxelShape field_18637 = Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
	VoxelShape field_18638 = Block.createCuboidShape(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
	VoxelShape field_18639 = VoxelShapes.union(field_18637, field_18638);

	default VoxelShape method_16820() {
		return field_18639;
	}

	@Nullable
	World getEntityWorld();

	double getX();

	double getY();

	double getZ();
}
