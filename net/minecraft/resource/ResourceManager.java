package net.minecraft.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;

public interface ResourceManager {
	Set<String> getAllNamespaces();

	Resource getResource(Identifier identifier) throws IOException;

	boolean containsResource(Identifier identifier);

	List<Resource> getAllResources(Identifier identifier) throws IOException;

	Collection<Identifier> findResources(String string, Predicate<String> predicate);
}
