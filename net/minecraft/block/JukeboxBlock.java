package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemSchema;
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
import net.minecraft.world.level.storage.LevelDataType;

public class JukeboxBlock extends BlockWithEntity {
	public static final BooleanProperty HAS_RECORD = BooleanProperty.of("has_record");

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemSchema(JukeboxBlock.JukeboxBlockEntity.class, "RecordItem"));
	}

	protected JukeboxBlock() {
		super(Material.WOOD, MaterialColor.DIRT);
		this.setDefaultState(this.stateManager.getDefaultState().with(HAS_RECORD, false));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		if ((Boolean)state.get(HAS_RECORD)) {
			this.removeRecord(world, pos, state);
			state = state.with(HAS_RECORD, false);
			world.setBlockState(pos, state, 2);
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
				if (!itemStack.isEmpty()) {
					world.syncGlobalEvent(1010, pos, 0);
					world.method_8509(pos, null);
					jukeboxBlockEntity.setRecord(ItemStack.EMPTY);
					float f = 0.7F;
					double d = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					double e = (double)(world.random.nextFloat() * 0.7F) + 0.060000002F + 0.6;
					double g = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
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
			if (!itemStack.isEmpty()) {
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
		private ItemStack field_15141 = ItemStack.EMPTY;

		@Override
		public void fromNbt(NbtCompound nbt) {
			super.fromNbt(nbt);
			if (nbt.contains("RecordItem", 10)) {
				this.setRecord(new ItemStack(nbt.getCompound("RecordItem")));
			} else if (nbt.getInt("Record") > 0) {
				this.setRecord(new ItemStack(Item.byRawId(nbt.getInt("Record"))));
			}
		}

		@Override
		public NbtCompound toNbt(NbtCompound nbt) {
			super.toNbt(nbt);
			if (!this.getRecord().isEmpty()) {
				nbt.put("RecordItem", this.getRecord().toNbt(new NbtCompound()));
			}

			return nbt;
		}

		public ItemStack getRecord() {
			return this.field_15141;
		}

		public void setRecord(ItemStack record) {
			this.field_15141 = record;
			this.markDirty();
		}
	}
}
