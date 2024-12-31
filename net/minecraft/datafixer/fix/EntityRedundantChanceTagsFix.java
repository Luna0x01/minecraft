package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_3402;

public class EntityRedundantChanceTagsFix extends DataFix {
	public EntityRedundantChanceTagsFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityRedundantChanceTagsFix", this.getInputSchema().getType(class_3402.field_16596), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Dynamic<?> dynamic2 = dynamic;
					if (Objects.equals(dynamic.get("HandDropChances"), Optional.of(dynamic.createList(Stream.generate(() -> dynamic2.createFloat(0.0F)).limit(2L))))) {
						dynamic = dynamic.remove("HandDropChances");
					}

					if (Objects.equals(dynamic.get("ArmorDropChances"), Optional.of(dynamic.createList(Stream.generate(() -> dynamic2.createFloat(0.0F)).limit(4L))))) {
						dynamic = dynamic.remove("ArmorDropChances");
					}

					return dynamic;
				})
		);
	}
}
