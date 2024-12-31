package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2782;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.Function;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class AdvancementRewards {
	public static final AdvancementRewards REWARDS = new AdvancementRewards(0, new Identifier[0], new Identifier[0], Function.FunctionIdentifier.EMPTY);
	private final int field_16304;
	private final Identifier[] field_16305;
	private final Identifier[] field_16306;
	private final Function.FunctionIdentifier field_16307;

	public AdvancementRewards(int i, Identifier[] identifiers, Identifier[] identifiers2, Function.FunctionIdentifier functionIdentifier) {
		this.field_16304 = i;
		this.field_16305 = identifiers;
		this.field_16306 = identifiers2;
		this.field_16307 = functionIdentifier;
	}

	public void method_14859(ServerPlayerEntity player) {
		player.method_15934(this.field_16304);
		class_2782 lv = new class_2782.class_2783(player.getServerWorld()).method_11997(player).method_17981(new BlockPos(player)).method_11994();
		boolean bl = false;

		for (Identifier identifier : this.field_16305) {
			for (ItemStack itemStack : player.server.method_20334().method_12006(identifier).method_11981(player.getRandom(), lv)) {
				if (player.method_13617(itemStack)) {
					player.world
						.playSound(
							null,
							player.x,
							player.y,
							player.z,
							Sounds.ENTITY_ITEM_PICKUP,
							SoundCategory.PLAYERS,
							0.2F,
							((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					bl = true;
				} else {
					ItemEntity itemEntity = player.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.method_15847(player.getUuid());
					}
				}
			}
		}

		if (bl) {
			player.playerScreenHandler.sendContentUpdates();
		}

		if (this.field_16306.length > 0) {
			player.method_14155(this.field_16306);
		}

		MinecraftServer minecraftServer = player.server;
		Function function = this.field_16307.method_14540(minecraftServer.method_14911());
		if (function != null) {
			minecraftServer.method_14911().method_14944(function, player.method_15582().method_17448().method_17449(2));
		}
	}

	public String toString() {
		return "AdvancementRewards{experience="
			+ this.field_16304
			+ ", loot="
			+ Arrays.toString(this.field_16305)
			+ ", recipes="
			+ Arrays.toString(this.field_16306)
			+ ", function="
			+ this.field_16307
			+ '}';
	}

	public JsonElement method_20386() {
		if (this == REWARDS) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_16304 != 0) {
				jsonObject.addProperty("experience", this.field_16304);
			}

			if (this.field_16305.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (Identifier identifier : this.field_16305) {
					jsonArray.add(identifier.toString());
				}

				jsonObject.add("loot", jsonArray);
			}

			if (this.field_16306.length > 0) {
				JsonArray jsonArray2 = new JsonArray();

				for (Identifier identifier2 : this.field_16306) {
					jsonArray2.add(identifier2.toString());
				}

				jsonObject.add("recipes", jsonArray2);
			}

			if (this.field_16307.method_17358() != null) {
				jsonObject.addProperty("function", this.field_16307.method_17358().toString());
			}

			return jsonObject;
		}
	}

	public static class class_3338 implements JsonDeserializer<AdvancementRewards> {
		public AdvancementRewards deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "rewards");
			int i = JsonHelper.getInt(jsonObject, "experience", 0);
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "loot", new JsonArray());
			Identifier[] identifiers = new Identifier[jsonArray.size()];

			for (int j = 0; j < identifiers.length; j++) {
				identifiers[j] = new Identifier(JsonHelper.asString(jsonArray.get(j), "loot[" + j + "]"));
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "recipes", new JsonArray());
			Identifier[] identifiers2 = new Identifier[jsonArray2.size()];

			for (int k = 0; k < identifiers2.length; k++) {
				identifiers2[k] = new Identifier(JsonHelper.asString(jsonArray2.get(k), "recipes[" + k + "]"));
			}

			Function.FunctionIdentifier functionIdentifier;
			if (jsonObject.has("function")) {
				functionIdentifier = new Function.FunctionIdentifier(new Identifier(JsonHelper.getString(jsonObject, "function")));
			} else {
				functionIdentifier = Function.FunctionIdentifier.EMPTY;
			}

			return new AdvancementRewards(i, identifiers, identifiers2, functionIdentifier);
		}
	}

	public static class class_4395 {
		private int field_21644;
		private final List<Identifier> field_21645 = Lists.newArrayList();
		private final List<Identifier> field_21646 = Lists.newArrayList();
		@Nullable
		private Identifier field_21647;

		public static AdvancementRewards.class_4395 method_20388(int i) {
			return new AdvancementRewards.class_4395().method_20389(i);
		}

		public AdvancementRewards.class_4395 method_20389(int i) {
			this.field_21644 += i;
			return this;
		}

		public static AdvancementRewards.class_4395 method_20390(Identifier identifier) {
			return new AdvancementRewards.class_4395().method_20391(identifier);
		}

		public AdvancementRewards.class_4395 method_20391(Identifier identifier) {
			this.field_21646.add(identifier);
			return this;
		}

		public AdvancementRewards method_20387() {
			return new AdvancementRewards(
				this.field_21644,
				(Identifier[])this.field_21645.toArray(new Identifier[0]),
				(Identifier[])this.field_21646.toArray(new Identifier[0]),
				this.field_21647 == null ? Function.FunctionIdentifier.EMPTY : new Function.FunctionIdentifier(this.field_21647)
			);
		}
	}
}
