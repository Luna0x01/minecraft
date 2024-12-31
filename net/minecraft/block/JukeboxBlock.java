package net.minecraft.block;

import net.minecraft.class_3742;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class JukeboxBlock extends BlockWithEntity {
	public static final BooleanProperty field_18379 = Properties.HAS_RECORD;

	protected JukeboxBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18379, Boolean.valueOf(false)));
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if ((Boolean)state.getProperty(field_18379)) {
			this.method_8802(world, pos);
			state = state.withProperty(field_18379, Boolean.valueOf(false));
			world.setBlockState(pos, state, 2);
			return true;
		} else {
			return false;
		}
	}

	public void method_8801(IWorld iWorld, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
		if (blockEntity instanceof class_3742) {
			((class_3742)blockEntity).method_16828(itemStack.copy());
			iWorld.setBlockState(blockPos, blockState.withProperty(field_18379, Boolean.valueOf(true)), 2);
		}
	}

	private void method_8802(World world, BlockPos blockPos) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof class_3742) {
				class_3742 lv = (class_3742)blockEntity;
				ItemStack itemStack = lv.method_16829();
				if (!itemStack.isEmpty()) {
					world.syncGlobalEvent(1010, blockPos, 0);
					world.method_8509(blockPos, null);
					lv.method_16828(ItemStack.EMPTY);
					float f = 0.7F;
					double d = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					double e = (double)(world.random.nextFloat() * 0.7F) + 0.060000002F + 0.6;
					double g = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					ItemStack itemStack2 = itemStack.copy();
					ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack2);
					itemEntity.setToDefaultPickupDelay();
					world.method_3686(itemEntity);
				}
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			this.method_8802(world, pos);
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!world.isClient) {
			super.method_410(blockState, world, blockPos, f, 0);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new class_3742();
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof class_3742) {
			Item item = ((class_3742)blockEntity).method_16829().getItem();
			if (item instanceof MusicDiscItem) {
				return ((MusicDiscItem)item).method_16119();
			}
		}

		return 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18379);
	}
}
