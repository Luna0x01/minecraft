package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class QuartzBlock extends Block {
	public static final EnumProperty<QuartzBlock.QuartzType> VARIANT = EnumProperty.of("variant", QuartzBlock.QuartzType.class);

	public QuartzBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.DEFAULT));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (id == QuartzBlock.QuartzType.LINES_X.getId()) {
			switch (dir.getAxis()) {
				case Z:
					return this.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.LINES_Z);
				case X:
					return this.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.LINES_Y);
				case Y:
				default:
					return this.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.LINES_X);
			}
		} else {
			return id == QuartzBlock.QuartzType.CHISELED.getId()
				? this.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.CHISELED)
				: this.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.DEFAULT);
		}
	}

	@Override
	public int getMeta(BlockState state) {
		QuartzBlock.QuartzType quartzType = state.get(VARIANT);
		return quartzType != QuartzBlock.QuartzType.LINES_Y && quartzType != QuartzBlock.QuartzType.LINES_Z
			? quartzType.getId()
			: QuartzBlock.QuartzType.LINES_X.getId();
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		QuartzBlock.QuartzType quartzType = state.get(VARIANT);
		return quartzType != QuartzBlock.QuartzType.LINES_Y && quartzType != QuartzBlock.QuartzType.LINES_Z
			? super.createStackFromBlock(state)
			: new ItemStack(Item.fromBlock(this), 1, QuartzBlock.QuartzType.LINES_X.getId());
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item, 1, QuartzBlock.QuartzType.DEFAULT.getId()));
		stacks.add(new ItemStack(item, 1, QuartzBlock.QuartzType.CHISELED.getId()));
		stacks.add(new ItemStack(item, 1, QuartzBlock.QuartzType.LINES_X.getId()));
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.QUARTZ;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, QuartzBlock.QuartzType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((QuartzBlock.QuartzType)state.get(VARIANT)).getId();
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((QuartzBlock.QuartzType)state.get(VARIANT)) {
					case LINES_Y:
						return state.with(VARIANT, QuartzBlock.QuartzType.LINES_Z);
					case LINES_Z:
						return state.with(VARIANT, QuartzBlock.QuartzType.LINES_Y);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum QuartzType implements StringIdentifiable {
		DEFAULT(0, "default", "default"),
		CHISELED(1, "chiseled", "chiseled"),
		LINES_X(2, "lines_y", "lines"),
		LINES_Y(3, "lines_x", "lines"),
		LINES_Z(4, "lines_z", "lines");

		private static final QuartzBlock.QuartzType[] TYPES = new QuartzBlock.QuartzType[values().length];
		private final int id;
		private final String name;
		private final String stateName;

		private QuartzType(int j, String string2, String string3) {
			this.id = j;
			this.name = string2;
			this.stateName = string3;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.stateName;
		}

		public static QuartzBlock.QuartzType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		static {
			for (QuartzBlock.QuartzType quartzType : values()) {
				TYPES[quartzType.getId()] = quartzType;
			}
		}
	}
}
