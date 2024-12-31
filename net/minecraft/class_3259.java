package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

public class class_3259 implements class_3262 {
	private final List<RecipeType> field_15903 = Lists.newArrayList();
	private long field_15904;
	private boolean field_15905;

	public class_3259(RecipeType recipeType) {
		this.field_15903.add(recipeType);
	}

	@Override
	public class_3262.class_3263 method_14486(class_3264 arg, long l) {
		if (this.field_15905) {
			this.field_15904 = l;
			this.field_15905 = false;
		}

		if (this.field_15903.isEmpty()) {
			return class_3262.class_3263.HIDE;
		} else {
			arg.method_14494().getTextureManager().bindTexture(field_15914);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			arg.drawTexture(0, 0, 0, 32, 160, 32);
			arg.method_14494().textRenderer.method_18355(I18n.translate("recipe.toast.title"), 30.0F, 7.0F, -11534256);
			arg.method_14494().textRenderer.method_18355(I18n.translate("recipe.toast.description"), 30.0F, 18.0F, -16777216);
			DiffuseLighting.enable();
			RecipeType recipeType = (RecipeType)this.field_15903.get((int)(l / (5000L / (long)this.field_15903.size()) % (long)this.field_15903.size()));
			ItemStack itemStack;
			if (recipeType instanceof class_3584) {
				itemStack = new ItemStack(Blocks.FURNACE);
			} else {
				itemStack = new ItemStack(Blocks.CRAFTING_TABLE);
			}

			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6F, 0.6F, 1.0F);
			arg.method_14494().getHeldItemRenderer().method_19374(null, itemStack, 3, 3);
			GlStateManager.popMatrix();
			arg.method_14494().getHeldItemRenderer().method_19374(null, recipeType.getOutput(), 8, 8);
			return l - this.field_15904 >= 5000L ? class_3262.class_3263.HIDE : class_3262.class_3263.SHOW;
		}
	}

	public void method_18431(RecipeType recipeType) {
		if (this.field_15903.add(recipeType)) {
			this.field_15905 = true;
		}
	}

	public static void method_14482(class_3264 arg, RecipeType recipeType) {
		class_3259 lv = arg.method_14493(class_3259.class, field_15915);
		if (lv == null) {
			arg.method_14491(new class_3259(recipeType));
		} else {
			lv.method_18431(recipeType);
		}
	}
}
