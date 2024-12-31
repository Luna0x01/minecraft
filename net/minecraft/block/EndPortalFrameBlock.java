package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final BooleanProperty EYE = BooleanProperty.of("eye");

	public EndPortalFrameBlock() {
		super(Material.STONE, MaterialColor.GREEN);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EYE, false));
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public void setBlockItemBounds() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		if ((Boolean)world.getBlockState(pos).get(EYE)) {
			this.setBoundingBox(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}

		this.setBlockItemBounds();
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection().getOpposite()).with(EYE, false);
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		return world.getBlockState(pos).get(EYE) ? 15 : 0;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(EYE, (data & 4) != 0).with(FACING, Direction.fromHorizontal(data & 3));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		if ((Boolean)state.get(EYE)) {
			i |= 4;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, EYE);
	}
}
