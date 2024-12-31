package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;

public class class_3435 extends class_3415 {
	public class_3435(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_15335(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> class_3416.method_15287(schema));
	}

	protected static void method_15341(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> DSL.optionalFields("Items", DSL.list(class_3402.field_16592.in(schema))));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		schema.registerSimple(map, "minecraft:area_effect_cloud");
		method_15335(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", string -> DSL.optionalFields("inBlockState", class_3402.field_16593.in(schema)));
		method_15335(schema, map, "minecraft:bat");
		method_15335(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		method_15335(schema, map, "minecraft:cave_spider");
		schema.register(
			map,
			"minecraft:chest_minecart",
			string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		method_15335(schema, map, "minecraft:chicken");
		schema.register(map, "minecraft:commandblock_minecart", string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		method_15335(schema, map, "minecraft:cow");
		method_15335(schema, map, "minecraft:creeper");
		schema.register(
			map,
			"minecraft:donkey",
			string -> DSL.optionalFields(
					"Items", DSL.list(class_3402.field_16592.in(schema)), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		schema.registerSimple(map, "minecraft:dragon_fireball");
		schema.registerSimple(map, "minecraft:egg");
		method_15335(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		method_15335(schema, map, "minecraft:ender_dragon");
		schema.register(
			map, "minecraft:enderman", string -> DSL.optionalFields("carriedBlockState", class_3402.field_16593.in(schema), class_3416.method_15287(schema))
		);
		method_15335(schema, map, "minecraft:endermite");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:evocation_fangs");
		method_15335(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(
			map,
			"minecraft:falling_block",
			string -> DSL.optionalFields("BlockState", class_3402.field_16593.in(schema), "TileEntityData", class_3402.field_16591.in(schema))
		);
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(map, "minecraft:fireworks_rocket", string -> DSL.optionalFields("FireworksItem", class_3402.field_16592.in(schema)));
		schema.register(map, "minecraft:furnace_minecart", string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		method_15335(schema, map, "minecraft:ghast");
		method_15335(schema, map, "minecraft:giant");
		method_15335(schema, map, "minecraft:guardian");
		schema.register(
			map,
			"minecraft:hopper_minecart",
			string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		schema.register(
			map,
			"minecraft:horse",
			string -> DSL.optionalFields(
					"ArmorItem", class_3402.field_16592.in(schema), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		method_15335(schema, map, "minecraft:husk");
		schema.registerSimple(map, "minecraft:illusion_illager");
		schema.register(map, "minecraft:item", string -> DSL.optionalFields("Item", class_3402.field_16592.in(schema)));
		schema.register(map, "minecraft:item_frame", string -> DSL.optionalFields("Item", class_3402.field_16592.in(schema)));
		schema.registerSimple(map, "minecraft:leash_knot");
		schema.register(
			map,
			"minecraft:llama",
			string -> DSL.optionalFields(
					"Items",
					DSL.list(class_3402.field_16592.in(schema)),
					"SaddleItem",
					class_3402.field_16592.in(schema),
					"DecorItem",
					class_3402.field_16592.in(schema),
					class_3416.method_15287(schema)
				)
		);
		schema.registerSimple(map, "minecraft:llama_spit");
		method_15335(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		method_15335(schema, map, "minecraft:mooshroom");
		schema.register(
			map,
			"minecraft:mule",
			string -> DSL.optionalFields(
					"Items", DSL.list(class_3402.field_16592.in(schema)), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		method_15335(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		method_15335(schema, map, "minecraft:pig");
		method_15335(schema, map, "minecraft:polar_bear");
		schema.register(map, "minecraft:potion", string -> DSL.optionalFields("Potion", class_3402.field_16592.in(schema)));
		method_15335(schema, map, "minecraft:rabbit");
		method_15335(schema, map, "minecraft:sheep");
		method_15335(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		method_15335(schema, map, "minecraft:silverfish");
		method_15335(schema, map, "minecraft:skeleton");
		schema.register(
			map, "minecraft:skeleton_horse", string -> DSL.optionalFields("SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema))
		);
		method_15335(schema, map, "minecraft:slime");
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		method_15335(schema, map, "minecraft:snowman");
		schema.register(
			map, "minecraft:spawner_minecart", string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema), class_3402.field_16599.in(schema))
		);
		schema.register(map, "minecraft:spectral_arrow", string -> DSL.optionalFields("inBlockState", class_3402.field_16593.in(schema)));
		method_15335(schema, map, "minecraft:spider");
		method_15335(schema, map, "minecraft:squid");
		method_15335(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(map, "minecraft:tnt_minecart", string -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		method_15335(schema, map, "minecraft:vex");
		schema.register(
			map,
			"minecraft:villager",
			string -> DSL.optionalFields(
					"Inventory",
					DSL.list(class_3402.field_16592.in(schema)),
					"Offers",
					DSL.optionalFields(
						"Recipes",
						DSL.list(
							DSL.optionalFields("buy", class_3402.field_16592.in(schema), "buyB", class_3402.field_16592.in(schema), "sell", class_3402.field_16592.in(schema))
						)
					),
					class_3416.method_15287(schema)
				)
		);
		method_15335(schema, map, "minecraft:villager_golem");
		method_15335(schema, map, "minecraft:vindication_illager");
		method_15335(schema, map, "minecraft:witch");
		method_15335(schema, map, "minecraft:wither");
		method_15335(schema, map, "minecraft:wither_skeleton");
		schema.registerSimple(map, "minecraft:wither_skull");
		method_15335(schema, map, "minecraft:wolf");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		method_15335(schema, map, "minecraft:zombie");
		schema.register(map, "minecraft:zombie_horse", string -> DSL.optionalFields("SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)));
		method_15335(schema, map, "minecraft:zombie_pigman");
		method_15335(schema, map, "minecraft:zombie_villager");
		return map;
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		method_15341(schema, map, "minecraft:furnace");
		method_15341(schema, map, "minecraft:chest");
		method_15341(schema, map, "minecraft:trapped_chest");
		schema.registerSimple(map, "minecraft:ender_chest");
		schema.register(map, "minecraft:jukebox", string -> DSL.optionalFields("RecordItem", class_3402.field_16592.in(schema)));
		method_15341(schema, map, "minecraft:dispenser");
		method_15341(schema, map, "minecraft:dropper");
		schema.registerSimple(map, "minecraft:sign");
		schema.register(map, "minecraft:mob_spawner", string -> class_3402.field_16599.in(schema));
		schema.register(map, "minecraft:piston", string -> DSL.optionalFields("blockState", class_3402.field_16593.in(schema)));
		method_15341(schema, map, "minecraft:brewing_stand");
		schema.registerSimple(map, "minecraft:enchanting_table");
		schema.registerSimple(map, "minecraft:end_portal");
		schema.registerSimple(map, "minecraft:beacon");
		schema.registerSimple(map, "minecraft:skull");
		schema.registerSimple(map, "minecraft:daylight_detector");
		method_15341(schema, map, "minecraft:hopper");
		schema.registerSimple(map, "minecraft:comparator");
		schema.registerSimple(map, "minecraft:banner");
		schema.registerSimple(map, "minecraft:structure_block");
		schema.registerSimple(map, "minecraft:end_gateway");
		schema.registerSimple(map, "minecraft:command_block");
		method_15341(schema, map, "minecraft:shulker_box");
		schema.registerSimple(map, "minecraft:bed");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		schema.registerType(false, class_3402.field_16582, DSL::remainder);
		schema.registerType(false, class_3402.field_16603, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(
			false,
			class_3402.field_16583,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", class_3402.field_16595.in(schema)),
					"Inventory",
					DSL.list(class_3402.field_16592.in(schema)),
					"EnderItems",
					DSL.list(class_3402.field_16592.in(schema)),
					DSL.optionalFields(
						"ShoulderEntityLeft",
						class_3402.field_16595.in(schema),
						"ShoulderEntityRight",
						class_3402.field_16595.in(schema),
						"recipeBook",
						DSL.optionalFields("recipes", DSL.list(class_3402.field_16603.in(schema)), "toBeDisplayed", DSL.list(class_3402.field_16603.in(schema)))
					)
				)
		);
		schema.registerType(
			false,
			class_3402.field_16584,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(class_3402.field_16595.in(schema)),
						"TileEntities",
						DSL.list(class_3402.field_16591.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", class_3402.field_16597.in(schema))),
						"Sections",
						DSL.list(DSL.optionalFields("Palette", DSL.list(class_3402.field_16593.in(schema))))
					)
				)
		);
		schema.registerType(true, class_3402.field_16591, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), map2));
		schema.registerType(
			true, class_3402.field_16595, () -> DSL.optionalFields("Passengers", DSL.list(class_3402.field_16595.in(schema)), class_3402.field_16596.in(schema))
		);
		schema.registerType(true, class_3402.field_16596, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), map));
		schema.registerType(
			true,
			class_3402.field_16592,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						class_3402.field_16598.in(schema),
						"tag",
						DSL.optionalFields(
							"EntityTag",
							class_3402.field_16595.in(schema),
							"BlockEntityTag",
							class_3402.field_16591.in(schema),
							"CanDestroy",
							DSL.list(class_3402.field_16597.in(schema)),
							"CanPlaceOn",
							DSL.list(class_3402.field_16597.in(schema))
						)
					),
					class_3448.field_16624,
					HookFunction.IDENTITY
				)
		);
		schema.registerType(false, class_3402.field_16585, () -> DSL.compoundList(DSL.list(class_3402.field_16592.in(schema))));
		schema.registerType(false, class_3402.field_16586, DSL::remainder);
		schema.registerType(
			false,
			class_3402.field_16587,
			() -> DSL.optionalFields(
					"entities",
					DSL.list(DSL.optionalFields("nbt", class_3402.field_16595.in(schema))),
					"blocks",
					DSL.list(DSL.optionalFields("nbt", class_3402.field_16591.in(schema))),
					"palette",
					DSL.list(class_3402.field_16593.in(schema))
				)
		);
		schema.registerType(false, class_3402.field_16597, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_3402.field_16598, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_3402.field_16593, DSL::remainder);
		Supplier<TypeTemplate> supplier = () -> DSL.compoundList(class_3402.field_16598.in(schema), DSL.constType(DSL.intType()));
		schema.registerType(
			false,
			class_3402.field_16588,
			() -> DSL.optionalFields(
					"stats",
					DSL.optionalFields(
						"minecraft:mined",
						DSL.compoundList(class_3402.field_16597.in(schema), DSL.constType(DSL.intType())),
						"minecraft:crafted",
						(TypeTemplate)supplier.get(),
						"minecraft:used",
						(TypeTemplate)supplier.get(),
						"minecraft:broken",
						(TypeTemplate)supplier.get(),
						"minecraft:picked_up",
						(TypeTemplate)supplier.get(),
						DSL.optionalFields(
							"minecraft:dropped",
							(TypeTemplate)supplier.get(),
							"minecraft:killed",
							DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.intType())),
							"minecraft:killed_by",
							DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.intType())),
							"minecraft:custom",
							DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType()))
						)
					)
				)
		);
		schema.registerType(
			false,
			class_3402.field_16589,
			() -> DSL.optionalFields(
					"data",
					DSL.optionalFields(
						"Features",
						DSL.compoundList(class_3402.field_16600.in(schema)),
						"Objectives",
						DSL.list(class_3402.field_16601.in(schema)),
						"Teams",
						DSL.list(class_3402.field_16602.in(schema))
					)
				)
		);
		schema.registerType(
			false,
			class_3402.field_16600,
			() -> DSL.optionalFields(
					"Children",
					DSL.list(
						DSL.optionalFields(
							"CA",
							class_3402.field_16593.in(schema),
							"CB",
							class_3402.field_16593.in(schema),
							"CC",
							class_3402.field_16593.in(schema),
							"CD",
							class_3402.field_16593.in(schema)
						)
					)
				)
		);
		schema.registerType(false, class_3402.field_16601, DSL::remainder);
		schema.registerType(false, class_3402.field_16602, DSL::remainder);
		schema.registerType(
			true,
			class_3402.field_16599,
			() -> DSL.optionalFields(
					"SpawnPotentials", DSL.list(DSL.fields("Entity", class_3402.field_16595.in(schema))), "SpawnData", class_3402.field_16595.in(schema)
				)
		);
		schema.registerType(
			false,
			class_3402.field_16590,
			() -> DSL.optionalFields(
					"minecraft:adventure/adventuring_time",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16604.in(schema), DSL.constType(DSL.string()))),
					"minecraft:adventure/kill_a_mob",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.string()))),
					"minecraft:adventure/kill_all_mobs",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.string()))),
					"minecraft:husbandry/bred_all_animals",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.string())))
				)
		);
		schema.registerType(false, class_3402.field_16604, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_3402.field_16594, () -> DSL.constType(DSL.namespacedString()));
	}
}
