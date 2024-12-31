package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Leaves1Block extends LeavesBlock {
	public static final EnumProperty<PlanksBlock.WoodType> VARIANT = EnumProperty.of(
		"variant", PlanksBlock.WoodType.class, new Predicate<PlanksBlock.WoodType>() {
			public boolean apply(PlanksBlock.WoodType woodType) {
				return woodType.getId() < 4;
			}
		}
	);

	public Leaves1Block() {
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, PlanksBlock.WoodType.OAK).with(CHECK_DECAY, true).with(DECAYABLE, true));
	}

	@Override
	public int getColor(BlockState state) {
		if (state.getBlock() != this) {
			return super.getColor(state);
		} else {
			PlanksBlock.WoodType woodType = state.get(VARIANT);
			if (woodType == PlanksBlock.WoodType.SPRUCE) {
				return FoliageColors.getSpruceColor();
			} else {
				return woodType == PlanksBlock.WoodType.BIRCH ? FoliageColors.getBirchColor() : super.getColor(state);
			}
		}
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		BlockState blockState = view.getBlockState(pos);
		if (blockState.getBlock() == this) {
			PlanksBlock.WoodType woodType = blockState.get(VARIANT);
			if (woodType == PlanksBlock.WoodType.SPRUCE) {
				return FoliageColors.getSpruceColor();
			}

			if (woodType == PlanksBlock.WoodType.BIRCH) {
				return FoliageColors.getBirchColor();
			}
		}

		return super.getBlockColor(view, pos, id);
	}

	@Override
	protected void dropApple(World world, BlockPos pos, BlockState state, int dropChance) {
		if (state.get(VARIANT) == PlanksBlock.WoodType.OAK && world.random.nextInt(dropChance) == 0) {
			onBlockBreak(world, pos, new ItemStack(Items.APPLE, 1, 0));
		}
	}

	@Override
	protected int getSaplingDropChance(BlockState state) {
		return state.get(VARIANT) == PlanksBlock.WoodType.JUNGLE ? 40 : super.getSaplingDropChance(state);
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item, 1, PlanksBlock.WoodType.OAK.getId()));
		stacks.add(new ItemStack(item, 1, PlanksBlock.WoodType.SPRUCE.getId()));
		stacks.add(new ItemStack(item, 1, PlanksBlock.WoodType.BIRCH.getId()));
		stacks.add(new ItemStack(item, 1, PlanksBlock.WoodType.JUNGLE.getId()));
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(Item.fromBlock(this), 1, ((PlanksBlock.WoodType)state.get(VARIANT)).getId());
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, this.getWoodType(data)).with(DECAYABLE, (data & 4) == 0).with(CHECK_DECAY, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
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
		return PlanksBlock.WoodType.getById((state & 3) % 4);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT, CHECK_DECAY, DECAYABLE);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
	}

	@Override
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
			player.incrementStat(Stats.BLOCK_STATS[Block.getIdByBlock(this)]);
			onBlockBreak(world, pos, new ItemStack(Item.fromBlock(this), 1, ((PlanksBlock.WoodType)state.get(VARIANT)).getId()));
		} else {
			super.harvest(world, player, pos, state, be);
		}
	}
}
