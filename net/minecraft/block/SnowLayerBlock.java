package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SnowLayerBlock extends Block {
	public static final IntProperty LAYERS = IntProperty.of("layers", 1, 8);
	protected static final Box[] LAYERS_TO_SHAPE = new Box[]{
		new Box(0.0, 0.0, 0.0, 1.0, 0.0, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.875, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
	};

	protected SnowLayerBlock() {
		super(Material.SNOW_LAYER);
		this.setDefaultState(this.stateManager.getDefaultState().with(LAYERS, 1));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return LAYERS_TO_SHAPE[state.get(LAYERS)];
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return (Integer)view.getBlockState(pos).get(LAYERS) < 5;
	}

	@Override
	public boolean method_11568(BlockState state) {
		return (Integer)state.get(LAYERS) == 7;
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(LAYERS) - 1;
		float f = 0.125F;
		Box box = state.getCollisionBox((BlockView)world, pos);
		return new Box(box.minX, box.minY, box.minZ, box.maxX, (double)((float)i * 0.125F), box.maxZ);
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
		BlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		if (block != Blocks.ICE && block != Blocks.PACKED_ICE) {
			if (blockState.getMaterial() == Material.FOLIAGE) {
				return true;
			} else {
				return block == this && blockState.get(LAYERS) >= 7 ? true : blockState.isFullBoundsCubeForCulling() && blockState.getMaterial().blocksMovement();
			}
		} else {
			return false;
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		this.isPlacementValid(world, blockPos, blockState);
	}

	private boolean isPlacementValid(World world, BlockPos pos, BlockState state) {
		if (!this.canBePlacedAtPos(world, pos)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable ItemStack stack) {
		onBlockBreak(world, pos, new ItemStack(Items.SNOWBALL, (Integer)state.get(LAYERS) + 1, 0));
		world.setAir(pos);
		player.incrementStat(Stats.mined(this));
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SNOWBALL;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.getLightAtPos(LightType.BLOCK, pos) > 11) {
			this.dropAsItem(world, pos, world.getBlockState(pos), 0);
			world.setAir(pos);
		}
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		if (direction == Direction.UP) {
			return true;
		} else {
			BlockState blockState = view.getBlockState(pos.offset(direction));
			return blockState.getBlock() == this && blockState.get(LAYERS) >= state.get(LAYERS) ? true : super.method_8654(state, view, pos, direction);
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(LAYERS, (data & 7) + 1);
	}

	@Override
	public boolean method_8638(BlockView blockView, BlockPos blockPos) {
		return (Integer)blockView.getBlockState(blockPos).get(LAYERS) == 1;
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(LAYERS) - 1;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, LAYERS);
	}
}
