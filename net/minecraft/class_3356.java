package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.RecipesUnlockS2CPacket;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3356 extends class_3355 {
	private static final Logger LOGGER = LogManager.getLogger();

	public void method_14995(List<RecipeType> list, ServerPlayerEntity serverPlayerEntity) {
		List<RecipeType> list2 = Lists.newArrayList();

		for (RecipeType recipeType : list) {
			if (!this.field_16469.get(method_14990(recipeType)) && !recipeType.method_14251()) {
				this.method_14983(recipeType);
				this.method_14993(recipeType);
				list2.add(recipeType);
				AchievementsAndCriterions.field_16334.method_14388(serverPlayerEntity, recipeType);
			}
		}

		this.method_14996(RecipesUnlockS2CPacket.Action.ADD, serverPlayerEntity, list2);
	}

	public void method_14998(List<RecipeType> list, ServerPlayerEntity serverPlayerEntity) {
		List<RecipeType> list2 = Lists.newArrayList();

		for (RecipeType recipeType : list) {
			if (this.field_16469.get(method_14990(recipeType))) {
				this.method_14989(recipeType);
				list2.add(recipeType);
			}
		}

		this.method_14996(RecipesUnlockS2CPacket.Action.REMOVE, serverPlayerEntity, list2);
	}

	private void method_14996(RecipesUnlockS2CPacket.Action action, ServerPlayerEntity serverPlayerEntity, List<RecipeType> list) {
		serverPlayerEntity.networkHandler.sendPacket(new RecipesUnlockS2CPacket(action, list, Collections.emptyList(), this.bookOpen, this.filterActive));
	}

	public NbtCompound method_14999() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putBoolean("isGuiOpen", this.bookOpen);
		nbtCompound.putBoolean("isFilteringCraftable", this.filterActive);
		NbtList nbtList = new NbtList();

		for (RecipeType recipeType : this.method_15000()) {
			nbtList.add(new NbtString(RecipeDispatcher.REGISTRY.getIdentifier(recipeType).toString()));
		}

		nbtCompound.put("recipes", nbtList);
		NbtList nbtList2 = new NbtList();

		for (RecipeType recipeType2 : this.method_15001()) {
			nbtList2.add(new NbtString(RecipeDispatcher.REGISTRY.getIdentifier(recipeType2).toString()));
		}

		nbtCompound.put("toBeDisplayed", nbtList2);
		return nbtCompound;
	}

	public void method_14994(NbtCompound nbtCompound) {
		this.bookOpen = nbtCompound.getBoolean("isGuiOpen");
		this.filterActive = nbtCompound.getBoolean("isFilteringCraftable");
		NbtList nbtList = nbtCompound.getList("recipes", 8);

		for (int i = 0; i < nbtList.size(); i++) {
			Identifier identifier = new Identifier(nbtList.getString(i));
			RecipeType recipeType = RecipeDispatcher.get(identifier);
			if (recipeType == null) {
				LOGGER.info("Tried to load unrecognized recipe: {} removed now.", identifier);
			} else {
				this.method_14983(recipeType);
			}
		}

		NbtList nbtList2 = nbtCompound.getList("toBeDisplayed", 8);

		for (int j = 0; j < nbtList2.size(); j++) {
			Identifier identifier2 = new Identifier(nbtList2.getString(j));
			RecipeType recipeType2 = RecipeDispatcher.get(identifier2);
			if (recipeType2 == null) {
				LOGGER.info("Tried to load unrecognized recipe: {} removed now.", identifier2);
			} else {
				this.method_14993(recipeType2);
			}
		}
	}

	private List<RecipeType> method_15000() {
		List<RecipeType> list = Lists.newArrayList();

		for (int i = this.field_16469.nextSetBit(0); i >= 0; i = this.field_16469.nextSetBit(i + 1)) {
			list.add(RecipeDispatcher.REGISTRY.getByRawId(i));
		}

		return list;
	}

	private List<RecipeType> method_15001() {
		List<RecipeType> list = Lists.newArrayList();

		for (int i = this.field_16470.nextSetBit(0); i >= 0; i = this.field_16470.nextSetBit(i + 1)) {
			list.add(RecipeDispatcher.REGISTRY.getByRawId(i));
		}

		return list;
	}

	public void method_14997(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.networkHandler
			.sendPacket(new RecipesUnlockS2CPacket(RecipesUnlockS2CPacket.Action.INIT, this.method_15000(), this.method_15001(), this.bookOpen, this.filterActive));
	}
}
