package net.minecraft.block;

import net.minecraft.class_3685;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock extends class_3685 {
	public static final IntProperty field_18477 = Properties.ROTATION;
	protected static final VoxelShape field_18478 = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

	protected SkullBlock(SkullBlock.class_3722 arg, Block.Builder builder) {
		super(arg, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18477, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18478;
	}

	@Override
	public VoxelShape method_16593(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(field_18477, Integer.valueOf(MathHelper.floor((double)(context.method_16147() * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(field_18477, Integer.valueOf(rotation.rotate((Integer)state.getProperty(field_18477), 16)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withProperty(field_18477, Integer.valueOf(mirror.mirror((Integer)state.getProperty(field_18477), 16)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18477);
	}

	public interface class_3722 {
	}

	public static enum class_3723 implements SkullBlock.class_3722 {
		SKELETON,
		WITHER_SKELETON,
		PLAYER,
		ZOMBIE,
		CREEPER,
		DRAGON;
	}
}
