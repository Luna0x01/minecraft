package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_3402;
import net.minecraft.class_4519;

public class EntityHorseSplitFix extends class_4519 {
	public EntityHorseSplitFix(Schema schema, boolean bl) {
		super("EntityHorseSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> method_21739(String string, Typed<?> typed) {
		Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
		if (Objects.equals("EntityHorse", string)) {
			int i = dynamic.getInt("Type");
			String string2;
			switch (i) {
				case 0:
				default:
					string2 = "Horse";
					break;
				case 1:
					string2 = "Donkey";
					break;
				case 2:
					string2 = "Mule";
					break;
				case 3:
					string2 = "ZombieHorse";
					break;
				case 4:
					string2 = "SkeletonHorse";
			}

			dynamic.remove("Type");
			Type<?> type = (Type<?>)this.getOutputSchema().findChoiceType(class_3402.field_16596).types().get(string2);
			return Pair.of(string2, ((Optional)type.readTyped(typed.write()).getSecond()).orElseThrow(() -> new IllegalStateException("Could not parse the new horse")));
		} else {
			return Pair.of(string, typed);
		}
	}
}
