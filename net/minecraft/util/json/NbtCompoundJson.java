package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;

public class NbtCompoundJson {
	public static final NbtCompoundJson EMPTY = new NbtCompoundJson(null);
	@Nullable
	private final NbtCompound nbt;

	public NbtCompoundJson(@Nullable NbtCompound nbtCompound) {
		this.nbt = nbtCompound;
	}

	public boolean method_14343(ItemStack stack) {
		return this == EMPTY ? true : this.method_14345(stack.getNbt());
	}

	public boolean method_14346(Entity entity) {
		return this == EMPTY ? true : this.method_14345(AbstractCommand.getEntityNbt(entity));
	}

	public boolean method_14345(@Nullable NbtElement nbt) {
		return nbt == null ? this == EMPTY : this.nbt == null || NbtHelper.matches(this.nbt, nbt, true);
	}

	public static NbtCompoundJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			NbtCompound nbtCompound;
			try {
				nbtCompound = StringNbtReader.parse(JsonHelper.asString(element, "nbt"));
			} catch (NbtException var3) {
				throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
			}

			return new NbtCompoundJson(nbtCompound);
		} else {
			return EMPTY;
		}
	}
}
