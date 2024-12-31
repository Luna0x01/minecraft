package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3416 extends Schema {
	public class_3416(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_15287(Schema schema) {
		return DSL.optionalFields("ArmorItems", DSL.list(class_3402.field_16592.in(schema)), "HandItems", DSL.list(class_3402.field_16592.in(schema)));
	}

	protected static void method_15289(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> method_15287(schema));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_15289(schema, map, "ArmorStand");
		method_15289(schema, map, "Creeper");
		method_15289(schema, map, "Skeleton");
		method_15289(schema, map, "Spider");
		method_15289(schema, map, "Giant");
		method_15289(schema, map, "Zombie");
		method_15289(schema, map, "Slime");
		method_15289(schema, map, "Ghast");
		method_15289(schema, map, "PigZombie");
		schema.register(map, "Enderman", string -> DSL.optionalFields("carried", class_3402.field_16597.in(schema), method_15287(schema)));
		method_15289(schema, map, "CaveSpider");
		method_15289(schema, map, "Silverfish");
		method_15289(schema, map, "Blaze");
		method_15289(schema, map, "LavaSlime");
		method_15289(schema, map, "EnderDragon");
		method_15289(schema, map, "WitherBoss");
		method_15289(schema, map, "Bat");
		method_15289(schema, map, "Witch");
		method_15289(schema, map, "Endermite");
		method_15289(schema, map, "Guardian");
		method_15289(schema, map, "Pig");
		method_15289(schema, map, "Sheep");
		method_15289(schema, map, "Cow");
		method_15289(schema, map, "Chicken");
		method_15289(schema, map, "Squid");
		method_15289(schema, map, "Wolf");
		method_15289(schema, map, "MushroomCow");
		method_15289(schema, map, "SnowMan");
		method_15289(schema, map, "Ozelot");
		method_15289(schema, map, "VillagerGolem");
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
					method_15287(schema)
				)
		);
		method_15289(schema, map, "Rabbit");
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
					method_15287(schema)
				)
		);
		method_15289(schema, map, "Shulker");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
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
		schema.registerType(false, class_3402.field_16593, DSL::remainder);
	}
}
