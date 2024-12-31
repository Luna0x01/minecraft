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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.DistanceJson;
import net.minecraft.util.json.LocationJson;
import net.minecraft.util.math.Vec3d;

public class class_3218 implements Criterion<class_3218.class_3696> {
	private static final Identifier field_15775 = new Identifier("nether_travel");
	private final Map<AdvancementFile, class_3218.class_3219> field_15776 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15775;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3218.class_3696> arg) {
		class_3218.class_3219 lv = (class_3218.class_3219)this.field_15776.get(file);
		if (lv == null) {
			lv = new class_3218.class_3219(file);
			this.field_15776.put(file, lv);
		}

		lv.method_14359(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3218.class_3696> arg) {
		class_3218.class_3219 lv = (class_3218.class_3219)this.field_15776.get(file);
		if (lv != null) {
			lv.method_14360(arg);
			if (lv.method_14357()) {
				this.field_15776.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15776.remove(file);
	}

	public class_3218.class_3696 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationJson locationJson = LocationJson.fromJson(jsonObject.get("entered"));
		LocationJson locationJson2 = LocationJson.fromJson(jsonObject.get("exited"));
		DistanceJson distanceJson = DistanceJson.fromJson(jsonObject.get("distance"));
		return new class_3218.class_3696(locationJson, locationJson2, distanceJson);
	}

	public void method_14354(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d) {
		class_3218.class_3219 lv = (class_3218.class_3219)this.field_15776.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14358(serverPlayerEntity.getServerWorld(), vec3d, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
		}
	}

	static class class_3219 {
		private final AdvancementFile field_15777;
		private final Set<Criterion.class_3353<class_3218.class_3696>> field_15778 = Sets.newHashSet();

		public class_3219(AdvancementFile advancementFile) {
			this.field_15777 = advancementFile;
		}

		public boolean method_14357() {
			return this.field_15778.isEmpty();
		}

		public void method_14359(Criterion.class_3353<class_3218.class_3696> arg) {
			this.field_15778.add(arg);
		}

		public void method_14360(Criterion.class_3353<class_3218.class_3696> arg) {
			this.field_15778.remove(arg);
		}

		public void method_14358(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
			List<Criterion.class_3353<class_3218.class_3696>> list = null;

			for (Criterion.class_3353<class_3218.class_3696> lv : this.field_15778) {
				if (lv.method_14975().method_16633(serverWorld, vec3d, d, e, f)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3218.class_3696> lv2 : list) {
					lv2.method_14976(this.field_15777);
				}
			}
		}
	}

	public static class class_3696 extends AbstractCriterionInstance {
		private final LocationJson field_18201;
		private final LocationJson field_18202;
		private final DistanceJson field_18203;

		public class_3696(LocationJson locationJson, LocationJson locationJson2, DistanceJson distanceJson) {
			super(class_3218.field_15775);
			this.field_18201 = locationJson;
			this.field_18202 = locationJson2;
			this.field_18203 = distanceJson;
		}

		public static class_3218.class_3696 method_16632(DistanceJson distanceJson) {
			return new class_3218.class_3696(LocationJson.EMPTY, LocationJson.EMPTY, distanceJson);
		}

		public boolean method_16633(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
			if (!this.field_18201.method_14322(serverWorld, vec3d.x, vec3d.y, vec3d.z)) {
				return false;
			} else {
				return !this.field_18202.method_14322(serverWorld, d, e, f) ? false : this.field_18203.method_14124(vec3d.x, vec3d.y, vec3d.z, d, e, f);
			}
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entered", this.field_18201.method_16352());
			jsonObject.add("exited", this.field_18202.method_16352());
			jsonObject.add("distance", this.field_18203.method_15702());
			return jsonObject;
		}
	}
}
