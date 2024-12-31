package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class EntityHorseSplitFix extends EntityTransformFix {
	public EntityHorseSplitFix(Schema outputSchema, boolean changesType) {
		super("EntityHorseSplitFix", outputSchema, changesType);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
		Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
		if (Objects.equals("EntityHorse", choice)) {
			int i = dynamic.get("Type").asInt(0);

			String string5 = switch (i) {
				default -> "Horse";
				case 1 -> "Donkey";
				case 2 -> "Mule";
				case 3 -> "ZombieHorse";
				case 4 -> "SkeletonHorse";
			};
			dynamic.remove("Type");
			Type<?> type = (Type<?>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string5);
			return Pair.of(
				string5,
				(Typed)((Pair)typed.write().flatMap(type::readTyped).result().orElseThrow(() -> new IllegalStateException("Could not parse the new horse"))).getFirst()
			);
		} else {
			return Pair.of(choice, typed);
		}
	}
}
