package net.minecraft.datafixer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import net.minecraft.class_3385;
import net.minecraft.class_3386;
import net.minecraft.class_3387;
import net.minecraft.class_3388;
import net.minecraft.class_3389;
import net.minecraft.class_3390;
import net.minecraft.class_3393;
import net.minecraft.class_3394;
import net.minecraft.class_3395;
import net.minecraft.class_3396;
import net.minecraft.class_3397;
import net.minecraft.class_3398;
import net.minecraft.class_3399;
import net.minecraft.class_3400;
import net.minecraft.class_3401;
import net.minecraft.class_3402;
import net.minecraft.class_3403;
import net.minecraft.class_3404;
import net.minecraft.class_3405;
import net.minecraft.class_3408;
import net.minecraft.class_3409;
import net.minecraft.class_3410;
import net.minecraft.class_3411;
import net.minecraft.class_3413;
import net.minecraft.class_3414;
import net.minecraft.class_3415;
import net.minecraft.class_3416;
import net.minecraft.class_3417;
import net.minecraft.class_3418;
import net.minecraft.class_3419;
import net.minecraft.class_3420;
import net.minecraft.class_3421;
import net.minecraft.class_3422;
import net.minecraft.class_3423;
import net.minecraft.class_3424;
import net.minecraft.class_3425;
import net.minecraft.class_3426;
import net.minecraft.class_3427;
import net.minecraft.class_3428;
import net.minecraft.class_3429;
import net.minecraft.class_3433;
import net.minecraft.class_3434;
import net.minecraft.class_3435;
import net.minecraft.class_3436;
import net.minecraft.class_3437;
import net.minecraft.class_3438;
import net.minecraft.class_3439;
import net.minecraft.class_3440;
import net.minecraft.class_3441;
import net.minecraft.class_3442;
import net.minecraft.class_3443;
import net.minecraft.class_3444;
import net.minecraft.class_3445;
import net.minecraft.class_3446;
import net.minecraft.class_3447;
import net.minecraft.class_3448;
import net.minecraft.class_3449;
import net.minecraft.class_3450;
import net.minecraft.class_4490;
import net.minecraft.class_4491;
import net.minecraft.class_4492;
import net.minecraft.class_4493;
import net.minecraft.class_4494;
import net.minecraft.class_4495;
import net.minecraft.class_4496;
import net.minecraft.class_4497;
import net.minecraft.class_4498;
import net.minecraft.class_4499;
import net.minecraft.class_4501;
import net.minecraft.class_4502;
import net.minecraft.class_4509;
import net.minecraft.class_4510;
import net.minecraft.class_4511;
import net.minecraft.class_4512;
import net.minecraft.class_4513;
import net.minecraft.class_4514;
import net.minecraft.class_4516;
import net.minecraft.class_4517;
import net.minecraft.class_4518;
import net.minecraft.class_4520;
import net.minecraft.class_4521;
import net.minecraft.class_4522;
import net.minecraft.class_4523;
import net.minecraft.class_4524;
import net.minecraft.class_4525;
import net.minecraft.datafixer.fix.BedBlockEntityFix;
import net.minecraft.datafixer.fix.BedItemColorFix;
import net.minecraft.datafixer.fix.BlockEntityIdFix;
import net.minecraft.datafixer.fix.BlockEntityShulkerBoxColorFix;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixer.fix.EntityArmorStandSilentFix;
import net.minecraft.datafixer.fix.EntityElderGuardianSplitFix;
import net.minecraft.datafixer.fix.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixer.fix.EntityHealthFix;
import net.minecraft.datafixer.fix.EntityHorseSaddleFix;
import net.minecraft.datafixer.fix.EntityHorseSplitFix;
import net.minecraft.datafixer.fix.EntityIdFix;
import net.minecraft.datafixer.fix.EntityMinecartIdentifiersFix;
import net.minecraft.datafixer.fix.EntityRedundantChanceTagsFix;
import net.minecraft.datafixer.fix.EntityRidingToPassengerFix;
import net.minecraft.datafixer.fix.EntityShulkerColorFix;
import net.minecraft.datafixer.fix.EntitySkeletonSplitFix;
import net.minecraft.datafixer.fix.EntityStringUuidFix;
import net.minecraft.datafixer.fix.EntityZombieSplitFix;
import net.minecraft.datafixer.fix.EntityZombieVillagerTypeFix;
import net.minecraft.datafixer.fix.HangingEntityFix;
import net.minecraft.datafixer.fix.ItemBannerColorFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemPotionFix;
import net.minecraft.datafixer.fix.ItemShulkerColorFix;
import net.minecraft.datafixer.fix.ItemSpawnEggFix;
import net.minecraft.datafixer.fix.ItemWaterPotionFix;
import net.minecraft.datafixer.fix.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixer.fix.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixer.fix.OptionsForceVBOFix;
import net.minecraft.datafixer.fix.OptionsLowerCaseLanguageFix;

