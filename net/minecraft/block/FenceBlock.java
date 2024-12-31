package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.LeadItem;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FenceBlock extends Block {
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");

	public FenceBlock(Material material) {
		this(material, material.getColor());
	}

	public FenceBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		boolean bl = this.canConnect(world, pos.north());
		boolean bl2 = this.canConnect(world, pos.south());
		boolean bl3 = this.canConnect(world, pos.west());
		boolean bl4 = this.canConnect(world, pos.east());
		float f = 0.375F;
		float g = 0.625F;
		float h = 0.375F;
		float i = 0.625F;
		if (bl) {
			h = 0.0F;
		}

		if (bl2) {
			i = 1.0F;
		}

		if (bl || bl2) {
			this.setBoundingBox(f, 0.0F, h, g, 1.5F, i);
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}

		h = 0.375F;
		i = 0.625F;
		if (bl3) {
			f = 0.0F;
		}

		if (bl4) {
			g = 1.0F;
		}

		if (bl3 || bl4 || !bl && !bl2) {
			this.setBoundingBox(f, 0.0F, h, g, 1.5F, i);
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}

		if (bl) {
			h = 0.0F;
		}

		if (bl2) {
			i = 1.0F;
		}

		this.setBoundingBox(f, 0.0F, h, g, 1.0F, i);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		boolean bl = this.canConnect(view, pos.north());
		boolean bl2 = this.canConnect(view, pos.south());
		boolean bl3 = this.canConnect(view, pos.west());
		boolean bl4 = this.canConnect(view, pos.east());
		float f = 0.375F;
		float g = 0.625F;
		float h = 0.375F;
		float i = 0.625F;
		if (bl) {
			h = 0.0F;
		}

		if (bl2) {
			i = 1.0F;
		}

		if (bl3) {
			f = 0.0F;
		}

		if (bl4) {
			g = 1.0F;
		}

		this.setBoundingBox(f, 0.0F, h, g, 1.0F, i);
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
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return false;
	}

	public boolean canConnect(BlockView view, BlockPos pos) {
		Block block = view.getBlockState(pos).getBlock();
		if (block == Blocks.BARRIER) {
			return false;
		} else if ((!(block instanceof FenceBlock) || block.material != this.material) && !(block instanceof FenceGateBlock)) {
			return block.material.isOpaque() && block.renderAsNormalBlock() ? block.material != Material.PUMPKIN : false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return true;
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		return world.isClient ? true : LeadItem.useLead(player, world, pos);
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(NORTH, this.canConnect(view, pos.north()))
			.with(EAST, this.canConnect(view, pos.east()))
			.with(SOUTH, this.canConnect(view, pos.south()))
			.with(WEST, this.canConnect(view, pos.west()));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, NORTH, EAST, WEST, SOUTH);
	}
}
