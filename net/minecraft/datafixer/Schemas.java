package net.minecraft.datafixer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.function.BiFunction;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.fix.AddTrappedChestFix;
import net.minecraft.datafixer.fix.AdvancementRenameFix;
import net.minecraft.datafixer.fix.AdvancementsFix;
import net.minecraft.datafixer.fix.BedBlockEntityFix;
import net.minecraft.datafixer.fix.BedItemColorFix;
import net.minecraft.datafixer.fix.BeehiveRenameFix;
import net.minecraft.datafixer.fix.BiomeFormatFix;
import net.minecraft.datafixer.fix.BiomesFix;
import net.minecraft.datafixer.fix.BlockEntityBannerColorFix;
import net.minecraft.datafixer.fix.BlockEntityBlockStateFix;
import net.minecraft.datafixer.fix.BlockEntityCustomNameToComponentFix;
import net.minecraft.datafixer.fix.BlockEntityIdFix;
import net.minecraft.datafixer.fix.BlockEntityJukeboxFix;
import net.minecraft.datafixer.fix.BlockEntityKeepPacked;
import net.minecraft.datafixer.fix.BlockEntityShulkerBoxColorFix;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.BlockNameFlatteningFix;
import net.minecraft.datafixer.fix.BlockStateStructureTemplateFix;
import net.minecraft.datafixer.fix.CatTypeFix;
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
import net.minecraft.datafixer.fix.EntityCustomNameToComponentFix;
import net.minecraft.datafixer.fix.EntityElderGuardianSplitFix;
import net.minecraft.datafixer.fix.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixer.fix.EntityHealthFix;
import net.minecraft.datafixer.fix.EntityHorseSaddleFix;
import net.minecraft.datafixer.fix.EntityHorseSplitFix;
import net.minecraft.datafixer.fix.EntityIdFix;
import net.minecraft.datafixer.fix.EntityItemFrameDirectionFix;
import net.minecraft.datafixer.fix.EntityMinecartIdentifiersFix;
import net.minecraft.datafixer.fix.EntityPaintingMotiveFix;
import net.minecraft.datafixer.fix.EntityPufferfishRenameFix;
import net.minecraft.datafixer.fix.EntityRavagerRenameFix;
import net.minecraft.datafixer.fix.EntityRedundantChanceTagsFix;
import net.minecraft.datafixer.fix.EntityRidingToPassengerFix;
import net.minecraft.datafixer.fix.EntityShulkerColorFix;
import net.minecraft.datafixer.fix.EntitySkeletonSplitFix;
import net.minecraft.datafixer.fix.EntityStringUuidFix;
import net.minecraft.datafixer.fix.EntityTheRenameningBlock;
import net.minecraft.datafixer.fix.EntityTippedArrowFix;
import net.minecraft.datafixer.fix.EntityWolfColorFix;
import net.minecraft.datafixer.fix.EntityZombieSplitFix;
import net.minecraft.datafixer.fix.EntityZombieVillagerTypeFix;
import net.minecraft.datafixer.fix.HangingEntityFix;
import net.minecraft.datafixer.fix.HeightmapRenamingFix;
import net.minecraft.datafixer.fix.IglooMetadataRemovalFix;
import net.minecraft.datafixer.fix.ItemBannerColorFix;
import net.minecraft.datafixer.fix.ItemCustomNameToComponentFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemInstanceMapIdFix;
import net.minecraft.datafixer.fix.ItemInstanceSpawnEggFix;
import net.minecraft.datafixer.fix.ItemInstanceTheFlatteningFix;
import net.minecraft.datafixer.fix.ItemLoreToComponentFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.fix.ItemPotionFix;
import net.minecraft.datafixer.fix.ItemShulkerBoxColorFix;
import net.minecraft.datafixer.fix.ItemSpawnEggFix;
import net.minecraft.datafixer.fix.ItemStackEnchantmentFix;
import net.minecraft.datafixer.fix.ItemWaterPotionFix;
import net.minecraft.datafixer.fix.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixer.fix.LeavesFix;
import net.minecraft.datafixer.fix.LevelDataGeneratorOptionsFix;
import net.minecraft.datafixer.fix.LevelFlatGeneratorInfoFix;
import net.minecraft.datafixer.fix.MapIdFix;
import net.minecraft.datafixer.fix.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixer.fix.NewVillageFix;
import net.minecraft.datafixer.fix.ObjectiveDisplayNameFix;
import net.minecraft.datafixer.fix.ObjectiveRenderTypeFix;
import net.minecraft.datafixer.fix.OminousBannerBlockEntityRenameFix;
import net.minecraft.datafixer.fix.OminousBannerItemRenameFix;
import net.minecraft.datafixer.fix.OptionsAddTextBackgroundFix;
import net.minecraft.datafixer.fix.OptionsForceVBOFix;
import net.minecraft.datafixer.fix.OptionsKeyLwjgl3Fix;
import net.minecraft.datafixer.fix.OptionsKeyTranslationFix;
import net.minecraft.datafixer.fix.OptionsLowerCaseLanguageFix;
import net.minecraft.datafixer.fix.PointOfInterestReorganizationFix;
import net.minecraft.datafixer.fix.RecipeFix;
import net.minecraft.datafixer.fix.RecipeRenameFix;
import net.minecraft.datafixer.fix.RecipeRenamingFix;
import net.minecraft.datafixer.fix.RemovePoiValidTagFix;
import net.minecraft.datafixer.fix.SavedDataVillageCropFix;
import net.minecraft.datafixer.fix.StatsCounterFix;
import net.minecraft.datafixer.fix.SwimStatsRenameFix;
import net.minecraft.datafixer.fix.TeamDisplayNameFix;
import net.minecraft.datafixer.fix.VillagerProfessionFix;
import net.minecraft.datafixer.fix.VillagerTradeFix;
import net.minecraft.datafixer.fix.VillagerXpRebuildFix;
import net.minecraft.datafixer.fix.WriteAndReadFix;
import net.minecraft.datafixer.fix.ZombieVillagerXpRebuildFix;
import net.minecraft.datafixer.mapping.LegacyCoralBlockMapping;
import net.minecraft.datafixer.mapping.LegacyCoralFanBlockMapping;
import net.minecraft.datafixer.mapping.LegacyDyeItemMapping;
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
import net.minecraft.datafixer.schema.Schema501;
import net.minecraft.datafixer.schema.Schema700;
import net.minecraft.datafixer.schema.Schema701;
import net.minecraft.datafixer.schema.Schema702;
import net.minecraft.datafixer.schema.Schema703;
import net.minecraft.datafixer.schema.Schema704;
import net.minecraft.datafixer.schema.Schema705;
import net.minecraft.datafixer.schema.Schema808;
import net.minecraft.datafixer.schema.Schema99;
import net.minecraft.datafixer.schema.SchemaIdentifierNormalize;
import net.minecraft.util.Util;

