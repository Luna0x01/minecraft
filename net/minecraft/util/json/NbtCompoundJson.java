package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
		return this == EMPTY ? true : this.method_14345(method_16546(entity));
	}

	public boolean method_14345(@Nullable NbtElement nbtElement) {
		return nbtElement == null ? this == EMPTY : this.nbt == null || NbtHelper.areEqual(this.nbt, nbtElement, true);
	}

	public JsonElement method_16545() {
		return (JsonElement)(this != EMPTY && this.nbt != null ? new JsonPrimitive(this.nbt.toString()) : JsonNull.INSTANCE);
	}

	public static NbtCompoundJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			NbtCompound nbtCompound;
			try {
				nbtCompound = StringNbtReader.parse(JsonHelper.asString(element, "nbt"));
			} catch (CommandSyntaxException var3) {
				throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
			}

			return new NbtCompoundJson(nbtCompound);
		} else {
			return EMPTY;
		}
	}

	public static NbtCompound method_16546(Entity entity) {
		NbtCompound nbtCompound = entity.toNbt(new NbtCompound());
		if (entity instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)entity).inventory.getMainHandStack();
			if (!itemStack.isEmpty()) {
				nbtCompound.put("SelectedItem", itemStack.toNbt(new NbtCompound()));
			}
		}

		return nbtCompound;
	}
}
