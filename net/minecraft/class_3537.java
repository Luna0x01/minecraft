package net.minecraft;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public interface class_3537 {
	void method_14210(@Nullable RecipeType recipeType);

	@Nullable
	RecipeType method_14211();

	default void method_15986(PlayerEntity playerEntity) {
		RecipeType recipeType = this.method_14211();
		if (recipeType != null && !recipeType.method_14251()) {
			playerEntity.method_15927(Lists.newArrayList(new RecipeType[]{recipeType}));
			this.method_14210(null);
		}
	}

	default boolean method_15985(World world, ServerPlayerEntity serverPlayerEntity, @Nullable RecipeType recipeType) {
		if (recipeType == null
			|| !recipeType.method_14251() && world.getGameRules().getBoolean("doLimitedCrafting") && !serverPlayerEntity.method_14965().method_21399(recipeType)) {
			return false;
		} else {
			this.method_14210(recipeType);
			return true;
		}
	}
}
