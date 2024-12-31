package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TorchBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate<Direction>() {
		public boolean apply(Direction direction) {
			return direction != Direction.DOWN;
		}
	});

	protected TorchBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	private boolean canBePlacedAt(World world, BlockPos pos) {
		if (World.isOpaque(world, pos)) {
			return true;
		} else {
			Block block = world.getBlockState(pos).getBlock();
			return block instanceof FenceBlock || block == Blocks.GLASS || block == Blocks.COBBLESTONE_WALL || block == Blocks.STAINED_GLASS;
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		for (Direction direction : FACING.getValues()) {
			if (this.canBePlacedAt(world, pos, direction)) {
				return true;
			}
		}

		return false;
	}

	private boolean canBePlacedAt(World world, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir.getOpposite());
		boolean bl = dir.getAxis().isHorizontal();
		return bl && world.renderAsNormalBlock(blockPos, true) || dir.equals(Direction.UP) && this.canBePlacedAt(world, blockPos);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (this.canBePlacedAt(world, pos, dir)) {
			return this.getDefaultState().with(FACING, dir);
		} else {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (world.renderAsNormalBlock(pos.offset(direction.getOpposite()), true)) {
					return this.getDefaultState().with(FACING, direction);
				}
			}

			return this.getDefaultState();
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.canBePlacedAt(world, pos, state);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		this.neighborUpdate(world, pos, state);
	}

	protected boolean neighborUpdate(World world, BlockPos pos, BlockState state) {
		if (!this.canBePlacedAt(world, pos, state)) {
			return true;
		} else {
			Direction direction = state.get(FACING);
			Direction.Axis axis = direction.getAxis();
			Direction direction2 = direction.getOpposite();
			boolean bl = false;
			if (axis.isHorizontal() && !world.renderAsNormalBlock(pos.offset(direction2), true)) {
				bl = true;
			} else if (axis.isVertical() && !this.canBePlacedAt(world, pos.offset(direction2))) {
				bl = true;
			}

			if (bl) {
				this.dropAsItem(world, pos, state, 0);
				world.setAir(pos);
				return true;
			} else {
				return false;
			}
		}
	}

	protected boolean canBePlacedAt(World world, BlockPos pos, BlockState state) {
		if (state.getBlock() == this && this.canBePlacedAt(world, pos, state.get(FACING))) {
			return true;
		} else {
			if (world.getBlockState(pos).getBlock() == this) {
				this.dropAsItem(world, pos, state, 0);
				world.setAir(pos);
			}

			return false;
		}
	}

	@Override
	public BlockHitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		Direction direction = world.getBlockState(pos).get(FACING);
		float f = 0.15F;
		if (direction == Direction.EAST) {
			this.setBoundingBox(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
		} else if (direction == Direction.WEST) {
			this.setBoundingBox(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
		} else if (direction == Direction.SOUTH) {
			this.setBoundingBox(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
		} else if (direction == Direction.NORTH) {
			this.setBoundingBox(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
		} else {
			f = 0.1F;
			this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
		}

		return super.rayTrace(world, pos, start, end);
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		Direction direction = state.get(FACING);
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getY() + 0.7;
		double f = (double)pos.getZ() + 0.5;
		double g = 0.22;
		double h = 0.27;
		if (direction.getAxis().isHorizontal()) {
			Direction direction2 = direction.getOpposite();
			world.addParticle(ParticleType.SMOKE, d + h * (double)direction2.getOffsetX(), e + g, f + h * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
			world.addParticle(ParticleType.FIRE, d + h * (double)direction2.getOffsetX(), e + g, f + h * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
		} else {
			world.addParticle(ParticleType.SMOKE, d, e, f, 0.0, 0.0, 0.0);
			world.addParticle(ParticleType.FIRE, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState();
		switch (data) {
			case 1:
				blockState = blockState.with(FACING, Direction.EAST);
				break;
			case 2:
				blockState = blockState.with(FACING, Direction.WEST);
				break;
			case 3:
				blockState = blockState.with(FACING, Direction.SOUTH);
				break;
			case 4:
				blockState = blockState.with(FACING, Direction.NORTH);
				break;
			case 5:
			default:
				blockState = blockState.with(FACING, Direction.UP);
		}

		return blockState;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		switch ((Direction)state.get(FACING)) {
			case EAST:
				i |= 1;
				break;
			case WEST:
				i |= 2;
				break;
			case SOUTH:
				i |= 3;
				break;
			case NORTH:
				i |= 4;
				break;
			case DOWN:
			case UP:
			default:
				i |= 5;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
