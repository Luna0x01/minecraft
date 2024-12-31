package net.minecraft.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Arrays;
import net.minecraft.class_2782;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.Function;
import net.minecraft.sound.Sounds;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
		player.addExperience(this.field_16304);
		class_2782 lv = new class_2782.class_2783(player.getServerWorld()).method_11997(player).method_11994();
		boolean bl = false;

		for (Identifier identifier : this.field_16305) {
			for (ItemStack itemStack : player.world.method_11487().method_12006(identifier).method_11981(player.getRandom(), lv)) {
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
						itemEntity.setOwner(player.getTranslationKey());
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

		final MinecraftServer minecraftServer = player.server;
		Function function = this.field_16307.method_14540(minecraftServer.method_14911());
		if (function != null) {
			CommandSource commandSource = new CommandSource() {
				@Override
				public String getTranslationKey() {
					return player.getTranslationKey();
				}

				@Override
				public Text getName() {
					return player.getName();
				}

				@Override
				public void sendMessage(Text text) {
				}

				@Override
				public boolean canUseCommand(int permissionLevel, String commandLiteral) {
					return permissionLevel <= 2;
				}

				@Override
				public BlockPos getBlockPos() {
					return player.getBlockPos();
				}

				@Override
				public Vec3d getPos() {
					return player.getPos();
				}

				@Override
				public World getWorld() {
					return player.world;
				}

				@Override
				public Entity getEntity() {
					return player;
				}

				@Override
				public boolean sendCommandFeedback() {
					return minecraftServer.worlds[0].getGameRules().getBoolean("commandBlockOutput");
				}

				@Override
				public void setStat(CommandStats.Type statsType, int value) {
					player.setStat(statsType, value);
				}

				@Override
				public MinecraftServer getMinecraftServer() {
					return player.getMinecraftServer();
				}
			};
			minecraftServer.method_14911().execute(function, commandSource);
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
				RecipeType recipeType = RecipeDispatcher.get(identifiers2[k]);
				if (recipeType == null) {
					throw new JsonSyntaxException("Unknown recipe '" + identifiers2[k] + "'");
				}
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
}
