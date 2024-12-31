package net.minecraft.client.render.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.Leaves2Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log1Block;
import net.minecraft.block.Log2Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.QuartzBlock;
import net.minecraft.block.RedSandstoneBlock;
import net.minecraft.block.RedSandstoneSlabBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WoolBlock;
import net.minecraft.client.BlockStateMap;
import net.minecraft.client.BlockStateMapper;
import net.minecraft.client.render.BlockStateIdentifierMap;
import net.minecraft.client.render.BlockStateIdentifierMapAccess;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;

public class BlockModelShapes {
	private final Map<BlockState, BakedModel> models = Maps.newIdentityHashMap();
	private final BlockStateMapper blockStateMapper = new BlockStateMapper();
	private final BakedModelManager bakedModelManager;

	public BlockModelShapes(BakedModelManager bakedModelManager) {
		this.bakedModelManager = bakedModelManager;
		this.init();
	}

	public BlockStateMapper getBlockStateMapper() {
		return this.blockStateMapper;
	}

	public Sprite getParticleSprite(BlockState state) {
		Block block = state.getBlock();
		BakedModel bakedModel = this.getBakedModel(state);
		if (bakedModel == null || bakedModel == this.bakedModelManager.getBakedModel()) {
			if (block == Blocks.WALL_SIGN
				|| block == Blocks.STANDING_SIGN
				|| block == Blocks.CHEST
				|| block == Blocks.TRAPPED_CHEST
				|| block == Blocks.STANDING_BANNER
				|| block == Blocks.WALL_BANNER
				|| block == Blocks.BED) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/planks_oak");
			}

			if (block == Blocks.ENDERCHEST) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/obsidian");
			}

			if (block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/lava_still");
			}

			if (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/water_still");
			}

