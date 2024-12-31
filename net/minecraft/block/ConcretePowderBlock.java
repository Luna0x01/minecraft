package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

	public ConcretePowderBlock() {
		super(Material.SAND);
		this.setDefaultState(this.stateManager.getDefaultState().with(COLOR, DyeColor.WHITE));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
		if (currentStateInPos.getMaterial().isFluid()) {
			world.setBlockState(pos, Blocks.CONCRETE.getDefaultState().with(WoolBlock.COLOR, fallingBlockState.get(COLOR)), 3);
		}
	}

	protected boolean method_14320(World world, BlockPos blockPos, BlockState blockState) {
		boolean bl = false;

		for (Direction direction : Direction.values()) {
			if (direction != Direction.DOWN) {
				BlockPos blockPos2 = blockPos.offset(direction);
				if (world.getBlockState(blockPos2).getMaterial() == Material.WATER) {
					bl = true;
					break;
				}
			}
		}

		if (bl) {
			world.setBlockState(blockPos, Blocks.CONCRETE.getDefaultState().with(WoolBlock.COLOR, blockState.get(COLOR)), 3);
		}

		return bl;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!this.method_14320(world, pos, state)) {
			super.neighborUpdate(state, world, pos, block, neighborPos);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!this.method_14320(world, pos, state)) {
			super.onCreation(world, pos, state);
		}
	}

	@Override
	public int getMeta(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> stacks) {
		for (DyeColor dyeColor : DyeColor.values()) {
			stacks.add(new ItemStack(this, 1, dyeColor.getId()));
		}
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return MaterialColor.fromDye(state.get(COLOR));
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
