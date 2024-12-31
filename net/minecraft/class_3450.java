package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3450 extends Schema {
	private static final Logger field_16626 = LogManager.getLogger();
	private static final Map<String, String> field_16627 = (Map<String, String>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("minecraft:furnace", "Furnace");
		hashMap.put("minecraft:lit_furnace", "Furnace");
		hashMap.put("minecraft:chest", "Chest");
		hashMap.put("minecraft:trapped_chest", "Chest");
		hashMap.put("minecraft:ender_chest", "EnderChest");
		hashMap.put("minecraft:jukebox", "RecordPlayer");
		hashMap.put("minecraft:dispenser", "Trap");
		hashMap.put("minecraft:dropper", "Dropper");
		hashMap.put("minecraft:sign", "Sign");
		hashMap.put("minecraft:mob_spawner", "MobSpawner");
		hashMap.put("minecraft:noteblock", "Music");
		hashMap.put("minecraft:brewing_stand", "Cauldron");
		hashMap.put("minecraft:enhanting_table", "EnchantTable");
		hashMap.put("minecraft:command_block", "CommandBlock");
		hashMap.put("minecraft:beacon", "Beacon");
		hashMap.put("minecraft:skull", "Skull");
		hashMap.put("minecraft:daylight_detector", "DLDetector");
		hashMap.put("minecraft:hopper", "Hopper");
		hashMap.put("minecraft:banner", "Banner");
		hashMap.put("minecraft:flower_pot", "FlowerPot");
		hashMap.put("minecraft:repeating_command_block", "CommandBlock");
		hashMap.put("minecraft:chain_command_block", "CommandBlock");
		hashMap.put("minecraft:standing_sign", "Sign");
		hashMap.put("minecraft:wall_sign", "Sign");
		hashMap.put("minecraft:piston_head", "Piston");
		hashMap.put("minecraft:daylight_detector_inverted", "DLDetector");
		hashMap.put("minecraft:unpowered_comparator", "Comparator");
		hashMap.put("minecraft:powered_comparator", "Comparator");
		hashMap.put("minecraft:wall_banner", "Banner");
		hashMap.put("minecraft:standing_banner", "Banner");
		hashMap.put("minecraft:structure_block", "Structure");
		hashMap.put("minecraft:end_portal", "Airportal");
		hashMap.put("minecraft:end_gateway", "EndGateway");
		hashMap.put("minecraft:shield", "Banner");
	});
	protected static final HookFunction field_16625 = new HookFunction() {
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			return class_3450.method_15439(new Dynamic(dynamicOps, object), class_3450.field_16627, "ArmorStand");
		}
	};

	public class_3450(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_15441(Schema schema) {
		return DSL.optionalFields("Equipment", DSL.list(class_3402.field_16592.in(schema)));
	}

	protected static void method_15443(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> method_15441(schema));
	}

	protected static void method_15449(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
	}

	protected static void method_15454(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema)));
	}

	protected static void method_15458(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> DSL.optionalFields("Items", DSL.list(class_3402.field_16592.in(schema))));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		schema.register(map, "Item", string -> DSL.optionalFields("Item", class_3402.field_16592.in(schema)));
		schema.registerSimple(map, "XPOrb");
		method_15449(schema, map, "ThrownEgg");
		schema.registerSimple(map, "LeashKnot");
		schema.registerSimple(map, "Painting");
		schema.register(map, "Arrow", string -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
		schema.register(map, "TippedArrow", string -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
		schema.register(map, "SpectralArrow", string -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema)));
		method_15449(schema, map, "Snowball");
		method_15449(schema, map, "Fireball");
		method_15449(schema, map, "SmallFireball");
		method_15449(schema, map, "ThrownEnderpearl");
		schema.registerSimple(map, "EyeOfEnderSignal");
		schema.register(map, "ThrownPotion", string -> DSL.optionalFields("inTile", class_3402.field_16597.in(schema), "Potion", class_3402.field_16592.in(schema)));
		method_15449(schema, map, "ThrownExpBottle");
		schema.register(map, "ItemFrame", string -> DSL.optionalFields("Item", class_3402.field_16592.in(schema)));
		method_15449(schema, map, "WitherSkull");
		schema.registerSimple(map, "PrimedTnt");
		schema.register(
			map, "FallingSand", string -> DSL.optionalFields("Block", class_3402.field_16597.in(schema), "TileEntityData", class_3402.field_16591.in(schema))
		);
		schema.register(map, "FireworksRocketEntity", string -> DSL.optionalFields("FireworksItem", class_3402.field_16592.in(schema)));
		schema.registerSimple(map, "Boat");
		schema.register(
			map, "Minecart", () -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		method_15454(schema, map, "MinecartRideable");
		schema.register(
			map, "MinecartChest", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		method_15454(schema, map, "MinecartFurnace");
		method_15454(schema, map, "MinecartTNT");
		schema.register(map, "MinecartSpawner", () -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), class_3402.field_16599.in(schema)));
		schema.register(
			map, "MinecartHopper", string -> DSL.optionalFields("DisplayTile", class_3402.field_16597.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		method_15454(schema, map, "MinecartCommandBlock");
		method_15443(schema, map, "ArmorStand");
		method_15443(schema, map, "Creeper");
		method_15443(schema, map, "Skeleton");
		method_15443(schema, map, "Spider");
		method_15443(schema, map, "Giant");
		method_15443(schema, map, "Zombie");
		method_15443(schema, map, "Slime");
		method_15443(schema, map, "Ghast");
		method_15443(schema, map, "PigZombie");
		schema.register(map, "Enderman", string -> DSL.optionalFields("carried", class_3402.field_16597.in(schema), method_15441(schema)));
		method_15443(schema, map, "CaveSpider");
		method_15443(schema, map, "Silverfish");
		method_15443(schema, map, "Blaze");
		method_15443(schema, map, "LavaSlime");
		method_15443(schema, map, "EnderDragon");
		method_15443(schema, map, "WitherBoss");
		method_15443(schema, map, "Bat");
		method_15443(schema, map, "Witch");
		method_15443(schema, map, "Endermite");
		method_15443(schema, map, "Guardian");
		method_15443(schema, map, "Pig");
		method_15443(schema, map, "Sheep");
		method_15443(schema, map, "Cow");
		method_15443(schema, map, "Chicken");
		method_15443(schema, map, "Squid");
		method_15443(schema, map, "Wolf");
		method_15443(schema, map, "MushroomCow");
		method_15443(schema, map, "SnowMan");
		method_15443(schema, map, "Ozelot");
		method_15443(schema, map, "VillagerGolem");
		schema.register(
			map,
			"EntityHorse",
			string -> DSL.optionalFields(
					"Items",
					DSL.list(class_3402.field_16592.in(schema)),
					"ArmorItem",
					class_3402.field_16592.in(schema),
					"SaddleItem",
					class_3402.field_16592.in(schema),
					method_15441(schema)
				)
		);
		method_15443(schema, map, "Rabbit");
		schema.register(
			map,
			"Villager",
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
					method_15441(schema)
				)
		);
		schema.registerSimple(map, "EnderCrystal");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		method_15443(schema, map, "Shulker");
		return map;
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		method_15458(schema, map, "Furnace");
		method_15458(schema, map, "Chest");
		schema.registerSimple(map, "EnderChest");
		schema.register(map, "RecordPlayer", string -> DSL.optionalFields("RecordItem", class_3402.field_16592.in(schema)));
		method_15458(schema, map, "Trap");
		method_15458(schema, map, "Dropper");
		schema.registerSimple(map, "Sign");
		schema.register(map, "MobSpawner", string -> class_3402.field_16599.in(schema));
		schema.registerSimple(map, "Music");
		schema.registerSimple(map, "Piston");
		method_15458(schema, map, "Cauldron");
		schema.registerSimple(map, "EnchantTable");
		schema.registerSimple(map, "Airportal");
		schema.registerSimple(map, "Control");
		schema.registerSimple(map, "Beacon");
		schema.registerSimple(map, "Skull");
		schema.registerSimple(map, "DLDetector");
		method_15458(schema, map, "Hopper");
		schema.registerSimple(map, "Comparator");
		schema.register(map, "FlowerPot", string -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), class_3402.field_16598.in(schema))));
		schema.registerSimple(map, "Banner");
		schema.registerSimple(map, "Structure");
		schema.registerSimple(map, "EndGateway");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		schema.registerType(false, class_3402.field_16582, DSL::remainder);
		schema.registerType(
			false,
			class_3402.field_16583,
			() -> DSL.optionalFields("Inventory", DSL.list(class_3402.field_16592.in(schema)), "EnderItems", DSL.list(class_3402.field_16592.in(schema)))
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
						DSL.list(DSL.fields("i", class_3402.field_16597.in(schema)))
					)
				)
		);
		schema.registerType(true, class_3402.field_16591, () -> DSL.taggedChoiceLazy("id", DSL.string(), map2));
		schema.registerType(true, class_3402.field_16595, () -> DSL.optionalFields("Riding", class_3402.field_16595.in(schema), class_3402.field_16596.in(schema)));
		schema.registerType(false, class_3402.field_16594, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(true, class_3402.field_16596, () -> DSL.taggedChoiceLazy("id", DSL.string(), map));
		schema.registerType(
			true,
			class_3402.field_16592,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						DSL.or(DSL.constType(DSL.intType()), class_3402.field_16598.in(schema)),
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
					field_16625,
					HookFunction.IDENTITY
				)
		);
		schema.registerType(false, class_3402.field_16586, DSL::remainder);
		schema.registerType(false, class_3402.field_16597, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString())));
		schema.registerType(false, class_3402.field_16598, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_3402.field_16588, DSL::remainder);
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
		schema.registerType(false, class_3402.field_16600, DSL::remainder);
		schema.registerType(false, class_3402.field_16601, DSL::remainder);
		schema.registerType(false, class_3402.field_16602, DSL::remainder);
		schema.registerType(true, class_3402.field_16599, DSL::remainder);
	}

	protected static <T> T method_15439(Dynamic<T> dynamic, Map<String, String> map, String string) {
		return (T)dynamic.update("tag", dynamic2 -> dynamic2.update("BlockEntityTag", dynamic2x -> {
				String stringxx = dynamic.getString("id");
				String string2 = (String)map.get(class_3415.method_15286(stringxx));
				if (string2 == null) {
					field_16626.warn("Unable to resolve BlockEntity for ItemStack: {}", stringxx);
					return dynamic2x;
				} else {
					return dynamic2x.set("id", dynamic.createString(string2));
				}
			}).update("EntityTag", dynamic2x -> {
				String string2 = dynamic.getString("id");
				return Objects.equals(class_3415.method_15286(string2), "minecraft:armor_stand") ? dynamic2x.set("id", dynamic.createString(string)) : dynamic2x;
			})).getValue();
	}
}
