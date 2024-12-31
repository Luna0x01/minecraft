package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
	private final Map<String, CriterionProgress> criteria = Maps.newHashMap();
	private String[][] requirements = new String[0][];

	public void method_14836(Map<String, Criteria> map, String[][] requirements) {
		Set<String> set = map.keySet();
		Iterator<Entry<String, CriterionProgress>> iterator = this.criteria.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, CriterionProgress> entry = (Entry<String, CriterionProgress>)iterator.next();
			if (!set.contains(entry.getKey())) {
				iterator.remove();
			}
		}

		for (String string : set) {
			if (!this.criteria.containsKey(string)) {
				this.criteria.put(string, new CriterionProgress(this));
			}
		}

		this.requirements = requirements;
	}

	public boolean method_14833() {
		if (this.requirements.length == 0) {
			return false;
		} else {
			for (String[] strings : this.requirements) {
				boolean bl = false;

				for (String string : strings) {
					CriterionProgress criterionProgress = this.getCriteria(string);
					if (criterionProgress != null && criterionProgress.hasBeenObtained()) {
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
	}

	public boolean method_14838() {
		for (CriterionProgress criterionProgress : this.criteria.values()) {
			if (criterionProgress.hasBeenObtained()) {
				return true;
			}
		}

		return false;
	}

	public boolean method_14835(String string) {
		CriterionProgress criterionProgress = (CriterionProgress)this.criteria.get(string);
		if (criterionProgress != null && !criterionProgress.hasBeenObtained()) {
			criterionProgress.setObtained();
			return true;
		} else {
			return false;
		}
	}

	public boolean method_14840(String string) {
		CriterionProgress criterionProgress = (CriterionProgress)this.criteria.get(string);
		if (criterionProgress != null && criterionProgress.hasBeenObtained()) {
			criterionProgress.reset();
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
	}

	public void writeToByteBuf(PacketByteBuf buf) {
		buf.writeVarInt(this.criteria.size());

		for (Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
			buf.writeString((String)entry.getKey());
			((CriterionProgress)entry.getValue()).writeToByteBuf(buf);
		}
	}

	public static AdvancementProgress fromPacketByteBuf(PacketByteBuf buf) {
		AdvancementProgress advancementProgress = new AdvancementProgress();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			advancementProgress.criteria.put(buf.readString(32767), CriterionProgress.fromPacketByteBuf(buf, advancementProgress));
		}

		return advancementProgress;
	}

	@Nullable
	public CriterionProgress getCriteria(String name) {
		return (CriterionProgress)this.criteria.get(name);
	}

	public float method_14842() {
		if (this.criteria.isEmpty()) {
			return 0.0F;
		} else {
			float f = (float)this.requirements.length;
			float g = (float)this.method_14848();
			return g / f;
		}
	}

	@Nullable
	public String method_14844() {
		if (this.criteria.isEmpty()) {
			return null;
		} else {
			int i = this.requirements.length;
			if (i <= 1) {
				return null;
			} else {
				int j = this.method_14848();
				return j + "/" + i;
			}
		}
	}

	private int method_14848() {
		int i = 0;

		for (String[] strings : this.requirements) {
			boolean bl = false;

			for (String string : strings) {
				CriterionProgress criterionProgress = this.getCriteria(string);
				if (criterionProgress != null && criterionProgress.hasBeenObtained()) {
					bl = true;
					break;
				}
			}

			if (bl) {
				i++;
			}
		}

		return i;
	}

	public Iterable<String> method_14845() {
		List<String> list = Lists.newArrayList();

		for (Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
			if (!((CriterionProgress)entry.getValue()).hasBeenObtained()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public Iterable<String> method_14846() {
		List<String> list = Lists.newArrayList();

		for (Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
			if (((CriterionProgress)entry.getValue()).hasBeenObtained()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	@Nullable
	public Date method_14847() {
		Date date = null;

		for (CriterionProgress criterionProgress : this.criteria.values()) {
			if (criterionProgress.hasBeenObtained() && (date == null || criterionProgress.getObtainDate().before(date))) {
				date = criterionProgress.getObtainDate();
			}
		}

		return date;
	}

	public int compareTo(AdvancementProgress advancementProgress) {
		Date date = this.method_14847();
		Date date2 = advancementProgress.method_14847();
		if (date == null && date2 != null) {
			return 1;
		} else if (date != null && date2 == null) {
			return -1;
		} else {
			return date == null && date2 == null ? 0 : date.compareTo(date2);
		}
	}

	public static class class_3335 implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
		public JsonElement serialize(AdvancementProgress advancementProgress, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, CriterionProgress> entry : advancementProgress.criteria.entrySet()) {
				CriterionProgress criterionProgress = (CriterionProgress)entry.getValue();
				if (criterionProgress.hasBeenObtained()) {
					jsonObject2.add((String)entry.getKey(), criterionProgress.toJson());
				}
			}

			if (!jsonObject2.entrySet().isEmpty()) {
				jsonObject.add("criteria", jsonObject2);
			}

			jsonObject.addProperty("done", advancementProgress.method_14833());
			return jsonObject;
		}

		public AdvancementProgress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "criteria", new JsonObject());
			AdvancementProgress advancementProgress = new AdvancementProgress();

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				String string = (String)entry.getKey();
				advancementProgress.criteria.put(string, CriterionProgress.read(advancementProgress, JsonHelper.asString((JsonElement)entry.getValue(), string)));
			}

			return advancementProgress;
		}
	}
}
