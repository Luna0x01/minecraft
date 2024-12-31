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
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.LocationJson;
import net.minecraft.util.math.BlockPos;

public class class_3222 implements Criterion<class_3222.class_3224> {
	private static final Identifier field_15783 = new Identifier("placed_block");
	private final Map<AdvancementFile, class_3222.class_3223> field_15784 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15783;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3222.class_3224> arg) {
		class_3222.class_3223 lv = (class_3222.class_3223)this.field_15784.get(file);
		if (lv == null) {
			lv = new class_3222.class_3223(file);
			this.field_15784.put(file, lv);
		}

		lv.method_14374(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3222.class_3224> arg) {
		class_3222.class_3223 lv = (class_3222.class_3223)this.field_15784.get(file);
		if (lv != null) {
			lv.method_14375(arg);
			if (lv.method_14372()) {
				this.field_15784.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15784.remove(file);
	}

	public class_3222.class_3224 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
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

		LocationJson locationJson = LocationJson.fromJson(jsonObject.get("location"));
		class_3200 lv = class_3200.method_14295(jsonObject.get("item"));
		return new class_3222.class_3224(block, map, locationJson, lv);
	}

	public void method_14369(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, ItemStack itemStack) {
		BlockState blockState = serverPlayerEntity.world.getBlockState(blockPos);
		class_3222.class_3223 lv = (class_3222.class_3223)this.field_15784.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14373(blockState, blockPos, serverPlayerEntity.getServerWorld(), itemStack);
		}
	}

	static class class_3223 {
		private final AdvancementFile field_15785;
		private final Set<Criterion.class_3353<class_3222.class_3224>> field_15786 = Sets.newHashSet();

		public class_3223(AdvancementFile advancementFile) {
			this.field_15785 = advancementFile;
		}

		public boolean method_14372() {
			return this.field_15786.isEmpty();
		}

		public void method_14374(Criterion.class_3353<class_3222.class_3224> arg) {
			this.field_15786.add(arg);
		}

		public void method_14375(Criterion.class_3353<class_3222.class_3224> arg) {
			this.field_15786.remove(arg);
		}

		public void method_14373(BlockState blockState, BlockPos blockPos, ServerWorld serverWorld, ItemStack itemStack) {
			List<Criterion.class_3353<class_3222.class_3224>> list = null;

			for (Criterion.class_3353<class_3222.class_3224> lv : this.field_15786) {
				if (lv.method_14975().method_14376(blockState, blockPos, serverWorld, itemStack)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3222.class_3224> lv2 : list) {
					lv2.method_14976(this.field_15785);
				}
			}
		}
	}

	public static class class_3224 extends AbstractCriterionInstance {
		private final Block field_15787;
		private final Map<Property<?>, Object> field_15788;
		private final LocationJson field_15789;
		private final class_3200 field_15790;

		public class_3224(@Nullable Block block, @Nullable Map<Property<?>, Object> map, LocationJson locationJson, class_3200 arg) {
			super(class_3222.field_15783);
			this.field_15787 = block;
			this.field_15788 = map;
			this.field_15789 = locationJson;
			this.field_15790 = arg;
		}

		public boolean method_14376(BlockState blockState, BlockPos blockPos, ServerWorld serverWorld, ItemStack itemStack) {
			if (this.field_15787 != null && blockState.getBlock() != this.field_15787) {
				return false;
			} else {
				if (this.field_15788 != null) {
					for (Entry<Property<?>, Object> entry : this.field_15788.entrySet()) {
						if (blockState.get((Property)entry.getKey()) != entry.getValue()) {
							return false;
						}
					}
				}

				return !this.field_15789.method_14323(serverWorld, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ())
					? false
					: this.field_15790.method_14294(itemStack);
			}
		}
	}
}
