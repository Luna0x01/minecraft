package net.minecraft.datafixer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.fix.AddTrappedChestFix;
import net.minecraft.datafixer.fix.AdvancementRenameFix;
import net.minecraft.datafixer.fix.AdvancementsFix;
import net.minecraft.datafixer.fix.ArrowPickupFix;
import net.minecraft.datafixer.fix.BedBlockEntityFix;
import net.minecraft.datafixer.fix.BedItemColorFix;
import net.minecraft.datafixer.fix.BeehiveRenameFix;
import net.minecraft.datafixer.fix.BiomeFormatFix;
import net.minecraft.datafixer.fix.BiomeRenameFix;
import net.minecraft.datafixer.fix.BiomesFix;
import net.minecraft.datafixer.fix.BitStorageAlignFix;
import net.minecraft.datafixer.fix.BlockEntityBannerColorFix;
import net.minecraft.datafixer.fix.BlockEntityBlockStateFix;
import net.minecraft.datafixer.fix.BlockEntityCustomNameToTextFix;
import net.minecraft.datafixer.fix.BlockEntityIdFix;
import net.minecraft.datafixer.fix.BlockEntityJukeboxFix;
import net.minecraft.datafixer.fix.BlockEntityKeepPacked;
import net.minecraft.datafixer.fix.BlockEntityShulkerBoxColorFix;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixer.fix.BlockEntityUuidFix;
import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.BlockNameFlatteningFix;
import net.minecraft.datafixer.fix.BlockStateStructureTemplateFix;
import net.minecraft.datafixer.fix.CatTypeFix;
import net.minecraft.datafixer.fix.CauldronRenameFix;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.fix.ChoiceTypesFix;
import net.minecraft.datafixer.fix.ChunkLightRemoveFix;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import net.minecraft.datafixer.fix.ChunkStatusFix;
import net.minecraft.datafixer.fix.ChunkStatusFix2;
import net.minecraft.datafixer.fix.ChunkStructuresTemplateRenameFix;
import net.minecraft.datafixer.fix.ChunkToProtoChunkFix;
import net.minecraft.datafixer.fix.ColorlessShulkerEntityFix;
import net.minecraft.datafixer.fix.EntityArmorStandSilentFix;
import net.minecraft.datafixer.fix.EntityBlockStateFix;
import net.minecraft.datafixer.fix.EntityCatSplitFix;
import net.minecraft.datafixer.fix.EntityCodSalmonFix;
import net.minecraft.datafixer.fix.EntityCustomNameToTextFix;
import net.minecraft.datafixer.fix.EntityElderGuardianSplitFix;
import net.minecraft.datafixer.fix.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixer.fix.EntityHealthFix;
import net.minecraft.datafixer.fix.EntityHorseSaddleFix;
import net.minecraft.datafixer.fix.EntityHorseSplitFix;
import net.minecraft.datafixer.fix.EntityIdFix;
import net.minecraft.datafixer.fix.EntityItemFrameDirectionFix;
import net.minecraft.datafixer.fix.EntityMinecartIdentifiersFix;
import net.minecraft.datafixer.fix.EntityPaintingMotiveFix;
import net.minecraft.datafixer.fix.EntityProjectileOwnerFix;
import net.minecraft.datafixer.fix.EntityPufferfishRenameFix;
import net.minecraft.datafixer.fix.EntityRavagerRenameFix;
import net.minecraft.datafixer.fix.EntityRedundantChanceTagsFix;
import net.minecraft.datafixer.fix.EntityRidingToPassengerFix;
import net.minecraft.datafixer.fix.EntityShulkerColorFix;
import net.minecraft.datafixer.fix.EntityShulkerRotationFix;
import net.minecraft.datafixer.fix.EntitySkeletonSplitFix;
import net.minecraft.datafixer.fix.EntityStringUuidFix;
import net.minecraft.datafixer.fix.EntityTheRenameningBlock;
import net.minecraft.datafixer.fix.EntityTippedArrowFix;
import net.minecraft.datafixer.fix.EntityUuidFix;
import net.minecraft.datafixer.fix.EntityWolfColorFix;
import net.minecraft.datafixer.fix.EntityZombieSplitFix;
import net.minecraft.datafixer.fix.EntityZombieVillagerTypeFix;
import net.minecraft.datafixer.fix.EntityZombifiedPiglinRenameFix;
import net.minecraft.datafixer.fix.FurnaceRecipesFix;
import net.minecraft.datafixer.fix.HangingEntityFix;
import net.minecraft.datafixer.fix.HeightmapRenamingFix;
import net.minecraft.datafixer.fix.IglooMetadataRemovalFix;
import net.minecraft.datafixer.fix.ItemBannerColorFix;
import net.minecraft.datafixer.fix.ItemCustomNameToComponentFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemInstanceMapIdFix;
import net.minecraft.datafixer.fix.ItemInstanceSpawnEggFix;
import net.minecraft.datafixer.fix.ItemInstanceTheFlatteningFix;
import net.minecraft.datafixer.fix.ItemLoreToTextFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.fix.ItemPotionFix;
import net.minecraft.datafixer.fix.ItemShulkerBoxColorFix;
import net.minecraft.datafixer.fix.ItemSpawnEggFix;
import net.minecraft.datafixer.fix.ItemStackEnchantmentFix;
import net.minecraft.datafixer.fix.ItemStackUuidFix;
import net.minecraft.datafixer.fix.ItemWaterPotionFix;
import net.minecraft.datafixer.fix.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixer.fix.JigsawBlockNameFix;
import net.minecraft.datafixer.fix.JigsawPropertiesFix;
import net.minecraft.datafixer.fix.JigsawRotationFix;
import net.minecraft.datafixer.fix.LeavesFix;
import net.minecraft.datafixer.fix.LevelDataGeneratorOptionsFix;
import net.minecraft.datafixer.fix.LevelFlatGeneratorInfoFix;
import net.minecraft.datafixer.fix.MapIdFix;
import net.minecraft.datafixer.fix.MemoryExpiryDataFix;
import net.minecraft.datafixer.fix.MissingDimensionFix;
import net.minecraft.datafixer.fix.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixer.fix.NewVillageFix;
import net.minecraft.datafixer.fix.ObjectiveDisplayNameFix;
import net.minecraft.datafixer.fix.ObjectiveRenderTypeFix;
import net.minecraft.datafixer.fix.OminousBannerBlockEntityRenameFix;
import net.minecraft.datafixer.fix.OminousBannerItemRenameFix;
import net.minecraft.datafixer.fix.OptionFix;
import net.minecraft.datafixer.fix.OptionsAddTextBackgroundFix;
import net.minecraft.datafixer.fix.OptionsForceVBOFix;
import net.minecraft.datafixer.fix.OptionsKeyLwjgl3Fix;
import net.minecraft.datafixer.fix.OptionsKeyTranslationFix;
import net.minecraft.datafixer.fix.OptionsLowerCaseLanguageFix;
import net.minecraft.datafixer.fix.PersistentStateUuidFix;
import net.minecraft.datafixer.fix.PlayerUuidFix;
import net.minecraft.datafixer.fix.PointOfInterestReorganizationFix;
import net.minecraft.datafixer.fix.RecipeFix;
import net.minecraft.datafixer.fix.RecipeRenameFix;
import net.minecraft.datafixer.fix.RecipeRenamingFix;
import net.minecraft.datafixer.fix.RedstoneConnectionsFix;
import net.minecraft.datafixer.fix.RemoveGolemGossipFix;
import net.minecraft.datafixer.fix.RemovePoiValidTagFix;
import net.minecraft.datafixer.fix.RenameItemStackAttributesFix;
import net.minecraft.datafixer.fix.SavedDataVillageCropFix;
import net.minecraft.datafixer.fix.StatsCounterFix;
import net.minecraft.datafixer.fix.StatsRenameFix;
import net.minecraft.datafixer.fix.StriderGravityFix;
import net.minecraft.datafixer.fix.StructureFeatureChildrenPoolElementFix;
import net.minecraft.datafixer.fix.StructureReferenceFix;
import net.minecraft.datafixer.fix.StructureSeparationDataFix;
import net.minecraft.datafixer.fix.TeamDisplayNameFix;
import net.minecraft.datafixer.fix.VillagerFollowRangeFix;
import net.minecraft.datafixer.fix.VillagerGossipFix;
import net.minecraft.datafixer.fix.VillagerProfessionFix;
import net.minecraft.datafixer.fix.VillagerTradeFix;
import net.minecraft.datafixer.fix.VillagerXpRebuildFix;
import net.minecraft.datafixer.fix.WallPropertyFix;
import net.minecraft.datafixer.fix.WorldUuidFix;
import net.minecraft.datafixer.fix.WriteAndReadFix;
import net.minecraft.datafixer.fix.ZombieVillagerXpRebuildFix;
import net.minecraft.datafixer.mapping.LegacyCoralBlockMapping;
import net.minecraft.datafixer.mapping.LegacyCoralFanBlockMapping;
import net.minecraft.datafixer.mapping.LegacyDyeItemMapping;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;
import net.minecraft.datafixer.schema.Schema102;
import net.minecraft.datafixer.schema.Schema1022;
import net.minecraft.datafixer.schema.Schema106;
import net.minecraft.datafixer.schema.Schema107;
import net.minecraft.datafixer.schema.Schema1125;
import net.minecraft.datafixer.schema.Schema135;
import net.minecraft.datafixer.schema.Schema143;
import net.minecraft.datafixer.schema.Schema1451;
import net.minecraft.datafixer.schema.Schema1451v1;
import net.minecraft.datafixer.schema.Schema1451v2;
import net.minecraft.datafixer.schema.Schema1451v3;
import net.minecraft.datafixer.schema.Schema1451v4;
import net.minecraft.datafixer.schema.Schema1451v5;
import net.minecraft.datafixer.schema.Schema1451v6;
import net.minecraft.datafixer.schema.Schema1451v7;
import net.minecraft.datafixer.schema.Schema1460;
import net.minecraft.datafixer.schema.Schema1466;
import net.minecraft.datafixer.schema.Schema1470;
import net.minecraft.datafixer.schema.Schema1481;
import net.minecraft.datafixer.schema.Schema1483;
import net.minecraft.datafixer.schema.Schema1486;
import net.minecraft.datafixer.schema.Schema1510;
import net.minecraft.datafixer.schema.Schema1800;
import net.minecraft.datafixer.schema.Schema1801;
import net.minecraft.datafixer.schema.Schema1904;
import net.minecraft.datafixer.schema.Schema1906;
import net.minecraft.datafixer.schema.Schema1909;
import net.minecraft.datafixer.schema.Schema1920;
import net.minecraft.datafixer.schema.Schema1928;
import net.minecraft.datafixer.schema.Schema1929;
import net.minecraft.datafixer.schema.Schema1931;
import net.minecraft.datafixer.schema.Schema2100;
import net.minecraft.datafixer.schema.Schema2501;
import net.minecraft.datafixer.schema.Schema2502;
import net.minecraft.datafixer.schema.Schema2505;
import net.minecraft.datafixer.schema.Schema2509;
import net.minecraft.datafixer.schema.Schema2519;
import net.minecraft.datafixer.schema.Schema2522;
import net.minecraft.datafixer.schema.Schema2551;
import net.minecraft.datafixer.schema.Schema2568;
import net.minecraft.datafixer.schema.Schema2571;
import net.minecraft.datafixer.schema.Schema2684;
import net.minecraft.datafixer.schema.Schema2686;
import net.minecraft.datafixer.schema.Schema2688;
import net.minecraft.datafixer.schema.Schema2704;
import net.minecraft.datafixer.schema.Schema2707;
import net.minecraft.datafixer.schema.Schema501;
import net.minecraft.datafixer.schema.Schema700;
import net.minecraft.datafixer.schema.Schema701;
import net.minecraft.datafixer.schema.Schema702;
import net.minecraft.datafixer.schema.Schema703;
import net.minecraft.datafixer.schema.Schema704;
import net.minecraft.datafixer.schema.Schema705;
import net.minecraft.datafixer.schema.Schema808;
import net.minecraft.datafixer.schema.Schema99;
import net.minecraft.util.Util;

