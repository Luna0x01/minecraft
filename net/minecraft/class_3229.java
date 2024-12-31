package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_3229 implements Criterion<class_3229.class_3231> {
	private static final Identifier field_15808 = new Identifier("recipe_unlocked");
	private final Map<AdvancementFile, class_3229.class_3230> field_15809 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15808;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3229.class_3231> arg) {
		class_3229.class_3230 lv = (class_3229.class_3230)this.field_15809.get(file);
		if (lv == null) {
			lv = new class_3229.class_3230(file);
			this.field_15809.put(file, lv);
		}

		lv.method_14393(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3229.class_3231> arg) {
		class_3229.class_3230 lv = (class_3229.class_3230)this.field_15809.get(file);
		if (lv != null) {
			lv.method_14394(arg);
			if (lv.method_14391()) {
				this.field_15809.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15809.remove(file);
	}

	public class_3229.class_3231 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
		RecipeType recipeType = RecipeDispatcher.get(identifier);
		if (recipeType == null) {
			throw new JsonSyntaxException("Unknown recipe '" + identifier + "'");
		} else {
			return new class_3229.class_3231(recipeType);
		}
	}

	public void method_14388(ServerPlayerEntity serverPlayerEntity, RecipeType recipeType) {
		class_3229.class_3230 lv = (class_3229.class_3230)this.field_15809.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14392(recipeType);
		}
	}

	static class class_3230 {
		private final AdvancementFile field_15810;
		private final Set<Criterion.class_3353<class_3229.class_3231>> field_15811 = Sets.newHashSet();

		public class_3230(AdvancementFile advancementFile) {
			this.field_15810 = advancementFile;
		}

		public boolean method_14391() {
			return this.field_15811.isEmpty();
		}

		public void method_14393(Criterion.class_3353<class_3229.class_3231> arg) {
			this.field_15811.add(arg);
		}

		public void method_14394(Criterion.class_3353<class_3229.class_3231> arg) {
			this.field_15811.remove(arg);
		}

		public void method_14392(RecipeType recipeType) {
			List<Criterion.class_3353<class_3229.class_3231>> list = null;

			for (Criterion.class_3353<class_3229.class_3231> lv : this.field_15811) {
				if (lv.method_14975().method_14395(recipeType)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3229.class_3231> lv2 : list) {
					lv2.method_14976(this.field_15810);
				}
			}
		}
	}

	public static class class_3231 extends AbstractCriterionInstance {
		private final RecipeType field_15812;

		public class_3231(RecipeType recipeType) {
			super(class_3229.field_15808);
			this.field_15812 = recipeType;
		}

		public boolean method_14395(RecipeType recipeType) {
			return this.field_15812 == recipeType;
		}
	}
}
