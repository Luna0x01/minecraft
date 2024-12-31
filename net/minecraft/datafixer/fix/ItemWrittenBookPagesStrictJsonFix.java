package net.minecraft.datafixer.fix;

import com.google.gson.JsonParseException;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.JsonHelper;

public class ItemWrittenBookPagesStrictJsonFix implements DataFix {
	@Override
	public int getVersion() {
		return 165;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:written_book".equals(tag.getString("id"))) {
			NbtCompound nbtCompound = tag.getCompound("tag");
			if (nbtCompound.contains("pages", 9)) {
				NbtList nbtList = nbtCompound.getList("pages", 8);

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);
					Text text = null;
					if (!"null".equals(string) && !ChatUtil.isEmpty(string)) {
						if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
							try {
								text = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
								if (text == null) {
									text = new LiteralText("");
								}
							} catch (JsonParseException var10) {
							}

							if (text == null) {
								try {
									text = Text.Serializer.deserializeText(string);
								} catch (JsonParseException var9) {
								}
							}

							if (text == null) {
								try {
									text = Text.Serializer.lenientDeserializeText(string);
								} catch (JsonParseException var8) {
								}
							}

							if (text == null) {
								text = new LiteralText(string);
							}
						} else {
							text = new LiteralText(string);
						}
					} else {
						text = new LiteralText("");
					}

					nbtList.set(i, new NbtString(Text.Serializer.serialize(text)));
				}

				nbtCompound.put("pages", nbtList);
			}
		}

		return tag;
	}
}
