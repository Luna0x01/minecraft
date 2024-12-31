package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_3694;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class SoulSandBlock extends Block {
	protected static final VoxelShape field_18496 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

	public SoulSandBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18496;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.velocityX *= 0.4;
		entity.velocityZ *= 0.4;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		class_3694.method_16630(world, pos.up(), false);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 20;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
