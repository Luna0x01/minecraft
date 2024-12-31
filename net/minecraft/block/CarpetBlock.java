package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CarpetBlock extends Block {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);
	protected static final Box field_12837 = new Box(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

	protected CarpetBlock() {
		super(Material.CARPET);
		this.setDefaultState(this.stateManager.getDefaultState().with(COLOR, DyeColor.WHITE));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12837;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getMaterialColor();
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
		return super.canBePlacedAtPos(world, pos) && this.isOnSolidBlock(world, pos);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		this.continueToExist(world, blockPos, blockState);
	}

	private boolean continueToExist(World world, BlockPos pos, BlockState state) {
		if (!this.isOnSolidBlock(world, pos)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
			return false;
		} else {
			return true;
		}
	}

	private boolean isOnSolidBlock(World world, BlockPos pos) {
		return !world.isAir(pos.down());
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		if (direction == Direction.UP) {
			return true;
		} else {
			return view.getBlockState(pos.offset(direction)).getBlock() == this ? true : super.method_8654(state, view, pos, direction);
		}
	}

	@Override
	public int getMeta(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (int i = 0; i < 16; i++) {
			stacks.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(COLOR, DyeColor.byId(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, COLOR);
	}
}
