package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DoublePlantBlock extends PlantBlock implements Growable {
	public static final EnumProperty<DoublePlantBlock.DoublePlantType> VARIANT = EnumProperty.of("variant", DoublePlantBlock.DoublePlantType.class);
	public static final EnumProperty<DoublePlantBlock.HalfType> HALF = EnumProperty.of("half", DoublePlantBlock.HalfType.class);
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.DIRECTION;

	public DoublePlantBlock() {
		super(Material.REPLACEABLE_PLANT);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(VARIANT, DoublePlantBlock.DoublePlantType.SUNFLOWER)
				.with(HALF, DoublePlantBlock.HalfType.LOWER)
				.with(FACING, Direction.NORTH)
		);
		this.setStrength(0.0F);
		this.setBlockSoundGroup(BlockSoundGroup.field_12761);
		this.setTranslationKey("doublePlant");
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return collisionBox;
	}

	private DoublePlantBlock.DoublePlantType method_11606(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		if (blockState.getBlock() == this) {
			blockState = blockState.getBlockState(blockView, blockPos);
			return blockState.get(VARIANT);
		} else {
			return DoublePlantBlock.DoublePlantType.FERN;
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && world.isAir(pos.up());
	}

	@Override
	public boolean method_8638(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		if (blockState.getBlock() != this) {
			return true;
		} else {
			DoublePlantBlock.DoublePlantType doublePlantType = blockState.getBlockState(blockView, blockPos).get(VARIANT);
			return doublePlantType == DoublePlantBlock.DoublePlantType.FERN || doublePlantType == DoublePlantBlock.DoublePlantType.GRASS;
		}
	}

	@Override
	protected void plantAt(World world, BlockPos pos, BlockState state) {
		if (!this.canPlantAt(world, pos, state)) {
			boolean bl = state.get(HALF) == DoublePlantBlock.HalfType.UPPER;
			BlockPos blockPos = bl ? pos : pos.up();
			BlockPos blockPos2 = bl ? pos.down() : pos;
			Block block = (Block)(bl ? this : world.getBlockState(blockPos).getBlock());
			Block block2 = (Block)(bl ? world.getBlockState(blockPos2).getBlock() : this);
			if (block == this) {
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
			}

			if (block2 == this) {
				world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
				if (!bl) {
					this.dropAsItem(world, blockPos2, state, 0);
				}
			}
		}
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		if (state.get(HALF) == DoublePlantBlock.HalfType.UPPER) {
			return world.getBlockState(pos.down()).getBlock() == this;
		} else {
			BlockState blockState = world.getBlockState(pos.up());
			return blockState.getBlock() == this && super.canPlantAt(world, pos, blockState);
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		if (state.get(HALF) == DoublePlantBlock.HalfType.UPPER) {
			return Items.AIR;
		} else {
			DoublePlantBlock.DoublePlantType doublePlantType = state.get(VARIANT);
			if (doublePlantType == DoublePlantBlock.DoublePlantType.FERN) {
				return Items.AIR;
			} else if (doublePlantType == DoublePlantBlock.DoublePlantType.GRASS) {
				return random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
			} else {
				return super.getDropItem(state, random, id);
			}
		}
	}

	@Override
	public int getMeta(BlockState state) {
		return state.get(HALF) != DoublePlantBlock.HalfType.UPPER && state.get(VARIANT) != DoublePlantBlock.DoublePlantType.GRASS
			? ((DoublePlantBlock.DoublePlantType)state.get(VARIANT)).getId()
			: 0;
	}

	public void plantAt(World world, BlockPos pos, DoublePlantBlock.DoublePlantType type, int flags) {
		world.setBlockState(pos, this.getDefaultState().with(HALF, DoublePlantBlock.HalfType.LOWER).with(VARIANT, type), flags);
		world.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoublePlantBlock.HalfType.UPPER), flags);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoublePlantBlock.HalfType.UPPER), 2);
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (world.isClient || stack.getItem() != Items.SHEARS || state.get(HALF) != DoublePlantBlock.HalfType.LOWER || !this.onBreak(world, pos, state, player)) {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (state.get(HALF) == DoublePlantBlock.HalfType.UPPER) {
			if (world.getBlockState(pos.down()).getBlock() == this) {
				if (player.abilities.creativeMode) {
					world.setAir(pos.down());
				} else {
					BlockState blockState = world.getBlockState(pos.down());
					DoublePlantBlock.DoublePlantType doublePlantType = blockState.get(VARIANT);
					if (doublePlantType != DoublePlantBlock.DoublePlantType.FERN && doublePlantType != DoublePlantBlock.DoublePlantType.GRASS) {
						world.removeBlock(pos.down(), true);
					} else if (world.isClient) {
						world.setAir(pos.down());
					} else if (!player.getMainHandStack().isEmpty() && player.getMainHandStack().getItem() == Items.SHEARS) {
						this.onBreak(world, pos, blockState, player);
						world.setAir(pos.down());
					} else {
						world.removeBlock(pos.down(), true);
					}
				}
			}
		} else if (world.getBlockState(pos.up()).getBlock() == this) {
			world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 2);
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	private boolean onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoublePlantBlock.DoublePlantType doublePlantType = state.get(VARIANT);
		if (doublePlantType != DoublePlantBlock.DoublePlantType.FERN && doublePlantType != DoublePlantBlock.DoublePlantType.GRASS) {
			return false;
		} else {
			player.incrementStat(Stats.mined(this));
			int i = (doublePlantType == DoublePlantBlock.DoublePlantType.GRASS ? TallPlantBlock.GrassType.GRASS : TallPlantBlock.GrassType.FERN).getId();
			onBlockBreak(world, pos, new ItemStack(Blocks.TALLGRASS, 2, i));
			return true;
		}
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> stacks) {
		for (DoublePlantBlock.DoublePlantType doublePlantType : DoublePlantBlock.DoublePlantType.values()) {
			stacks.add(new ItemStack(this, 1, doublePlantType.getId()));
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this, 1, this.method_11606(world, blockPos, blockState).getId());
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		DoublePlantBlock.DoublePlantType doublePlantType = this.method_11606(world, pos, state);
		return doublePlantType != DoublePlantBlock.DoublePlantType.GRASS && doublePlantType != DoublePlantBlock.DoublePlantType.FERN;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		onBlockBreak(world, pos, new ItemStack(this, 1, this.method_11606(world, pos, state).getId()));
	}

	@Override
	public BlockState stateFromData(int data) {
		return (data & 8) > 0
			? this.getDefaultState().with(HALF, DoublePlantBlock.HalfType.UPPER)
			: this.getDefaultState().with(HALF, DoublePlantBlock.HalfType.LOWER).with(VARIANT, DoublePlantBlock.DoublePlantType.getById(data & 7));
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(HALF) == DoublePlantBlock.HalfType.UPPER) {
			BlockState blockState = view.getBlockState(pos.down());
			if (blockState.getBlock() == this) {
				state = state.with(VARIANT, blockState.get(VARIANT));
			}
		}

		return state;
	}

	@Override
	public int getData(BlockState state) {
		return state.get(HALF) == DoublePlantBlock.HalfType.UPPER
			? 8 | ((Direction)state.get(FACING)).getHorizontal()
			: ((DoublePlantBlock.DoublePlantType)state.get(VARIANT)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, HALF, VARIANT, FACING);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	public static enum DoublePlantType implements StringIdentifiable {
		SUNFLOWER(0, "sunflower"),
		SYRINGA(1, "syringa"),
		GRASS(2, "double_grass", "grass"),
		FERN(3, "double_fern", "fern"),
		ROSE(4, "double_rose", "rose"),
		PAEONIA(5, "paeonia");

		private static final DoublePlantBlock.DoublePlantType[] VALUES = new DoublePlantBlock.DoublePlantType[values().length];
		private final int id;
		private final String name;
		private final String translationKey;

		private DoublePlantType(int j, String string2) {
			this(j, string2, string2);
		}

		private DoublePlantType(int j, String string2, String string3) {
			this.id = j;
			this.name = string2;
			this.translationKey = string3;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public static DoublePlantBlock.DoublePlantType getById(int id) {
			if (id < 0 || id >= VALUES.length) {
				id = 0;
			}

			return VALUES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getSingleName() {
			return this.translationKey;
		}

		static {
			for (DoublePlantBlock.DoublePlantType doublePlantType : values()) {
				VALUES[doublePlantType.getId()] = doublePlantType;
			}
		}
	}

	public static enum HalfType implements StringIdentifiable {
		UPPER,
		LOWER;

		public String toString() {
			return this.asString();
		}

		@Override
		public String asString() {
			return this == UPPER ? "upper" : "lower";
		}
	}
}
