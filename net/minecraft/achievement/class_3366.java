package net.minecraft.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class class_3366 implements Criterion<class_3366.class_3368> {
	private static final Identifier field_16514 = new Identifier("brewed_potion");
	private final Map<AdvancementFile, class_3366.class_3367> field_16515 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_16514;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3366.class_3368> arg) {
		class_3366.class_3367 lv = (class_3366.class_3367)this.field_16515.get(file);
		if (lv == null) {
			lv = new class_3366.class_3367(file);
			this.field_16515.put(file, lv);
		}

		lv.method_15067(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3366.class_3368> arg) {
		class_3366.class_3367 lv = (class_3366.class_3367)this.field_16515.get(file);
		if (lv != null) {
			lv.method_15068(arg);
			if (lv.method_15065()) {
				this.field_16515.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_16515.remove(file);
	}

	public class_3366.class_3368 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Potion potion = null;
		if (jsonObject.has("potion")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
			if (!Registry.POTION.containsId(identifier)) {
				throw new JsonSyntaxException("Unknown potion '" + identifier + "'");
			}

			potion = Registry.POTION.get(identifier);
		}

		return new class_3366.class_3368(potion);
	}

	public void method_15062(ServerPlayerEntity serverPlayerEntity, Potion potion) {
		class_3366.class_3367 lv = (class_3366.class_3367)this.field_16515.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15066(potion);
		}
	}

	static class class_3367 {
		private final AdvancementFile field_16516;
		private final Set<Criterion.class_3353<class_3366.class_3368>> field_16517 = Sets.newHashSet();

		public class_3367(AdvancementFile advancementFile) {
			this.field_16516 = advancementFile;
		}

		public boolean method_15065() {
			return this.field_16517.isEmpty();
		}

		public void method_15067(Criterion.class_3353<class_3366.class_3368> arg) {
			this.field_16517.add(arg);
		}

		public void method_15068(Criterion.class_3353<class_3366.class_3368> arg) {
			this.field_16517.remove(arg);
		}

		public void method_15066(Potion potion) {
			List<Criterion.class_3353<class_3366.class_3368>> list = null;

			for (Criterion.class_3353<class_3366.class_3368> lv : this.field_16517) {
				if (lv.method_14975().method_15069(potion)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3366.class_3368> lv2 : list) {
					lv2.method_14976(this.field_16516);
				}
			}
		}
	}

	public static class class_3368 extends AbstractCriterionInstance {
		private final Potion field_16518;

		public class_3368(@Nullable Potion potion) {
			super(class_3366.field_16514);
			this.field_16518 = potion;
		}

		public static class_3366.class_3368 method_15110() {
			return new class_3366.class_3368(null);
		}

		public boolean method_15069(Potion potion) {
			return this.field_16518 == null || this.field_16518 == potion;
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_16518 != null) {
				jsonObject.addProperty("potion", Registry.POTION.getId(this.field_16518).toString());
			}

			return jsonObject;
		}
	}
}
