package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Schema99 extends Schema {
	private static final Logger LOGGER = LogManager.getLogger();
	static final Map<String, String> field_5748 = (Map<String, String>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
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
	protected static final HookFunction field_5747 = new HookFunction() {
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			return Schema99.method_5359(new Dynamic(dynamicOps, object), Schema99.field_5748, "ArmorStand");
		}
	};

	public Schema99(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	protected static TypeTemplate targetEquipment(Schema schema) {
		return DSL.optionalFields("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
	}

	protected static void targetEquipment(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, () -> targetEquipment(schema));
	}

	protected static void targetInTile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, () -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
	}

	protected static void targetDisplayTile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
	}

	protected static void targetItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		schema.register(map, "Item", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
		schema.registerSimple(map, "XPOrb");
		targetInTile(schema, map, "ThrownEgg");
		schema.registerSimple(map, "LeashKnot");
		schema.registerSimple(map, "Painting");
		schema.register(map, "Arrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
		schema.register(map, "TippedArrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
		schema.register(map, "SpectralArrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
		targetInTile(schema, map, "Snowball");
		targetInTile(schema, map, "Fireball");
		targetInTile(schema, map, "SmallFireball");
		targetInTile(schema, map, "ThrownEnderpearl");
		schema.registerSimple(map, "EyeOfEnderSignal");
		schema.register(
			map, "ThrownPotion", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema), "Potion", TypeReferences.ITEM_STACK.in(schema))
		);
		targetInTile(schema, map, "ThrownExpBottle");
		schema.register(map, "ItemFrame", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
		targetInTile(schema, map, "WitherSkull");
		schema.registerSimple(map, "PrimedTnt");
		schema.register(
			map, "FallingSand", string -> DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema))
		);
		schema.register(map, "FireworksRocketEntity", string -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)));
		schema.registerSimple(map, "Boat");
		schema.register(
			map, "Minecart", () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		targetDisplayTile(schema, map, "MinecartRideable");
		schema.register(
			map,
			"MinecartChest",
			string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		targetDisplayTile(schema, map, "MinecartFurnace");
		targetDisplayTile(schema, map, "MinecartTNT");
		schema.register(
			map, "MinecartSpawner", () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema))
		);
		schema.register(
			map,
			"MinecartHopper",
			string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		targetDisplayTile(schema, map, "MinecartCommandBlock");
		targetEquipment(schema, map, "ArmorStand");
		targetEquipment(schema, map, "Creeper");
		targetEquipment(schema, map, "Skeleton");
		targetEquipment(schema, map, "Spider");
		targetEquipment(schema, map, "Giant");
		targetEquipment(schema, map, "Zombie");
		targetEquipment(schema, map, "Slime");
		targetEquipment(schema, map, "Ghast");
		targetEquipment(schema, map, "PigZombie");
		schema.register(map, "Enderman", string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), targetEquipment(schema)));
		targetEquipment(schema, map, "CaveSpider");
		targetEquipment(schema, map, "Silverfish");
		targetEquipment(schema, map, "Blaze");
		targetEquipment(schema, map, "LavaSlime");
		targetEquipment(schema, map, "EnderDragon");
		targetEquipment(schema, map, "WitherBoss");
		targetEquipment(schema, map, "Bat");
		targetEquipment(schema, map, "Witch");
		targetEquipment(schema, map, "Endermite");
		targetEquipment(schema, map, "Guardian");
		targetEquipment(schema, map, "Pig");
		targetEquipment(schema, map, "Sheep");
		targetEquipment(schema, map, "Cow");
		targetEquipment(schema, map, "Chicken");
		targetEquipment(schema, map, "Squid");
		targetEquipment(schema, map, "Wolf");
		targetEquipment(schema, map, "MushroomCow");
		targetEquipment(schema, map, "SnowMan");
		targetEquipment(schema, map, "Ozelot");
		targetEquipment(schema, map, "VillagerGolem");
		schema.register(
			map,
			"EntityHorse",
			string -> DSL.optionalFields(
					"Items",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"ArmorItem",
					TypeReferences.ITEM_STACK.in(schema),
					"SaddleItem",
					TypeReferences.ITEM_STACK.in(schema),
					targetEquipment(schema)
				)
		);
		targetEquipment(schema, map, "Rabbit");
		schema.register(
			map,
			"Villager",
			string -> DSL.optionalFields(
					"Inventory",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"Offers",
					DSL.optionalFields(
						"Recipes",
						DSL.list(
							DSL.optionalFields(
								"buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)
							)
						)
					),
					targetEquipment(schema)
				)
		);
		schema.registerSimple(map, "EnderCrystal");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		targetEquipment(schema, map, "Shulker");
		return map;
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		targetItems(schema, map, "Furnace");
		targetItems(schema, map, "Chest");
		schema.registerSimple(map, "EnderChest");
		schema.register(map, "RecordPlayer", string -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema)));
		targetItems(schema, map, "Trap");
		targetItems(schema, map, "Dropper");
		schema.registerSimple(map, "Sign");
		schema.register(map, "MobSpawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
		schema.registerSimple(map, "Music");
		schema.registerSimple(map, "Piston");
		targetItems(schema, map, "Cauldron");
		schema.registerSimple(map, "EnchantTable");
		schema.registerSimple(map, "Airportal");
		schema.registerSimple(map, "Control");
		schema.registerSimple(map, "Beacon");
		schema.registerSimple(map, "Skull");
		schema.registerSimple(map, "DLDetector");
		targetItems(schema, map, "Hopper");
		schema.registerSimple(map, "Comparator");
		schema.register(map, "FlowerPot", string -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema))));
		schema.registerSimple(map, "Banner");
		schema.registerSimple(map, "Structure");
		schema.registerSimple(map, "EndGateway");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		schema.registerType(false, TypeReferences.LEVEL, DSL::remainder);
		schema.registerType(
			false,
			TypeReferences.PLAYER,
			() -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		schema.registerType(
			false,
			TypeReferences.CHUNK,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(TypeReferences.ENTITY_TREE.in(schema)),
						"TileEntities",
						DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema)))
					)
				)
		);
		schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), blockEntityTypes));
		schema.registerType(
			true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(schema), TypeReferences.ENTITY.in(schema))
		);
		schema.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
		schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), entityTypes));
		schema.registerType(
			true,
			TypeReferences.ITEM_STACK,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)),
						"tag",
						DSL.optionalFields(
							"EntityTag",
							TypeReferences.ENTITY_TREE.in(schema),
							"BlockEntityTag",
							TypeReferences.BLOCK_ENTITY.in(schema),
							"CanDestroy",
							DSL.list(TypeReferences.BLOCK_NAME.in(schema)),
							"CanPlaceOn",
							DSL.list(TypeReferences.BLOCK_NAME.in(schema)),
							"Items",
							DSL.list(TypeReferences.ITEM_STACK.in(schema))
						)
					),
					field_5747,
					HookFunction.IDENTITY
				)
		);
		schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
		schema.registerType(
			false, TypeReferences.BLOCK_NAME, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(IdentifierNormalizingSchema.getIdentifierType()))
		);
		schema.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
		schema.registerType(false, TypeReferences.STATS, DSL::remainder);
		schema.registerType(
			false,
			TypeReferences.SAVED_DATA,
			() -> DSL.optionalFields(
					"data",
					DSL.optionalFields(
						"Features",
						DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)),
						"Objectives",
						DSL.list(TypeReferences.OBJECTIVE.in(schema)),
						"Teams",
						DSL.list(TypeReferences.TEAM.in(schema))
					)
				)
		);
		schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
		schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
		schema.registerType(false, TypeReferences.TEAM, DSL::remainder);
		schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
		schema.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
		schema.registerType(true, TypeReferences.CHUNK_GENERATOR_SETTINGS, DSL::remainder);
		schema.registerType(false, TypeReferences.ENTITY_CHUNK, () -> DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema))));
	}

	protected static <T> T method_5359(Dynamic<T> dynamic, Map<String, String> map, String string) {
		return (T)dynamic.update("tag", dynamic2 -> dynamic2.update("BlockEntityTag", dynamic2x -> {
				String stringxx = (String)dynamic.get("id").asString().result().map(IdentifierNormalizingSchema::normalize).orElse("minecraft:air");
				if (!"minecraft:air".equals(stringxx)) {
					String string2 = (String)map.get(stringxx);
					if (string2 != null) {
						return dynamic2x.set("id", dynamic.createString(string2));
					}

					LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", stringxx);
				}

				return dynamic2x;
			}).update("EntityTag", dynamic2x -> {
				String string2 = dynamic.get("id").asString("");
				return "minecraft:armor_stand".equals(IdentifierNormalizingSchema.normalize(string2)) ? dynamic2x.set("id", dynamic.createString(string)) : dynamic2x;
			})).getValue();
	}
}
