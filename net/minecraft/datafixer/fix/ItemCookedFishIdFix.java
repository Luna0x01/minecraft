package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ItemCookedFishIdFix implements DataFix {
	private static final Identifier COOKED_FISH_IDENTIFIER = new Identifier("cooked_fished");

	@Override
	public int getVersion() {
		return 502;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if (tag.contains("id", 8) && COOKED_FISH_IDENTIFIER.equals(new Identifier(tag.getString("id")))) {
			tag.putString("id", "minecraft:cooked_fish");
		}

		return tag;
	}
}
