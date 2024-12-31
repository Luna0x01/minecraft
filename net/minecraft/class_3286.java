package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

public class class_3286 {
	private final List<RecipeType> field_16079 = Lists.newArrayList();
	private final Set<RecipeType> field_20456 = Sets.newHashSet();
	private final Set<RecipeType> field_20457 = Sets.newHashSet();
	private final Set<RecipeType> field_20458 = Sets.newHashSet();
	private boolean field_16083 = true;

	public boolean method_14625() {
		return !this.field_20458.isEmpty();
	}

	public void method_14628(class_4471 arg) {
		for (RecipeType recipeType : this.field_16079) {
			if (arg.method_21399(recipeType)) {
				this.field_20458.add(recipeType);
			}
		}
	}

	public void method_14626(class_3175 arg, int i, int j, class_4471 arg2) {
		for (int k = 0; k < this.field_16079.size(); k++) {
			RecipeType recipeType = (RecipeType)this.field_16079.get(k);
			boolean bl = recipeType.method_14250(i, j) && arg2.method_21399(recipeType);
			if (bl) {
				this.field_20457.add(recipeType);
			} else {
				this.field_20457.remove(recipeType);
			}

			if (bl && arg.method_14172(recipeType, null)) {
				this.field_20456.add(recipeType);
			} else {
				this.field_20456.remove(recipeType);
			}
		}
	}

	public boolean method_14627(RecipeType recipeType) {
		return this.field_20456.contains(recipeType);
	}

	public boolean method_14630() {
		return !this.field_20456.isEmpty();
	}

	public boolean method_14633() {
		return !this.field_20457.isEmpty();
	}

	public List<RecipeType> method_14634() {
		return this.field_16079;
	}

	public List<RecipeType> method_14629(boolean bl) {
		List<RecipeType> list = Lists.newArrayList();
		Set<RecipeType> set = bl ? this.field_20456 : this.field_20457;

		for (RecipeType recipeType : this.field_16079) {
			if (set.contains(recipeType)) {
				list.add(recipeType);
			}
		}

		return list;
	}

	public List<RecipeType> method_14632(boolean bl) {
		List<RecipeType> list = Lists.newArrayList();

		for (RecipeType recipeType : this.field_16079) {
			if (this.field_20457.contains(recipeType) && this.field_20456.contains(recipeType) == bl) {
				list.add(recipeType);
			}
		}

		return list;
	}

	public void method_14631(RecipeType recipeType) {
		this.field_16079.add(recipeType);
		if (this.field_16083) {
			ItemStack itemStack = ((RecipeType)this.field_16079.get(0)).getOutput();
			ItemStack itemStack2 = recipeType.getOutput();
			this.field_16083 = ItemStack.equalsIgnoreNbt(itemStack, itemStack2) && ItemStack.equalsIgnoreDamage(itemStack, itemStack2);
		}
	}

	public boolean method_14635() {
		return this.field_16083;
	}
}
