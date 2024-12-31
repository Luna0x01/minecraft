package net.minecraft.client.render.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_4290;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockModelShapes {
	private final Map<BlockState, BakedModel> models = Maps.newIdentityHashMap();
	private final BakedModelManager bakedModelManager;

	public BlockModelShapes(BakedModelManager bakedModelManager) {
		this.bakedModelManager = bakedModelManager;
	}

	public Sprite getParticleSprite(BlockState state) {
		return this.getBakedModel(state).getParticleSprite();
	}

	public BakedModel getBakedModel(BlockState state) {
		BakedModel bakedModel = (BakedModel)this.models.get(state);
		if (bakedModel == null) {
			bakedModel = this.bakedModelManager.getBakedModel();
		}

		return bakedModel;
	}

	public BakedModelManager getBakedModelManager() {
		return this.bakedModelManager;
	}

	public void reload() {
		this.models.clear();

		for (Block block : Registry.BLOCK) {
			block.getStateManager().getBlockStates().forEach(blockState -> {
				BakedModel var10000 = (BakedModel)this.models.put(blockState, this.bakedModelManager.method_19594(method_19186(blockState)));
			});
		}
	}

	public static class_4290 method_19186(BlockState blockState) {
		return method_19185(Registry.BLOCK.getId(blockState.getBlock()), blockState);
	}

	public static class_4290 method_19185(Identifier identifier, BlockState blockState) {
		return new class_4290(identifier, method_19184(blockState.getEntries()));
	}

	public static String method_19184(Map<Property<?>, Comparable<?>> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
			if (stringBuilder.length() != 0) {
				stringBuilder.append(',');
			}

			Property<?> property = (Property<?>)entry.getKey();
			stringBuilder.append(property.getName());
			stringBuilder.append('=');
			stringBuilder.append(method_19183(property, (Comparable<?>)entry.getValue()));
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> String method_19183(Property<T> property, Comparable<?> comparable) {
		return property.name((T)comparable);
	}
}
