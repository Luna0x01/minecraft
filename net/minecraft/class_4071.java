package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Random;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4071 extends class_2795 {
	private static final Logger field_19799 = LogManager.getLogger();
	private final String field_19800;
	private final class_3082.class_3083 field_19801;
	private final byte field_19802;
	private final int field_19803;
	private final boolean field_19804;

	public class_4071(class_2816[] args, String string, class_3082.class_3083 arg, byte b, int i, boolean bl) {
		super(args);
		this.field_19800 = string;
		this.field_19801 = arg;
		this.field_19802 = b;
		this.field_19803 = i;
		this.field_19804 = bl;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		if (itemStack.getItem() != Items.MAP) {
			return itemStack;
		} else {
			BlockPos blockPos = arg.method_17979();
			if (blockPos == null) {
				return itemStack;
			} else {
				ServerWorld serverWorld = arg.method_17980();
				BlockPos blockPos2 = serverWorld.method_13688(this.field_19800, blockPos, this.field_19803, this.field_19804);
				if (blockPos2 != null) {
					ItemStack itemStack2 = FilledMapItem.method_16113(serverWorld, blockPos2.getX(), blockPos2.getZ(), this.field_19802, true, true);
					FilledMapItem.method_13664(serverWorld, itemStack2);
					MapState.method_13830(itemStack2, blockPos2, "+", this.field_19801);
					itemStack2.setCustomName(new TranslatableText("filled_map." + this.field_19800.toLowerCase(Locale.ROOT)));
					return itemStack2;
				} else {
					return itemStack;
				}
			}
		}
	}

	public static class class_4072 extends class_2795.class_2796<class_4071> {
		protected class_4072() {
			super(new Identifier("exploration_map"), class_4071.class);
		}

		public void method_12031(JsonObject jsonObject, class_4071 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("destination", jsonSerializationContext.serialize(arg.field_19800));
			jsonObject.add("decoration", jsonSerializationContext.serialize(arg.field_19801.toString().toLowerCase(Locale.ROOT)));
		}

		public class_4071 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			String string = jsonObject.has("destination") ? JsonHelper.getString(jsonObject, "destination") : "Buried_Treasure";
			string = class_3844.field_19152.containsKey(string.toLowerCase(Locale.ROOT)) ? string : "Buried_Treasure";
			String string2 = jsonObject.has("decoration") ? JsonHelper.getString(jsonObject, "decoration") : "mansion";
			class_3082.class_3083 lv = class_3082.class_3083.MANSION;

			try {
				lv = class_3082.class_3083.valueOf(string2.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException var10) {
				class_4071.field_19799.error("Error while parsing loot table decoration entry. Found {}. Defaulting to MANSION", string2);
			}

			byte b = jsonObject.has("zoom") ? JsonHelper.method_21505(jsonObject, "zoom") : 2;
			int i = jsonObject.has("search_radius") ? JsonHelper.getInt(jsonObject, "search_radius") : 50;
			boolean bl = jsonObject.has("skip_existing_chunks") ? JsonHelper.getBoolean(jsonObject, "skip_existing_chunks") : true;
			return new class_4071(args, string, lv, b, i, bl);
		}
	}
}
