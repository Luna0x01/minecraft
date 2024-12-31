package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3187 extends Item {
	private static final Logger field_15667 = LogManager.getLogger();

	public class_3187() {
		this.setMaxCount(1);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		NbtCompound nbtCompound = itemStack.getNbt();
		if (!player.abilities.creativeMode) {
			player.equipStack(hand, ItemStack.EMPTY);
		}

		if (nbtCompound != null && nbtCompound.contains("Recipes", 9)) {
			if (!world.isClient) {
				NbtList nbtList = nbtCompound.getList("Recipes", 8);
				List<RecipeType> list = Lists.newArrayList();

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);
					RecipeType recipeType = RecipeDispatcher.get(new Identifier(string));
					if (recipeType == null) {
						field_15667.error("Invalid recipe: " + string);
						return new TypedActionResult<>(ActionResult.FAIL, itemStack);
					}

					list.add(recipeType);
				}

				player.method_14154(list);
				player.incrementStat(Stats.used(this));
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			field_15667.error("Tag not valid: " + nbtCompound);
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
