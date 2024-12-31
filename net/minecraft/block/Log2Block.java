package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;

public class Log2Block extends LogBlock {
	public static final EnumProperty<PlanksBlock.WoodType> VARIANT = EnumProperty.of(
		"variant", PlanksBlock.WoodType.class, new Predicate<PlanksBlock.WoodType>() {
			public boolean apply(@Nullable PlanksBlock.WoodType woodType) {
				return woodType.getId() >= 4;
			}
		}
	);

	public Log2Block() {
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, PlanksBlock.WoodType.ACACIA).with(LOG_AXIS, LogBlock.Axis.Y));
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		PlanksBlock.WoodType woodType = state.get(VARIANT);
		switch ((LogBlock.Axis)state.get(LOG_AXIS)) {
			case X:
			case Z:
			case NONE:
			default:
				switch (woodType) {
					case ACACIA:
					default:
						return MaterialColor.STONE;
					case DARK_OAK:
						return PlanksBlock.WoodType.DARK_OAK.getMaterialColor();
				}
			case Y:
				return woodType.getMaterialColor();
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item, 1, PlanksBlock.WoodType.ACACIA.getId() - 4));
		stacks.add(new ItemStack(item, 1, PlanksBlock.WoodType.DARK_OAK.getId() - 4));
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(VARIANT, PlanksBlock.WoodType.getById((data & 3) + 4));
		switch (data & 12) {
			case 0:
				blockState = blockState.with(LOG_AXIS, LogBlock.Axis.Y);
				break;
			case 4:
				blockState = blockState.with(LOG_AXIS, LogBlock.Axis.X);
				break;
			case 8:
				blockState = blockState.with(LOG_AXIS, LogBlock.Axis.Z);
				break;
			default:
				blockState = blockState.with(LOG_AXIS, LogBlock.Axis.NONE);
		}

		return blockState;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((PlanksBlock.WoodType)state.get(VARIANT)).getId() - 4;
		switch ((LogBlock.Axis)state.get(LOG_AXIS)) {
			case X:
				i |= 4;
				break;
			case Z:
				i |= 8;
				break;
			case NONE:
				i |= 12;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT, LOG_AXIS);
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(Item.fromBlock(this), 1, ((PlanksBlock.WoodType)state.get(VARIANT)).getId() - 4);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getId() - 4;
	}
}
