package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;
import net.minecraft.class_3395;
import net.minecraft.class_3402;

public class EntityZombieVillagerTypeFix extends class_3395 {
	private static final Random RANDOM = new Random();

	public EntityZombieVillagerTypeFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityZombieVillagerTypeFix", class_3402.field_16596, "Zombie");
	}

	public Dynamic<?> method_21753(Dynamic<?> dynamic) {
		if (dynamic.getBoolean("IsVillager")) {
			if (!dynamic.get("ZombieType").isPresent()) {
				int i = this.clampType(dynamic.getInt("VillagerProfession", -1));
				if (i == -1) {
					i = this.clampType(RANDOM.nextInt(6));
				}

				dynamic = dynamic.set("ZombieType", dynamic.createInt(i));
			}

			dynamic = dynamic.remove("IsVillager");
		}

		return dynamic;
	}

	private int clampType(int type) {
		return type >= 0 && type < 6 ? type : -1;
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21753);
	}
}
