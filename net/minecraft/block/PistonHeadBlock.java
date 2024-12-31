package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonHeadBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing");
	public static final EnumProperty<PistonHeadBlock.PistonHeadType> TYPE = EnumProperty.of("type", PistonHeadBlock.PistonHeadType.class);
	public static final BooleanProperty SHORT = BooleanProperty.of("short");

	public PistonHeadBlock() {
		super(Material.PISTON);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonHeadBlock.PistonHeadType.DEFAULT).with(SHORT, false));
		this.setSound(STONE);
		this.setStrength(0.5F);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.abilities.creativeMode) {
			Direction direction = state.get(FACING);
			if (direction != null) {
				BlockPos blockPos = pos.offset(direction.getOpposite());
				Block block = world.getBlockState(blockPos).getBlock();
				if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
					world.setAir(blockPos);
				}
			}
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		pos = pos.offset(direction);
		BlockState blockState = world.getBlockState(pos);
		if ((blockState.getBlock() == Blocks.PISTON || blockState.getBlock() == Blocks.STICKY_PISTON) && (Boolean)blockState.get(PistonBlock.EXTENDED)) {
			blockState.getBlock().dropAsItem(world, pos, blockState, 0);
			world.setAir(pos);
		}
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
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
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setHeadBoundingBox(state);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setMainBoundingBox(state);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	private void setMainBoundingBox(BlockState state) {
		float f = 0.25F;
		float g = 0.375F;
		float h = 0.625F;
		float i = 0.25F;
		float j = 0.75F;
		switch ((Direction)state.get(FACING)) {
			case DOWN:
				this.setBoundingBox(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
				break;
			case UP:
				this.setBoundingBox(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
				break;
			case NORTH:
				this.setBoundingBox(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
				break;
			case SOUTH:
				this.setBoundingBox(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
				break;
			case WEST:
				this.setBoundingBox(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
				break;
			case EAST:
				this.setBoundingBox(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
		}
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setHeadBoundingBox(view.getBlockState(pos));
	}

	public void setHeadBoundingBox(BlockState state) {
		float f = 0.25F;
		Direction direction = state.get(FACING);
		if (direction != null) {
			switch (direction) {
				case DOWN:
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
					break;
				case UP:
					this.setBoundingBox(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
					break;
				case NORTH:
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
					break;
				case SOUTH:
					this.setBoundingBox(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
					break;
				case WEST:
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
					break;
				case EAST:
					this.setBoundingBox(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.PISTON && blockState.getBlock() != Blocks.STICKY_PISTON) {
			world.setAir(pos);
		} else {
			blockState.getBlock().neighborUpdate(world, blockPos, blockState, block);
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return true;
	}

	public static Direction getDirection(int data) {
		int i = data & 7;
		return i > 5 ? null : Direction.getById(i);
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return world.getBlockState(pos).get(TYPE) == PistonHeadBlock.PistonHeadType.STICKY ? Item.fromBlock(Blocks.STICKY_PISTON) : Item.fromBlock(Blocks.PISTON);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(FACING, getDirection(data))
			.with(TYPE, (data & 8) > 0 ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT);
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
		return new StateManager(this, FACING, TYPE, SHORT);
	}

	public static enum PistonHeadType implements StringIdentifiable {
		DEFAULT("normal"),
		STICKY("sticky");

		private final String name;

		private PistonHeadType(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
