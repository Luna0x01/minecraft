package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndRodBlock extends FacingBlock {
	protected static final Box field_12658 = new Box(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
	protected static final Box field_12659 = new Box(0.375, 0.375, 0.0, 0.625, 0.625, 1.0);
	protected static final Box field_12660 = new Box(0.0, 0.375, 0.375, 1.0, 0.625, 0.625);

	protected EndRodBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.with(FACING, mirror.apply(state.get(FACING)));
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch (((Direction)state.get(FACING)).getAxis()) {
			case X:
			default:
				return field_12660;
			case Z:
				return field_12659;
			case Y:
				return field_12658;
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = world.getBlockState(pos.offset(dir.getOpposite()));
		if (blockState.getBlock() == Blocks.END_ROD) {
			Direction direction = blockState.get(FACING);
			if (direction == dir) {
				return this.getDefaultState().with(FACING, dir.getOpposite());
			}
		}

		return this.getDefaultState().with(FACING, dir);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction direction = state.get(FACING);
		double d = (double)pos.getX() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double e = (double)pos.getY() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double f = (double)pos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double g = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
		if (random.nextInt(5) == 0) {
			world.addParticle(
				ParticleType.END_ROD,
				d + (double)direction.getOffsetX() * g,
				e + (double)direction.getOffsetY() * g,
				f + (double)direction.getOffsetZ() * g,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005
			);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState();
		return blockState.with(FACING, Direction.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}
}
