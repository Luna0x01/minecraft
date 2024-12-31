package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = PistonHeadBlock.FACING;
	public static final EnumProperty<PistonHeadBlock.PistonHeadType> TYPE = PistonHeadBlock.TYPE;

	public PistonExtensionBlock() {
		super(Material.PISTON);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonHeadBlock.PistonHeadType.DEFAULT));
		this.setStrength(-1.0F);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return null;
	}

	public static BlockEntity createPistonEntity(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		return new PistonBlockEntity(pushedBlock, dir, extending, source);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			((PistonBlockEntity)blockEntity).finish();
		} else {
			super.onBreaking(world, pos, state);
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return false;
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof PistonBlock && (Boolean)blockState.get(PistonBlock.EXTENDED)) {
			world.setAir(blockPos);
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
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		if (!world.isClient && world.getBlockEntity(pos) == null) {
			world.setAir(pos);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.AIR;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient) {
			PistonBlockEntity pistonBlockEntity = this.getPistonEntity(world, pos);
			if (pistonBlockEntity != null) {
				BlockState blockState = pistonBlockEntity.getPushedBlock();
				blockState.getBlock().dropAsItem(world, pos, blockState, 0);
			}
		}
	}

	@Nullable
	@Override
	public BlockHitResult method_414(BlockState blockState, World world, BlockPos blockPos, Vec3d vec3d, Vec3d vec3d2) {
		return null;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			world.getBlockEntity(pos);
		}
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		PistonBlockEntity pistonBlockEntity = this.getPistonEntity(view, pos);
		return pistonBlockEntity == null ? null : pistonBlockEntity.method_11701(view, pos);
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
		PistonBlockEntity pistonBlockEntity = this.getPistonEntity(world, pos);
		if (pistonBlockEntity != null) {
			pistonBlockEntity.method_13749(world, pos, entityBox, boxes, entity);
		}
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		PistonBlockEntity pistonBlockEntity = this.getPistonEntity(view, pos);
		return pistonBlockEntity != null ? pistonBlockEntity.method_11701(view, pos) : collisionBox;
	}

	@Nullable
	private PistonBlockEntity getPistonEntity(BlockView view, BlockPos pos) {
		BlockEntity blockEntity = view.getBlockEntity(pos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(FACING, PistonHeadBlock.getDirection(data))
			.with(TYPE, (data & 8) > 0 ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if (state.get(TYPE) == PistonHeadBlock.PistonHeadType.STICKY) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, TYPE);
	}
}
