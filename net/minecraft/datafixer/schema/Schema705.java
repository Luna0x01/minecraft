package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema705 extends IdentifierNormalizingSchema {
	protected static final HookFunction field_5746 = new HookFunction() {
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			return Schema99.method_5359(new Dynamic(dynamicOps, object), Schema704.BLOCK_RENAMES, "minecraft:armor_stand");
		}
	};

	public Schema705(int i, Schema schema) {
		super(i, schema);
	}

	protected static void targetEntityItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, () -> Schema100.targetItems(schema));
	}

	protected static void targetInTile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, () -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
		schema.registerSimple(map, "minecraft:area_effect_cloud");
		targetEntityItems(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
		targetEntityItems(schema, map, "minecraft:bat");
		targetEntityItems(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		targetEntityItems(schema, map, "minecraft:cave_spider");
		schema.register(
			map,
			"minecraft:chest_minecart",
			string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		targetEntityItems(schema, map, "minecraft:chicken");
		schema.register(map, "minecraft:commandblock_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
		targetEntityItems(schema, map, "minecraft:cow");
		targetEntityItems(schema, map, "minecraft:creeper");
		schema.register(
			map,
			"minecraft:donkey",
			string -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				)
		);
		schema.registerSimple(map, "minecraft:dragon_fireball");
		targetInTile(schema, map, "minecraft:egg");
		targetEntityItems(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		targetEntityItems(schema, map, "minecraft:ender_dragon");
		schema.register(map, "minecraft:enderman", string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100.targetItems(schema)));
		targetEntityItems(schema, map, "minecraft:endermite");
		targetInTile(schema, map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(
			map,
			"minecraft:falling_block",
			string -> DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema))
		);
		targetInTile(schema, map, "minecraft:fireball");
		schema.register(map, "minecraft:fireworks_rocket", string -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)));
		schema.register(map, "minecraft:furnace_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
		targetEntityItems(schema, map, "minecraft:ghast");
		targetEntityItems(schema, map, "minecraft:giant");
		targetEntityItems(schema, map, "minecraft:guardian");
		schema.register(
			map,
			"minecraft:hopper_minecart",
			string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		schema.register(
			map,
			"minecraft:horse",
			string -> DSL.optionalFields(
					"ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				)
		);
		targetEntityItems(schema, map, "minecraft:husk");
		schema.register(map, "minecraft:item", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
		schema.register(map, "minecraft:item_frame", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
		schema.registerSimple(map, "minecraft:leash_knot");
		targetEntityItems(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
		targetEntityItems(schema, map, "minecraft:mooshroom");
		schema.register(
			map,
			"minecraft:mule",
			string -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				)
		);
		targetEntityItems(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		targetEntityItems(schema, map, "minecraft:pig");
		targetEntityItems(schema, map, "minecraft:polar_bear");
		schema.register(
			map, "minecraft:potion", string -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema), "inTile", TypeReferences.BLOCK_NAME.in(schema))
		);
		targetEntityItems(schema, map, "minecraft:rabbit");
		targetEntityItems(schema, map, "minecraft:sheep");
		targetEntityItems(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		targetEntityItems(schema, map, "minecraft:silverfish");
		targetEntityItems(schema, map, "minecraft:skeleton");
		schema.register(
			map, "minecraft:skeleton_horse", string -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema))
		);
		targetEntityItems(schema, map, "minecraft:slime");
		targetInTile(schema, map, "minecraft:small_fireball");
		targetInTile(schema, map, "minecraft:snowball");
		targetEntityItems(schema, map, "minecraft:snowman");
		schema.register(
			map,
			"minecraft:spawner_minecart",
			string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema))
		);
		schema.register(map, "minecraft:spectral_arrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
		targetEntityItems(schema, map, "minecraft:spider");
		targetEntityItems(schema, map, "minecraft:squid");
		targetEntityItems(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(map, "minecraft:tnt_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
		schema.register(
			map,
			"minecraft:villager",
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
					Schema100.targetItems(schema)
				)
		);
		targetEntityItems(schema, map, "minecraft:villager_golem");
		targetEntityItems(schema, map, "minecraft:witch");
		targetEntityItems(schema, map, "minecraft:wither");
		targetEntityItems(schema, map, "minecraft:wither_skeleton");
		targetInTile(schema, map, "minecraft:wither_skull");
		targetEntityItems(schema, map, "minecraft:wolf");
		targetInTile(schema, map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		targetEntityItems(schema, map, "minecraft:zombie");
		schema.register(
			map, "minecraft:zombie_horse", string -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema))
		);
		targetEntityItems(schema, map, "minecraft:zombie_pigman");
		targetEntityItems(schema, map, "minecraft:zombie_villager");
		schema.registerSimple(map, "minecraft:evocation_fangs");
		targetEntityItems(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:illusion_illager");
		schema.register(
			map,
			"minecraft:llama",
			string -> DSL.optionalFields(
					"Items",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"SaddleItem",
					TypeReferences.ITEM_STACK.in(schema),
					"DecorItem",
					TypeReferences.ITEM_STACK.in(schema),
					Schema100.targetItems(schema)
				)
		);
		schema.registerSimple(map, "minecraft:llama_spit");
		targetEntityItems(schema, map, "minecraft:vex");
		targetEntityItems(schema, map, "minecraft:vindication_illager");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", getIdentifierType(), entityTypes));
		schema.registerType(
			true,
			TypeReferences.ITEM_STACK,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						TypeReferences.ITEM_NAME.in(schema),
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
					field_5746,
					HookFunction.IDENTITY
				)
		);
	}
}