			if (block == Blocks.SKULL) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/soul_sand");
			}

			if (block == Blocks.BARRIER) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:items/barrier");
			}

			if (block == Blocks.STRUCTURE_VOID) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:items/structure_void");
			}

			if (block == Blocks.WHITE_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_white");
			}

			if (block == Blocks.ORANGE_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_orange");
			}

			if (block == Blocks.MAGENTA_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_magenta");
			}

			if (block == Blocks.LIGHT_BLUE_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_light_blue");
			}

			if (block == Blocks.YELLOW_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_yellow");
			}

			if (block == Blocks.LIME_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_lime");
			}

			if (block == Blocks.PINK_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_pink");
			}

			if (block == Blocks.GRAY_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_gray");
			}

			if (block == Blocks.SILVER_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_silver");
			}

			if (block == Blocks.CYAN_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_cyan");
			}

			if (block == Blocks.PURPLE_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_purple");
			}

			if (block == Blocks.BLUE_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_blue");
			}

			if (block == Blocks.BROWN_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_brown");
			}

			if (block == Blocks.GREEN_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_green");
			}

			if (block == Blocks.RED_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_red");
			}

			if (block == Blocks.BLACK_SHULKER_BOX) {
				return this.bakedModelManager.getAtlas().getSprite("minecraft:blocks/shulker_top_black");
			}
		}

		if (bakedModel == null) {
			bakedModel = this.bakedModelManager.getBakedModel();
		}

		return bakedModel.getParticleSprite();
	}

	public BakedModel getBakedModel(BlockState state) {
		BakedModel bakedModel = (BakedModel)this.models.get(state);
		if (bakedModel == null) {
			bakedModel = this.bakedModelManager.getBakedModel();
		}

		return bakedModel;
	}

	public BakedModelManager getBakedModelManager() {
		return this.bakedModelManager;
	}

	public void reload() {
		this.models.clear();

		for (Entry<BlockState, ModelIdentifier> entry : this.blockStateMapper.getBlockStateMap().entrySet()) {
			this.models.put(entry.getKey(), this.bakedModelManager.getByIdentifier((ModelIdentifier)entry.getValue()));
		}
	}

	public void putBlock(Block block, BlockStateIdentifierMapAccess blockStateIdentifierMapAccess) {
		this.blockStateMapper.putBlock(block, blockStateIdentifierMapAccess);
	}

	public void addBlocks(Block... blocks) {
		this.blockStateMapper.putBlocks(blocks);
	}

	private void init() {
		this.addBlocks(
			Blocks.AIR,
			Blocks.FLOWING_WATER,
			Blocks.WATER,
			Blocks.FLOWING_LAVA,
			Blocks.LAVA,
			Blocks.PISTON_EXTENSION,
			Blocks.CHEST,
			Blocks.ENDERCHEST,
			Blocks.TRAPPED_CHEST,
			Blocks.STANDING_SIGN,
			Blocks.SKULL,
			Blocks.END_PORTAL,
			Blocks.BARRIER,
			Blocks.WALL_SIGN,
			Blocks.WALL_BANNER,
			Blocks.STANDING_BANNER,
			Blocks.END_GATEWAY,
			Blocks.STRUCTURE_VOID,
			Blocks.WHITE_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.LIGHT_BLUE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.SILVER_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.BLACK_SHULKER_BOX,
			Blocks.BED
		);
		this.putBlock(Blocks.STONE, new BlockStateMap.Builder().defaultProperty(StoneBlock.VARIANT).build());
		this.putBlock(Blocks.PRISMARINE, new BlockStateMap.Builder().defaultProperty(PrismarineBlock.VARIANT).build());
		this.putBlock(
			Blocks.LEAVES,
			new BlockStateMap.Builder().defaultProperty(Leaves1Block.VARIANT).suffix("_leaves").ignoreProperties(LeavesBlock.CHECK_DECAY, LeavesBlock.DECAYABLE).build()
		);
		this.putBlock(
			Blocks.LEAVES2,
			new BlockStateMap.Builder().defaultProperty(Leaves2Block.VARIANT).suffix("_leaves").ignoreProperties(LeavesBlock.CHECK_DECAY, LeavesBlock.DECAYABLE).build()
		);
		this.putBlock(Blocks.CACTUS, new BlockStateMap.Builder().ignoreProperties(CactusBlock.AGE).build());
		this.putBlock(Blocks.SUGARCANE, new BlockStateMap.Builder().ignoreProperties(SugarCaneBlock.AGE).build());
		this.putBlock(Blocks.JUKEBOX, new BlockStateMap.Builder().ignoreProperties(JukeboxBlock.HAS_RECORD).build());
		this.putBlock(Blocks.COBBLESTONE_WALL, new BlockStateMap.Builder().defaultProperty(WallBlock.VARIANT).suffix("_wall").build());
		this.putBlock(Blocks.DOUBLE_PLANT, new BlockStateMap.Builder().defaultProperty(DoublePlantBlock.VARIANT).ignoreProperties(DoublePlantBlock.FACING).build());
		this.putBlock(Blocks.OAK_FENCE_GATE, new BlockStateMap.Builder().ignoreProperties(FenceGateBlock.POWERED).build());
		this.putBlock(Blocks.SPRUCE_FENCE_GATE, new BlockStateMap.Builder().ignoreProperties(FenceGateBlock.POWERED).build());
		this.putBlock(Blocks.BIRCH_FENCE_GATE, new BlockStateMap.Builder().ignoreProperties(FenceGateBlock.POWERED).build());
		this.putBlock(Blocks.JUNGLE_FENCE_GATE, new BlockStateMap.Builder().ignoreProperties(FenceGateBlock.POWERED).build());
		this.putBlock(Blocks.DARK_OAK_FENCE_GATE, new BlockStateMap.Builder().ignoreProperties(FenceGateBlock.POWERED).build());
		this.putBlock(Blocks.ACACIA_FENCE_GATE, new BlockStateMap.Builder().ignoreProperties(FenceGateBlock.POWERED).build());
		this.putBlock(Blocks.TRIPWIRE, new BlockStateMap.Builder().ignoreProperties(TripwireBlock.DISARMED, TripwireBlock.POWERED).build());
		this.putBlock(Blocks.DOUBLE_WOODEN_SLAB, new BlockStateMap.Builder().defaultProperty(PlanksBlock.VARIANT).suffix("_double_slab").build());
		this.putBlock(Blocks.WOODEN_SLAB, new BlockStateMap.Builder().defaultProperty(PlanksBlock.VARIANT).suffix("_slab").build());
		this.putBlock(Blocks.TNT, new BlockStateMap.Builder().ignoreProperties(TntBlock.EXPLODE).build());
		this.putBlock(Blocks.FIRE, new BlockStateMap.Builder().ignoreProperties(FireBlock.AGE).build());
		this.putBlock(Blocks.REDSTONE_WIRE, new BlockStateMap.Builder().ignoreProperties(RedstoneWireBlock.POWER).build());
		this.putBlock(Blocks.WOODEN_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.SPRUCE_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.BIRCH_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.JUNGLE_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.ACACIA_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.DARK_OAK_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.IRON_DOOR, new BlockStateMap.Builder().ignoreProperties(DoorBlock.POWERED).build());
		this.putBlock(Blocks.WOOL, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_wool").build());
		this.putBlock(Blocks.CARPET, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_carpet").build());
		this.putBlock(Blocks.STAINED_TERRACOTTA, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_stained_hardened_clay").build());
		this.putBlock(Blocks.STAINED_GLASS_PANE, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_stained_glass_pane").build());
		this.putBlock(Blocks.STAINED_GLASS, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_stained_glass").build());
		this.putBlock(Blocks.SANDSTONE, new BlockStateMap.Builder().defaultProperty(SandstoneBlock.VARIANT).build());
		this.putBlock(Blocks.RED_SANDSTONE, new BlockStateMap.Builder().defaultProperty(RedSandstoneBlock.TYPE).build());
		this.putBlock(Blocks.TALLGRASS, new BlockStateMap.Builder().defaultProperty(TallPlantBlock.TYPE).build());
		this.putBlock(Blocks.YELLOW_FLOWER, new BlockStateMap.Builder().defaultProperty(Blocks.YELLOW_FLOWER.getFlowerProperties()).build());
		this.putBlock(Blocks.RED_FLOWER, new BlockStateMap.Builder().defaultProperty(Blocks.RED_FLOWER.getFlowerProperties()).build());
		this.putBlock(Blocks.STONE_SLAB, new BlockStateMap.Builder().defaultProperty(StoneSlabBlock.VARIANT).suffix("_slab").build());
		this.putBlock(Blocks.STONE_SLAB2, new BlockStateMap.Builder().defaultProperty(RedSandstoneSlabBlock.VARIANT).suffix("_slab").build());
		this.putBlock(Blocks.MONSTER_EGG, new BlockStateMap.Builder().defaultProperty(InfestedBlock.VARIANT).suffix("_monster_egg").build());
		this.putBlock(Blocks.STONE_BRICKS, new BlockStateMap.Builder().defaultProperty(StoneBrickBlock.VARIANT).build());
		this.putBlock(Blocks.DISPENSER, new BlockStateMap.Builder().ignoreProperties(DispenserBlock.TRIGGERED).build());
		this.putBlock(Blocks.DROPPER, new BlockStateMap.Builder().ignoreProperties(DropperBlock.TRIGGERED).build());
		this.putBlock(Blocks.LOG, new BlockStateMap.Builder().defaultProperty(Log1Block.VARIANT).suffix("_log").build());
		this.putBlock(Blocks.LOG2, new BlockStateMap.Builder().defaultProperty(Log2Block.VARIANT).suffix("_log").build());
		this.putBlock(Blocks.PLANKS, new BlockStateMap.Builder().defaultProperty(PlanksBlock.VARIANT).suffix("_planks").build());
		this.putBlock(Blocks.SAPLING, new BlockStateMap.Builder().defaultProperty(SaplingBlock.TYPE).suffix("_sapling").build());
		this.putBlock(Blocks.SAND, new BlockStateMap.Builder().defaultProperty(SandBlock.sandType).build());
		this.putBlock(Blocks.HOPPER, new BlockStateMap.Builder().ignoreProperties(HopperBlock.ENABLED).build());
		this.putBlock(Blocks.FLOWER_POT, new BlockStateMap.Builder().ignoreProperties(FlowerPotBlock.LEGACY_DATA).build());
		this.putBlock(Blocks.CONCRETE, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_concrete").build());
		this.putBlock(Blocks.CONCRETE_POWDER, new BlockStateMap.Builder().defaultProperty(WoolBlock.COLOR).suffix("_concrete_powder").build());
		this.putBlock(Blocks.QUARTZ_BLOCK, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				QuartzBlock.QuartzType quartzType = state.get(QuartzBlock.VARIANT);
				switch (quartzType) {
					case DEFAULT:
					default:
						return new ModelIdentifier("quartz_block", "normal");
					case CHISELED:
						return new ModelIdentifier("chiseled_quartz_block", "normal");
					case LINES_X:
						return new ModelIdentifier("quartz_column", "axis=y");
					case LINES_Y:
						return new ModelIdentifier("quartz_column", "axis=x");
					case LINES_Z:
						return new ModelIdentifier("quartz_column", "axis=z");
				}
			}
		});
		this.putBlock(Blocks.DEADBUSH, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				return new ModelIdentifier("dead_bush", "normal");
			}
		});
		this.putBlock(Blocks.PUMPKIN_STEM, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				Map<Property<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getPropertyMap());
				if (state.get(StemBlock.FACING) != Direction.UP) {
					map.remove(StemBlock.AGE);
				}

				return new ModelIdentifier(Block.REGISTRY.getIdentifier(state.getBlock()), this.getPropertyStateString(map));
			}
		});
		this.putBlock(Blocks.MELON_STEM, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				Map<Property<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getPropertyMap());
				if (state.get(StemBlock.FACING) != Direction.UP) {
					map.remove(StemBlock.AGE);
				}

				return new ModelIdentifier(Block.REGISTRY.getIdentifier(state.getBlock()), this.getPropertyStateString(map));
			}
		});
		this.putBlock(Blocks.DIRT, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				Map<Property<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getPropertyMap());
				String string = DirtBlock.VARIANT.name((DirtBlock.DirtType)map.remove(DirtBlock.VARIANT));
				if (DirtBlock.DirtType.PODZOL != state.get(DirtBlock.VARIANT)) {
					map.remove(DirtBlock.SNOWY);
				}

				return new ModelIdentifier(string, this.getPropertyStateString(map));
			}
		});
		this.putBlock(Blocks.DOUBLE_STONE_SLAB, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				Map<Property<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getPropertyMap());
				String string = StoneSlabBlock.VARIANT.name((StoneSlabBlock.SlabType)map.remove(StoneSlabBlock.VARIANT));
				map.remove(StoneSlabBlock.SEAMLESS);
				String string2 = state.get(StoneSlabBlock.SEAMLESS) ? "all" : "normal";
				return new ModelIdentifier(string + "_double_slab", string2);
			}
		});
		this.putBlock(Blocks.DOUBLE_STONE_SLAB2, new BlockStateIdentifierMap() {
			@Override
			protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
				Map<Property<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getPropertyMap());
				String string = RedSandstoneSlabBlock.VARIANT.name((RedSandstoneSlabBlock.SlabType)map.remove(RedSandstoneSlabBlock.VARIANT));
				map.remove(StoneSlabBlock.SEAMLESS);
				String string2 = state.get(RedSandstoneSlabBlock.SEAMLESS) ? "all" : "normal";
				return new ModelIdentifier(string + "_double_slab", string2);
			}
		});
	}
}
