package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ComparatorBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final EnumProperty<ComparatorBlock.ComparatorType> MODE = EnumProperty.of("mode", ComparatorBlock.ComparatorType.class);

	public ComparatorBlock(boolean bl) {
		super(bl);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(DIRECTION, Direction.NORTH).with(POWERED, false).with(MODE, ComparatorBlock.ComparatorType.COMPARE)
		);
		this.blockEntity = true;
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.comparator.name");
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.COMPARATOR;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.COMPARATOR);
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 2;
	}

	@Override
	protected BlockState getPoweredState(BlockState state) {
		Boolean boolean_ = state.get(POWERED);
		ComparatorBlock.ComparatorType comparatorType = state.get(MODE);
		Direction direction = state.get(DIRECTION);
		return Blocks.POWERED_COMPARATOR.getDefaultState().with(DIRECTION, direction).with(POWERED, boolean_).with(MODE, comparatorType);
	}

	@Override
	protected BlockState getUnpoweredState(BlockState state) {
		Boolean boolean_ = state.get(POWERED);
		ComparatorBlock.ComparatorType comparatorType = state.get(MODE);
		Direction direction = state.get(DIRECTION);
		return Blocks.UNPOWERED_COMPARATOR.getDefaultState().with(DIRECTION, direction).with(POWERED, boolean_).with(MODE, comparatorType);
	}

	@Override
	protected boolean isPowered(BlockState state) {
		return this.powered || (Boolean)state.get(POWERED);
	}

	@Override
	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
	}

	private int calculateOutputSignal(World world, BlockPos pos, BlockState state) {
		return state.get(MODE) == ComparatorBlock.ComparatorType.SUBTRACT
			? Math.max(this.getPower(world, pos, state) - this.getMaxInputLevelSides(world, pos, state), 0)
			: this.getPower(world, pos, state);
	}

	@Override
	protected boolean hasPower(World world, BlockPos pos, BlockState state) {
		int i = this.getPower(world, pos, state);
		if (i >= 15) {
			return true;
		} else if (i == 0) {
			return false;
		} else {
			int j = this.getMaxInputLevelSides(world, pos, state);
			return j == 0 ? true : i >= j;
		}
	}

	@Override
	protected int getPower(World world, BlockPos pos, BlockState state) {
		int i = super.getPower(world, pos, state);
		Direction direction = state.get(DIRECTION);
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (blockState.method_11736()) {
			i = blockState.getComparatorOutput(world, blockPos);
		} else if (i < 15 && blockState.method_11734()) {
			blockPos = blockPos.offset(direction);
			blockState = world.getBlockState(blockPos);
			if (blockState.method_11736()) {
				i = blockState.getComparatorOutput(world, blockPos);
			} else if (blockState.getMaterial() == Material.AIR) {
				ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos);
				if (itemFrameEntity != null) {
					i = itemFrameEntity.getComparatorPower();
				}
			}
		}

		return i;
	}

	@Nullable
	private ItemFrameEntity getAttachedItemFrame(World world, Direction facing, BlockPos pos) {
		List<ItemFrameEntity> list = world.getEntitiesInBox(
			ItemFrameEntity.class,
			new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)),
			new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					return entity != null && entity.getHorizontalDirection() == facing;
				}
			}
		);
		return list.size() == 1 ? (ItemFrameEntity)list.get(0) : null;
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
		if (!playerEntity.abilities.allowModifyWorld) {
			return false;
		} else {
			blockState = blockState.withDefaultValue(MODE);
			float i = blockState.get(MODE) == ComparatorBlock.ComparatorType.SUBTRACT ? 0.55F : 0.5F;
			world.method_11486(playerEntity, blockPos, Sounds.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, i);
			world.setBlockState(blockPos, blockState, 2);
			this.update(world, blockPos, blockState);
			return true;
		}
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!world.hasScheduledTick(pos, this)) {
			int i = this.calculateOutputSignal(world, pos, state);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			int j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
			if (i != j || this.isPowered(state) != this.hasPower(world, pos, state)) {
				if (this.isTargetNotAligned(world, pos, state)) {
					world.createAndScheduleBlockTick(pos, this, 2, -1);
				} else {
					world.createAndScheduleBlockTick(pos, this, 2, 0);
				}
			}
		}
	}

	private void update(World world, BlockPos pos, BlockState state) {
		int i = this.calculateOutputSignal(world, pos, state);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		int j = 0;
		if (blockEntity instanceof ComparatorBlockEntity) {
			ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
			j = comparatorBlockEntity.getOutputSignal();
			comparatorBlockEntity.setOutputSignal(i);
		}

		if (j != i || state.get(MODE) == ComparatorBlock.ComparatorType.COMPARE) {
			boolean bl = this.hasPower(world, pos, state);
			boolean bl2 = this.isPowered(state);
			if (bl2 && !bl) {
				world.setBlockState(pos, state.with(POWERED, false), 2);
			} else if (!bl2 && bl) {
				world.setBlockState(pos, state.with(POWERED, true), 2);
			}

			this.updateTarget(world, pos, state);
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this.powered) {
			world.setBlockState(pos, this.getUnpoweredState(state).with(POWERED, true), 4);
		}

		this.update(world, pos, state);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
		world.setBlockEntity(pos, this.createBlockEntity(world, 0));
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		world.removeBlockEntity(pos);
		this.updateTarget(world, pos, state);
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		super.onSyncedBlockEvent(state, world, pos, type, data);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.onBlockAction(type, data);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new ComparatorBlockEntity();
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(DIRECTION, Direction.fromHorizontal(data))
			.with(POWERED, (data & 8) > 0)
			.with(MODE, (data & 4) > 0 ? ComparatorBlock.ComparatorType.SUBTRACT : ComparatorBlock.ComparatorType.COMPARE);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(DIRECTION)).getHorizontal();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		if (state.get(MODE) == ComparatorBlock.ComparatorType.SUBTRACT) {
			i |= 4;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(DIRECTION)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION, MODE, POWERED);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState()
			.with(DIRECTION, entity.getHorizontalDirection().getOpposite())
			.with(POWERED, false)
			.with(MODE, ComparatorBlock.ComparatorType.COMPARE);
	}

	public static enum ComparatorType implements StringIdentifiable {
		COMPARE("compare"),
		SUBTRACT("subtract");

		private final String name;

		private ComparatorType(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
