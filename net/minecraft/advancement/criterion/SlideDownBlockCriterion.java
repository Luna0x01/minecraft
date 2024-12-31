package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SlideDownBlockCriterion extends AbstractCriterion<SlideDownBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("slide_down_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public SlideDownBlockCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), string -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + string);
			});
		}

		return new SlideDownBlockCriterion.Conditions(block, statePredicate);
	}

	@Nullable
	private static Block getBlock(JsonObject jsonObject) {
		if (jsonObject.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			return (Block)Registry.field_11146.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void test(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
		this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.test(blockState));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final StatePredicate state;

		public Conditions(@Nullable Block block, StatePredicate statePredicate) {
			super(SlideDownBlockCriterion.ID);
			this.block = block;
			this.state = statePredicate;
		}

		public static SlideDownBlockCriterion.Conditions create(Block block) {
			return new SlideDownBlockCriterion.Conditions(block, StatePredicate.ANY);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.field_11146.getId(this.block).toString());
			}

			jsonObject.add("state", this.state.toJson());
			return jsonObject;
		}

		public boolean test(BlockState blockState) {
			return this.block != null && blockState.getBlock() != this.block ? false : this.state.test(blockState);
		}
	}
}
