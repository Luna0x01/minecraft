package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3238 implements Criterion<class_3238.class_3240> {
	public static final Identifier field_15823 = new Identifier("tick");
	private final Map<AdvancementFile, class_3238.class_3239> field_15824 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15823;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3238.class_3240> arg) {
		class_3238.class_3239 lv = (class_3238.class_3239)this.field_15824.get(file);
		if (lv == null) {
			lv = new class_3238.class_3239(file);
			this.field_15824.put(file, lv);
		}

		lv.method_14416(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3238.class_3240> arg) {
		class_3238.class_3239 lv = (class_3238.class_3239)this.field_15824.get(file);
		if (lv != null) {
			lv.method_14418(arg);
			if (lv.method_14415()) {
				this.field_15824.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15824.remove(file);
	}

	public class_3238.class_3240 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_3238.class_3240();
	}

	public void method_14413(ServerPlayerEntity serverPlayerEntity) {
		class_3238.class_3239 lv = (class_3238.class_3239)this.field_15824.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14417();
		}
	}

	static class class_3239 {
		private final AdvancementFile field_15825;
		private final Set<Criterion.class_3353<class_3238.class_3240>> field_15826 = Sets.newHashSet();

		public class_3239(AdvancementFile advancementFile) {
			this.field_15825 = advancementFile;
		}

		public boolean method_14415() {
			return this.field_15826.isEmpty();
		}

		public void method_14416(Criterion.class_3353<class_3238.class_3240> arg) {
			this.field_15826.add(arg);
		}

		public void method_14418(Criterion.class_3353<class_3238.class_3240> arg) {
			this.field_15826.remove(arg);
		}

		public void method_14417() {
			for (Criterion.class_3353<class_3238.class_3240> lv : Lists.newArrayList(this.field_15826)) {
				lv.method_14976(this.field_15825);
			}
		}
	}

	public static class class_3240 extends AbstractCriterionInstance {
		public class_3240() {
			super(class_3238.field_15823);
		}
	}
}
