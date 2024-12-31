package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityKeepPacked extends ChoiceFix {
	public BlockEntityKeepPacked(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityKeepPacked", TypeReferences.BLOCK_ENTITY, "DUMMY");
	}

	private static Dynamic<?> keepPacked(Dynamic<?> dynamic) {
		return dynamic.set("keepPacked", dynamic.createBoolean(true));
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), BlockEntityKeepPacked::keepPacked);
	}
}
