package net.minecraft.datafixer.fix;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_3402;

public class EntityHealthFix extends DataFix {
	private static final Set<String> ENTITIES = Sets.newHashSet(
		new String[]{
			"ArmorStand",
			"Bat",
			"Blaze",
			"CaveSpider",
			"Chicken",
			"Cow",
			"Creeper",
			"EnderDragon",
			"Enderman",
			"Endermite",
			"EntityHorse",
			"Ghast",
			"Giant",
			"Guardian",
			"LavaSlime",
			"MushroomCow",
			"Ozelot",
			"Pig",
			"PigZombie",
			"Rabbit",
			"Sheep",
			"Shulker",
			"Silverfish",
			"Skeleton",
			"Slime",
			"SnowMan",
			"Spider",
			"Squid",
			"Villager",
			"VillagerGolem",
			"Witch",
			"WitherBoss",
			"Wolf",
			"Zombie"
		}
	);

	public EntityHealthFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public Dynamic<?> method_21711(Dynamic<?> dynamic) {
		Optional<Number> optional = dynamic.get("HealF").flatMap(Dynamic::getNumberValue);
		Optional<Number> optional2 = dynamic.get("Health").flatMap(Dynamic::getNumberValue);
		float f;
		if (optional.isPresent()) {
			f = ((Number)optional.get()).floatValue();
			dynamic = dynamic.remove("HealF");
		} else {
			if (!optional2.isPresent()) {
				return dynamic;
			}

			f = ((Number)optional2.get()).floatValue();
		}

		return dynamic.set("Health", dynamic.createFloat(f));
	}

	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityHealthFix", this.getInputSchema().getType(class_3402.field_16596), typed -> typed.update(DSL.remainderFinder(), this::method_21711)
		);
	}
}
