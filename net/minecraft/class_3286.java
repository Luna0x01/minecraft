package net.minecraft;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

public class class_3286 {
	private List<RecipeType> field_16079 = Lists.newArrayList();
	private final BitSet field_16080 = new BitSet();
	private final BitSet field_16081 = new BitSet();
	private final BitSet field_16082 = new BitSet();
	private boolean field_16083 = true;

	public boolean method_14625() {
		return !this.field_16082.isEmpty();
	}

	public void method_14628(class_3355 arg) {
		for (int i = 0; i < this.field_16079.size(); i++) {
			this.field_16082.set(i, arg.method_14987((RecipeType)this.field_16079.get(i)));
		}
	}

	public void method_14626(class_3175 arg, int i, int j, class_3355 arg2) {
		for (int k = 0; k < this.field_16079.size(); k++) {
			RecipeType recipeType = (RecipeType)this.field_16079.get(k);
			boolean bl = recipeType.method_14250(i, j) && arg2.method_14987(recipeType);
			this.field_16081.set(k, bl);
			this.field_16080.set(k, bl && arg.method_14172(recipeType, null));
		}
	}

	public boolean method_14627(RecipeType recipeType) {
		return this.field_16080.get(this.field_16079.indexOf(recipeType));
	}

	public boolean method_14630() {
		return !this.field_16080.isEmpty();
	}

	public boolean method_14633() {
		return !this.field_16081.isEmpty();
	}

	public List<RecipeType> method_14634() {
		return this.field_16079;
	}

	public List<RecipeType> method_14629(boolean bl) {
		List<RecipeType> list = Lists.newArrayList();

		for (int i = this.field_16082.nextSetBit(0); i >= 0; i = this.field_16082.nextSetBit(i + 1)) {
			if ((bl ? this.field_16080 : this.field_16081).get(i)) {
				list.add(this.field_16079.get(i));
			}
		}

		return list;
	}

	public List<RecipeType> method_14632(boolean bl) {
		List<RecipeType> list = Lists.newArrayList();

		for (int i = this.field_16082.nextSetBit(0); i >= 0; i = this.field_16082.nextSetBit(i + 1)) {
			if (this.field_16081.get(i) && this.field_16080.get(i) == bl) {
				list.add(this.field_16079.get(i));
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
