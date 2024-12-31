package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_4470;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
			Text text = advancementDisplay.getTitle();
			Formatting formatting = advancementDisplay.getAdvancementType().getColor();
			Text text2 = text.method_20177().formatted(formatting).append("\n").append(advancementDisplay.getDescription());
			Text text3 = text.method_20177().styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
			this.field_16271 = new LiteralText("[").append(text3).append("]").formatted(formatting);
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
		private Identifier field_21558;
		private SimpleAdvancement field_21559;
		private AdvancementDisplay field_21560;
		private AdvancementRewards field_21561 = AdvancementRewards.REWARDS;
		private Map<String, Criteria> field_21562 = Maps.newLinkedHashMap();
		private String[][] field_21563;
		private class_4470 field_21564 = class_4470.AND;

		private TaskAdvancement(
			@Nullable Identifier identifier,
			@Nullable AdvancementDisplay advancementDisplay,
			AdvancementRewards advancementRewards,
			Map<String, Criteria> map,
			String[][] strings
		) {
			this.field_21558 = identifier;
			this.field_21560 = advancementDisplay;
			this.field_21561 = advancementRewards;
			this.field_21562 = map;
			this.field_21563 = strings;
		}

		private TaskAdvancement() {
		}

		public static SimpleAdvancement.TaskAdvancement method_20248() {
			return new SimpleAdvancement.TaskAdvancement();
		}

		public SimpleAdvancement.TaskAdvancement method_20253(SimpleAdvancement simpleAdvancement) {
			this.field_21559 = simpleAdvancement;
			return this;
		}

		public SimpleAdvancement.TaskAdvancement method_20256(Identifier identifier) {
			this.field_21558 = identifier;
			return this;
		}

		public SimpleAdvancement.TaskAdvancement method_20249(
			Itemable itemable, Text text, Text text2, @Nullable Identifier identifier, AdvancementType advancementType, boolean bl, boolean bl2, boolean bl3
		) {
			return this.method_20257(new AdvancementDisplay(new ItemStack(itemable.getItem()), text, text2, identifier, advancementType, bl, bl2, bl3));
		}

		public SimpleAdvancement.TaskAdvancement method_20257(AdvancementDisplay advancementDisplay) {
			this.field_21560 = advancementDisplay;
			return this;
		}

		public SimpleAdvancement.TaskAdvancement method_20254(AdvancementRewards.class_4395 arg) {
			return this.method_20255(arg.method_20387());
		}

		public SimpleAdvancement.TaskAdvancement method_20255(AdvancementRewards advancementRewards) {
			this.field_21561 = advancementRewards;
			return this;
		}

		public SimpleAdvancement.TaskAdvancement method_20251(String string, CriterionInstance criterionInstance) {
			return this.method_20250(string, new Criteria(criterionInstance));
		}

		public SimpleAdvancement.TaskAdvancement method_20250(String string, Criteria criteria) {
			if (this.field_21562.containsKey(string)) {
				throw new IllegalArgumentException("Duplicate criterion " + string);
			} else {
				this.field_21562.put(string, criteria);
				return this;
			}
		}

		public SimpleAdvancement.TaskAdvancement method_20258(class_4470 arg) {
			this.field_21564 = arg;
			return this;
		}

		public boolean method_14806(Function<Identifier, SimpleAdvancement> function) {
			if (this.field_21558 == null) {
				return true;
			} else {
				if (this.field_21559 == null) {
					this.field_21559 = (SimpleAdvancement)function.apply(this.field_21558);
				}

				return this.field_21559 != null;
			}
		}

		public SimpleAdvancement method_14807(Identifier identifier) {
			if (!this.method_14806(identifierx -> null)) {
				throw new IllegalStateException("Tried to build incomplete advancement!");
			} else {
				if (this.field_21563 == null) {
					this.field_21563 = this.field_21564.createRequirements(this.field_21562.keySet());
				}

				return new SimpleAdvancement(identifier, this.field_21559, this.field_21560, this.field_21561, this.field_21562, this.field_21563);
			}
		}

		public SimpleAdvancement method_20252(Consumer<SimpleAdvancement> consumer, String string) {
			SimpleAdvancement simpleAdvancement = this.method_14807(new Identifier(string));
			consumer.accept(simpleAdvancement);
			return simpleAdvancement;
		}

		public JsonObject method_20259() {
			if (this.field_21563 == null) {
				this.field_21563 = this.field_21564.createRequirements(this.field_21562.keySet());
			}

			JsonObject jsonObject = new JsonObject();
			if (this.field_21559 != null) {
				jsonObject.addProperty("parent", this.field_21559.getIdentifier().toString());
			} else if (this.field_21558 != null) {
				jsonObject.addProperty("parent", this.field_21558.toString());
			}

			if (this.field_21560 != null) {
				jsonObject.add("display", this.field_21560.method_21313());
			}

			jsonObject.add("rewards", this.field_21561.method_20386());
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, Criteria> entry : this.field_21562.entrySet()) {
				jsonObject2.add((String)entry.getKey(), ((Criteria)entry.getValue()).method_20522());
			}

			jsonObject.add("criteria", jsonObject2);
			JsonArray jsonArray = new JsonArray();

			for (String[] strings : this.field_21563) {
				JsonArray jsonArray2 = new JsonArray();

				for (String string : strings) {
					jsonArray2.add(string);
				}

				jsonArray.add(jsonArray2);
			}

			jsonObject.add("requirements", jsonArray);
			return jsonObject;
		}

		public void writeToByteBuf(PacketByteBuf buf) {
			if (this.field_21558 == null) {
				buf.writeBoolean(false);
			} else {
				buf.writeBoolean(true);
				buf.writeIdentifier(this.field_21558);
			}

			if (this.field_21560 == null) {
				buf.writeBoolean(false);
			} else {
				buf.writeBoolean(true);
				this.field_21560.writeTo(buf);
			}

			Criteria.writeAllToByteBuf(this.field_21562, buf);
			buf.writeVarInt(this.field_21563.length);

			for (String[] strings : this.field_21563) {
				buf.writeVarInt(strings.length);

				for (String string : strings) {
					buf.writeString(string);
				}
			}
		}

		public String toString() {
			return "Task Advancement{parentId="
				+ this.field_21558
				+ ", display="
				+ this.field_21560
				+ ", rewards="
				+ this.field_21561
				+ ", criteria="
				+ this.field_21562
				+ ", requirements="
				+ Arrays.deepToString(this.field_21563)
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

		public Map<String, Criteria> method_20260() {
			return this.field_21562;
		}
	}
}
