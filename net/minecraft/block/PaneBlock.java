package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PaneBlock extends Block {
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	private final boolean canDrop;

	protected PaneBlock(Material material, boolean bl) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		this.canDrop = bl;
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(NORTH, this.canConnectToGlass(view.getBlockState(pos.north()).getBlock()))
			.with(SOUTH, this.canConnectToGlass(view.getBlockState(pos.south()).getBlock()))
			.with(WEST, this.canConnectToGlass(view.getBlockState(pos.west()).getBlock()))
			.with(EAST, this.canConnectToGlass(view.getBlockState(pos.east()).getBlock()));
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return !this.canDrop ? null : super.getDropItem(state, random, id);
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
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return view.getBlockState(pos).getBlock() == this ? false : super.isSideInvisible(view, pos, facing);
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		boolean bl = this.canConnectToGlass(world.getBlockState(pos.north()).getBlock());
		boolean bl2 = this.canConnectToGlass(world.getBlockState(pos.south()).getBlock());
		boolean bl3 = this.canConnectToGlass(world.getBlockState(pos.west()).getBlock());
		boolean bl4 = this.canConnectToGlass(world.getBlockState(pos.east()).getBlock());
		if ((!bl3 || !bl4) && (bl3 || bl4 || bl || bl2)) {
			if (bl3) {
				this.setBoundingBox(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
				super.appendCollisionBoxes(world, pos, state, box, list, entity);
			} else if (bl4) {
				this.setBoundingBox(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
				super.appendCollisionBoxes(world, pos, state, box, list, entity);
			}
		} else {
			this.setBoundingBox(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}

		if ((!bl || !bl2) && (bl3 || bl4 || bl || bl2)) {
			if (bl) {
				this.setBoundingBox(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
				super.appendCollisionBoxes(world, pos, state, box, list, entity);
			} else if (bl2) {
				this.setBoundingBox(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
				super.appendCollisionBoxes(world, pos, state, box, list, entity);
			}
		} else {
			this.setBoundingBox(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}
	}

	@Override
	public void setBlockItemBounds() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		float f = 0.4375F;
		float g = 0.5625F;
		float h = 0.4375F;
		float i = 0.5625F;
		boolean bl = this.canConnectToGlass(view.getBlockState(pos.north()).getBlock());
		boolean bl2 = this.canConnectToGlass(view.getBlockState(pos.south()).getBlock());
		boolean bl3 = this.canConnectToGlass(view.getBlockState(pos.west()).getBlock());
		boolean bl4 = this.canConnectToGlass(view.getBlockState(pos.east()).getBlock());
		if ((!bl3 || !bl4) && (bl3 || bl4 || bl || bl2)) {
			if (bl3) {
				f = 0.0F;
			} else if (bl4) {
				g = 1.0F;
			}
		} else {
			f = 0.0F;
			g = 1.0F;
		}

		if ((!bl || !bl2) && (bl3 || bl4 || bl || bl2)) {
			if (bl) {
				h = 0.0F;
			} else if (bl2) {
				i = 1.0F;
			}
		} else {
			h = 0.0F;
			i = 1.0F;
		}

		this.setBoundingBox(f, 0.0F, h, g, 1.0F, i);
	}

	public final boolean canConnectToGlass(Block block) {
		return block.isFullBlock()
			|| block == this
			|| block == Blocks.GLASS
			|| block == Blocks.STAINED_GLASS
			|| block == Blocks.STAINED_GLASS_PANE
			|| block instanceof PaneBlock;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, NORTH, EAST, WEST, SOUTH);
	}
}
