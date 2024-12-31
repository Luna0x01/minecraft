package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.LocationJson;

public class class_3210 implements Criterion<class_3210.class_3613> {
	private final Identifier field_15758;
	private final Map<AdvancementFile, class_3210.class_3211> field_15759 = Maps.newHashMap();

	public class_3210(Identifier identifier) {
		this.field_15758 = identifier;
	}

	@Override
	public Identifier getIdentifier() {
		return this.field_15758;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3210.class_3613> arg) {
		class_3210.class_3211 lv = (class_3210.class_3211)this.field_15759.get(file);
		if (lv == null) {
			lv = new class_3210.class_3211(file);
			this.field_15759.put(file, lv);
		}

		lv.method_14330(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3210.class_3613> arg) {
		class_3210.class_3211 lv = (class_3210.class_3211)this.field_15759.get(file);
		if (lv != null) {
			lv.method_14331(arg);
			if (lv.method_14328()) {
				this.field_15759.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15759.remove(file);
	}

	public class_3210.class_3613 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationJson locationJson = LocationJson.fromJson(jsonObject);
		return new class_3210.class_3613(this.field_15758, locationJson);
	}

	public void method_14326(ServerPlayerEntity serverPlayerEntity) {
		class_3210.class_3211 lv = (class_3210.class_3211)this.field_15759.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14329(serverPlayerEntity.getServerWorld(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
		}
	}

	static class class_3211 {
		private final AdvancementFile field_15760;
		private final Set<Criterion.class_3353<class_3210.class_3613>> field_15761 = Sets.newHashSet();

		public class_3211(AdvancementFile advancementFile) {
			this.field_15760 = advancementFile;
		}

		public boolean method_14328() {
			return this.field_15761.isEmpty();
		}

		public void method_14330(Criterion.class_3353<class_3210.class_3613> arg) {
			this.field_15761.add(arg);
		}

		public void method_14331(Criterion.class_3353<class_3210.class_3613> arg) {
			this.field_15761.remove(arg);
		}

		public void method_14329(ServerWorld serverWorld, double d, double e, double f) {
			List<Criterion.class_3353<class_3210.class_3613>> list = null;

			for (Criterion.class_3353<class_3210.class_3613> lv : this.field_15761) {
				if (lv.method_14975().method_16490(serverWorld, d, e, f)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3210.class_3613> lv2 : list) {
					lv2.method_14976(this.field_15760);
				}
			}
		}
	}

	public static class class_3613 extends AbstractCriterionInstance {
		private final LocationJson field_17682;

		public class_3613(Identifier identifier, LocationJson locationJson) {
			super(identifier);
			this.field_17682 = locationJson;
		}

		public static class_3210.class_3613 method_16489(LocationJson locationJson) {
			return new class_3210.class_3613(AchievementsAndCriterions.LOCATION.field_15758, locationJson);
		}

		public static class_3210.class_3613 method_16491() {
			return new class_3210.class_3613(AchievementsAndCriterions.SLEPT_IN_BED.field_15758, LocationJson.EMPTY);
		}

		public boolean method_16490(ServerWorld serverWorld, double d, double e, double f) {
			return this.field_17682.method_14322(serverWorld, d, e, f);
		}

		@Override
		public JsonElement method_21241() {
			return this.field_17682.method_16352();
		}
	}
}
