package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2810 extends class_2795 {
	private final NbtCompound field_13237;

	public class_2810(class_2816[] args, NbtCompound nbtCompound) {
		super(args);
		this.field_13237 = nbtCompound;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		NbtCompound nbtCompound = itemStack.getNbt();
		if (nbtCompound == null) {
			nbtCompound = (NbtCompound)this.field_13237.copy();
		} else {
			nbtCompound.copyFrom(this.field_13237);
		}

		itemStack.setNbt(nbtCompound);
		return itemStack;
	}

	public static class class_2811 extends class_2795.class_2796<class_2810> {
		public class_2811() {
			super(new Identifier("set_nbt"), class_2810.class);
		}

		public void method_12031(JsonObject jsonObject, class_2810 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("tag", arg.field_13237.toString());
		}

		public class_2810 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(JsonHelper.getString(jsonObject, "tag"));
				return new class_2810(args, nbtCompound);
			} catch (NbtException var5) {
				throw new JsonSyntaxException(var5);
			}
		}
	}
}
