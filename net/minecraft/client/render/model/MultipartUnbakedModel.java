package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

public class MultipartUnbakedModel implements UnbakedModel {
	private final StateManager<Block, BlockState> stateFactory;
	private final List<MultipartModelComponent> components;

	public MultipartUnbakedModel(StateManager<Block, BlockState> stateManager, List<MultipartModelComponent> list) {
		this.stateFactory = stateManager;
		this.components = list;
	}

	public List<MultipartModelComponent> getComponents() {
		return this.components;
	}

	public Set<WeightedUnbakedModel> getModels() {
		Set<WeightedUnbakedModel> set = Sets.newHashSet();

		for (MultipartModelComponent multipartModelComponent : this.components) {
			set.add(multipartModelComponent.getModel());
		}

		return set;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof MultipartUnbakedModel)) {
			return false;
		} else {
			MultipartUnbakedModel multipartUnbakedModel = (MultipartUnbakedModel)object;
			return Objects.equals(this.stateFactory, multipartUnbakedModel.stateFactory) && Objects.equals(this.components, multipartUnbakedModel.components);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.stateFactory, this.components});
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return (Collection<Identifier>)this.getComponents()
			.stream()
			.flatMap(multipartModelComponent -> multipartModelComponent.getModel().getModelDependencies().stream())
			.collect(Collectors.toSet());
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> set) {
		return (Collection<SpriteIdentifier>)this.getComponents()
			.stream()
			.flatMap(multipartModelComponent -> multipartModelComponent.getModel().getTextureDependencies(function, set).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();

		for (MultipartModelComponent multipartModelComponent : this.getComponents()) {
			BakedModel bakedModel = multipartModelComponent.getModel().bake(modelLoader, function, modelBakeSettings, identifier);
			if (bakedModel != null) {
				builder.addComponent(multipartModelComponent.getPredicate(this.stateFactory), bakedModel);
			}
		}

		return builder.build();
	}

	public static class Deserializer implements JsonDeserializer<MultipartUnbakedModel> {
		private final ModelVariantMap.DeserializationContext context;

		public Deserializer(ModelVariantMap.DeserializationContext deserializationContext) {
			this.context = deserializationContext;
		}

		public MultipartUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new MultipartUnbakedModel(this.context.getStateFactory(), this.deserializeComponents(jsonDeserializationContext, jsonElement.getAsJsonArray()));
		}

		private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
			List<MultipartModelComponent> list = Lists.newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				list.add(jsonDeserializationContext.deserialize(jsonElement, MultipartModelComponent.class));
			}

			return list;
		}
	}
}
