package net.minecraft.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.dimension.DimensionType;

public class class_3370 implements Criterion<class_3370.class_3372> {
	private static final Identifier field_16519 = new Identifier("changed_dimension");
	private final Map<AdvancementFile, class_3370.class_3371> field_16520 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_16519;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3370.class_3372> arg) {
		class_3370.class_3371 lv = (class_3370.class_3371)this.field_16520.get(file);
		if (lv == null) {
			lv = new class_3370.class_3371(file);
			this.field_16520.put(file, lv);
		}

		lv.method_15076(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3370.class_3372> arg) {
		class_3370.class_3371 lv = (class_3370.class_3371)this.field_16520.get(file);
		if (lv != null) {
			lv.method_15077(arg);
			if (lv.method_15074()) {
				this.field_16520.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_16520.remove(file);
	}

	public class_3370.class_3372 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.method_17199(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.method_17199(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new class_3370.class_3372(dimensionType, dimensionType2);
	}

	public void method_15071(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, DimensionType dimensionType2) {
		class_3370.class_3371 lv = (class_3370.class_3371)this.field_16520.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15075(dimensionType, dimensionType2);
		}
	}

	static class class_3371 {
		private final AdvancementFile field_16521;
		private final Set<Criterion.class_3353<class_3370.class_3372>> field_16522 = Sets.newHashSet();

		public class_3371(AdvancementFile advancementFile) {
			this.field_16521 = advancementFile;
		}

		public boolean method_15074() {
			return this.field_16522.isEmpty();
		}

		public void method_15076(Criterion.class_3353<class_3370.class_3372> arg) {
			this.field_16522.add(arg);
		}

		public void method_15077(Criterion.class_3353<class_3370.class_3372> arg) {
			this.field_16522.remove(arg);
		}

		public void method_15075(DimensionType dimensionType, DimensionType dimensionType2) {
			List<Criterion.class_3353<class_3370.class_3372>> list = null;

			for (Criterion.class_3353<class_3370.class_3372> lv : this.field_16522) {
				if (lv.method_14975().method_15078(dimensionType, dimensionType2)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3370.class_3372> lv2 : list) {
					lv2.method_14976(this.field_16521);
				}
			}
		}
	}

	public static class class_3372 extends AbstractCriterionInstance {
		@Nullable
		private final DimensionType field_16523;
		@Nullable
		private final DimensionType field_16524;

		public class_3372(@Nullable DimensionType dimensionType, @Nullable DimensionType dimensionType2) {
			super(class_3370.field_16519);
			this.field_16523 = dimensionType;
			this.field_16524 = dimensionType2;
		}

		public static class_3370.class_3372 method_15243(DimensionType dimensionType) {
			return new class_3370.class_3372(null, dimensionType);
		}

		public boolean method_15078(DimensionType dimensionType, DimensionType dimensionType2) {
			return this.field_16523 != null && this.field_16523 != dimensionType ? false : this.field_16524 == null || this.field_16524 == dimensionType2;
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_16523 != null) {
				jsonObject.addProperty("from", DimensionType.method_17196(this.field_16523).toString());
			}

			if (this.field_16524 != null) {
				jsonObject.addProperty("to", DimensionType.method_17196(this.field_16524).toString());
			}

			return jsonObject;
		}
	}
}
