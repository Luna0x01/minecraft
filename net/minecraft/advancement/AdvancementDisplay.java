package net.minecraft.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class AdvancementDisplay {
	private final Text title;
	private final Text description;
	private final ItemStack displayStack;
	private final Identifier field_16477;
	private final AdvancementType type;
	private final boolean field_16479;
	private final boolean field_16480;
	private final boolean field_16481;
	private float field_16482;
	private float field_16483;

	public AdvancementDisplay(
		ItemStack itemStack, Text text, Text text2, @Nullable Identifier identifier, AdvancementType advancementType, boolean bl, boolean bl2, boolean bl3
	) {
		this.title = text;
		this.description = text2;
		this.displayStack = itemStack;
		this.field_16477 = identifier;
		this.type = advancementType;
		this.field_16479 = bl;
		this.field_16480 = bl2;
		this.field_16481 = bl3;
	}

	public void method_15003(float f, float g) {
		this.field_16482 = f;
		this.field_16483 = g;
	}

	public Text getTitle() {
		return this.title;
	}

	public Text getDescription() {
		return this.description;
	}

	public ItemStack getDisplayStack() {
		return this.displayStack;
	}

	@Nullable
	public Identifier method_15010() {
		return this.field_16477;
	}

	public AdvancementType getAdvancementType() {
		return this.type;
	}

	public float method_15012() {
		return this.field_16482;
	}

	public float method_15013() {
		return this.field_16483;
	}

	public boolean method_15014() {
		return this.field_16479;
	}

	public boolean method_15015() {
		return this.field_16480;
	}

	public boolean method_15016() {
		return this.field_16481;
	}

	public static AdvancementDisplay fromJson(JsonObject object, JsonDeserializationContext ctx) {
		Text text = JsonHelper.deserialize(object, "title", ctx, Text.class);
		Text text2 = JsonHelper.deserialize(object, "description", ctx, Text.class);
		if (text != null && text2 != null) {
			ItemStack itemStack = getIconStack(JsonHelper.getObject(object, "icon"));
			Identifier identifier = object.has("background") ? new Identifier(JsonHelper.getString(object, "background")) : null;
			AdvancementType advancementType = object.has("frame") ? AdvancementType.fromString(JsonHelper.getString(object, "frame")) : AdvancementType.TASK;
			boolean bl = JsonHelper.getBoolean(object, "show_toast", true);
			boolean bl2 = JsonHelper.getBoolean(object, "announce_to_chat", true);
			boolean bl3 = JsonHelper.getBoolean(object, "hidden", false);
			return new AdvancementDisplay(itemStack, text, text2, identifier, advancementType, bl, bl2, bl3);
		} else {
			throw new JsonSyntaxException("Both title and description must be set");
		}
	}

	private static ItemStack getIconStack(JsonObject object) {
		if (!object.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = JsonHelper.getItem(object, "item");
			if (object.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack itemStack = new ItemStack(item);
				if (object.has("nbt")) {
					try {
						NbtCompound nbtCompound = StringNbtReader.parse(JsonHelper.asString(object.get("nbt"), "nbt"));
						itemStack.setNbt(nbtCompound);
					} catch (CommandSyntaxException var4) {
						throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
					}
				}

				return itemStack;
			}
		}
	}

	public void writeTo(PacketByteBuf buf) {
		buf.writeText(this.title);
		buf.writeText(this.description);
		buf.writeItemStack(this.displayStack);
		buf.writeEnumConstant(this.type);
		int i = 0;
		if (this.field_16477 != null) {
			i |= 1;
		}

		if (this.field_16479) {
			i |= 2;
		}

		if (this.field_16481) {
			i |= 4;
		}

		buf.writeInt(i);
		if (this.field_16477 != null) {
			buf.writeIdentifier(this.field_16477);
		}

		buf.writeFloat(this.field_16482);
		buf.writeFloat(this.field_16483);
	}

	public static AdvancementDisplay fromPacketByteBuf(PacketByteBuf buf) {
		Text text = buf.readText();
		Text text2 = buf.readText();
		ItemStack itemStack = buf.readItemStack();
		AdvancementType advancementType = buf.readEnumConstant(AdvancementType.class);
		int i = buf.readInt();
		Identifier identifier = (i & 1) != 0 ? buf.readIdentifier() : null;
		boolean bl = (i & 2) != 0;
		boolean bl2 = (i & 4) != 0;
		AdvancementDisplay advancementDisplay = new AdvancementDisplay(itemStack, text, text2, identifier, advancementType, bl, false, bl2);
		advancementDisplay.method_15003(buf.readFloat(), buf.readFloat());
		return advancementDisplay;
	}

	public JsonElement method_21313() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("icon", this.method_21314());
		jsonObject.add("title", Text.Serializer.method_20183(this.title));
		jsonObject.add("description", Text.Serializer.method_20183(this.description));
		jsonObject.addProperty("frame", this.type.getType());
		jsonObject.addProperty("show_toast", this.field_16479);
		jsonObject.addProperty("announce_to_chat", this.field_16480);
		jsonObject.addProperty("hidden", this.field_16481);
		if (this.field_16477 != null) {
			jsonObject.addProperty("background", this.field_16477.toString());
		}

		return jsonObject;
	}

	private JsonObject method_21314() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", Registry.ITEM.getId(this.displayStack.getItem()).toString());
		return jsonObject;
	}
}
