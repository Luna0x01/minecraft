package net.minecraft.client.search;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;

public class SearchManager implements SynchronousResourceReloadListener {
	public static final SearchManager.Key<ItemStack> ITEM_TOOLTIP = new SearchManager.Key<>();
	public static final SearchManager.Key<ItemStack> ITEM_TAG = new SearchManager.Key<>();
	public static final SearchManager.Key<RecipeResultCollection> RECIPE_OUTPUT = new SearchManager.Key<>();
	private final Map<SearchManager.Key<?>, SearchableContainer<?>> instances = Maps.newHashMap();

	@Override
	public void apply(ResourceManager resourceManager) {
		for (SearchableContainer<?> searchableContainer : this.instances.values()) {
			searchableContainer.reload();
		}
	}

	public <T> void put(SearchManager.Key<T> key, SearchableContainer<T> searchableContainer) {
		this.instances.put(key, searchableContainer);
	}

	public <T> SearchableContainer<T> get(SearchManager.Key<T> key) {
		return (SearchableContainer<T>)this.instances.get(key);
	}

	public static class Key<T> {
	}
}
