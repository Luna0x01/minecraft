package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.util.Identifier;

public class class_4471 {
	protected final Set<Identifier> field_22062 = Sets.newHashSet();
	protected final Set<Identifier> field_22063 = Sets.newHashSet();
	protected boolean field_22064;
	protected boolean field_22065;
	protected boolean field_22066;
	protected boolean field_22067;

	public void method_21396(class_4471 arg) {
		this.field_22062.clear();
		this.field_22063.clear();
		this.field_22062.addAll(arg.field_22062);
		this.field_22063.addAll(arg.field_22063);
	}

	public void method_21394(RecipeType recipeType) {
		if (!recipeType.method_14251()) {
			this.method_21395(recipeType.method_16202());
		}
	}

	protected void method_21395(Identifier identifier) {
		this.field_22062.add(identifier);
	}

	public boolean method_21399(@Nullable RecipeType recipeType) {
		return recipeType == null ? false : this.field_22062.contains(recipeType.method_16202());
	}

	public void method_21403(RecipeType recipeType) {
		this.method_21400(recipeType.method_16202());
	}

	protected void method_21400(Identifier identifier) {
		this.field_22062.remove(identifier);
		this.field_22063.remove(identifier);
	}

	public boolean method_21407(RecipeType recipeType) {
		return this.field_22063.contains(recipeType.method_16202());
	}

	public void method_21409(RecipeType recipeType) {
		this.field_22063.remove(recipeType.method_16202());
	}

	public void method_21410(RecipeType recipeType) {
		this.method_21404(recipeType.method_16202());
	}

	protected void method_21404(Identifier identifier) {
		this.field_22063.add(identifier);
	}

	public boolean method_21392() {
		return this.field_22064;
	}

	public void method_21397(boolean bl) {
		this.field_22064 = bl;
	}

	public boolean method_21393(class_3536 arg) {
		return arg instanceof FurnaceScreenHandler ? this.field_22067 : this.field_22065;
	}

	public boolean method_21398() {
		return this.field_22065;
	}

	public void method_21401(boolean bl) {
		this.field_22065 = bl;
	}

	public boolean method_21402() {
		return this.field_22066;
	}

	public void method_21405(boolean bl) {
		this.field_22066 = bl;
	}

	public boolean method_21406() {
		return this.field_22067;
	}

	public void method_21408(boolean bl) {
		this.field_22067 = bl;
	}
}
