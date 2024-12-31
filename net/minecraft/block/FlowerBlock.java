package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;

public class FlowerBlock extends PlantBlock {
	protected static final VoxelShape field_18343 = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);

	public FlowerBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		Vec3d vec3d = state.getOffsetPos(world, pos);
		return field_18343.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}
}
