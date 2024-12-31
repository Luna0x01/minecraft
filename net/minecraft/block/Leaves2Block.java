package net.minecraft.block;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Leaves2Block extends LeavesBlock {
	public static final EnumProperty<PlanksBlock.WoodType> VARIANT = EnumProperty.of(
		"variant", PlanksBlock.WoodType.class, new Predicate<PlanksBlock.WoodType>() {
			public boolean apply(@Nullable PlanksBlock.WoodType woodType) {
				return woodType.getId() >= 4;
			}
		}
	);

	public Leaves2Block() {
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, PlanksBlock.WoodType.ACACIA).with(CHECK_DECAY, true).with(DECAYABLE, true));
	}

	@Override
	protected void dropApple(World world, BlockPos pos, BlockState state, int dropChance) {
		if (state.get(VARIANT) == PlanksBlock.WoodType.DARK_OAK && world.random.nextInt(dropChance) == 0) {
			onBlockBreak(world, pos, new ItemStack(Items.APPLE));
		}
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this, 1, blockState.getBlock().getData(blockState) & 3);
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(item, 1, 0));
		defaultedList.add(new ItemStack(item, 1, 1));
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(Item.fromBlock(this), 1, ((PlanksBlock.WoodType)state.get(VARIANT)).getId() - 4);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, this.getWoodType(data)).with(DECAYABLE, (data & 4) == 0).with(CHECK_DECAY, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((PlanksBlock.WoodType)state.get(VARIANT)).getId() - 4;
		if (!(Boolean)state.get(DECAYABLE)) {
			i |= 4;
		}

		if ((Boolean)state.get(CHECK_DECAY)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public PlanksBlock.WoodType getWoodType(int state) {
		return PlanksBlock.WoodType.getById((state & 3) + 4);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT, CHECK_DECAY, DECAYABLE);
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (!world.isClient && stack.getItem() == Items.SHEARS) {
			player.incrementStat(Stats.mined(this));
			onBlockBreak(world, pos, new ItemStack(Item.fromBlock(this), 1, ((PlanksBlock.WoodType)state.get(VARIANT)).getId() - 4));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}
}