public class Schemas {
	private static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> EMPTY_IDENTIFIER_NORMALIZE = IdentifierNormalizingSchema::new;
	private static final DataFixer FIXER = create();

	private static DataFixer create() {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(SharedConstants.getGameVersion().getWorldVersion());
		build(dataFixerBuilder);
		return dataFixerBuilder.build(Util.getBootstrapExecutor());
	}

	public static DataFixer getFixer() {
		return FIXER;
	}

	private static void build(DataFixerBuilder builder) {
		Schema schema = builder.addSchema(99, Schema99::new);
		Schema schema2 = builder.addSchema(100, Schema100::new);
		builder.addFixer(new EntityEquipmentToArmorAndHandFix(schema2, true));
		Schema schema3 = builder.addSchema(101, EMPTY);
		builder.addFixer(new BlockEntitySignTextStrictJsonFix(schema3, false));
		Schema schema4 = builder.addSchema(102, Schema102::new);
		builder.addFixer(new ItemIdFix(schema4, true));
		builder.addFixer(new ItemPotionFix(schema4, false));
		Schema schema5 = builder.addSchema(105, EMPTY);
		builder.addFixer(new ItemSpawnEggFix(schema5, true));
		Schema schema6 = builder.addSchema(106, Schema106::new);
		builder.addFixer(new MobSpawnerEntityIdentifiersFix(schema6, true));
		Schema schema7 = builder.addSchema(107, Schema107::new);
		builder.addFixer(new EntityMinecartIdentifiersFix(schema7, true));
		Schema schema8 = builder.addSchema(108, EMPTY);
		builder.addFixer(new EntityStringUuidFix(schema8, true));
		Schema schema9 = builder.addSchema(109, EMPTY);
		builder.addFixer(new EntityHealthFix(schema9, true));
		Schema schema10 = builder.addSchema(110, EMPTY);
		builder.addFixer(new EntityHorseSaddleFix(schema10, true));
		Schema schema11 = builder.addSchema(111, EMPTY);
		builder.addFixer(new HangingEntityFix(schema11, true));
		Schema schema12 = builder.addSchema(113, EMPTY);
		builder.addFixer(new EntityRedundantChanceTagsFix(schema12, true));
		Schema schema13 = builder.addSchema(135, Schema135::new);
		builder.addFixer(new EntityRidingToPassengerFix(schema13, true));
		Schema schema14 = builder.addSchema(143, Schema143::new);
		builder.addFixer(new EntityTippedArrowFix(schema14, true));
		Schema schema15 = builder.addSchema(147, EMPTY);
		builder.addFixer(new EntityArmorStandSilentFix(schema15, true));
		Schema schema16 = builder.addSchema(165, EMPTY);
		builder.addFixer(new ItemWrittenBookPagesStrictJsonFix(schema16, true));
		Schema schema17 = builder.addSchema(501, Schema501::new);
		builder.addFixer(new ChoiceTypesFix(schema17, "Add 1.10 entities fix", TypeReferences.ENTITY));
		Schema schema18 = builder.addSchema(502, EMPTY);
		builder.addFixer(
			ItemNameFix.create(
				schema18,
				"cooked_fished item renamer",
				string -> Objects.equals(IdentifierNormalizingSchema.normalize(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string
			)
		);
		builder.addFixer(new EntityZombieVillagerTypeFix(schema18, false));
		Schema schema19 = builder.addSchema(505, EMPTY);
		builder.addFixer(new OptionsForceVBOFix(schema19, false));
		Schema schema20 = builder.addSchema(700, Schema700::new);
		builder.addFixer(new EntityElderGuardianSplitFix(schema20, true));
		Schema schema21 = builder.addSchema(701, Schema701::new);
		builder.addFixer(new EntitySkeletonSplitFix(schema21, true));
		Schema schema22 = builder.addSchema(702, Schema702::new);
		builder.addFixer(new EntityZombieSplitFix(schema22, true));
		Schema schema23 = builder.addSchema(703, Schema703::new);
		builder.addFixer(new EntityHorseSplitFix(schema23, true));
		Schema schema24 = builder.addSchema(704, Schema704::new);
		builder.addFixer(new BlockEntityIdFix(schema24, true));
		Schema schema25 = builder.addSchema(705, Schema705::new);
		builder.addFixer(new EntityIdFix(schema25, true));
		Schema schema26 = builder.addSchema(804, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemBannerColorFix(schema26, true));
		Schema schema27 = builder.addSchema(806, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemWaterPotionFix(schema27, false));
		Schema schema28 = builder.addSchema(808, Schema808::new);
		builder.addFixer(new ChoiceTypesFix(schema28, "added shulker box", TypeReferences.BLOCK_ENTITY));
		Schema schema29 = builder.addSchema(808, 1, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityShulkerColorFix(schema29, false));
		Schema schema30 = builder.addSchema(813, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemShulkerBoxColorFix(schema30, false));
		builder.addFixer(new BlockEntityShulkerBoxColorFix(schema30, false));
		Schema schema31 = builder.addSchema(816, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsLowerCaseLanguageFix(schema31, false));
		Schema schema32 = builder.addSchema(820, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(ItemNameFix.create(schema32, "totem item renamer", replacing("minecraft:totem", "minecraft:totem_of_undying")));
		Schema schema33 = builder.addSchema(1022, Schema1022::new);
		builder.addFixer(new WriteAndReadFix(schema33, "added shoulder entities to players", TypeReferences.PLAYER));
		Schema schema34 = builder.addSchema(1125, Schema1125::new);
		builder.addFixer(new BedBlockEntityFix(schema34, true));
		builder.addFixer(new BedItemColorFix(schema34, false));
		Schema schema35 = builder.addSchema(1344, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsKeyLwjgl3Fix(schema35, false));
		Schema schema36 = builder.addSchema(1446, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsKeyTranslationFix(schema36, false));
		Schema schema37 = builder.addSchema(1450, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlockStateStructureTemplateFix(schema37, false));
		Schema schema38 = builder.addSchema(1451, Schema1451::new);
		builder.addFixer(new ChoiceTypesFix(schema38, "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY));
		Schema schema39 = builder.addSchema(1451, 1, Schema1451v1::new);
		builder.addFixer(new ChunkPalettedStorageFix(schema39, true));
		Schema schema40 = builder.addSchema(1451, 2, Schema1451v2::new);
		builder.addFixer(new BlockEntityBlockStateFix(schema40, true));
		Schema schema41 = builder.addSchema(1451, 3, Schema1451v3::new);
		builder.addFixer(new EntityBlockStateFix(schema41, true));
		builder.addFixer(new ItemInstanceMapIdFix(schema41, false));
		Schema schema42 = builder.addSchema(1451, 4, Schema1451v4::new);
		builder.addFixer(new BlockNameFlatteningFix(schema42, true));
		builder.addFixer(new ItemInstanceTheFlatteningFix(schema42, false));
		Schema schema43 = builder.addSchema(1451, 5, Schema1451v5::new);
		builder.addFixer(new ChoiceTypesFix(schema43, "RemoveNoteBlockFlowerPotFix", TypeReferences.BLOCK_ENTITY));
		builder.addFixer(new ItemInstanceSpawnEggFix(schema43, false));
		builder.addFixer(new EntityWolfColorFix(schema43, false));
		builder.addFixer(new BlockEntityBannerColorFix(schema43, false));
		builder.addFixer(new LevelFlatGeneratorInfoFix(schema43, false));
		Schema schema44 = builder.addSchema(1451, 6, Schema1451v6::new);
		builder.addFixer(new StatsCounterFix(schema44, true));
		builder.addFixer(new WriteAndReadFix(schema44, "Rewrite objectives", TypeReferences.OBJECTIVE));
		builder.addFixer(new BlockEntityJukeboxFix(schema44, false));
		Schema schema45 = builder.addSchema(1451, 7, Schema1451v7::new);
		builder.addFixer(new SavedDataVillageCropFix(schema45, true));
		Schema schema46 = builder.addSchema(1451, 7, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerTradeFix(schema46, false));
		Schema schema47 = builder.addSchema(1456, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityItemFrameDirectionFix(schema47, false));
		Schema schema48 = builder.addSchema(1458, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityCustomNameToTextFix(schema48, false));
		builder.addFixer(new ItemCustomNameToComponentFix(schema48, false));
		builder.addFixer(new BlockEntityCustomNameToTextFix(schema48, false));
		Schema schema49 = builder.addSchema(1460, Schema1460::new);
		builder.addFixer(new EntityPaintingMotiveFix(schema49, false));
		Schema schema50 = builder.addSchema(1466, Schema1466::new);
		builder.addFixer(new ChunkToProtoChunkFix(schema50, true));
		Schema schema51 = builder.addSchema(1470, Schema1470::new);
		builder.addFixer(new ChoiceTypesFix(schema51, "Add 1.13 entities fix", TypeReferences.ENTITY));
		Schema schema52 = builder.addSchema(1474, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ColorlessShulkerEntityFix(schema52, false));
		builder.addFixer(
			BlockNameFix.create(
				schema52,
				"Colorless shulker block fixer",
				string -> Objects.equals(IdentifierNormalizingSchema.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		builder.addFixer(
			ItemNameFix.create(
				schema52,
				"Colorless shulker item fixer",
				string -> Objects.equals(IdentifierNormalizingSchema.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		Schema schema53 = builder.addSchema(1475, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema53, "Flowing fixer", replacing(ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava"))
			)
		);
		Schema schema54 = builder.addSchema(1480, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema54, "Rename coral blocks", replacing(LegacyCoralBlockMapping.MAP)));
		builder.addFixer(ItemNameFix.create(schema54, "Rename coral items", replacing(LegacyCoralBlockMapping.MAP)));
		Schema schema55 = builder.addSchema(1481, Schema1481::new);
		builder.addFixer(new ChoiceTypesFix(schema55, "Add conduit", TypeReferences.BLOCK_ENTITY));
		Schema schema56 = builder.addSchema(1483, Schema1483::new);
		builder.addFixer(new EntityPufferfishRenameFix(schema56, true));
		builder.addFixer(ItemNameFix.create(schema56, "Rename pufferfish egg item", replacing(EntityPufferfishRenameFix.RENAMED_FISH)));
		Schema schema57 = builder.addSchema(1484, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema57,
				"Rename seagrass items",
				replacing(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema57,
				"Rename seagrass blocks",
				replacing(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
			)
		);
		builder.addFixer(new HeightmapRenamingFix(schema57, false));
		Schema schema58 = builder.addSchema(1486, Schema1486::new);
		builder.addFixer(new EntityCodSalmonFix(schema58, true));
		builder.addFixer(ItemNameFix.create(schema58, "Rename cod/salmon egg items", replacing(EntityCodSalmonFix.SPAWN_EGGS)));
		Schema schema59 = builder.addSchema(1487, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* blocks",
				replacing(
					ImmutableMap.of(
						"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
					)
				)
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* items",
				replacing(
					ImmutableMap.of(
						"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
					)
				)
			)
		);
		Schema schema60 = builder.addSchema(1488, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema60, "Rename kelp/kelptop", replacing(ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant"))
			)
		);
		builder.addFixer(ItemNameFix.create(schema60, "Rename kelptop", replacing("minecraft:kelp_top", "minecraft:kelp")));
		builder.addFixer(new ChoiceFix(schema60, false, "Command block block entity custom name fix", TypeReferences.BLOCK_ENTITY, "minecraft:command_block") {
			@Override
			protected Typed<?> transform(Typed<?> inputType) {
				return inputType.update(DSL.remainderFinder(), EntityCustomNameToTextFix::fixCustomName);
			}
		});
		builder.addFixer(new ChoiceFix(schema60, false, "Command block minecart custom name fix", TypeReferences.ENTITY, "minecraft:commandblock_minecart") {
			@Override
			protected Typed<?> transform(Typed<?> inputType) {
				return inputType.update(DSL.remainderFinder(), EntityCustomNameToTextFix::fixCustomName);
			}
		});
		builder.addFixer(new IglooMetadataRemovalFix(schema60, false));
		Schema schema61 = builder.addSchema(1490, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema61, "Rename melon_block", replacing("minecraft:melon_block", "minecraft:melon")));
		builder.addFixer(
			ItemNameFix.create(
				schema61,
				"Rename melon_block/melon/speckled_melon",
				replacing(
					ImmutableMap.of(
						"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
					)
				)
			)
		);
		Schema schema62 = builder.addSchema(1492, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStructuresTemplateRenameFix(schema62, false));
		Schema schema63 = builder.addSchema(1494, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemStackEnchantmentFix(schema63, false));
		Schema schema64 = builder.addSchema(1496, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LeavesFix(schema64, false));
		Schema schema65 = builder.addSchema(1500, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlockEntityKeepPacked(schema65, false));
		Schema schema66 = builder.addSchema(1501, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AdvancementsFix(schema66, false));
		Schema schema67 = builder.addSchema(1502, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RecipeFix(schema67, false));
		Schema schema68 = builder.addSchema(1506, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LevelDataGeneratorOptionsFix(schema68, false));
		Schema schema69 = builder.addSchema(1510, Schema1510::new);
		builder.addFixer(BlockNameFix.create(schema69, "Block renamening fix", replacing(EntityTheRenameningBlock.BLOCKS)));
		builder.addFixer(ItemNameFix.create(schema69, "Item renamening fix", replacing(EntityTheRenameningBlock.ITEMS)));
		builder.addFixer(new RecipeRenamingFix(schema69, false));
		builder.addFixer(new EntityTheRenameningBlock(schema69, true));
		builder.addFixer(
			new StatsRenameFix(
				schema69,
				"SwimStatsRenameFix",
				ImmutableMap.of("minecraft:swim_one_cm", "minecraft:walk_on_water_one_cm", "minecraft:dive_one_cm", "minecraft:walk_under_water_one_cm")
			)
		);
		Schema schema70 = builder.addSchema(1514, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ObjectiveDisplayNameFix(schema70, false));
		builder.addFixer(new TeamDisplayNameFix(schema70, false));
		builder.addFixer(new ObjectiveRenderTypeFix(schema70, false));
		Schema schema71 = builder.addSchema(1515, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema71, "Rename coral fan blocks", replacing(LegacyCoralFanBlockMapping.MAP)));
		Schema schema72 = builder.addSchema(1624, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AddTrappedChestFix(schema72, false));
		Schema schema73 = builder.addSchema(1800, Schema1800::new);
		builder.addFixer(new ChoiceTypesFix(schema73, "Added 1.14 mobs fix", TypeReferences.ENTITY));
		builder.addFixer(ItemNameFix.create(schema73, "Rename dye items", replacing(LegacyDyeItemMapping.MAP)));
		Schema schema74 = builder.addSchema(1801, Schema1801::new);
		builder.addFixer(new ChoiceTypesFix(schema74, "Added Illager Beast", TypeReferences.ENTITY));
		Schema schema75 = builder.addSchema(1802, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema75,
				"Rename sign blocks & stone slabs",
				replacing(
					ImmutableMap.of(
						"minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"
					)
				)
			)
		);
		builder.addFixer(
			ItemNameFix.create(
				schema75,
				"Rename sign item & stone slabs",
				replacing(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign"))
			)
		);
		Schema schema76 = builder.addSchema(1803, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemLoreToTextFix(schema76, false));
		Schema schema77 = builder.addSchema(1904, Schema1904::new);
		builder.addFixer(new ChoiceTypesFix(schema77, "Added Cats", TypeReferences.ENTITY));
		builder.addFixer(new EntityCatSplitFix(schema77, false));
		Schema schema78 = builder.addSchema(1905, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStatusFix(schema78, false));
		Schema schema79 = builder.addSchema(1906, Schema1906::new);
		builder.addFixer(new ChoiceTypesFix(schema79, "Add POI Blocks", TypeReferences.BLOCK_ENTITY));
		Schema schema80 = builder.addSchema(1909, Schema1909::new);
		builder.addFixer(new ChoiceTypesFix(schema80, "Add jigsaw", TypeReferences.BLOCK_ENTITY));
		Schema schema81 = builder.addSchema(1911, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStatusFix2(schema81, false));
		Schema schema82 = builder.addSchema(1917, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new CatTypeFix(schema82, false));
		Schema schema83 = builder.addSchema(1918, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerProfessionFix(schema83, "minecraft:villager"));
		builder.addFixer(new VillagerProfessionFix(schema83, "minecraft:zombie_villager"));
		Schema schema84 = builder.addSchema(1920, Schema1920::new);
		builder.addFixer(new NewVillageFix(schema84, false));
		builder.addFixer(new ChoiceTypesFix(schema84, "Add campfire", TypeReferences.BLOCK_ENTITY));
		Schema schema85 = builder.addSchema(1925, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new MapIdFix(schema85, false));
		Schema schema86 = builder.addSchema(1928, Schema1928::new);
		builder.addFixer(new EntityRavagerRenameFix(schema86, true));
		builder.addFixer(ItemNameFix.create(schema86, "Rename ravager egg item", replacing(EntityRavagerRenameFix.ITEMS)));
		Schema schema87 = builder.addSchema(1929, Schema1929::new);
		builder.addFixer(new ChoiceTypesFix(schema87, "Add Wandering Trader and Trader Llama", TypeReferences.ENTITY));
		Schema schema88 = builder.addSchema(1931, Schema1931::new);
		builder.addFixer(new ChoiceTypesFix(schema88, "Added Fox", TypeReferences.ENTITY));
		Schema schema89 = builder.addSchema(1936, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsAddTextBackgroundFix(schema89, false));
		Schema schema90 = builder.addSchema(1946, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new PointOfInterestReorganizationFix(schema90, false));
		Schema schema91 = builder.addSchema(1948, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OminousBannerItemRenameFix(schema91, false));
		Schema schema92 = builder.addSchema(1953, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OminousBannerBlockEntityRenameFix(schema92, false));
		Schema schema93 = builder.addSchema(1955, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerXpRebuildFix(schema93, false));
		builder.addFixer(new ZombieVillagerXpRebuildFix(schema93, false));
		Schema schema94 = builder.addSchema(1961, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkLightRemoveFix(schema94, false));
		Schema schema95 = builder.addSchema(1963, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RemoveGolemGossipFix(schema95, false));
		Schema schema96 = builder.addSchema(2100, Schema2100::new);
		builder.addFixer(new ChoiceTypesFix(schema96, "Added Bee and Bee Stinger", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema96, "Add beehive", TypeReferences.BLOCK_ENTITY));
		builder.addFixer(new RecipeRenameFix(schema96, false, "Rename sugar recipe", replacing("minecraft:sugar", "sugar_from_sugar_cane")));
		builder.addFixer(
			new AdvancementRenameFix(
				schema96, false, "Rename sugar recipe advancement", replacing("minecraft:recipes/misc/sugar", "minecraft:recipes/misc/sugar_from_sugar_cane")
			)
		);
		Schema schema97 = builder.addSchema(2202, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BiomeFormatFix(schema97, false));
		Schema schema98 = builder.addSchema(2209, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(ItemNameFix.create(schema98, "Rename bee_hive item to beehive", replacing("minecraft:bee_hive", "minecraft:beehive")));
		builder.addFixer(new BeehiveRenameFix(schema98));
		builder.addFixer(BlockNameFix.create(schema98, "Rename bee_hive block to beehive", replacing("minecraft:bee_hive", "minecraft:beehive")));
		Schema schema99 = builder.addSchema(2211, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureReferenceFix(schema99, false));
		Schema schema100 = builder.addSchema(2218, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RemovePoiValidTagFix(schema100, false));
		Schema schema101 = builder.addSchema(2501, Schema2501::new);
		builder.addFixer(new FurnaceRecipesFix(schema101, true));
		Schema schema102 = builder.addSchema(2502, Schema2502::new);
		builder.addFixer(new ChoiceTypesFix(schema102, "Added Hoglin", TypeReferences.ENTITY));
		Schema schema103 = builder.addSchema(2503, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new WallPropertyFix(schema103, false));
		builder.addFixer(
			new AdvancementRenameFix(
				schema103, false, "Composter category change", replacing("minecraft:recipes/misc/composter", "minecraft:recipes/decorations/composter")
			)
		);
		Schema schema104 = builder.addSchema(2505, Schema2505::new);
		builder.addFixer(new ChoiceTypesFix(schema104, "Added Piglin", TypeReferences.ENTITY));
		builder.addFixer(new MemoryExpiryDataFix(schema104, "minecraft:villager"));
		Schema schema105 = builder.addSchema(2508, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema105,
				"Renamed fungi items to fungus",
				replacing(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema105,
				"Renamed fungi blocks to fungus",
				replacing(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
			)
		);
		Schema schema106 = builder.addSchema(2509, Schema2509::new);
		builder.addFixer(new EntityZombifiedPiglinRenameFix(schema106));
		builder.addFixer(ItemNameFix.create(schema106, "Rename zombie pigman egg item", replacing(EntityZombifiedPiglinRenameFix.RENAMES)));
		Schema schema107 = builder.addSchema(2511, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityProjectileOwnerFix(schema107));
		Schema schema108 = builder.addSchema(2514, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityUuidFix(schema108));
		builder.addFixer(new BlockEntityUuidFix(schema108));
		builder.addFixer(new PlayerUuidFix(schema108));
		builder.addFixer(new WorldUuidFix(schema108));
		builder.addFixer(new PersistentStateUuidFix(schema108));
		builder.addFixer(new ItemStackUuidFix(schema108));
		Schema schema109 = builder.addSchema(2516, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerGossipFix(schema109, "minecraft:villager"));
		builder.addFixer(new VillagerGossipFix(schema109, "minecraft:zombie_villager"));
		Schema schema110 = builder.addSchema(2518, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new JigsawPropertiesFix(schema110, false));
		builder.addFixer(new JigsawRotationFix(schema110, false));
		Schema schema111 = builder.addSchema(2519, Schema2519::new);
		builder.addFixer(new ChoiceTypesFix(schema111, "Added Strider", TypeReferences.ENTITY));
		Schema schema112 = builder.addSchema(2522, Schema2522::new);
		builder.addFixer(new ChoiceTypesFix(schema112, "Added Zoglin", TypeReferences.ENTITY));
		Schema schema113 = builder.addSchema(2523, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RenameItemStackAttributesFix(schema113));
		Schema schema114 = builder.addSchema(2527, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BitStorageAlignFix(schema114));
		Schema schema115 = builder.addSchema(2528, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema115,
				"Rename soul fire torch and soul fire lantern",
				replacing(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema115,
				"Rename soul fire torch and soul fire lantern",
				replacing(
					ImmutableMap.of(
						"minecraft:soul_fire_torch",
						"minecraft:soul_torch",
						"minecraft:soul_fire_wall_torch",
						"minecraft:soul_wall_torch",
						"minecraft:soul_fire_lantern",
						"minecraft:soul_lantern"
					)
				)
			)
		);
		Schema schema116 = builder.addSchema(2529, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StriderGravityFix(schema116, false));
		Schema schema117 = builder.addSchema(2531, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RedstoneConnectionsFix(schema117));
		Schema schema118 = builder.addSchema(2533, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerFollowRangeFix(schema118));
		Schema schema119 = builder.addSchema(2535, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityShulkerRotationFix(schema119));
		Schema schema120 = builder.addSchema(2550, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureSeparationDataFix(schema120));
		Schema schema121 = builder.addSchema(2551, Schema2551::new);
		builder.addFixer(new WriteAndReadFix(schema121, "add types to WorldGenData", TypeReferences.CHUNK_GENERATOR_SETTINGS));
		Schema schema122 = builder.addSchema(2552, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BiomeRenameFix(schema122, false, "Nether biome rename", ImmutableMap.of("minecraft:nether", "minecraft:nether_wastes")));
		Schema schema123 = builder.addSchema(2553, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BiomesFix(schema123, false));
		Schema schema124 = builder.addSchema(2558, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new MissingDimensionFix(schema124, false));
		builder.addFixer(new OptionFix(schema124, false, "Rename swapHands setting", "key_key.swapHands", "key_key.swapOffhand"));
		Schema schema125 = builder.addSchema(2568, Schema2568::new);
		builder.addFixer(new ChoiceTypesFix(schema125, "Added Piglin Brute", TypeReferences.ENTITY));
		Schema schema126 = builder.addSchema(2571, Schema2571::new);
		builder.addFixer(new ChoiceTypesFix(schema126, "Added Goat", TypeReferences.ENTITY));
		Schema schema127 = builder.addSchema(2679, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new CauldronRenameFix(schema127, false));
		Schema schema128 = builder.addSchema(2680, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(ItemNameFix.create(schema128, "Renamed grass path item to dirt path", replacing("minecraft:grass_path", "minecraft:dirt_path")));
		builder.addFixer(JigsawBlockNameFix.create(schema128, "Renamed grass path block to dirt path", replacing("minecraft:grass_path", "minecraft:dirt_path")));
		Schema schema129 = builder.addSchema(2684, Schema2684::new);
		builder.addFixer(new ChoiceTypesFix(schema129, "Added Sculk Sensor", TypeReferences.BLOCK_ENTITY));
		Schema schema130 = builder.addSchema(2686, Schema2686::new);
		builder.addFixer(new ChoiceTypesFix(schema130, "Added Axolotl", TypeReferences.ENTITY));
		Schema schema131 = builder.addSchema(2688, Schema2688::new);
		builder.addFixer(new ChoiceTypesFix(schema131, "Added Glow Squid", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema131, "Added Glow Item Frame", TypeReferences.ENTITY));
		Schema schema132 = builder.addSchema(2690, EMPTY_IDENTIFIER_NORMALIZE);
		ImmutableMap<String, String> immutableMap = ImmutableMap.builder()
			.put("minecraft:weathered_copper_block", "minecraft:oxidized_copper_block")
			.put("minecraft:semi_weathered_copper_block", "minecraft:weathered_copper_block")
			.put("minecraft:lightly_weathered_copper_block", "minecraft:exposed_copper_block")
			.put("minecraft:weathered_cut_copper", "minecraft:oxidized_cut_copper")
			.put("minecraft:semi_weathered_cut_copper", "minecraft:weathered_cut_copper")
			.put("minecraft:lightly_weathered_cut_copper", "minecraft:exposed_cut_copper")
			.put("minecraft:weathered_cut_copper_stairs", "minecraft:oxidized_cut_copper_stairs")
			.put("minecraft:semi_weathered_cut_copper_stairs", "minecraft:weathered_cut_copper_stairs")
			.put("minecraft:lightly_weathered_cut_copper_stairs", "minecraft:exposed_cut_copper_stairs")
			.put("minecraft:weathered_cut_copper_slab", "minecraft:oxidized_cut_copper_slab")
			.put("minecraft:semi_weathered_cut_copper_slab", "minecraft:weathered_cut_copper_slab")
			.put("minecraft:lightly_weathered_cut_copper_slab", "minecraft:exposed_cut_copper_slab")
			.put("minecraft:waxed_semi_weathered_copper", "minecraft:waxed_weathered_copper")
			.put("minecraft:waxed_lightly_weathered_copper", "minecraft:waxed_exposed_copper")
			.put("minecraft:waxed_semi_weathered_cut_copper", "minecraft:waxed_weathered_cut_copper")
			.put("minecraft:waxed_lightly_weathered_cut_copper", "minecraft:waxed_exposed_cut_copper")
			.put("minecraft:waxed_semi_weathered_cut_copper_stairs", "minecraft:waxed_weathered_cut_copper_stairs")
			.put("minecraft:waxed_lightly_weathered_cut_copper_stairs", "minecraft:waxed_exposed_cut_copper_stairs")
			.put("minecraft:waxed_semi_weathered_cut_copper_slab", "minecraft:waxed_weathered_cut_copper_slab")
			.put("minecraft:waxed_lightly_weathered_cut_copper_slab", "minecraft:waxed_exposed_cut_copper_slab")
			.build();
		builder.addFixer(ItemNameFix.create(schema132, "Renamed copper block items to new oxidized terms", replacing(immutableMap)));
		builder.addFixer(JigsawBlockNameFix.create(schema132, "Renamed copper blocks to new oxidized terms", replacing(immutableMap)));
		Schema schema133 = builder.addSchema(2691, EMPTY_IDENTIFIER_NORMALIZE);
		ImmutableMap<String, String> immutableMap2 = ImmutableMap.builder()
			.put("minecraft:waxed_copper", "minecraft:waxed_copper_block")
			.put("minecraft:oxidized_copper_block", "minecraft:oxidized_copper")
			.put("minecraft:weathered_copper_block", "minecraft:weathered_copper")
			.put("minecraft:exposed_copper_block", "minecraft:exposed_copper")
			.build();
		builder.addFixer(ItemNameFix.create(schema133, "Rename copper item suffixes", replacing(immutableMap2)));
		builder.addFixer(JigsawBlockNameFix.create(schema133, "Rename copper blocks suffixes", replacing(immutableMap2)));
		Schema schema134 = builder.addSchema(2696, EMPTY_IDENTIFIER_NORMALIZE);
		ImmutableMap<String, String> immutableMap3 = ImmutableMap.builder()
			.put("minecraft:grimstone", "minecraft:deepslate")
			.put("minecraft:grimstone_slab", "minecraft:cobbled_deepslate_slab")
			.put("minecraft:grimstone_stairs", "minecraft:cobbled_deepslate_stairs")
			.put("minecraft:grimstone_wall", "minecraft:cobbled_deepslate_wall")
			.put("minecraft:polished_grimstone", "minecraft:polished_deepslate")
			.put("minecraft:polished_grimstone_slab", "minecraft:polished_deepslate_slab")
			.put("minecraft:polished_grimstone_stairs", "minecraft:polished_deepslate_stairs")
			.put("minecraft:polished_grimstone_wall", "minecraft:polished_deepslate_wall")
			.put("minecraft:grimstone_tiles", "minecraft:deepslate_tiles")
			.put("minecraft:grimstone_tile_slab", "minecraft:deepslate_tile_slab")
			.put("minecraft:grimstone_tile_stairs", "minecraft:deepslate_tile_stairs")
			.put("minecraft:grimstone_tile_wall", "minecraft:deepslate_tile_wall")
			.put("minecraft:grimstone_bricks", "minecraft:deepslate_bricks")
			.put("minecraft:grimstone_brick_slab", "minecraft:deepslate_brick_slab")
			.put("minecraft:grimstone_brick_stairs", "minecraft:deepslate_brick_stairs")
			.put("minecraft:grimstone_brick_wall", "minecraft:deepslate_brick_wall")
			.put("minecraft:chiseled_grimstone", "minecraft:chiseled_deepslate")
			.build();
		builder.addFixer(ItemNameFix.create(schema134, "Renamed grimstone block items to deepslate", replacing(immutableMap3)));
		builder.addFixer(JigsawBlockNameFix.create(schema134, "Renamed grimstone blocks to deepslate", replacing(immutableMap3)));
		Schema schema135 = builder.addSchema(2700, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			JigsawBlockNameFix.create(
				schema135,
				"Renamed cave vines blocks",
				replacing(ImmutableMap.of("minecraft:cave_vines_head", "minecraft:cave_vines", "minecraft:cave_vines_body", "minecraft:cave_vines_plant"))
			)
		);
		Schema schema136 = builder.addSchema(2701, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureFeatureChildrenPoolElementFix(schema136));
		Schema schema137 = builder.addSchema(2702, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ArrowPickupFix(schema137));
		Schema schema138 = builder.addSchema(2704, Schema2704::new);
		builder.addFixer(new ChoiceTypesFix(schema138, "Added Goat", TypeReferences.ENTITY));
		Schema schema139 = builder.addSchema(2707, Schema2707::new);
		builder.addFixer(new ChoiceTypesFix(schema139, "Added Marker", TypeReferences.ENTITY));
		Schema schema140 = builder.addSchema(2710, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new StatsRenameFix(schema140, "Renamed play_one_minute stat to play_time", ImmutableMap.of("minecraft:play_one_minute", "minecraft:play_time"))
		);
		Schema schema141 = builder.addSchema(2717, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema141, "Rename azalea_leaves_flowers", replacing(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema141, "Rename azalea_leaves_flowers items", replacing(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
			)
		);
	}

	private static UnaryOperator<String> replacing(Map<String, String> replacements) {
		return string -> (String)replacements.getOrDefault(string, string);
	}

	private static UnaryOperator<String> replacing(String old, String current) {
		return string3 -> Objects.equals(string3, old) ? current : string3;
	}
}
