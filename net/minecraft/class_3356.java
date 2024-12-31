package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.RecipesUnlockS2CPacket;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3356 extends class_4471 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RecipeDispatcher field_22068;

	public class_3356(RecipeDispatcher recipeDispatcher) {
		this.field_22068 = recipeDispatcher;
	}

	public int method_21411(Collection<RecipeType> collection, ServerPlayerEntity serverPlayerEntity) {
		List<Identifier> list = Lists.newArrayList();
		int i = 0;

		for (RecipeType recipeType : collection) {
			Identifier identifier = recipeType.method_16202();
			if (!this.field_22062.contains(identifier) && !recipeType.method_14251()) {
				this.method_21395(identifier);
				this.method_21404(identifier);
				list.add(identifier);
				AchievementsAndCriterions.field_16334.method_14388(serverPlayerEntity, recipeType);
				i++;
			}
		}

		this.method_14996(RecipesUnlockS2CPacket.Action.ADD, serverPlayerEntity, list);
		return i;
	}

	public int method_21412(Collection<RecipeType> collection, ServerPlayerEntity serverPlayerEntity) {
		List<Identifier> list = Lists.newArrayList();
		int i = 0;

		for (RecipeType recipeType : collection) {
			Identifier identifier = recipeType.method_16202();
			if (this.field_22062.contains(identifier)) {
				this.method_21400(identifier);
				list.add(identifier);
				i++;
			}
		}

		this.method_14996(RecipesUnlockS2CPacket.Action.REMOVE, serverPlayerEntity, list);
		return i;
	}

	private void method_14996(RecipesUnlockS2CPacket.Action action, ServerPlayerEntity serverPlayerEntity, List<Identifier> list) {
		serverPlayerEntity.networkHandler
			.sendPacket(new RecipesUnlockS2CPacket(action, list, Collections.emptyList(), this.field_22064, this.field_22065, this.field_22066, this.field_22067));
	}

	public NbtCompound method_14999() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putBoolean("isGuiOpen", this.field_22064);
		nbtCompound.putBoolean("isFilteringCraftable", this.field_22065);
		nbtCompound.putBoolean("isFurnaceGuiOpen", this.field_22066);
		nbtCompound.putBoolean("isFurnaceFilteringCraftable", this.field_22067);
		NbtList nbtList = new NbtList();

		for (Identifier identifier : this.field_22062) {
			nbtList.add((NbtElement)(new NbtString(identifier.toString())));
		}

		nbtCompound.put("recipes", nbtList);
		NbtList nbtList2 = new NbtList();

		for (Identifier identifier2 : this.field_22063) {
			nbtList2.add((NbtElement)(new NbtString(identifier2.toString())));
		}

		nbtCompound.put("toBeDisplayed", nbtList2);
		return nbtCompound;
	}

	public void method_14994(NbtCompound nbtCompound) {
		this.field_22064 = nbtCompound.getBoolean("isGuiOpen");
		this.field_22065 = nbtCompound.getBoolean("isFilteringCraftable");
		this.field_22066 = nbtCompound.getBoolean("isFurnaceGuiOpen");
		this.field_22067 = nbtCompound.getBoolean("isFurnaceFilteringCraftable");
		NbtList nbtList = nbtCompound.getList("recipes", 8);

		for (int i = 0; i < nbtList.size(); i++) {
			Identifier identifier = new Identifier(nbtList.getString(i));
			RecipeType recipeType = this.field_22068.method_16207(identifier);
			if (recipeType == null) {
				LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier);
			} else {
				this.method_21394(recipeType);
			}
		}

		NbtList nbtList2 = nbtCompound.getList("toBeDisplayed", 8);

		for (int j = 0; j < nbtList2.size(); j++) {
			Identifier identifier2 = new Identifier(nbtList2.getString(j));
			RecipeType recipeType2 = this.field_22068.method_16207(identifier2);
			if (recipeType2 == null) {
				LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier2);
			} else {
				this.method_21410(recipeType2);
			}
		}
	}

	public void method_14997(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.networkHandler
			.sendPacket(
				new RecipesUnlockS2CPacket(
					RecipesUnlockS2CPacket.Action.INIT, this.field_22062, this.field_22063, this.field_22064, this.field_22065, this.field_22066, this.field_22067
				)
			);
	}
}
