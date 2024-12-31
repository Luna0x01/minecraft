package net.minecraft.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;

public interface ResourceManager {
	Set<String> getAllNamespaces();

	Resource getResource(Identifier id) throws IOException;

	List<Resource> getAllResources(Identifier id) throws IOException;

	Collection<Identifier> method_21372(String string, Predicate<String> predicate);
}
