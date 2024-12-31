package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.class_3406;

public class EntityElderGuardianSplitFix extends class_3406 {
	public EntityElderGuardianSplitFix(Schema schema, boolean bl) {
		super("EntityElderGuardianSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> method_15251(String string, Dynamic<?> dynamic) {
		return Pair.of(Objects.equals(string, "Guardian") && dynamic.getBoolean("Elder") ? "ElderGuardian" : string, dynamic);
	}
}