public class Schemas {
	private static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> EMPTY_IDENTIFIER_NORMALIZE = SchemaIdentifierNormalize::new;
	private static final DataFixer fixer = create();

	private static DataFixer create() {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(SharedConstants.getGameVersion().getWorldVersion());
		build(dataFixerBuilder);
		return dataFixerBuilder.build(Util.getServerWorkerExecutor());
	}

	public static DataFixer getFixer() {
		return fixer;
	}

	private static void build(DataFixerBuilder dataFixerBuilder) {
		Schema schema = dataFixerBuilder.addSchema(99, Schema99::new);
		Schema schema2 = dataFixerBuilder.addSchema(100, Schema100::new);
		dataFixerBuilder.addFixer(new EntityEquipmentToArmorAndHandFix(schema2, true));
		Schema schema3 = dataFixerBuilder.addSchema(101, EMPTY);
		dataFixerBuilder.addFixer(new BlockEntitySignTextStrictJsonFix(schema3, false));
		Schema schema4 = dataFixerBuilder.addSchema(102, Schema102::new);
		dataFixerBuilder.addFixer(new ItemIdFix(schema4, true));
		dataFixerBuilder.addFixer(new ItemPotionFix(schema4, false));
		Schema schema5 = dataFixerBuilder.addSchema(105, EMPTY);
		dataFixerBuilder.addFixer(new ItemSpawnEggFix(schema5, true));
		Schema schema6 = dataFixerBuilder.addSchema(106, Schema106::new);
		dataFixerBuilder.addFixer(new MobSpawnerEntityIdentifiersFix(schema6, true));
		Schema schema7 = dataFixerBuilder.addSchema(107, Schema107::new);
		dataFixerBuilder.addFixer(new EntityMinecartIdentifiersFix(schema7, true));
		Schema schema8 = dataFixerBuilder.addSchema(108, EMPTY);
		dataFixerBuilder.addFixer(new EntityStringUuidFix(schema8, true));
		Schema schema9 = dataFixerBuilder.addSchema(109, EMPTY);
		dataFixerBuilder.addFixer(new EntityHealthFix(schema9, true));
		Schema schema10 = dataFixerBuilder.addSchema(110, EMPTY);
		dataFixerBuilder.addFixer(new EntityHorseSaddleFix(schema10, true));
		Schema schema11 = dataFixerBuilder.addSchema(111, EMPTY);
		dataFixerBuilder.addFixer(new HangingEntityFix(schema11, true));
		Schema schema12 = dataFixerBuilder.addSchema(113, EMPTY);
		dataFixerBuilder.addFixer(new EntityRedundantChanceTagsFix(schema12, true));
		Schema schema13 = dataFixerBuilder.addSchema(135, Schema135::new);
		dataFixerBuilder.addFixer(new EntityRidingToPassengerFix(schema13, true));
		Schema schema14 = dataFixerBuilder.addSchema(143, Schema143::new);
		dataFixerBuilder.addFixer(new EntityTippedArrowFix(schema14, true));
		Schema schema15 = dataFixerBuilder.addSchema(147, EMPTY);
		dataFixerBuilder.addFixer(new EntityArmorStandSilentFix(schema15, true));
		Schema schema16 = dataFixerBuilder.addSchema(165, EMPTY);
		dataFixerBuilder.addFixer(new ItemWrittenBookPagesStrictJsonFix(schema16, true));
		Schema schema17 = dataFixerBuilder.addSchema(501, Schema501::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema17, "Add 1.10 entities fix", TypeReferences.ENTITY));
		Schema schema18 = dataFixerBuilder.addSchema(502, EMPTY);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(
				schema18,
				"cooked_fished item renamer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string
			)
		);
		dataFixerBuilder.addFixer(new EntityZombieVillagerTypeFix(schema18, false));
		Schema schema19 = dataFixerBuilder.addSchema(505, EMPTY);
		dataFixerBuilder.addFixer(new OptionsForceVBOFix(schema19, false));
		Schema schema20 = dataFixerBuilder.addSchema(700, Schema700::new);
		dataFixerBuilder.addFixer(new EntityElderGuardianSplitFix(schema20, true));
		Schema schema21 = dataFixerBuilder.addSchema(701, Schema701::new);
		dataFixerBuilder.addFixer(new EntitySkeletonSplitFix(schema21, true));
		Schema schema22 = dataFixerBuilder.addSchema(702, Schema702::new);
		dataFixerBuilder.addFixer(new EntityZombieSplitFix(schema22, true));
		Schema schema23 = dataFixerBuilder.addSchema(703, Schema703::new);
		dataFixerBuilder.addFixer(new EntityHorseSplitFix(schema23, true));
		Schema schema24 = dataFixerBuilder.addSchema(704, Schema704::new);
		dataFixerBuilder.addFixer(new BlockEntityIdFix(schema24, true));
		Schema schema25 = dataFixerBuilder.addSchema(705, Schema705::new);
		dataFixerBuilder.addFixer(new EntityIdFix(schema25, true));
		Schema schema26 = dataFixerBuilder.addSchema(804, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ItemBannerColorFix(schema26, true));
		Schema schema27 = dataFixerBuilder.addSchema(806, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ItemWaterPotionFix(schema27, false));
		Schema schema28 = dataFixerBuilder.addSchema(808, Schema808::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema28, "added shulker box", TypeReferences.BLOCK_ENTITY));
		Schema schema29 = dataFixerBuilder.addSchema(808, 1, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new EntityShulkerColorFix(schema29, false));
		Schema schema30 = dataFixerBuilder.addSchema(813, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ItemShulkerBoxColorFix(schema30, false));
		dataFixerBuilder.addFixer(new BlockEntityShulkerBoxColorFix(schema30, false));
		Schema schema31 = dataFixerBuilder.addSchema(816, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new OptionsLowerCaseLanguageFix(schema31, false));
		Schema schema32 = dataFixerBuilder.addSchema(820, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(schema32, "totem item renamer", string -> Objects.equals(string, "minecraft:totem") ? "minecraft:totem_of_undying" : string)
		);
		Schema schema33 = dataFixerBuilder.addSchema(1022, Schema1022::new);
		dataFixerBuilder.addFixer(new WriteAndReadFix(schema33, "added shoulder entities to players", TypeReferences.PLAYER));
		Schema schema34 = dataFixerBuilder.addSchema(1125, Schema1125::new);
		dataFixerBuilder.addFixer(new BedBlockEntityFix(schema34, true));
		dataFixerBuilder.addFixer(new BedItemColorFix(schema34, false));
		Schema schema35 = dataFixerBuilder.addSchema(1344, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new OptionsKeyLwjgl3Fix(schema35, false));
		Schema schema36 = dataFixerBuilder.addSchema(1446, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new OptionsKeyTranslationFix(schema36, false));
		Schema schema37 = dataFixerBuilder.addSchema(1450, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new BlockStateStructureTemplateFix(schema37, false));
		Schema schema38 = dataFixerBuilder.addSchema(1451, Schema1451::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema38, "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY));
		Schema schema39 = dataFixerBuilder.addSchema(1451, 1, Schema1451v1::new);
		dataFixerBuilder.addFixer(new ChunkPalettedStorageFix(schema39, true));
		Schema schema40 = dataFixerBuilder.addSchema(1451, 2, Schema1451v2::new);
		dataFixerBuilder.addFixer(new BlockEntityBlockStateFix(schema40, true));
		Schema schema41 = dataFixerBuilder.addSchema(1451, 3, Schema1451v3::new);
		dataFixerBuilder.addFixer(new EntityBlockStateFix(schema41, true));
		dataFixerBuilder.addFixer(new ItemInstanceMapIdFix(schema41, false));
		Schema schema42 = dataFixerBuilder.addSchema(1451, 4, Schema1451v4::new);
		dataFixerBuilder.addFixer(new BlockNameFlatteningFix(schema42, true));
		dataFixerBuilder.addFixer(new ItemInstanceTheFlatteningFix(schema42, false));
		Schema schema43 = dataFixerBuilder.addSchema(1451, 5, Schema1451v5::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema43, "RemoveNoteBlockFlowerPotFix", TypeReferences.BLOCK_ENTITY));
		dataFixerBuilder.addFixer(new ItemInstanceSpawnEggFix(schema43, false));
		dataFixerBuilder.addFixer(new EntityWolfColorFix(schema43, false));
		dataFixerBuilder.addFixer(new BlockEntityBannerColorFix(schema43, false));
		dataFixerBuilder.addFixer(new LevelFlatGeneratorInfoFix(schema43, false));
		Schema schema44 = dataFixerBuilder.addSchema(1451, 6, Schema1451v6::new);
		dataFixerBuilder.addFixer(new StatsCounterFix(schema44, true));
		dataFixerBuilder.addFixer(new BlockEntityJukeboxFix(schema44, false));
		Schema schema45 = dataFixerBuilder.addSchema(1451, 7, Schema1451v7::new);
		dataFixerBuilder.addFixer(new SavedDataVillageCropFix(schema45, true));
		Schema schema46 = dataFixerBuilder.addSchema(1451, 7, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new VillagerTradeFix(schema46, false));
		Schema schema47 = dataFixerBuilder.addSchema(1456, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new EntityItemFrameDirectionFix(schema47, false));
		Schema schema48 = dataFixerBuilder.addSchema(1458, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new EntityCustomNameToComponentFix(schema48, false));
		dataFixerBuilder.addFixer(new ItemCustomNameToComponentFix(schema48, false));
		dataFixerBuilder.addFixer(new BlockEntityCustomNameToComponentFix(schema48, false));
		Schema schema49 = dataFixerBuilder.addSchema(1460, Schema1460::new);
		dataFixerBuilder.addFixer(new EntityPaintingMotiveFix(schema49, false));
		Schema schema50 = dataFixerBuilder.addSchema(1466, Schema1466::new);
		dataFixerBuilder.addFixer(new ChunkToProtoChunkFix(schema50, true));
		Schema schema51 = dataFixerBuilder.addSchema(1470, Schema1470::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema51, "Add 1.13 entities fix", TypeReferences.ENTITY));
		Schema schema52 = dataFixerBuilder.addSchema(1474, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ColorlessShulkerEntityFix(schema52, false));
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema52,
				"Colorless shulker block fixer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(
				schema52,
				"Colorless shulker item fixer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		Schema schema53 = dataFixerBuilder.addSchema(1475, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema53,
				"Flowing fixer",
				string -> (String)ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(string, string)
			)
		);
		Schema schema54 = dataFixerBuilder.addSchema(1480, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(BlockNameFix.create(schema54, "Rename coral blocks", string -> (String)LegacyCoralBlockMapping.MAP.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(ItemNameFix.create(schema54, "Rename coral items", string -> (String)LegacyCoralBlockMapping.MAP.getOrDefault(string, string)));
		Schema schema55 = dataFixerBuilder.addSchema(1481, Schema1481::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema55, "Add conduit", TypeReferences.BLOCK_ENTITY));
		Schema schema56 = dataFixerBuilder.addSchema(1483, Schema1483::new);
		dataFixerBuilder.addFixer(new EntityPufferfishRenameFix(schema56, true));
		dataFixerBuilder.addFixer(
			ItemNameFix.create(schema56, "Rename pufferfish egg item", string -> (String)EntityPufferfishRenameFix.RENAMED_FISHES.getOrDefault(string, string))
		);
		Schema schema57 = dataFixerBuilder.addSchema(1484, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(
				schema57,
				"Rename seagrass items",
				string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass")
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema57,
				"Rename seagrass blocks",
				string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass")
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(new HeightmapRenamingFix(schema57, false));
		Schema schema58 = dataFixerBuilder.addSchema(1486, Schema1486::new);
		dataFixerBuilder.addFixer(new EntityCodSalmonFix(schema58, true));
		dataFixerBuilder.addFixer(
			ItemNameFix.create(schema58, "Rename cod/salmon egg items", string -> (String)EntityCodSalmonFix.SPAWN_EGGS.getOrDefault(string, string))
		);
		Schema schema59 = dataFixerBuilder.addSchema(1487, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* blocks",
				string -> (String)ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* items",
				string -> (String)ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema60 = dataFixerBuilder.addSchema(1488, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema60,
				"Rename kelp/kelptop",
				string -> (String)ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(ItemNameFix.create(schema60, "Rename kelptop", string -> Objects.equals(string, "minecraft:kelp_top") ? "minecraft:kelp" : string));
		dataFixerBuilder.addFixer(
			new ChoiceFix(schema60, false, "Command block block entity custom name fix", TypeReferences.BLOCK_ENTITY, "minecraft:command_block") {
				@Override
				protected Typed<?> transform(Typed<?> typed) {
					return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixCustomName);
				}
			}
		);
		dataFixerBuilder.addFixer(
			new ChoiceFix(schema60, false, "Command block minecart custom name fix", TypeReferences.ENTITY, "minecraft:commandblock_minecart") {
				@Override
				protected Typed<?> transform(Typed<?> typed) {
					return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixCustomName);
				}
			}
		);
		dataFixerBuilder.addFixer(new IglooMetadataRemovalFix(schema60, false));
		Schema schema61 = dataFixerBuilder.addSchema(1490, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(schema61, "Rename melon_block", string -> Objects.equals(string, "minecraft:melon_block") ? "minecraft:melon" : string)
		);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(
				schema61,
				"Rename melon_block/melon/speckled_melon",
				string -> (String)ImmutableMap.of(
							"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema62 = dataFixerBuilder.addSchema(1492, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ChunkStructuresTemplateRenameFix(schema62, false));
		Schema schema63 = dataFixerBuilder.addSchema(1494, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ItemStackEnchantmentFix(schema63, false));
		Schema schema64 = dataFixerBuilder.addSchema(1496, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new LeavesFix(schema64, false));
		Schema schema65 = dataFixerBuilder.addSchema(1500, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new BlockEntityKeepPacked(schema65, false));
		Schema schema66 = dataFixerBuilder.addSchema(1501, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new AdvancementsFix(schema66, false));
		Schema schema67 = dataFixerBuilder.addSchema(1502, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new RecipeFix(schema67, false));
		Schema schema68 = dataFixerBuilder.addSchema(1506, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new LevelDataGeneratorOptionsFix(schema68, false));
		Schema schema69 = dataFixerBuilder.addSchema(1508, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new BiomesFix(schema69, false));
		Schema schema70 = dataFixerBuilder.addSchema(1510, Schema1510::new);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(schema70, "Block renamening fix", string -> (String)EntityTheRenameningBlock.BLOCKS.getOrDefault(string, string))
		);
		dataFixerBuilder.addFixer(ItemNameFix.create(schema70, "Item renamening fix", string -> (String)EntityTheRenameningBlock.ITEMS.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(new RecipeRenamingFix(schema70, false));
		dataFixerBuilder.addFixer(new EntityTheRenameningBlock(schema70, true));
		dataFixerBuilder.addFixer(new SwimStatsRenameFix(schema70, false));
		Schema schema71 = dataFixerBuilder.addSchema(1514, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ObjectiveDisplayNameFix(schema71, false));
		dataFixerBuilder.addFixer(new TeamDisplayNameFix(schema71, false));
		dataFixerBuilder.addFixer(new ObjectiveRenderTypeFix(schema71, false));
		Schema schema72 = dataFixerBuilder.addSchema(1515, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(schema72, "Rename coral fan blocks", string -> (String)LegacyCoralFanBlockMapping.MAP.getOrDefault(string, string))
		);
		Schema schema73 = dataFixerBuilder.addSchema(1624, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new AddTrappedChestFix(schema73, false));
		Schema schema74 = dataFixerBuilder.addSchema(1800, Schema1800::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema74, "Added 1.14 mobs fix", TypeReferences.ENTITY));
		dataFixerBuilder.addFixer(ItemNameFix.create(schema74, "Rename dye items", string -> (String)LegacyDyeItemMapping.MAP.getOrDefault(string, string)));
		Schema schema75 = dataFixerBuilder.addSchema(1801, Schema1801::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema75, "Added Illager Beast", TypeReferences.ENTITY));
		Schema schema76 = dataFixerBuilder.addSchema(1802, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema76,
				"Rename sign blocks & stone slabs",
				string -> (String)ImmutableMap.of(
							"minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(
				schema76,
				"Rename sign item & stone slabs",
				string -> (String)ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign")
						.getOrDefault(string, string)
			)
		);
		Schema schema77 = dataFixerBuilder.addSchema(1803, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ItemLoreToComponentFix(schema77, false));
		Schema schema78 = dataFixerBuilder.addSchema(1904, Schema1904::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema78, "Added Cats", TypeReferences.ENTITY));
		dataFixerBuilder.addFixer(new EntityCatSplitFix(schema78, false));
		Schema schema79 = dataFixerBuilder.addSchema(1905, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ChunkStatusFix(schema79, false));
		Schema schema80 = dataFixerBuilder.addSchema(1906, Schema1906::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema80, "Add POI Blocks", TypeReferences.BLOCK_ENTITY));
		Schema schema81 = dataFixerBuilder.addSchema(1909, Schema1909::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema81, "Add jigsaw", TypeReferences.BLOCK_ENTITY));
		Schema schema82 = dataFixerBuilder.addSchema(1911, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ChunkStatusFix2(schema82, false));
		Schema schema83 = dataFixerBuilder.addSchema(1917, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new CatTypeFix(schema83, false));
		Schema schema84 = dataFixerBuilder.addSchema(1918, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new VillagerProfessionFix(schema84, "minecraft:villager"));
		dataFixerBuilder.addFixer(new VillagerProfessionFix(schema84, "minecraft:zombie_villager"));
		Schema schema85 = dataFixerBuilder.addSchema(1920, Schema1920::new);
		dataFixerBuilder.addFixer(new NewVillageFix(schema85, false));
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema85, "Add campfire", TypeReferences.BLOCK_ENTITY));
		Schema schema86 = dataFixerBuilder.addSchema(1925, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new MapIdFix(schema86, false));
		Schema schema87 = dataFixerBuilder.addSchema(1928, Schema1928::new);
		dataFixerBuilder.addFixer(new EntityRavagerRenameFix(schema87, true));
		dataFixerBuilder.addFixer(
			ItemNameFix.create(schema87, "Rename ravager egg item", string -> (String)EntityRavagerRenameFix.ITEMS.getOrDefault(string, string))
		);
		Schema schema88 = dataFixerBuilder.addSchema(1929, Schema1929::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema88, "Add Wandering Trader and Trader Llama", TypeReferences.ENTITY));
		Schema schema89 = dataFixerBuilder.addSchema(1931, Schema1931::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema89, "Added Fox", TypeReferences.ENTITY));
		Schema schema90 = dataFixerBuilder.addSchema(1936, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new OptionsAddTextBackgroundFix(schema90, false));
		Schema schema91 = dataFixerBuilder.addSchema(1946, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new PointOfInterestReorganizationFix(schema91, false));
		Schema schema92 = dataFixerBuilder.addSchema(1948, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new OminousBannerItemRenameFix(schema92, false));
		Schema schema93 = dataFixerBuilder.addSchema(1953, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new OminousBannerBlockEntityRenameFix(schema93, false));
		Schema schema94 = dataFixerBuilder.addSchema(1955, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new VillagerXpRebuildFix(schema94, false));
		dataFixerBuilder.addFixer(new ZombieVillagerXpRebuildFix(schema94, false));
		Schema schema95 = dataFixerBuilder.addSchema(1961, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new ChunkLightRemoveFix(schema95, false));
		Schema schema96 = dataFixerBuilder.addSchema(2100, Schema2100::new);
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema96, "Added Bee and Bee Stinger", TypeReferences.ENTITY));
		dataFixerBuilder.addFixer(new ChoiceTypesFix(schema96, "Add beehive", TypeReferences.BLOCK_ENTITY));
		dataFixerBuilder.addFixer(
			new RecipeRenameFix(schema96, false, "Rename sugar recipe", string -> "minecraft:sugar".equals(string) ? "sugar_from_sugar_cane" : string)
		);
		dataFixerBuilder.addFixer(
			new AdvancementRenameFix(
				schema96,
				false,
				"Rename sugar recipe advancement",
				string -> "minecraft:recipes/misc/sugar".equals(string) ? "minecraft:recipes/misc/sugar_from_sugar_cane" : string
			)
		);
		Schema schema97 = dataFixerBuilder.addSchema(2202, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new BiomeFormatFix(schema97, false));
		Schema schema98 = dataFixerBuilder.addSchema(2209, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(
			ItemNameFix.create(schema98, "Rename bee_hive item to beehive", string -> Objects.equals(string, "minecraft:bee_hive") ? "minecraft:beehive" : string)
		);
		dataFixerBuilder.addFixer(new BeehiveRenameFix(schema98));
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema98, "Rename bee_hive block to beehive", string -> (String)ImmutableMap.of("minecraft:bee_hive", "minecraft:beehive").getOrDefault(string, string)
			)
		);
		Schema schema99 = dataFixerBuilder.addSchema(2211, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new StructureReferenceFixer(schema99, false));
		Schema schema100 = dataFixerBuilder.addSchema(2218, EMPTY_IDENTIFIER_NORMALIZE);
		dataFixerBuilder.addFixer(new RemovePoiValidTagFix(schema100, false));
	}
}
