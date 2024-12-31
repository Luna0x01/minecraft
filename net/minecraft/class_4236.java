package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.class_2885;
import net.minecraft.client.class_2903;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

public class class_4236 implements class_4291 {
	private final StateManager<Block, BlockState> field_20823;
	private final List<class_2885> field_20824;

	public class_4236(StateManager<Block, BlockState> stateManager, List<class_2885> list) {
		this.field_20823 = stateManager;
		this.field_20824 = list;
	}

	public List<class_2885> method_19269() {
		return this.field_20824;
	}

	public Set<class_4234> method_19272() {
		Set<class_4234> set = Sets.newHashSet();

		for (class_2885 lv : this.field_20824) {
			set.add(lv.method_12393());
		}

		return set;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_4236)) {
			return false;
		} else {
			class_4236 lv = (class_4236)object;
			return Objects.equals(this.field_20823, lv.field_20823) && Objects.equals(this.field_20824, lv.field_20824);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_20823, this.field_20824});
	}

	@Override
	public Collection<Identifier> method_19600() {
		return (Collection<Identifier>)this.method_19269().stream().flatMap(arg -> arg.method_12393().method_19600().stream()).collect(Collectors.toSet());
	}

	@Override
	public Collection<Identifier> method_19598(Function<Identifier, class_4291> function, Set<String> set) {
		return (Collection<Identifier>)this.method_19269()
			.stream()
			.flatMap(arg -> arg.method_12393().method_19598(function, set).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public BakedModel method_19599(Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2, ModelRotation modelRotation, boolean bl) {
		class_2903.class_2904 lv = new class_2903.class_2904();

		for (class_2885 lv2 : this.method_19269()) {
			BakedModel bakedModel = lv2.method_12393().method_19599(function, function2, modelRotation, bl);
			if (bakedModel != null) {
				lv.method_19597(lv2.method_19276(this.field_20823), bakedModel);
			}
		}

		return lv.method_12517();
	}

	public static class class_2883 implements JsonDeserializer<class_4236> {
		private final ModelVariantMap.class_4232 field_20825;

		public class_2883(ModelVariantMap.class_4232 arg) {
			this.field_20825 = arg;
		}

		public class_4236 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new class_4236(this.field_20825.method_19234(), this.method_12390(jsonDeserializationContext, jsonElement.getAsJsonArray()));
		}

		private List<class_2885> method_12390(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
			List<class_2885> list = Lists.newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				list.add(jsonDeserializationContext.deserialize(jsonElement, class_2885.class));
			}

			return list;
		}
	}
}
