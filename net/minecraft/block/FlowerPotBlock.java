package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FlowerPotBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;

public class FlowerPotBlock extends BlockWithEntity {
	public static final IntProperty LEGACY_DATA = IntProperty.of("legacy_data", 0, 15);
	public static final EnumProperty<FlowerPotBlock.PottablePlantType> CONTENTS = EnumProperty.of("contents", FlowerPotBlock.PottablePlantType.class);
	protected static final Box field_12679 = new Box(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875);

	public FlowerPotBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(CONTENTS, FlowerPotBlock.PottablePlantType.EMPTY).with(LEGACY_DATA, 0));
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.flowerPot.name");
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12679;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		ItemStack itemStack = player.getStackInHand(hand);
		FlowerPotBlockEntity flowerPotBlockEntity = this.getPotEntity(world, pos);
		if (flowerPotBlockEntity == null) {
			return false;
		} else {
			ItemStack itemStack2 = flowerPotBlockEntity.method_11659();
			if (itemStack2.isEmpty()) {
				if (!this.method_13707(itemStack)) {
					return false;
				}

				flowerPotBlockEntity.method_13725(itemStack);
				player.incrementStat(Stats.FLOWER_POTTED);
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			} else {
				if (itemStack.isEmpty()) {
					player.equipStack(hand, itemStack2);
				} else if (!player.method_13617(itemStack2)) {
					player.dropItem(itemStack2, false);
				}

				flowerPotBlockEntity.method_13725(ItemStack.EMPTY);
			}

			flowerPotBlockEntity.markDirty();
			world.method_11481(pos, state, state, 3);
			return true;
		}
	}

	private boolean method_13707(ItemStack itemStack) {
		Block block = Block.getBlockFromItem(itemStack.getItem());
		if (block != Blocks.YELLOW_FLOWER
			&& block != Blocks.RED_FLOWER
			&& block != Blocks.CACTUS
			&& block != Blocks.BROWN_MUSHROOM
			&& block != Blocks.RED_MUSHROOM
			&& block != Blocks.SAPLING
			&& block != Blocks.DEADBUSH) {
			int i = itemStack.getData();
			return block == Blocks.TALLGRASS && i == TallPlantBlock.GrassType.FERN.getId();
		} else {
			return true;
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		FlowerPotBlockEntity flowerPotBlockEntity = this.getPotEntity(world, blockPos);
		if (flowerPotBlockEntity != null) {
			ItemStack itemStack = flowerPotBlockEntity.method_11659();
			if (!itemStack.isEmpty()) {
				return itemStack;
			}
		}

		return new ItemStack(Items.FLOWER_POT);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && world.getBlockState(pos.down()).method_11739();
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.getBlockState(pos.down()).method_11739()) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		FlowerPotBlockEntity flowerPotBlockEntity = this.getPotEntity(world, pos);
		if (flowerPotBlockEntity != null && flowerPotBlockEntity.getItem() != null) {
			onBlockBreak(world, pos, new ItemStack(flowerPotBlockEntity.getItem(), 1, flowerPotBlockEntity.getData()));
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreakByPlayer(world, pos, state, player);
		if (player.abilities.creativeMode) {
			FlowerPotBlockEntity flowerPotBlockEntity = this.getPotEntity(world, pos);
			if (flowerPotBlockEntity != null) {
				flowerPotBlockEntity.method_13725(ItemStack.EMPTY);
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.FLOWER_POT;
	}

	@Nullable
	private FlowerPotBlockEntity getPotEntity(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof FlowerPotBlockEntity ? (FlowerPotBlockEntity)blockEntity : null;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		Block block = null;
		int i = 0;
		switch (id) {
			case 1:
				block = Blocks.RED_FLOWER;
				i = FlowerBlock.FlowerType.POPPY.getDataIndex();
				break;
			case 2:
				block = Blocks.YELLOW_FLOWER;
				break;
			case 3:
				block = Blocks.SAPLING;
				i = PlanksBlock.WoodType.OAK.getId();
				break;
			case 4:
				block = Blocks.SAPLING;
				i = PlanksBlock.WoodType.SPRUCE.getId();
				break;
			case 5:
				block = Blocks.SAPLING;
				i = PlanksBlock.WoodType.BIRCH.getId();
				break;
			case 6:
				block = Blocks.SAPLING;
				i = PlanksBlock.WoodType.JUNGLE.getId();
				break;
			case 7:
				block = Blocks.RED_MUSHROOM;
				break;
			case 8:
				block = Blocks.BROWN_MUSHROOM;
				break;
			case 9:
				block = Blocks.CACTUS;
				break;
			case 10:
				block = Blocks.DEADBUSH;
				break;
			case 11:
				block = Blocks.TALLGRASS;
				i = TallPlantBlock.GrassType.FERN.getId();
				break;
			case 12:
				block = Blocks.SAPLING;
				i = PlanksBlock.WoodType.ACACIA.getId();
				break;
			case 13:
				block = Blocks.SAPLING;
				i = PlanksBlock.WoodType.DARK_OAK.getId();
		}

		return new FlowerPotBlockEntity(Item.fromBlock(block), i);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, CONTENTS, LEGACY_DATA);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(LEGACY_DATA);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		FlowerPotBlock.PottablePlantType pottablePlantType = FlowerPotBlock.PottablePlantType.EMPTY;
		BlockEntity blockEntity = view instanceof ChunkCache ? ((ChunkCache)view).method_13314(pos, Chunk.Status.CHECK) : view.getBlockEntity(pos);
		if (blockEntity instanceof FlowerPotBlockEntity) {
			FlowerPotBlockEntity flowerPotBlockEntity = (FlowerPotBlockEntity)blockEntity;
			Item item = flowerPotBlockEntity.getItem();
			if (item instanceof BlockItem) {
				int i = flowerPotBlockEntity.getData();
				Block block = Block.getBlockFromItem(item);
				if (block == Blocks.SAPLING) {
					switch (PlanksBlock.WoodType.getById(i)) {
						case OAK:
							pottablePlantType = FlowerPotBlock.PottablePlantType.OAK_SAPLING;
							break;
						case SPRUCE:
							pottablePlantType = FlowerPotBlock.PottablePlantType.SPRUCE_SAPLING;
							break;
						case BIRCH:
							pottablePlantType = FlowerPotBlock.PottablePlantType.BIRCH_SAPLING;
							break;
						case JUNGLE:
							pottablePlantType = FlowerPotBlock.PottablePlantType.JUNGLE_SAPLING;
							break;
						case ACACIA:
							pottablePlantType = FlowerPotBlock.PottablePlantType.ACACIA_SAPLING;
							break;
						case DARK_OAK:
							pottablePlantType = FlowerPotBlock.PottablePlantType.DARK_OAK_SAPLING;
							break;
						default:
							pottablePlantType = FlowerPotBlock.PottablePlantType.EMPTY;
					}
				} else if (block == Blocks.TALLGRASS) {
					switch (i) {
						case 0:
							pottablePlantType = FlowerPotBlock.PottablePlantType.DEAD_BUSH;
							break;
						case 2:
							pottablePlantType = FlowerPotBlock.PottablePlantType.FERN;
							break;
						default:
							pottablePlantType = FlowerPotBlock.PottablePlantType.EMPTY;
					}
				} else if (block == Blocks.YELLOW_FLOWER) {
					pottablePlantType = FlowerPotBlock.PottablePlantType.DANDELION;
				} else if (block == Blocks.RED_FLOWER) {
					switch (FlowerBlock.FlowerType.getType(FlowerBlock.Color.RED, i)) {
						case POPPY:
							pottablePlantType = FlowerPotBlock.PottablePlantType.POPPY;
							break;
						case BLUE_ORCHID:
							pottablePlantType = FlowerPotBlock.PottablePlantType.BLUE_ORCHID;
							break;
						case ALLIUM:
							pottablePlantType = FlowerPotBlock.PottablePlantType.ALLIUM;
							break;
						case HOUSTONIA:
							pottablePlantType = FlowerPotBlock.PottablePlantType.HOUSTONIA;
							break;
						case RED_TULIP:
							pottablePlantType = FlowerPotBlock.PottablePlantType.RED_TULIP;
							break;
						case ORANGE_TULIP:
							pottablePlantType = FlowerPotBlock.PottablePlantType.ORANGE_TULIP;
							break;
						case WHITE_TULIP:
							pottablePlantType = FlowerPotBlock.PottablePlantType.WHITE_TULIP;
							break;
						case PINK_TULIP:
							pottablePlantType = FlowerPotBlock.PottablePlantType.PINK_TULIP;
							break;
						case OXEYE_DAISY:
							pottablePlantType = FlowerPotBlock.PottablePlantType.OXEYE_DAISY;
							break;
						default:
							pottablePlantType = FlowerPotBlock.PottablePlantType.EMPTY;
					}
				} else if (block == Blocks.RED_MUSHROOM) {
					pottablePlantType = FlowerPotBlock.PottablePlantType.RED_MUSHROOM;
				} else if (block == Blocks.BROWN_MUSHROOM) {
					pottablePlantType = FlowerPotBlock.PottablePlantType.BROWN_MUSHROOM;
				} else if (block == Blocks.DEADBUSH) {
					pottablePlantType = FlowerPotBlock.PottablePlantType.DEAD_BUSH;
				} else if (block == Blocks.CACTUS) {
					pottablePlantType = FlowerPotBlock.PottablePlantType.CACTUS;
				}
			}
		}

		return state.with(CONTENTS, pottablePlantType);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	public static enum PottablePlantType implements StringIdentifiable {
		EMPTY("empty"),
		POPPY("rose"),
		BLUE_ORCHID("blue_orchid"),
		ALLIUM("allium"),
		HOUSTONIA("houstonia"),
		RED_TULIP("red_tulip"),
		ORANGE_TULIP("orange_tulip"),
		WHITE_TULIP("white_tulip"),
		PINK_TULIP("pink_tulip"),
		OXEYE_DAISY("oxeye_daisy"),
		DANDELION("dandelion"),
		OAK_SAPLING("oak_sapling"),
		SPRUCE_SAPLING("spruce_sapling"),
		BIRCH_SAPLING("birch_sapling"),
		JUNGLE_SAPLING("jungle_sapling"),
		ACACIA_SAPLING("acacia_sapling"),
		DARK_OAK_SAPLING("dark_oak_sapling"),
		RED_MUSHROOM("mushroom_red"),
		BROWN_MUSHROOM("mushroom_brown"),
		DEAD_BUSH("dead_bush"),
		FERN("fern"),
		CACTUS("cactus");

		private final String name;

		private PottablePlantType(String string2) {
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
