package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MushroomBlock extends Block {
	public static final EnumProperty<MushroomBlock.MushroomType> VARIANT = EnumProperty.of("variant", MushroomBlock.MushroomType.class);
	private final Block block;

	public MushroomBlock(Material material, MaterialColor materialColor, Block block) {
		super(material, materialColor);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, MushroomBlock.MushroomType.ALL_OUTSIDE));
		this.block = block;
	}

	@Override
	public int getDropCount(Random rand) {
		return Math.max(0, rand.nextInt(10) - 7);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		switch ((MushroomBlock.MushroomType)state.get(VARIANT)) {
			case ALL_STEM:
				return MaterialColor.WEB;
			case ALL_INSIDE:
				return MaterialColor.SAND;
			case STEM:
				return MaterialColor.SAND;
			default:
				return super.getMaterialColor(state);
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(this.block);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.block);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState();
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, MushroomBlock.MushroomType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((MushroomBlock.MushroomType)state.get(VARIANT)).getId();
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((MushroomBlock.MushroomType)state.get(VARIANT)) {
					case STEM:
						break;
					case NORTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_EAST);
					case NORTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH);
					case NORTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_WEST);
					case WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.EAST);
					case EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.WEST);
					case SOUTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_EAST);
					case SOUTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH);
					case SOUTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_WEST);
					default:
						return state;
				}
			case COUNTERCLOCKWISE_90:
				switch ((MushroomBlock.MushroomType)state.get(VARIANT)) {
					case STEM:
						break;
					case NORTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_WEST);
					case NORTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.WEST);
					case NORTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_WEST);
					case WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH);
					case EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH);
					case SOUTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_EAST);
					case SOUTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.EAST);
					case SOUTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_EAST);
					default:
						return state;
				}
			case CLOCKWISE_90:
				switch ((MushroomBlock.MushroomType)state.get(VARIANT)) {
					case STEM:
						break;
					case NORTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_EAST);
					case NORTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.EAST);
					case NORTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_EAST);
					case WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH);
					case EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH);
					case SOUTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_WEST);
					case SOUTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.WEST);
					case SOUTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_WEST);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		MushroomBlock.MushroomType mushroomType = state.get(VARIANT);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (mushroomType) {
					case NORTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_WEST);
					case NORTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH);
					case NORTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_EAST);
					case WEST:
					case EAST:
					default:
						return super.withMirror(state, mirror);
					case SOUTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_WEST);
					case SOUTH:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH);
					case SOUTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_EAST);
				}
			case FRONT_BACK:
				switch (mushroomType) {
					case NORTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_EAST);
					case NORTH:
					case SOUTH:
					default:
						break;
					case NORTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.NORTH_WEST);
					case WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.EAST);
					case EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.WEST);
					case SOUTH_WEST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_EAST);
					case SOUTH_EAST:
						return state.with(VARIANT, MushroomBlock.MushroomType.SOUTH_WEST);
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum MushroomType implements StringIdentifiable {
		NORTH_WEST(1, "north_west"),
		NORTH(2, "north"),
		NORTH_EAST(3, "north_east"),
		WEST(4, "west"),
		CENTER(5, "center"),
		EAST(6, "east"),
		SOUTH_WEST(7, "south_west"),
		SOUTH(8, "south"),
		SOUTH_EAST(9, "south_east"),
		STEM(10, "stem"),
		ALL_INSIDE(0, "all_inside"),
		ALL_OUTSIDE(14, "all_outside"),
		ALL_STEM(15, "all_stem");

		private static final MushroomBlock.MushroomType[] TYPES = new MushroomBlock.MushroomType[16];
		private final int id;
		private final String name;

		private MushroomType(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public static MushroomBlock.MushroomType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			MushroomBlock.MushroomType mushroomType = TYPES[id];
			return mushroomType == null ? TYPES[0] : mushroomType;
		}

		@Override
		public String asString() {
			return this.name;
		}

		static {
			for (MushroomBlock.MushroomType mushroomType : values()) {
				TYPES[mushroomType.getId()] = mushroomType;
			}
		}
	}
}
