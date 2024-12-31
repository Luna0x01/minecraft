package net.minecraft.resource;

import java.io.IOException;
import net.minecraft.util.Identifier;

public interface ResourceFactory {
	Resource getResource(Identifier id) throws IOException;
}
