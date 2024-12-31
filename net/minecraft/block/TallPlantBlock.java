package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TallPlantBlock extends PlantBlock implements Growable {
	public static final EnumProperty<TallPlantBlock.GrassType> TYPE = EnumProperty.of("type", TallPlantBlock.GrassType.class);

	protected TallPlantBlock() {
		super(Material.REPLACEABLE_PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(TYPE, TallPlantBlock.GrassType.DEAD_BUSH));
		float f = 0.4F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
	}

	@Override
	public int getColor() {
		return GrassColors.getColor(0.5, 1.0);
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		return this.canPlantOnTop(world.getBlockState(pos.down()).getBlock());
	}

	@Override
	public boolean isReplaceable(World world, BlockPos pos) {
		return true;
	}

	@Override
	public int getColor(BlockState state) {
		if (state.getBlock() != this) {
			return super.getColor(state);
		} else {
			TallPlantBlock.GrassType grassType = state.get(TYPE);
			return grassType == TallPlantBlock.GrassType.DEAD_BUSH ? 16777215 : GrassColors.getColor(0.5, 1.0);
		}
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return view.getBiome(pos).getGrassColor(pos);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : null;
	}

	@Override
	public int getBonusDrops(int id, Random rand) {
		return 1 + rand.nextInt(id * 2 + 1);
	}

	@Override
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
			player.incrementStat(Stats.BLOCK_STATS[Block.getIdByBlock(this)]);
			onBlockBreak(world, pos, new ItemStack(Blocks.TALLGRASS, 1, ((TallPlantBlock.GrassType)state.get(TYPE)).getId()));
		} else {
			super.harvest(world, player, pos, state, be);
		}
	}

	@Override
	public int getMeta(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.getBlock().getData(blockState);
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (int i = 1; i < 3; i++) {
			stacks.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return state.get(TYPE) != TallPlantBlock.GrassType.DEAD_BUSH;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		DoublePlantBlock.DoublePlantType doublePlantType = DoublePlantBlock.DoublePlantType.GRASS;
		if (state.get(TYPE) == TallPlantBlock.GrassType.FERN) {
			doublePlantType = DoublePlantBlock.DoublePlantType.FERN;
		}

		if (Blocks.DOUBLE_PLANT.canBePlacedAtPos(world, pos)) {
			Blocks.DOUBLE_PLANT.plantAt(world, pos, doublePlantType, 2);
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(TYPE, TallPlantBlock.GrassType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((TallPlantBlock.GrassType)state.get(TYPE)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, TYPE);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XYZ;
	}

	public static enum GrassType implements StringIdentifiable {
		DEAD_BUSH(0, "dead_bush"),
		GRASS(1, "tall_grass"),
		FERN(2, "fern");

		private static final TallPlantBlock.GrassType[] TYPES = new TallPlantBlock.GrassType[values().length];
		private final int id;
		private final String name;

		private GrassType(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public static TallPlantBlock.GrassType getById(int id) {
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
			for (TallPlantBlock.GrassType grassType : values()) {
				TYPES[grassType.getId()] = grassType;
			}
		}
	}
}
