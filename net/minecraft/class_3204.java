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
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.DistanceJson;
import net.minecraft.util.math.Vec3d;

public class class_3204 implements Criterion<class_3204.class_3591> {
	private static final Identifier field_15742 = new Identifier("levitation");
	private final Map<AdvancementFile, class_3204.class_3205> field_15743 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15742;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3204.class_3591> arg) {
		class_3204.class_3205 lv = (class_3204.class_3205)this.field_15743.get(file);
		if (lv == null) {
			lv = new class_3204.class_3205(file);
			this.field_15743.put(file, lv);
		}

		lv.method_14315(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3204.class_3591> arg) {
		class_3204.class_3205 lv = (class_3204.class_3205)this.field_15743.get(file);
		if (lv != null) {
			lv.method_14316(arg);
			if (lv.method_14313()) {
				this.field_15743.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15743.remove(file);
	}

	public class_3204.class_3591 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DistanceJson distanceJson = DistanceJson.fromJson(jsonObject.get("distance"));
		class_3638.class_3642 lv = class_3638.class_3642.method_16524(jsonObject.get("duration"));
		return new class_3204.class_3591(distanceJson, lv);
	}

	public void method_14310(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
		class_3204.class_3205 lv = (class_3204.class_3205)this.field_15743.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14314(serverPlayerEntity, vec3d, i);
		}
	}

	static class class_3205 {
		private final AdvancementFile field_15744;
		private final Set<Criterion.class_3353<class_3204.class_3591>> field_15745 = Sets.newHashSet();

		public class_3205(AdvancementFile advancementFile) {
			this.field_15744 = advancementFile;
		}

		public boolean method_14313() {
			return this.field_15745.isEmpty();
		}

		public void method_14315(Criterion.class_3353<class_3204.class_3591> arg) {
			this.field_15745.add(arg);
		}

		public void method_14316(Criterion.class_3353<class_3204.class_3591> arg) {
			this.field_15745.remove(arg);
		}

		public void method_14314(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			List<Criterion.class_3353<class_3204.class_3591>> list = null;

			for (Criterion.class_3353<class_3204.class_3591> lv : this.field_15745) {
				if (lv.method_14975().method_16270(serverPlayerEntity, vec3d, i)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3204.class_3591> lv2 : list) {
					lv2.method_14976(this.field_15744);
				}
			}
		}
	}

	public static class class_3591 extends AbstractCriterionInstance {
		private final DistanceJson field_17482;
		private final class_3638.class_3642 field_17483;

		public class_3591(DistanceJson distanceJson, class_3638.class_3642 arg) {
			super(class_3204.field_15742);
			this.field_17482 = distanceJson;
			this.field_17483 = arg;
		}

		public static class_3204.class_3591 method_16269(DistanceJson distanceJson) {
			return new class_3204.class_3591(distanceJson, class_3638.class_3642.field_17698);
		}

		public boolean method_16270(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			return !this.field_17482.method_14124(vec3d.x, vec3d.y, vec3d.z, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z)
				? false
				: this.field_17483.method_16531(i);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("distance", this.field_17482.method_15702());
			jsonObject.add("duration", this.field_17483.method_16513());
			return jsonObject;
		}
	}
}
