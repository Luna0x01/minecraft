package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

public abstract class class_3406 extends class_4519 {
	public class_3406(String string, Schema schema, boolean bl) {
		super(string, schema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> method_21739(String string, Typed<?> typed) {
		Pair<String, Dynamic<?>> pair = this.method_15251(string, (Dynamic<?>)typed.getOrCreate(DSL.remainderFinder()));
		return Pair.of(pair.getFirst(), typed.set(DSL.remainderFinder(), pair.getSecond()));
	}

	protected abstract Pair<String, Dynamic<?>> method_15251(String string, Dynamic<?> dynamic);
}
