package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
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

	public void render(T blockEntity, double x, double y, double z, float tickDelta, int destroyProgress) {
		Text text = blockEntity.getName();
		if (text != null && this.dispatcher.field_14963 != null && blockEntity.getPos().equals(this.dispatcher.field_14963.getBlockPos())) {
			this.method_13445(true);
			this.method_13444(blockEntity, text.asFormattedString(), x, y, z, 12);
			this.method_13445(false);
		}
	}

	protected void method_13445(boolean bl) {
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		if (bl) {
			GlStateManager.disableTexture();
		} else {
			GlStateManager.enableTexture();
		}

		GlStateManager.activeTexture(GLX.textureUnit);
	}

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

	public boolean method_12410(T blockEntity) {
		return false;
	}

	protected void method_13444(T blockEntity, String string, double x, double y, double z, int i) {
		Entity entity = this.dispatcher.entity;
		double d = blockEntity.getSquaredDistance(entity.x, entity.y, entity.z);
		if (!(d > (double)(i * i))) {
			float f = this.dispatcher.cameraYaw;
			float g = this.dispatcher.cameraPitch;
			boolean bl = false;
			GameRenderer.method_13427(this.getTextRenderer(), string, (float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F, 0, f, g, false, false);
		}
	}
}
