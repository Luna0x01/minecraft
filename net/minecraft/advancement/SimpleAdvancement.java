package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.ArrayUtils;

public class SimpleAdvancement {
	private final SimpleAdvancement parent;
	private final AdvancementDisplay display;
	private final AdvancementRewards rewards;
	private final Identifier identifier;
	private final Map<String, Criteria> criteria;
	private final String[][] requirements;
	private final Set<SimpleAdvancement> children = Sets.newLinkedHashSet();
	private final Text field_16271;

	public SimpleAdvancement(
		Identifier identifier,
		@Nullable SimpleAdvancement simpleAdvancement,
		@Nullable AdvancementDisplay advancementDisplay,
		AdvancementRewards advancementRewards,
		Map<String, Criteria> map,
		String[][] strings
	) {
		this.identifier = identifier;
		this.display = advancementDisplay;
		this.criteria = ImmutableMap.copyOf(map);
		this.parent = simpleAdvancement;
		this.rewards = advancementRewards;
		this.requirements = strings;
		if (simpleAdvancement != null) {
			simpleAdvancement.addChild(this);
		}

		if (advancementDisplay == null) {
			this.field_16271 = new LiteralText(identifier.toString());
		} else {
			this.field_16271 = new LiteralText("[");
			this.field_16271.getStyle().setFormatting(advancementDisplay.getAdvancementType().getColor());
			Text text = advancementDisplay.getTitle().copy();
			Text text2 = new LiteralText("");
			Text text3 = text.copy();
			text3.getStyle().setFormatting(advancementDisplay.getAdvancementType().getColor());
			text2.append(text3);
			text2.append("\n");
			text2.append(advancementDisplay.getDescription());
			text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2));
			this.field_16271.append(text);
			this.field_16271.append("]");
		}
	}

	public SimpleAdvancement.TaskAdvancement asTaskAdvancement() {
		return new SimpleAdvancement.TaskAdvancement(
			this.parent == null ? null : this.parent.getIdentifier(), this.display, this.rewards, this.criteria, this.requirements
		);
	}

	@Nullable
	public SimpleAdvancement getParent() {
		return this.parent;
	}

	@Nullable
	public AdvancementDisplay getDisplay() {
		return this.display;
	}

	public AdvancementRewards getRewards() {
		return this.rewards;
	}

	public String toString() {
		return "SimpleAdvancement{id="
			+ this.getIdentifier()
			+ ", parent="
			+ (this.parent == null ? "null" : this.parent.getIdentifier())
			+ ", display="
			+ this.display
			+ ", rewards="
			+ this.rewards
			+ ", criteria="
			+ this.criteria
			+ ", requirements="
			+ Arrays.deepToString(this.requirements)
			+ '}';
	}

	public Iterable<SimpleAdvancement> getChildren() {
		return this.children;
	}

	public Map<String, Criteria> getCriteria() {
		return this.criteria;
	}

	public int getRequirementsCount() {
		return this.requirements.length;
	}

	public void addChild(SimpleAdvancement child) {
		this.children.add(child);
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof SimpleAdvancement)) {
			return false;
		} else {
			SimpleAdvancement simpleAdvancement = (SimpleAdvancement)other;
			return this.identifier.equals(simpleAdvancement.identifier);
		}
	}

	public int hashCode() {
		return this.identifier.hashCode();
	}

	public String[][] getRequirements() {
		return this.requirements;
	}

	public Text method_14803() {
		return this.field_16271;
	}

	public static class TaskAdvancement {
		private final Identifier identifier;
		private SimpleAdvancement advancement;
		private final AdvancementDisplay display;
		private final AdvancementRewards rewards;
		private final Map<String, Criteria> criteria;
		private final String[][] requirements;

		TaskAdvancement(
			@Nullable Identifier identifier,
			@Nullable AdvancementDisplay advancementDisplay,
			AdvancementRewards advancementRewards,
			Map<String, Criteria> map,
			String[][] strings
		) {
			this.identifier = identifier;
			this.display = advancementDisplay;
			this.rewards = advancementRewards;
			this.criteria = map;
			this.requirements = strings;
		}

		public boolean method_14806(Function<Identifier, SimpleAdvancement> function) {
			if (this.identifier == null) {
				return true;
			} else {
				this.advancement = (SimpleAdvancement)function.apply(this.identifier);
				return this.advancement != null;
			}
		}

		public SimpleAdvancement method_14807(Identifier identifier) {
			return new SimpleAdvancement(identifier, this.advancement, this.display, this.rewards, this.criteria, this.requirements);
		}

		public void writeToByteBuf(PacketByteBuf buf) {
			if (this.identifier == null) {
				buf.writeBoolean(false);
			} else {
				buf.writeBoolean(true);
				buf.writeIdentifier(this.identifier);
			}

			if (this.display == null) {
				buf.writeBoolean(false);
			} else {
				buf.writeBoolean(true);
				this.display.writeTo(buf);
			}

			Criteria.writeAllToByteBuf(this.criteria, buf);
			buf.writeVarInt(this.requirements.length);

			for (String[] strings : this.requirements) {
				buf.writeVarInt(strings.length);

				for (String string : strings) {
					buf.writeString(string);
				}
			}
		}

		public String toString() {
			return "Task Advancement{parentId="
				+ this.identifier
				+ ", display="
				+ this.display
				+ ", rewards="
				+ this.rewards
				+ ", criteria="
				+ this.criteria
				+ ", requirements="
				+ Arrays.deepToString(this.requirements)
				+ '}';
		}

		public static SimpleAdvancement.TaskAdvancement method_14804(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = jsonObject.has("parent") ? new Identifier(JsonHelper.getString(jsonObject, "parent")) : null;
			AdvancementDisplay advancementDisplay = jsonObject.has("display")
				? AdvancementDisplay.fromJson(JsonHelper.getObject(jsonObject, "display"), jsonDeserializationContext)
				: null;
			AdvancementRewards advancementRewards = JsonHelper.deserialize(
				jsonObject, "rewards", AdvancementRewards.REWARDS, jsonDeserializationContext, AdvancementRewards.class
			);
			Map<String, Criteria> map = Criteria.readAllCriteria(JsonHelper.getObject(jsonObject, "criteria"), jsonDeserializationContext);
			if (map.isEmpty()) {
				throw new JsonSyntaxException("Advancement criteria cannot be empty");
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "requirements", new JsonArray());
				String[][] strings = new String[jsonArray.size()][];

				for (int i = 0; i < jsonArray.size(); i++) {
					JsonArray jsonArray2 = JsonHelper.asArray(jsonArray.get(i), "requirements[" + i + "]");
					strings[i] = new String[jsonArray2.size()];

					for (int j = 0; j < jsonArray2.size(); j++) {
						strings[i][j] = JsonHelper.asString(jsonArray2.get(j), "requirements[" + i + "][" + j + "]");
					}
				}

				if (strings.length == 0) {
					strings = new String[map.size()][];
					int k = 0;

					for (String string : map.keySet()) {
						strings[k++] = new String[]{string};
					}
				}

				for (String[] strings2 : strings) {
					if (strings2.length == 0 && map.isEmpty()) {
						throw new JsonSyntaxException("Requirement entry cannot be empty");
					}

					for (String string2 : strings2) {
						if (!map.containsKey(string2)) {
							throw new JsonSyntaxException("Unknown required criterion '" + string2 + "'");
						}
					}
				}

				for (String string3 : map.keySet()) {
					boolean bl = false;

					for (String[] strings3 : strings) {
						if (ArrayUtils.contains(strings3, string3)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						throw new JsonSyntaxException(
							"Criterion '" + string3 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required."
						);
					}
				}

				return new SimpleAdvancement.TaskAdvancement(identifier, advancementDisplay, advancementRewards, map, strings);
			}
		}

		public static SimpleAdvancement.TaskAdvancement fromPacketByteBuf(PacketByteBuf buf) {
			Identifier identifier = buf.readBoolean() ? buf.readIdentifier() : null;
			AdvancementDisplay advancementDisplay = buf.readBoolean() ? AdvancementDisplay.fromPacketByteBuf(buf) : null;
			Map<String, Criteria> map = Criteria.readAllCriteria(buf);
			String[][] strings = new String[buf.readVarInt()][];

			for (int i = 0; i < strings.length; i++) {
				strings[i] = new String[buf.readVarInt()];

				for (int j = 0; j < strings[i].length; j++) {
					strings[i][j] = buf.readString(32767);
				}
			}

			return new SimpleAdvancement.TaskAdvancement(identifier, advancementDisplay, AdvancementRewards.REWARDS, map, strings);
		}
	}
}
