package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class BlockEntityShulkerBoxColorFix implements DataFix {
	public static final String[] SHULKERS = new String[]{
		"minecraft:white_shulker_box",
		"minecraft:orange_shulker_box",
		"minecraft:magenta_shulker_box",
		"minecraft:light_blue_shulker_box",
		"minecraft:yellow_shulker_box",
		"minecraft:lime_shulker_box",
		"minecraft:pink_shulker_box",
		"minecraft:gray_shulker_box",
		"minecraft:silver_shulker_box",
		"minecraft:cyan_shulker_box",
		"minecraft:purple_shulker_box",
		"minecraft:blue_shulker_box",
		"minecraft:brown_shulker_box",
		"minecraft:green_shulker_box",
		"minecraft:red_shulker_box",
		"minecraft:black_shulker_box"
	};

	@Override
	public int getVersion() {
		return 813;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:shulker_box".equals(tag.getString("id")) && tag.contains("tag", 10)) {
			NbtCompound nbtCompound = tag.getCompound("tag");
			if (nbtCompound.contains("BlockEntityTag", 10)) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockEntityTag");
				if (nbtCompound2.getList("Items", 10).isEmpty()) {
					nbtCompound2.remove("Items");
				}

				int i = nbtCompound2.getInt("Color");
				nbtCompound2.remove("Color");
				if (nbtCompound2.isEmpty()) {
					nbtCompound.remove("BlockEntityTag");
				}

				if (nbtCompound.isEmpty()) {
					tag.remove("tag");
				}

				tag.putString("id", SHULKERS[i % 16]);
			}
		}

		return tag;
	}
}
