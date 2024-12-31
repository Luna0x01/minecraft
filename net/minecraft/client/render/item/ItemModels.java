package net.minecraft.client.render.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.MeshDefinition;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemModels {
	private final Map<Integer, ModelIdentifier> modelIds = Maps.newHashMap();
	private final Map<Integer, BakedModel> models = Maps.newHashMap();
	private final Map<Item, MeshDefinition> meshes = Maps.newHashMap();
	private final BakedModelManager modelManager;

	public ItemModels(BakedModelManager bakedModelManager) {
		this.modelManager = bakedModelManager;
	}

	public Sprite getSprite(Item item) {
		return this.getSprite(item, 0);
	}

	public Sprite getSprite(Item item, int metadata) {
		return this.getModel(new ItemStack(item, 1, metadata)).getParticleSprite();
	}

	public BakedModel getModel(ItemStack stack) {
		Item item = stack.getItem();
		BakedModel bakedModel = this.getModel(item, this.getMetadata(stack));
		if (bakedModel == null) {
			MeshDefinition meshDefinition = (MeshDefinition)this.meshes.get(item);
			if (meshDefinition != null) {
				bakedModel = this.modelManager.getByIdentifier(meshDefinition.getIdentifier(stack));
			}
		}

		if (bakedModel == null) {
			bakedModel = this.modelManager.getBakedModel();
		}

		return bakedModel;
	}

	protected int getMetadata(ItemStack stack) {
		return stack.getMaxDamage() > 0 ? 0 : stack.getData();
	}

	@Nullable
	protected BakedModel getModel(Item item, int metadata) {
		return (BakedModel)this.models.get(this.pack(item, metadata));
	}

	private int pack(Item item, int metadata) {
		return Item.getRawId(item) << 16 | metadata;
	}

	public void putModel(Item item, int metadata, ModelIdentifier id) {
		this.modelIds.put(this.pack(item, metadata), id);
		this.models.put(this.pack(item, metadata), this.modelManager.getByIdentifier(id));
	}

	public void putMesh(Item item, MeshDefinition definition) {
		this.meshes.put(item, definition);
	}

	public BakedModelManager getModelManager() {
		return this.modelManager;
	}

	public void reloadModels() {
		this.models.clear();

		for (Entry<Integer, ModelIdentifier> entry : this.modelIds.entrySet()) {
			this.models.put(entry.getKey(), this.modelManager.getByIdentifier((ModelIdentifier)entry.getValue()));
		}
	}
}
