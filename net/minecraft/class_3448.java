package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;

public class class_3448 extends class_3415 {
	protected static final HookFunction field_16624 = new HookFunction() {
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			return class_3450.method_15439(new Dynamic(dynamicOps, object), class_3447.field_16622, "minecraft:armor_stand");
		}
	};

	public class_3448(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_15408(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> class_3416.method_15287(schema));
	}

	protected static void method_15412(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		schema.registerSimple(map, "minecraft:area_effect_cloud");
		method_15408(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", string -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
		method_15408(schema, map, "minecraft:bat");
		method_15408(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		method_15408(schema, map, "minecraft:cave_spider");
		schema.register(
			map,
			"minecraft:chest_minecart",
			string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		method_15408(schema, map, "minecraft:chicken");
		schema.register(map, "minecraft:commandblock_minecart", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema)));
		method_15408(schema, map, "minecraft:cow");
		method_15408(schema, map, "minecraft:creeper");
		schema.register(
			map,
			"minecraft:donkey",
			string -> DSL.optionalFields(
					"Items", DSL.list(class_3402.field_16592.in(schema)), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		schema.registerSimple(map, "minecraft:dragon_fireball");
		method_15412(schema, map, "minecraft:egg");
		method_15408(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		method_15408(schema, map, "minecraft:ender_dragon");
		schema.register(map, "minecraft:enderman", string -> DSL.optionalFields("carried", class_3402.field_16597.in(schema), class_3416.method_15287(schema)));
		method_15408(schema, map, "minecraft:endermite");
		method_15412(schema, map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(
			map,
			"minecraft:falling_block",
			string -> DSL.optionalFields("Block", class_3402.field_16597.in(schema), "TileEntityData", class_3402.field_16591.in(schema))
		);
		method_15412(schema, map, "minecraft:fireball");
		schema.register(map, "minecraft:fireworks_rocket", string -> DSL.optionalFields("FireworksItem", class_3402.field_16592.in(schema)));
		schema.register(map, "minecraft:furnace_minecart", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema)));
		method_15408(schema, map, "minecraft:ghast");
		method_15408(schema, map, "minecraft:giant");
		method_15408(schema, map, "minecraft:guardian");
		schema.register(
			map,
			"minecraft:hopper_minecart",
			string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		schema.register(
			map,
			"minecraft:horse",
			string -> DSL.optionalFields(
					"ArmorItem", class_3402.field_16592.in(schema), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		method_15408(schema, map, "minecraft:husk");
		schema.register(map, "minecraft:item", string -> DSL.optionalFields("Item", class_3402.field_16592.in(schema)));
		schema.register(map, "minecraft:item_frame", string -> DSL.optionalFields("Item", class_3402.field_16592.in(schema)));
		schema.registerSimple(map, "minecraft:leash_knot");
		method_15408(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema)));
		method_15408(schema, map, "minecraft:mooshroom");
		schema.register(
			map,
			"minecraft:mule",
			string -> DSL.optionalFields(
					"Items", DSL.list(class_3402.field_16592.in(schema)), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		method_15408(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		method_15408(schema, map, "minecraft:pig");
		method_15408(schema, map, "minecraft:polar_bear");
		schema.register(
			map, "minecraft:potion", string -> DSL.optionalFields("Potion", class_3402.field_16592.in(schema), "inTile", class_3402.field_16597.in(schema))
		);
		method_15408(schema, map, "minecraft:rabbit");
		method_15408(schema, map, "minecraft:sheep");
		method_15408(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		method_15408(schema, map, "minecraft:silverfish");
		method_15408(schema, map, "minecraft:skeleton");
		schema.register(
			map, "minecraft:skeleton_horse", string -> DSL.optionalFields("SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema))
		);
		method_15408(schema, map, "minecraft:slime");
		method_15412(schema, map, "minecraft:small_fireball");
		method_15412(schema, map, "minecraft:snowball");
		method_15408(schema, map, "minecraft:snowman");
		schema.register(
			map, "minecraft:spawner_minecart", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), class_3402.field_16599.in(schema))
		);
		schema.register(map, "minecraft:spectral_arrow", string -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
		method_15408(schema, map, "minecraft:spider");
		method_15408(schema, map, "minecraft:squid");
		method_15408(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(map, "minecraft:tnt_minecart", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema)));
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
		method_15408(schema, map, "minecraft:villager_golem");
		method_15408(schema, map, "minecraft:witch");
		method_15408(schema, map, "minecraft:wither");
		method_15408(schema, map, "minecraft:wither_skeleton");
		method_15412(schema, map, "minecraft:wither_skull");
		method_15408(schema, map, "minecraft:wolf");
		method_15412(schema, map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		method_15408(schema, map, "minecraft:zombie");
		schema.register(map, "minecraft:zombie_horse", string -> DSL.optionalFields("SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)));
		method_15408(schema, map, "minecraft:zombie_pigman");
		method_15408(schema, map, "minecraft:zombie_villager");
		schema.registerSimple(map, "minecraft:evocation_fangs");
		method_15408(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:illusion_illager");
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
		method_15408(schema, map, "minecraft:vex");
		method_15408(schema, map, "minecraft:vindication_illager");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
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
					field_16624,
					HookFunction.IDENTITY
				)
		);
	}
}
