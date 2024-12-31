package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityMinecartIdentifiersFix implements DataFix {
	private static final List<String> MINECARTS = Lists.newArrayList(
		new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace", "MinecartTNT", "MinecartSpawner", "MinecartHopper", "MinecartCommandBlock"}
	);

	@Override
	public int getVersion() {
		return 106;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("Minecart".equals(tag.getString("id"))) {
			String string = "MinecartRideable";
			int i = tag.getInt("Type");
			if (i > 0 && i < MINECARTS.size()) {
				string = (String)MINECARTS.get(i);
			}

			tag.putString("id", string);
			tag.remove("Type");
		}

		return tag;
	}
}
