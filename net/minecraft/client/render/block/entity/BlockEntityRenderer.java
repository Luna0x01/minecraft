package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class BlockEntityRenderer<T extends BlockEntity> {
	protected static final Identifier[] DESTROY_STAGE_TEXTURE = new Identifier[]{
		new Identifier("textures/blocks/destroy_stage_0.png"),
		new Identifier("textures/blocks/destroy_stage_1.png"),
		new Identifier("textures/blocks/destroy_stage_2.png"),
		new Identifier("textures/blocks/destroy_stage_3.png"),
		new Identifier("textures/blocks/destroy_stage_4.png"),
		new Identifier("textures/blocks/destroy_stage_5.png"),
		new Identifier("textures/blocks/destroy_stage_6.png"),
		new Identifier("textures/blocks/destroy_stage_7.png"),
		new Identifier("textures/blocks/destroy_stage_8.png"),
		new Identifier("textures/blocks/destroy_stage_9.png")
	};
	protected BlockEntityRenderDispatcher dispatcher;

	public abstract void render(T blockEntity, double x, double y, double z, float tickDelta, int destroyProgress);

	protected void bindTexture(Identifier texture) {
		TextureManager textureManager = this.dispatcher.textureManager;
		if (textureManager != null) {
			textureManager.bindTexture(texture);
		}
	}

	protected World getWorld() {
		return this.dispatcher.world;
	}

	public void setDispatcher(BlockEntityRenderDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public TextRenderer getTextRenderer() {
		return this.dispatcher.getTextRenderer();
	}

	public boolean rendersOutsideBoundingBox() {
		return false;
	}
}
