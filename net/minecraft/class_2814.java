package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2814 implements class_2816 {
	private final Map<String, class_2789> field_13239;
	private final class_2782.class_2784 field_13240;

	public class_2814(Map<String, class_2789> map, class_2782.class_2784 arg) {
		this.field_13239 = map;
		this.field_13240 = arg;
	}

	@Override
	public boolean method_12074(Random random, class_2782 arg) {
		Entity entity = arg.method_11988(this.field_13240);
		if (entity == null) {
			return false;
		} else {
			Scoreboard scoreboard = entity.world.getScoreboard();

			for (Entry<String, class_2789> entry : this.field_13239.entrySet()) {
				if (!this.method_12070(entity, scoreboard, (String)entry.getKey(), (class_2789)entry.getValue())) {
					return false;
				}
			}

			return true;
		}
	}

	protected boolean method_12070(Entity entity, Scoreboard scoreboard, String string, class_2789 arg) {
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
		if (scoreboardObjective == null) {
			return false;
		} else {
			String string2 = entity.method_15586();
			return !scoreboard.playerHasObjective(string2, scoreboardObjective)
				? false
				: arg.inRangeInclusive(scoreboard.getPlayerScore(string2, scoreboardObjective).getScore());
		}
	}

	public static class class_2815 extends class_2816.class_2817<class_2814> {
		protected class_2815() {
			super(new Identifier("entity_scores"), class_2814.class);
		}

		public void method_12076(JsonObject jsonObject, class_2814 arg, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, class_2789> entry : arg.field_13239.entrySet()) {
				jsonObject2.add((String)entry.getKey(), jsonSerializationContext.serialize(entry.getValue()));
			}

			jsonObject.add("scores", jsonObject2);
			jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_13240));
		}

		public class_2814 method_12078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Set<Entry<String, JsonElement>> set = JsonHelper.getObject(jsonObject, "scores").entrySet();
			Map<String, class_2789> map = Maps.newLinkedHashMap();

			for (Entry<String, JsonElement> entry : set) {
				map.put(entry.getKey(), JsonHelper.deserialize((JsonElement)entry.getValue(), "score", jsonDeserializationContext, class_2789.class));
			}

			return new class_2814(map, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, class_2782.class_2784.class));
		}
	}
}
