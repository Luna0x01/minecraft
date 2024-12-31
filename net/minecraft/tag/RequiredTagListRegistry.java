package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RequiredTagListRegistry {
	private static final Set<RegistryKey<?>> REQUIRED_LIST_KEYS = Sets.newHashSet();
	private static final List<RequiredTagList<?>> ALL = Lists.newArrayList();

	public static <T> RequiredTagList<T> register(RegistryKey<? extends Registry<T>> registryKey, String dataType) {
		if (!REQUIRED_LIST_KEYS.add(registryKey)) {
			throw new IllegalStateException("Duplicate entry for static tag collection: " + registryKey);
		} else {
			RequiredTagList<T> requiredTagList = new RequiredTagList<>(registryKey, dataType);
			ALL.add(requiredTagList);
			return requiredTagList;
		}
	}

	public static void updateTagManager(TagManager tagManager) {
		ALL.forEach(list -> list.updateTagManager(tagManager));
	}

	public static void clearAllTags() {
		ALL.forEach(RequiredTagList::clearAllTags);
	}

	public static Multimap<RegistryKey<? extends Registry<?>>, Identifier> getMissingTags(TagManager tagManager) {
		Multimap<RegistryKey<? extends Registry<?>>, Identifier> multimap = HashMultimap.create();
		ALL.forEach(list -> multimap.putAll(list.getRegistryKey(), list.getMissingTags(tagManager)));
		return multimap;
	}

	public static void validateRegistrations() {
		validate();
	}

	private static Set<RequiredTagList<?>> getBuiltinTags() {
		return ImmutableSet.of(BlockTags.REQUIRED_TAGS, ItemTags.REQUIRED_TAGS, FluidTags.REQUIRED_TAGS, EntityTypeTags.REQUIRED_TAGS, GameEventTags.REQUIRED_TAGS);
	}

	private static void validate() {
		Set<RegistryKey<?>> set = (Set<RegistryKey<?>>)getBuiltinTags().stream().map(RequiredTagList::getRegistryKey).collect(Collectors.toSet());
		if (!Sets.difference(REQUIRED_LIST_KEYS, set).isEmpty()) {
			throw new IllegalStateException("Missing helper registrations");
		}
	}

	public static void forEach(Consumer<RequiredTagList<?>> consumer) {
		ALL.forEach(consumer);
	}

	public static TagManager createBuiltinTagManager() {
		TagManager.Builder builder = new TagManager.Builder();
		validate();
		ALL.forEach(list -> list.addToManager(builder));
		return builder.build();
	}
}