public class DataFixerFactory {
	private static final BiFunction<Integer, Schema, Schema> field_22259 = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> field_22260 = class_3415::new;
	private static final DataFixer field_22261 = method_21534();

	private static DataFixer method_21534() {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(1631);
		method_21532(dataFixerBuilder);
		return dataFixerBuilder.build(ForkJoinPool.commonPool());
	}

	public static DataFixer method_21531() {
		return field_22261;
	}

	private static void method_21532(DataFixerBuilder dataFixerBuilder) {
		Schema schema = dataFixerBuilder.addSchema(99, class_3450::new);
		Schema schema2 = dataFixerBuilder.addSchema(100, class_3416::new);
		dataFixerBuilder.addFixer(new EntityEquipmentToArmorAndHandFix(schema2, true));
		Schema schema3 = dataFixerBuilder.addSchema(101, field_22259);
		dataFixerBuilder.addFixer(new BlockEntitySignTextStrictJsonFix(schema3, false));
		Schema schema4 = dataFixerBuilder.addSchema(102, class_3417::new);
		dataFixerBuilder.addFixer(new ItemIdFix(schema4, true));
		dataFixerBuilder.addFixer(new ItemPotionFix(schema4, false));
		Schema schema5 = dataFixerBuilder.addSchema(105, field_22259);
		dataFixerBuilder.addFixer(new ItemSpawnEggFix(schema5, true));
		Schema schema6 = dataFixerBuilder.addSchema(106, class_3419::new);
		dataFixerBuilder.addFixer(new MobSpawnerEntityIdentifiersFix(schema6, true));
		Schema schema7 = dataFixerBuilder.addSchema(107, class_3420::new);
		dataFixerBuilder.addFixer(new EntityMinecartIdentifiersFix(schema7, true));
		Schema schema8 = dataFixerBuilder.addSchema(108, field_22259);
		dataFixerBuilder.addFixer(new EntityStringUuidFix(schema8, true));
		Schema schema9 = dataFixerBuilder.addSchema(109, field_22259);
		dataFixerBuilder.addFixer(new EntityHealthFix(schema9, true));
		Schema schema10 = dataFixerBuilder.addSchema(110, field_22259);
		dataFixerBuilder.addFixer(new EntityHorseSaddleFix(schema10, true));
		Schema schema11 = dataFixerBuilder.addSchema(111, field_22259);
		dataFixerBuilder.addFixer(new HangingEntityFix(schema11, true));
		Schema schema12 = dataFixerBuilder.addSchema(113, field_22259);
		dataFixerBuilder.addFixer(new EntityRedundantChanceTagsFix(schema12, true));
		Schema schema13 = dataFixerBuilder.addSchema(135, class_3422::new);
		dataFixerBuilder.addFixer(new EntityRidingToPassengerFix(schema13, true));
		Schema schema14 = dataFixerBuilder.addSchema(143, class_3423::new);
		dataFixerBuilder.addFixer(new class_4521(schema14, true));
		Schema schema15 = dataFixerBuilder.addSchema(147, field_22259);
		dataFixerBuilder.addFixer(new EntityArmorStandSilentFix(schema15, true));
		Schema schema16 = dataFixerBuilder.addSchema(165, field_22259);
		dataFixerBuilder.addFixer(new ItemWrittenBookPagesStrictJsonFix(schema16, true));
		Schema schema17 = dataFixerBuilder.addSchema(501, class_3442::new);
		dataFixerBuilder.addFixer(new class_4490(schema17, "Add 1.10 entities fix", class_3402.field_16596));
		Schema schema18 = dataFixerBuilder.addSchema(502, field_22259);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(
				schema18,
				"cooked_fished item renamer",
				string -> Objects.equals(class_3415.method_15286(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string
			)
		);
		dataFixerBuilder.addFixer(new EntityZombieVillagerTypeFix(schema18, false));
		Schema schema19 = dataFixerBuilder.addSchema(505, field_22259);
		dataFixerBuilder.addFixer(new OptionsForceVBOFix(schema19, false));
		Schema schema20 = dataFixerBuilder.addSchema(700, class_3443::new);
		dataFixerBuilder.addFixer(new EntityElderGuardianSplitFix(schema20, true));
		Schema schema21 = dataFixerBuilder.addSchema(701, class_3444::new);
		dataFixerBuilder.addFixer(new EntitySkeletonSplitFix(schema21, true));
		Schema schema22 = dataFixerBuilder.addSchema(702, class_3445::new);
		dataFixerBuilder.addFixer(new EntityZombieSplitFix(schema22, true));
		Schema schema23 = dataFixerBuilder.addSchema(703, class_3446::new);
		dataFixerBuilder.addFixer(new EntityHorseSplitFix(schema23, true));
		Schema schema24 = dataFixerBuilder.addSchema(704, class_3447::new);
		dataFixerBuilder.addFixer(new BlockEntityIdFix(schema24, true));
		Schema schema25 = dataFixerBuilder.addSchema(705, class_3448::new);
		dataFixerBuilder.addFixer(new EntityIdFix(schema25, true));
		Schema schema26 = dataFixerBuilder.addSchema(804, field_22260);
		dataFixerBuilder.addFixer(new ItemBannerColorFix(schema26, true));
		Schema schema27 = dataFixerBuilder.addSchema(806, field_22260);
		dataFixerBuilder.addFixer(new ItemWaterPotionFix(schema27, false));
		Schema schema28 = dataFixerBuilder.addSchema(808, class_3449::new);
		dataFixerBuilder.addFixer(new class_4490(schema28, "added shulker box", class_3402.field_16591));
		Schema schema29 = dataFixerBuilder.addSchema(808, 1, field_22260);
		dataFixerBuilder.addFixer(new EntityShulkerColorFix(schema29, false));
		Schema schema30 = dataFixerBuilder.addSchema(813, field_22260);
		dataFixerBuilder.addFixer(new BlockEntityShulkerBoxColorFix(schema30, false));
		dataFixerBuilder.addFixer(new ItemShulkerColorFix(schema30, false));
		Schema schema31 = dataFixerBuilder.addSchema(816, field_22260);
		dataFixerBuilder.addFixer(new OptionsLowerCaseLanguageFix(schema31, false));
		Schema schema32 = dataFixerBuilder.addSchema(820, field_22260);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(schema32, "totem item renamer", string -> Objects.equals(string, "minecraft:totem") ? "minecraft:totem_of_undying" : string)
		);
		Schema schema33 = dataFixerBuilder.addSchema(1022, class_3418::new);
		dataFixerBuilder.addFixer(new class_3414(schema33, "added shoulder entities to players", class_3402.field_16583));
		Schema schema34 = dataFixerBuilder.addSchema(1125, class_3421::new);
		dataFixerBuilder.addFixer(new BedBlockEntityFix(schema34, true));
		dataFixerBuilder.addFixer(new BedItemColorFix(schema34, false));
		Schema schema35 = dataFixerBuilder.addSchema(1344, field_22260);
		dataFixerBuilder.addFixer(new class_3398(schema35, false));
		Schema schema36 = dataFixerBuilder.addSchema(1446, field_22260);
		dataFixerBuilder.addFixer(new class_3399(schema36, false));
		Schema schema37 = dataFixerBuilder.addSchema(1450, field_22260);
		dataFixerBuilder.addFixer(new class_4501(schema37, false));
		Schema schema38 = dataFixerBuilder.addSchema(1451, class_3424::new);
		dataFixerBuilder.addFixer(new class_4490(schema38, "AddTrappedChestFix", class_3402.field_16591));
		Schema schema39 = dataFixerBuilder.addSchema(1451, 1, class_3425::new);
		dataFixerBuilder.addFixer(new class_4502(schema39, true));
		Schema schema40 = dataFixerBuilder.addSchema(1451, 2, class_3426::new);
		dataFixerBuilder.addFixer(new class_4494(schema40, true));
		Schema schema41 = dataFixerBuilder.addSchema(1451, 3, class_3427::new);
		dataFixerBuilder.addFixer(new class_4512(schema41, true));
		dataFixerBuilder.addFixer(new class_3387(schema41, false));
		Schema schema42 = dataFixerBuilder.addSchema(1451, 4, class_3428::new);
		dataFixerBuilder.addFixer(new class_4498(schema42, true));
		dataFixerBuilder.addFixer(new class_3389(schema42, false));
		Schema schema43 = dataFixerBuilder.addSchema(1451, 5, class_3429::new);
		dataFixerBuilder.addFixer(new class_4490(schema43, "RemoveNoteBlockFlowerPotFix", class_3402.field_16591));
		dataFixerBuilder.addFixer(new class_3388(schema43, false));
		dataFixerBuilder.addFixer(new class_4522(schema43, false));
		dataFixerBuilder.addFixer(new class_4493(schema43, false));
		dataFixerBuilder.addFixer(new class_3394(schema43, false));
		Schema schema44 = dataFixerBuilder.addSchema(1451, 6, class_3433::new);
		dataFixerBuilder.addFixer(new class_3408(schema44, true));
		dataFixerBuilder.addFixer(new class_4496(schema44, false));
		Schema schema45 = dataFixerBuilder.addSchema(1451, 7, class_3434::new);
		dataFixerBuilder.addFixer(new class_3405(schema45, true));
		Schema schema46 = dataFixerBuilder.addSchema(1451, 7, field_22260);
		dataFixerBuilder.addFixer(new class_3413(schema46, false));
		Schema schema47 = dataFixerBuilder.addSchema(1456, field_22260);
		dataFixerBuilder.addFixer(new class_4516(schema47, false));
		Schema schema48 = dataFixerBuilder.addSchema(1458, field_22260);
		dataFixerBuilder.addFixer(new class_4514(schema48, false));
		dataFixerBuilder.addFixer(new class_4525(schema48, false));
		dataFixerBuilder.addFixer(new class_4495(schema48, false));
		Schema schema49 = dataFixerBuilder.addSchema(1460, class_3435::new);
		dataFixerBuilder.addFixer(new class_4517(schema49, false));
		Schema schema50 = dataFixerBuilder.addSchema(1466, class_3436::new);
		dataFixerBuilder.addFixer(new class_4510(schema50, true));
		Schema schema51 = dataFixerBuilder.addSchema(1470, class_3437::new);
		dataFixerBuilder.addFixer(new class_4490(schema51, "Add 1.13 entities fix", class_3402.field_16596));
		Schema schema52 = dataFixerBuilder.addSchema(1474, field_22260);
		dataFixerBuilder.addFixer(new class_4511(schema52, false));
		dataFixerBuilder.addFixer(
			class_4499.method_21596(
				schema52,
				"Colorless shulker block fixer",
				string -> Objects.equals(class_3415.method_15286(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(
				schema52,
				"Colorless shulker item fixer",
				string -> Objects.equals(class_3415.method_15286(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		Schema schema53 = dataFixerBuilder.addSchema(1475, field_22260);
		dataFixerBuilder.addFixer(
			class_4499.method_21596(
				schema53,
				"Flowing fixer",
				string -> (String)ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(string, string)
			)
		);
		Schema schema54 = dataFixerBuilder.addSchema(1480, field_22260);
		dataFixerBuilder.addFixer(class_4499.method_21596(schema54, "Rename coral blocks", string -> (String)class_3404.field_16606.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(class_3385.method_15113(schema54, "Rename coral items", string -> (String)class_3404.field_16606.getOrDefault(string, string)));
		Schema schema55 = dataFixerBuilder.addSchema(1481, class_3438::new);
		dataFixerBuilder.addFixer(new class_4490(schema55, "Add conduit", class_3402.field_16591));
		Schema schema56 = dataFixerBuilder.addSchema(1483, class_3439::new);
		dataFixerBuilder.addFixer(new class_4518(schema56, true));
		dataFixerBuilder.addFixer(
			class_3385.method_15113(schema56, "Rename pufferfish egg item", string -> (String)class_4518.field_22336.getOrDefault(string, string))
		);
		Schema schema57 = dataFixerBuilder.addSchema(1484, field_22260);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(
				schema57,
				"Rename seagrass items",
				string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass")
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_4499.method_21596(
				schema57,
				"Rename seagrass blocks",
				string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass")
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(new class_4523(schema57, false));
		Schema schema58 = dataFixerBuilder.addSchema(1486, class_3440::new);
		dataFixerBuilder.addFixer(new class_4513(schema58, true));
		dataFixerBuilder.addFixer(
			class_3385.method_15113(schema58, "Rename cod/salmon egg items", string -> (String)class_4513.field_22330.getOrDefault(string, string))
		);
		Schema schema59 = dataFixerBuilder.addSchema(1487, field_22260);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(
				schema59,
				"Rename prismarine_brick(s)_* blocks",
				string -> (String)ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_4499.method_21596(
				schema59,
				"Rename prismarine_brick(s)_* items",
				string -> (String)ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema60 = dataFixerBuilder.addSchema(1488, field_22260);
		dataFixerBuilder.addFixer(
			class_4499.method_21596(
				schema60,
				"Rename kelp/kelptop",
				string -> (String)ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(schema60, "Rename kelptop", string -> Objects.equals(string, "minecraft:kelp_top") ? "minecraft:kelp" : string)
		);
		dataFixerBuilder.addFixer(new class_3395(schema60, false, "Command block block entity custom name fix", class_3402.field_16591, "minecraft:command_block") {
			@Override
			protected Typed<?> method_15200(Typed<?> typed) {
				return typed.update(DSL.remainderFinder(), class_4514::method_21701);
			}
		});
		dataFixerBuilder.addFixer(
			new class_3395(schema60, false, "Command block minecart custom name fix", class_3402.field_16596, "minecraft:commandblock_minecart") {
				@Override
				protected Typed<?> method_15200(Typed<?> typed) {
					return typed.update(DSL.remainderFinder(), class_4514::method_21701);
				}
			}
		);
		dataFixerBuilder.addFixer(new class_4524(schema60, false));
		Schema schema61 = dataFixerBuilder.addSchema(1490, field_22260);
		dataFixerBuilder.addFixer(
			class_4499.method_21596(schema61, "Rename melon_block", string -> Objects.equals(string, "minecraft:melon_block") ? "minecraft:melon" : string)
		);
		dataFixerBuilder.addFixer(
			class_3385.method_15113(
				schema61,
				"Rename melon_block/melon/speckled_melon",
				string -> (String)ImmutableMap.of(
							"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema62 = dataFixerBuilder.addSchema(1492, field_22260);
		dataFixerBuilder.addFixer(new class_4509(schema62, false));
		Schema schema63 = dataFixerBuilder.addSchema(1494, field_22260);
		dataFixerBuilder.addFixer(new class_3386(schema63, false));
		Schema schema64 = dataFixerBuilder.addSchema(1496, field_22260);
		dataFixerBuilder.addFixer(new class_3390(schema64, false));
		Schema schema65 = dataFixerBuilder.addSchema(1500, field_22260);
		dataFixerBuilder.addFixer(new class_4497(schema65, false));
		Schema schema66 = dataFixerBuilder.addSchema(1501, field_22260);
		dataFixerBuilder.addFixer(new class_4491(schema66, false));
		Schema schema67 = dataFixerBuilder.addSchema(1502, field_22260);
		dataFixerBuilder.addFixer(new class_3400(schema67, false));
		Schema schema68 = dataFixerBuilder.addSchema(1506, field_22260);
		dataFixerBuilder.addFixer(new class_3393(schema68, false));
		Schema schema69 = dataFixerBuilder.addSchema(1508, field_22260);
		dataFixerBuilder.addFixer(new class_4492(schema69, false));
		Schema schema70 = dataFixerBuilder.addSchema(1510, class_3441::new);
		dataFixerBuilder.addFixer(class_4499.method_21596(schema70, "Block renamening fix", string -> (String)class_4520.field_22339.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(class_3385.method_15113(schema70, "Item renamening fix", string -> (String)class_4520.field_22340.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(new class_3401(schema70, false));
		dataFixerBuilder.addFixer(new class_4520(schema70, true));
		dataFixerBuilder.addFixer(new class_3409(schema70, false));
		Schema schema71 = dataFixerBuilder.addSchema(1514, field_22260);
		dataFixerBuilder.addFixer(new class_3396(schema71, false));
		dataFixerBuilder.addFixer(new class_3410(schema71, false));
		dataFixerBuilder.addFixer(new class_3397(schema71, false));
		Schema schema72 = dataFixerBuilder.addSchema(1515, field_22260);
		dataFixerBuilder.addFixer(class_4499.method_21596(schema72, "Rename coral fan blocks", string -> (String)class_3403.field_16605.getOrDefault(string, string)));
		Schema schema73 = dataFixerBuilder.addSchema(1624, field_22260);
		dataFixerBuilder.addFixer(new class_3411(schema73, false));
	}
}
