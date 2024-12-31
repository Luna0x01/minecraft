package net.minecraft.client.model;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class Model implements Consumer<ModelPart> {
	protected final Function<Identifier, RenderLayer> layerFactory;
	public int textureWidth = 64;
	public int textureHeight = 32;

	public Model(Function<Identifier, RenderLayer> function) {
		this.layerFactory = function;
	}

	public void accept(ModelPart modelPart) {
	}

	public final RenderLayer getLayer(Identifier identifier) {
		return (RenderLayer)this.layerFactory.apply(identifier);
	}

	public abstract void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k);
}
