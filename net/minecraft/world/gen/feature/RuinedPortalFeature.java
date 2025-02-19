package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class RuinedPortalFeature extends StructureFeature<RuinedPortalFeatureConfig> {
	static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{
		"ruined_portal/portal_1",
		"ruined_portal/portal_2",
		"ruined_portal/portal_3",
		"ruined_portal/portal_4",
		"ruined_portal/portal_5",
		"ruined_portal/portal_6",
		"ruined_portal/portal_7",
		"ruined_portal/portal_8",
		"ruined_portal/portal_9",
		"ruined_portal/portal_10"
	};
	static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
	private static final float field_31512 = 0.05F;
	private static final float field_31513 = 0.5F;
	private static final float field_31514 = 0.5F;
	private static final float field_31508 = 0.8F;
	private static final float field_31509 = 0.8F;
	private static final float field_31510 = 0.5F;
	private static final int field_31511 = 15;

	public RuinedPortalFeature(Codec<RuinedPortalFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<RuinedPortalFeatureConfig> getStructureStartFactory() {
		return RuinedPortalFeature.Start::new;
	}

	static boolean isColdAt(BlockPos pos, Biome biome) {
		return biome.getTemperature(pos) < 0.15F;
	}

	static int getFloorHeight(
		Random random,
		ChunkGenerator chunkGenerator,
		RuinedPortalStructurePiece.VerticalPlacement verticalPlacement,
		boolean airPocket,
		int height,
		int blockCountY,
		BlockBox box,
		HeightLimitView world
	) {
		int i;
		if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
			if (airPocket) {
				i = MathHelper.nextBetween(random, 32, 100);
			} else if (random.nextFloat() < 0.5F) {
				i = MathHelper.nextBetween(random, 27, 29);
			} else {
				i = MathHelper.nextBetween(random, 29, 100);
			}
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
			int l = height - blockCountY;
			i = choosePlacementHeight(random, 70, l);
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
			int n = height - blockCountY;
			i = choosePlacementHeight(random, 15, n);
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED) {
			i = height - blockCountY + MathHelper.nextBetween(random, 2, 8);
		} else {
			i = height;
		}

		List<BlockPos> list = ImmutableList.of(
			new BlockPos(box.getMinX(), 0, box.getMinZ()),
			new BlockPos(box.getMaxX(), 0, box.getMinZ()),
			new BlockPos(box.getMinX(), 0, box.getMaxZ()),
			new BlockPos(box.getMaxX(), 0, box.getMaxZ())
		);
		List<VerticalBlockSample> list2 = (List<VerticalBlockSample>)list.stream()
			.map(blockPos -> chunkGenerator.getColumnSample(blockPos.getX(), blockPos.getZ(), world))
			.collect(Collectors.toList());
		Heightmap.Type type = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR
			? Heightmap.Type.OCEAN_FLOOR_WG
			: Heightmap.Type.WORLD_SURFACE_WG;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		int r;
		for (r = i; r > 15; r--) {
			int s = 0;
			mutable.set(0, r, 0);

			for (VerticalBlockSample verticalBlockSample : list2) {
				BlockState blockState = verticalBlockSample.getState(mutable);
				if (type.getBlockPredicate().test(blockState)) {
					if (++s == 3) {
						return r;
					}
				}
			}
		}

		return r;
	}

	private static int choosePlacementHeight(Random random, int min, int max) {
		return min < max ? MathHelper.nextBetween(random, min, max) : max;
	}

	public static class Start extends StructureStart<RuinedPortalFeatureConfig> {
		protected Start(StructureFeature<RuinedPortalFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			RuinedPortalFeatureConfig ruinedPortalFeatureConfig,
			HeightLimitView heightLimitView
		) {
			RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
			RuinedPortalStructurePiece.VerticalPlacement verticalPlacement;
			if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.DESERT) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED;
				properties.airPocket = false;
				properties.mossiness = 0.0F;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.JUNGLE) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
				properties.airPocket = this.random.nextFloat() < 0.5F;
				properties.mossiness = 0.8F;
				properties.overgrown = true;
				properties.vines = true;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.SWAMP) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
				properties.airPocket = false;
				properties.mossiness = 0.5F;
				properties.vines = true;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.MOUNTAIN) {
				boolean bl = this.random.nextFloat() < 0.5F;
				verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
				properties.airPocket = bl || this.random.nextFloat() < 0.5F;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.OCEAN) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
				properties.airPocket = false;
				properties.mossiness = 0.8F;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.NETHER) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER;
				properties.airPocket = this.random.nextFloat() < 0.5F;
				properties.mossiness = 0.0F;
				properties.replaceWithBlackstone = true;
			} else {
				boolean bl2 = this.random.nextFloat() < 0.5F;
				verticalPlacement = bl2 ? RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
				properties.airPocket = bl2 || this.random.nextFloat() < 0.5F;
			}

			Identifier identifier;
			if (this.random.nextFloat() < 0.05F) {
				identifier = new Identifier(RuinedPortalFeature.RARE_PORTAL_STRUCTURE_IDS[this.random.nextInt(RuinedPortalFeature.RARE_PORTAL_STRUCTURE_IDS.length)]);
			} else {
				identifier = new Identifier(RuinedPortalFeature.COMMON_PORTAL_STRUCTURE_IDS[this.random.nextInt(RuinedPortalFeature.COMMON_PORTAL_STRUCTURE_IDS.length)]);
			}

			Structure structure = structureManager.getStructureOrBlank(identifier);
			BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), this.random);
			BlockMirror blockMirror = this.random.nextFloat() < 0.5F ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
			BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
			BlockPos blockPos2 = chunkPos.getStartPos();
			BlockBox blockBox = structure.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
			BlockPos blockPos3 = blockBox.getCenter();
			int i = blockPos3.getX();
			int j = blockPos3.getZ();
			int k = chunkGenerator.getHeight(i, j, RuinedPortalStructurePiece.getHeightmapType(verticalPlacement), heightLimitView) - 1;
			int l = RuinedPortalFeature.getFloorHeight(
				this.random, chunkGenerator, verticalPlacement, properties.airPocket, k, blockBox.getBlockCountY(), blockBox, heightLimitView
			);
			BlockPos blockPos4 = new BlockPos(blockPos2.getX(), l, blockPos2.getZ());
			if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.MOUNTAIN
				|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.OCEAN
				|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.STANDARD) {
				properties.cold = RuinedPortalFeature.isColdAt(blockPos4, biome);
			}

			this.addPiece(
				new RuinedPortalStructurePiece(structureManager, blockPos4, verticalPlacement, properties, identifier, structure, blockRotation, blockMirror, blockPos)
			);
		}
	}

	public static enum Type implements StringIdentifiable {
		STANDARD("standard"),
		DESERT("desert"),
		JUNGLE("jungle"),
		SWAMP("swamp"),
		MOUNTAIN("mountain"),
		OCEAN("ocean"),
		NETHER("nether");

		public static final Codec<RuinedPortalFeature.Type> CODEC = StringIdentifiable.createCodec(RuinedPortalFeature.Type::values, RuinedPortalFeature.Type::byName);
		private static final Map<String, RuinedPortalFeature.Type> BY_NAME = (Map<String, RuinedPortalFeature.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(RuinedPortalFeature.Type::getName, type -> type));
		private final String name;

		private Type(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static RuinedPortalFeature.Type byName(String name) {
			return (RuinedPortalFeature.Type)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
