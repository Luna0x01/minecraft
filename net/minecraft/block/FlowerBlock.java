package net.minecraft.block;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public abstract class FlowerBlock extends PlantBlock {
	protected EnumProperty<FlowerBlock.FlowerType> flowerProperties;

	protected FlowerBlock() {
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(this.getFlowerProperties(), this.getColor() == FlowerBlock.Color.RED ? FlowerBlock.FlowerType.POPPY : FlowerBlock.FlowerType.DANDELION)
		);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((FlowerBlock.FlowerType)state.get(this.getFlowerProperties())).getDataIndex();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (FlowerBlock.FlowerType flowerType : FlowerBlock.FlowerType.getTypes(this.getColor())) {
			stacks.add(new ItemStack(item, 1, flowerType.getDataIndex()));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(this.getFlowerProperties(), FlowerBlock.FlowerType.getType(this.getColor(), data));
	}

	public abstract FlowerBlock.Color getColor();

	public Property<FlowerBlock.FlowerType> getFlowerProperties() {
		if (this.flowerProperties == null) {
			this.flowerProperties = EnumProperty.of("type", FlowerBlock.FlowerType.class, new Predicate<FlowerBlock.FlowerType>() {
				public boolean apply(@Nullable FlowerBlock.FlowerType flowerType) {
					return flowerType.getColor() == FlowerBlock.this.getColor();
				}
			});
		}

		return this.flowerProperties;
	}

	@Override
	public int getData(BlockState state) {
		return ((FlowerBlock.FlowerType)state.get(this.getFlowerProperties())).getDataIndex();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, this.getFlowerProperties());
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	public static enum Color {
		YELLOW,
		RED;

		public FlowerBlock getBlock() {
			return this == YELLOW ? Blocks.YELLOW_FLOWER : Blocks.RED_FLOWER;
		}
	}

	public static enum FlowerType implements StringIdentifiable {
		DANDELION(FlowerBlock.Color.YELLOW, 0, "dandelion"),
		POPPY(FlowerBlock.Color.RED, 0, "poppy"),
		BLUE_ORCHID(FlowerBlock.Color.RED, 1, "blue_orchid", "blueOrchid"),
		ALLIUM(FlowerBlock.Color.RED, 2, "allium"),
		HOUSTONIA(FlowerBlock.Color.RED, 3, "houstonia"),
		RED_TULIP(FlowerBlock.Color.RED, 4, "red_tulip", "tulipRed"),
		ORANGE_TULIP(FlowerBlock.Color.RED, 5, "orange_tulip", "tulipOrange"),
		WHITE_TULIP(FlowerBlock.Color.RED, 6, "white_tulip", "tulipWhite"),
		PINK_TULIP(FlowerBlock.Color.RED, 7, "pink_tulip", "tulipPink"),
		OXEYE_DAISY(FlowerBlock.Color.RED, 8, "oxeye_daisy", "oxeyeDaisy");

		private static final FlowerBlock.FlowerType[][] flowerType = new FlowerBlock.FlowerType[FlowerBlock.Color.values().length][];
		private final FlowerBlock.Color color;
		private final int dataIndex;
		private final String id;
		private final String name;

		private FlowerType(FlowerBlock.Color color, int j, String string2) {
			this(color, j, string2, string2);
		}

		private FlowerType(FlowerBlock.Color color, int j, String string2, String string3) {
			this.color = color;
			this.dataIndex = j;
			this.id = string2;
			this.name = string3;
		}

		public FlowerBlock.Color getColor() {
			return this.color;
		}

		public int getDataIndex() {
			return this.dataIndex;
		}

		public static FlowerBlock.FlowerType getType(FlowerBlock.Color color, int colorIndex) {
			FlowerBlock.FlowerType[] flowerTypes = flowerType[color.ordinal()];
			if (colorIndex < 0 || colorIndex >= flowerTypes.length) {
				colorIndex = 0;
			}

			return flowerTypes[colorIndex];
		}

		public static FlowerBlock.FlowerType[] getTypes(FlowerBlock.Color color) {
			return flowerType[color.ordinal()];
		}

		public String toString() {
			return this.id;
		}

		@Override
		public String asString() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		static {
			for (final FlowerBlock.Color color : FlowerBlock.Color.values()) {
				Collection<FlowerBlock.FlowerType> collection = Collections2.filter(Lists.newArrayList(values()), new Predicate<FlowerBlock.FlowerType>() {
					public boolean apply(@Nullable FlowerBlock.FlowerType flowerType) {
						return flowerType.getColor() == color;
					}
				});
				flowerType[color.ordinal()] = (FlowerBlock.FlowerType[])collection.toArray(new FlowerBlock.FlowerType[collection.size()]);
			}
		}
	}
}
