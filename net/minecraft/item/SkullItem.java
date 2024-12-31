package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_3559;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends class_3559 {
	public SkullItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
	}

	@Override
	public Text getDisplayName(ItemStack stack) {
		if (stack.getItem() == Items.PLAYER_HEAD && stack.hasNbt()) {
			String string = null;
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound.contains("SkullOwner", 8)) {
				string = nbtCompound.getString("SkullOwner");
			} else if (nbtCompound.contains("SkullOwner", 10)) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("SkullOwner");
				if (nbtCompound2.contains("Name", 8)) {
					string = nbtCompound2.getString("Name");
				}
			}

			if (string != null) {
				return new TranslatableText(this.getTranslationKey() + ".named", string);
			}
		}

		return super.getDisplayName(stack);
	}

	@Override
	public boolean postProcessNbt(NbtCompound nbt) {
		super.postProcessNbt(nbt);
		if (nbt.contains("SkullOwner", 8) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, nbt.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			nbt.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
