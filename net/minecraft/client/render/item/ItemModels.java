package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4290;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;

public class ItemModels {
	public final Int2ObjectMap<class_4290> field_20739 = new Int2ObjectOpenHashMap(256);
	private final Int2ObjectMap<BakedModel> field_20740 = new Int2ObjectOpenHashMap(256);
	private final BakedModelManager modelManager;

	public ItemModels(BakedModelManager bakedModelManager) {
		this.modelManager = bakedModelManager;
	}

	public Sprite method_19155(Itemable itemable) {
		return this.method_19154(new ItemStack(itemable));
	}

	public Sprite method_19154(ItemStack itemStack) {
		BakedModel bakedModel = this.getModel(itemStack);
		return (bakedModel == this.modelManager.getBakedModel() || bakedModel.isBuiltin()) && itemStack.getItem() instanceof BlockItem
			? this.modelManager.getModelShapes().getParticleSprite(((BlockItem)itemStack.getItem()).getBlock().getDefaultState())
			: bakedModel.getParticleSprite();
	}

	public BakedModel getModel(ItemStack stack) {
		BakedModel bakedModel = this.method_19152(stack.getItem());
		return bakedModel == null ? this.modelManager.getBakedModel() : bakedModel;
	}

	@Nullable
	public BakedModel method_19152(Item item) {
		return (BakedModel)this.field_20740.get(method_19156(item));
	}

	private static int method_19156(Item item) {
		return Item.getRawId(item);
	}

	public void method_19153(Item item, class_4290 arg) {
		this.field_20739.put(method_19156(item), arg);
		this.field_20740.put(method_19156(item), this.modelManager.method_19594(arg));
	}

	public BakedModelManager getModelManager() {
		return this.modelManager;
	}

	public void reloadModels() {
		this.field_20740.clear();
		ObjectIterator var1 = this.field_20739.entrySet().iterator();

		while (var1.hasNext()) {
			Entry<Integer, class_4290> entry = (Entry<Integer, class_4290>)var1.next();
			this.field_20740.put((Integer)entry.getKey(), this.modelManager.method_19594((class_4290)entry.getValue()));
		}
	}
}
