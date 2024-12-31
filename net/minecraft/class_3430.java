package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3430 implements Criterion<class_3430.class_3432> {
	private static final Identifier field_16617 = new Identifier("channeled_lightning");
	private final Map<AdvancementFile, class_3430.class_3431> field_16618 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_16617;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3430.class_3432> arg) {
		class_3430.class_3431 lv = (class_3430.class_3431)this.field_16618.get(file);
		if (lv == null) {
			lv = new class_3430.class_3431(file);
			this.field_16618.put(file, lv);
		}

		lv.method_15324(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3430.class_3432> arg) {
		class_3430.class_3431 lv = (class_3430.class_3431)this.field_16618.get(file);
		if (lv != null) {
			lv.method_15326(arg);
			if (lv.method_15323()) {
				this.field_16618.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_16618.remove(file);
	}

	public class_3430.class_3432 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3528[] lvs = class_3528.method_15908(jsonObject.get("victims"));
		return new class_3430.class_3432(lvs);
	}

	public void method_15320(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
		class_3430.class_3431 lv = (class_3430.class_3431)this.field_16618.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15325(serverPlayerEntity, collection);
		}
	}

	static class class_3431 {
		private final AdvancementFile field_16619;
		private final Set<Criterion.class_3353<class_3430.class_3432>> field_16620 = Sets.newHashSet();

		public class_3431(AdvancementFile advancementFile) {
			this.field_16619 = advancementFile;
		}

		public boolean method_15323() {
			return this.field_16620.isEmpty();
		}

		public void method_15324(Criterion.class_3353<class_3430.class_3432> arg) {
			this.field_16620.add(arg);
		}

		public void method_15326(Criterion.class_3353<class_3430.class_3432> arg) {
			this.field_16620.remove(arg);
		}

		public void method_15325(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			List<Criterion.class_3353<class_3430.class_3432>> list = null;

			for (Criterion.class_3353<class_3430.class_3432> lv : this.field_16620) {
				if (lv.method_14975().method_15327(serverPlayerEntity, collection)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3430.class_3432> lv2 : list) {
					lv2.method_14976(this.field_16619);
				}
			}
		}
	}

	public static class class_3432 extends AbstractCriterionInstance {
		private final class_3528[] field_16621;

		public class_3432(class_3528[] args) {
			super(class_3430.field_16617);
			this.field_16621 = args;
		}

		public static class_3430.class_3432 method_15328(class_3528... args) {
			return new class_3430.class_3432(args);
		}

		public boolean method_15327(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			for (class_3528 lv : this.field_16621) {
				boolean bl = false;

				for (Entity entity : collection) {
					if (lv.method_15906(serverPlayerEntity, entity)) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					return false;
				}
			}

			return true;
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", class_3528.method_15907(this.field_16621));
			return jsonObject;
		}
	}
}
