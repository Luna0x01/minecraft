package net.minecraft.resource;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Identifier;

public interface ResourceManager {
	Set<String> getAllNamespaces();

	Resource getResource(Identifier id) throws IOException;

	List<Resource> getAllResources(Identifier id) throws IOException;
}
