package net.minecraft;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_3181 implements Criterion<class_3181.class_3183> {
	private static final Identifier field_15651 = new Identifier("enter_block");
	private final Map<AdvancementFile, class_3181.class_3182> field_15652 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15651;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3181.class_3183> arg) {
		class_3181.class_3182 lv = (class_3181.class_3182)this.field_15652.get(file);
		if (lv == null) {
			lv = new class_3181.class_3182(file);
			this.field_15652.put(file, lv);
		}

		lv.method_14217(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3181.class_3183> arg) {
		class_3181.class_3182 lv = (class_3181.class_3182)this.field_15652.get(file);
		if (lv != null) {
			lv.method_14218(arg);
			if (lv.method_14215()) {
				this.field_15652.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15652.remove(file);
	}

	public class_3181.class_3183 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = null;
		if (jsonObject.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			if (!Block.REGISTRY.containsKey(identifier)) {
				throw new JsonSyntaxException("Unknown block type '" + identifier + "'");
			}

			block = Block.REGISTRY.get(identifier);
		}

		Map<Property<?>, Object> map = null;
		if (jsonObject.has("state")) {
			if (block == null) {
				throw new JsonSyntaxException("Can't define block state without a specific block type");
			}

			StateManager stateManager = block.getStateManager();

			for (Entry<String, JsonElement> entry : JsonHelper.getObject(jsonObject, "state").entrySet()) {
				Property<?> property = stateManager.getProperty((String)entry.getKey());
				if (property == null) {
					throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + Block.REGISTRY.getIdentifier(block) + "'");
				}

				String string = JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey());
				Optional<?> optional = property.method_11749(string);
				if (!optional.isPresent()) {
					throw new JsonSyntaxException(
						"Invalid block state value '" + string + "' for property '" + (String)entry.getKey() + "' on block '" + Block.REGISTRY.getIdentifier(block) + "'"
					);
				}

				if (map == null) {
					map = Maps.newHashMap();
				}

				map.put(property, optional.get());
			}
		}

		return new class_3181.class_3183(block, map);
	}

	public void method_14212(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
		class_3181.class_3182 lv = (class_3181.class_3182)this.field_15652.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14216(blockState);
		}
	}

	static class class_3182 {
		private final AdvancementFile field_15653;
		private final Set<Criterion.class_3353<class_3181.class_3183>> field_15654 = Sets.newHashSet();

		public class_3182(AdvancementFile advancementFile) {
			this.field_15653 = advancementFile;
		}

		public boolean method_14215() {
			return this.field_15654.isEmpty();
		}

		public void method_14217(Criterion.class_3353<class_3181.class_3183> arg) {
			this.field_15654.add(arg);
		}

		public void method_14218(Criterion.class_3353<class_3181.class_3183> arg) {
			this.field_15654.remove(arg);
		}

		public void method_14216(BlockState blockState) {
			List<Criterion.class_3353<class_3181.class_3183>> list = null;

			for (Criterion.class_3353<class_3181.class_3183> lv : this.field_15654) {
				if (lv.method_14975().method_14219(blockState)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3181.class_3183> lv2 : list) {
					lv2.method_14976(this.field_15653);
				}
			}
		}
	}

	public static class class_3183 extends AbstractCriterionInstance {
		private final Block field_15655;
		private final Map<Property<?>, Object> field_15656;

		public class_3183(@Nullable Block block, @Nullable Map<Property<?>, Object> map) {
			super(class_3181.field_15651);
			this.field_15655 = block;
			this.field_15656 = map;
		}

		public boolean method_14219(BlockState blockState) {
			if (this.field_15655 != null && blockState.getBlock() != this.field_15655) {
				return false;
			} else {
				if (this.field_15656 != null) {
					for (Entry<Property<?>, Object> entry : this.field_15656.entrySet()) {
						if (blockState.get((Property)entry.getKey()) != entry.getValue()) {
							return false;
						}
					}
				}

				return true;
			}
		}
	}
}
