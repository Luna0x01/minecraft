package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class JukeboxBlock extends BlockWithEntity {
	public static final BooleanProperty HAS_RECORD = BooleanProperty.of("has_record");

	protected JukeboxBlock() {
		super(Material.WOOD, MaterialColor.DIRT);
		this.setDefaultState(this.stateManager.getDefaultState().with(HAS_RECORD, false));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if ((Boolean)blockState.get(HAS_RECORD)) {
			this.removeRecord(world, blockPos, blockState);
			blockState = blockState.with(HAS_RECORD, false);
			world.setBlockState(blockPos, blockState, 2);
			return true;
		} else {
			return false;
		}
	}

	public void setRecord(World world, BlockPos pos, BlockState state, ItemStack stack) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof JukeboxBlock.JukeboxBlockEntity) {
				((JukeboxBlock.JukeboxBlockEntity)blockEntity).setRecord(stack.copy());
				world.setBlockState(pos, state.with(HAS_RECORD, true), 2);
			}
		}
	}

	private void removeRecord(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof JukeboxBlock.JukeboxBlockEntity) {
				JukeboxBlock.JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlock.JukeboxBlockEntity)blockEntity;
				ItemStack itemStack = jukeboxBlockEntity.getRecord();
				if (itemStack != null) {
					world.syncGlobalEvent(1010, pos, 0);
					world.method_8509(pos, null);
					jukeboxBlockEntity.setRecord(null);
					float f = 0.7F;
					double d = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
					double e = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.2 + 0.6;
					double g = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
					ItemStack itemStack2 = itemStack.copy();
					ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, itemStack2);
					itemEntity.setToDefaultPickupDelay();
					world.spawnEntity(itemEntity);
				}
			}
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		this.removeRecord(world, pos, state);
		super.onBreaking(world, pos, state);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient) {
			super.randomDropAsItem(world, pos, state, chance, 0);
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new JukeboxBlock.JukeboxBlockEntity();
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof JukeboxBlock.JukeboxBlockEntity) {
			ItemStack itemStack = ((JukeboxBlock.JukeboxBlockEntity)blockEntity).getRecord();
			if (itemStack != null) {
				return Item.getRawId(itemStack.getItem()) + 1 - Item.getRawId(Items.RECORD_13);
			}
		}

		return 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(HAS_RECORD, data > 0);
	}

	@Override
	public int getData(BlockState state) {
		return state.get(HAS_RECORD) ? 1 : 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, HAS_RECORD);
	}

	public static class JukeboxBlockEntity extends BlockEntity {
		private ItemStack record;

		@Override
		public void fromNbt(NbtCompound nbt) {
			super.fromNbt(nbt);
			if (nbt.contains("RecordItem", 10)) {
				this.setRecord(ItemStack.fromNbt(nbt.getCompound("RecordItem")));
			} else if (nbt.getInt("Record") > 0) {
				this.setRecord(new ItemStack(Item.byRawId(nbt.getInt("Record"))));
			}
		}

		@Override
		public NbtCompound toNbt(NbtCompound nbt) {
			super.toNbt(nbt);
			if (this.getRecord() != null) {
				nbt.put("RecordItem", this.getRecord().toNbt(new NbtCompound()));
			}

			return nbt;
		}

		@Nullable
		public ItemStack getRecord() {
			return this.record;
		}

		public void setRecord(@Nullable ItemStack record) {
			this.record = record;
			this.markDirty();
		}
	}
}
