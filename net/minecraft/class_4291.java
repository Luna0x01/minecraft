package net.minecraft;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public interface class_4291 {
	Collection<Identifier> method_19600();

	Collection<Identifier> method_19598(Function<Identifier, class_4291> function, Set<String> set);

	@Nullable
	BakedModel method_19599(Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2, ModelRotation modelRotation, boolean bl);
}
