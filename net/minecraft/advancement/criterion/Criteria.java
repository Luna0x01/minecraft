package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class Criteria {
	private final CriterionInstance instance;

	public Criteria(CriterionInstance criterionInstance) {
		this.instance = criterionInstance;
	}

	public Criteria() {
		this.instance = null;
	}

	public void writeToByteBuf(PacketByteBuf buf) {
	}

	public static Criteria readCriteria(JsonObject object, JsonDeserializationContext ctx) {
		Identifier identifier = new Identifier(JsonHelper.getString(object, "trigger"));
		Criterion<?> criterion = AchievementsAndCriterions.getInstance(identifier);
		if (criterion == null) {
			throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
		} else {
			CriterionInstance criterionInstance = criterion.fromJson(JsonHelper.getObject(object, "conditions", new JsonObject()), ctx);
			return new Criteria(criterionInstance);
		}
	}

	public static Criteria readCriteria(PacketByteBuf buf) {
		return new Criteria();
	}

	public static Map<String, Criteria> readAllCriteria(JsonObject object, JsonDeserializationContext ctx) {
		Map<String, Criteria> map = Maps.newHashMap();

		for (Entry<String, JsonElement> entry : object.entrySet()) {
			map.put(entry.getKey(), readCriteria(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), ctx));
		}

		return map;
	}

	public static Map<String, Criteria> readAllCriteria(PacketByteBuf buf) {
		Map<String, Criteria> map = Maps.newHashMap();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			map.put(buf.readString(32767), readCriteria(buf));
		}

		return map;
	}

	public static void writeAllToByteBuf(Map<String, Criteria> criteria, PacketByteBuf buf) {
		buf.writeVarInt(criteria.size());

		for (Entry<String, Criteria> entry : criteria.entrySet()) {
			buf.writeString((String)entry.getKey());
			((Criteria)entry.getValue()).writeToByteBuf(buf);
		}
	}

	@Nullable
	public CriterionInstance method_14879() {
		return this.instance;
	}
}
