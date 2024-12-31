package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3184 implements Criterion<class_3184.class_3186> {
	private static final Identifier field_15660 = new Identifier("entity_hurt_player");
	private final Map<AdvancementFile, class_3184.class_3185> field_15661 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15660;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3184.class_3186> arg) {
		class_3184.class_3185 lv = (class_3184.class_3185)this.field_15661.get(file);
		if (lv == null) {
			lv = new class_3184.class_3185(file);
			this.field_15661.put(file, lv);
		}

		lv.method_14229(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3184.class_3186> arg) {
		class_3184.class_3185 lv = (class_3184.class_3185)this.field_15661.get(file);
		if (lv != null) {
			lv.method_14230(arg);
			if (lv.method_14227()) {
				this.field_15661.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15661.remove(file);
	}

	public class_3184.class_3186 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3160 lv = class_3160.method_14116(jsonObject.get("damage"));
		return new class_3184.class_3186(lv);
	}

	public void method_14224(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
		class_3184.class_3185 lv = (class_3184.class_3185)this.field_15661.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14228(serverPlayerEntity, damageSource, f, g, bl);
		}
	}

	static class class_3185 {
		private final AdvancementFile field_15662;
		private final Set<Criterion.class_3353<class_3184.class_3186>> field_15663 = Sets.newHashSet();

		public class_3185(AdvancementFile advancementFile) {
			this.field_15662 = advancementFile;
		}

		public boolean method_14227() {
			return this.field_15663.isEmpty();
		}

		public void method_14229(Criterion.class_3353<class_3184.class_3186> arg) {
			this.field_15663.add(arg);
		}

		public void method_14230(Criterion.class_3353<class_3184.class_3186> arg) {
			this.field_15663.remove(arg);
		}

		public void method_14228(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
			List<Criterion.class_3353<class_3184.class_3186>> list = null;

			for (Criterion.class_3353<class_3184.class_3186> lv : this.field_15663) {
				if (lv.method_14975().method_14231(serverPlayerEntity, damageSource, f, g, bl)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3184.class_3186> lv2 : list) {
					lv2.method_14976(this.field_15662);
				}
			}
		}
	}

	public static class class_3186 extends AbstractCriterionInstance {
		private final class_3160 field_15664;

		public class_3186(class_3160 arg) {
			super(class_3184.field_15660);
			this.field_15664 = arg;
		}

		public boolean method_14231(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
			return this.field_15664.method_14117(serverPlayerEntity, damageSource, f, g, bl);
		}
	}
}
