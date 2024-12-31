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
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CarpetBlock extends Block {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

	protected CarpetBlock() {
		super(Material.CARPET);
		this.setDefaultState(this.stateManager.getDefaultState().with(COLOR, DyeColor.WHITE));
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setBoundingBoxWithData(0);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getMaterialColor();
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
	public void setBlockItemBounds() {
		this.setBoundingBoxWithData(0);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setBoundingBoxWithData(0);
	}

	protected void setBoundingBoxWithData(int data) {
		int i = 0;
		float f = (float)(1 * (1 + i)) / 16.0F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && this.isOnSolidBlock(world, pos);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		this.continueToExist(world, pos, state);
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
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return facing == Direction.UP ? true : super.isSideInvisible(view, pos, facing);
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
