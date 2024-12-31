package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;
import net.minecraft.world.gen.feature.BigTreeFeature;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GiantJungleTreeFeature;
import net.minecraft.world.gen.feature.GiantSpruceTreeFeature;
import net.minecraft.world.gen.feature.JungleTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class SaplingBlock extends PlantBlock implements Growable {
	public static final EnumProperty<PlanksBlock.WoodType> TYPE = EnumProperty.of("type", PlanksBlock.WoodType.class);
	public static final IntProperty STAGE = IntProperty.of("stage", 0, 1);
	protected static final Box field_12750 = new Box(0.099999994F, 0.0, 0.099999994F, 0.9F, 0.8F, 0.9F);

	protected SaplingBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(TYPE, PlanksBlock.WoodType.OAK).with(STAGE, 0));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12750;
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + "." + PlanksBlock.WoodType.OAK.getOldName() + ".name");
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			super.onScheduledTick(world, pos, state, rand);
			if (world.getLightLevelWithNeighbours(pos.up()) >= 9 && rand.nextInt(7) == 0) {
				this.grow(world, pos, state, rand);
			}
		}
	}

	public void grow(World world, BlockPos blockPos, BlockState blockState, Random random) {
		if ((Integer)blockState.get(STAGE) == 0) {
			world.setBlockState(blockPos, blockState.withDefaultValue(STAGE), 4);
		} else {
			this.generateTree(world, blockPos, blockState, random);
		}
	}

	public void generateTree(World world, BlockPos blockPos, BlockState blockState, Random random) {
		Feature feature = (Feature)(random.nextInt(10) == 0 ? new BigTreeFeature(true) : new JungleTreeFeature(true));
		int i = 0;
		int j = 0;
		boolean bl = false;
		switch ((PlanksBlock.WoodType)blockState.get(TYPE)) {
			case SPRUCE:
				label68:
				for (i = 0; i >= -1; i--) {
					for (j = 0; j >= -1; j--) {
						if (this.canSpawnGiantTree(world, blockPos, i, j, PlanksBlock.WoodType.SPRUCE)) {
							feature = new GiantSpruceTreeFeature(false, random.nextBoolean());
							bl = true;
							break label68;
						}
					}
				}

				if (!bl) {
					i = 0;
					j = 0;
					feature = new SpruceTreeFeature(true);
				}
				break;
			case BIRCH:
				feature = new BirchTreeFeature(true, false);
				break;
			case JUNGLE:
				BlockState blockState2 = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.JUNGLE);
				BlockState blockState3 = Blocks.LEAVES.getDefaultState().with(Leaves1Block.VARIANT, PlanksBlock.WoodType.JUNGLE).with(LeavesBlock.CHECK_DECAY, false);

				label82:
				for (i = 0; i >= -1; i--) {
					for (j = 0; j >= -1; j--) {
						if (this.canSpawnGiantTree(world, blockPos, i, j, PlanksBlock.WoodType.JUNGLE)) {
							feature = new GiantJungleTreeFeature(true, 10, 20, blockState2, blockState3);
							bl = true;
							break label82;
						}
					}
				}

				if (!bl) {
					i = 0;
					j = 0;
					feature = new JungleTreeFeature(true, 4 + random.nextInt(7), blockState2, blockState3, false);
				}
				break;
			case ACACIA:
				feature = new AcaciaTreeFeature(true);
				break;
			case DARK_OAK:
				label96:
				for (i = 0; i >= -1; i--) {
					for (j = 0; j >= -1; j--) {
						if (this.canSpawnGiantTree(world, blockPos, i, j, PlanksBlock.WoodType.DARK_OAK)) {
							feature = new DarkOakTreeFeature(true);
							bl = true;
							break label96;
						}
					}
				}

				if (!bl) {
					return;
				}
			case OAK:
		}

		BlockState blockState4 = Blocks.AIR.getDefaultState();
		if (bl) {
			world.setBlockState(blockPos.add(i, 0, j), blockState4, 4);
			world.setBlockState(blockPos.add(i + 1, 0, j), blockState4, 4);
			world.setBlockState(blockPos.add(i, 0, j + 1), blockState4, 4);
			world.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState4, 4);
		} else {
			world.setBlockState(blockPos, blockState4, 4);
		}

		if (!feature.generate(world, random, blockPos.add(i, 0, j))) {
			if (bl) {
				world.setBlockState(blockPos.add(i, 0, j), blockState, 4);
				world.setBlockState(blockPos.add(i + 1, 0, j), blockState, 4);
				world.setBlockState(blockPos.add(i, 0, j + 1), blockState, 4);
				world.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState, 4);
			} else {
				world.setBlockState(blockPos, blockState, 4);
			}
		}
	}

	private boolean canSpawnGiantTree(World world, BlockPos pos, int x, int z, PlanksBlock.WoodType woodType) {
		return this.isOfSameType(world, pos.add(x, 0, z), woodType)
			&& this.isOfSameType(world, pos.add(x + 1, 0, z), woodType)
			&& this.isOfSameType(world, pos.add(x, 0, z + 1), woodType)
			&& this.isOfSameType(world, pos.add(x + 1, 0, z + 1), woodType);
	}

	public boolean isOfSameType(World world, BlockPos blockPos, PlanksBlock.WoodType woodType) {
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.getBlock() == this && blockState.get(TYPE) == woodType;
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(TYPE)).getId();
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		for (PlanksBlock.WoodType woodType : PlanksBlock.WoodType.values()) {
			defaultedList.add(new ItemStack(item, 1, woodType.getId()));
		}
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return (double)world.random.nextFloat() < 0.45;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.grow(world, pos, state, random);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(TYPE, PlanksBlock.WoodType.getById(data & 7)).with(STAGE, (data & 8) >> 3);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((PlanksBlock.WoodType)state.get(TYPE)).getId();
		return i | (Integer)state.get(STAGE) << 3;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, TYPE, STAGE);
	}
}
