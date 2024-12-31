package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.text.Nameable;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class class_4239<T extends BlockEntity> {
	public static final Identifier[] field_20846 = new Identifier[]{
		new Identifier("textures/" + class_4288.field_21069.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21070.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21071.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21072.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21073.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21074.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21075.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21076.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21077.getPath() + ".png"),
		new Identifier("textures/" + class_4288.field_21078.getPath() + ".png")
	};
	protected BlockEntityRenderDispatcher field_20847;

	public void method_1631(T blockEntity, double d, double e, double f, float g, int i) {
		if (blockEntity instanceof Nameable && this.field_20847.field_14963 != null && blockEntity.getPos().equals(this.field_20847.field_14963.getBlockPos())) {
			this.method_19328(true);
			this.method_19326(blockEntity, ((Nameable)blockEntity).getName().asFormattedString(), d, e, f, 12);
			this.method_19328(false);
		}
	}

	protected void method_19328(boolean bl) {
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		if (bl) {
			GlStateManager.disableTexture();
		} else {
			GlStateManager.enableTexture();
		}

		GlStateManager.activeTexture(GLX.textureUnit);
	}

	protected void method_19327(Identifier identifier) {
		TextureManager textureManager = this.field_20847.textureManager;
		if (textureManager != null) {
			textureManager.bindTexture(identifier);
		}
	}

	protected World method_19325() {
		return this.field_20847.world;
	}

	public void method_1632(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.field_20847 = blockEntityRenderDispatcher;
	}

	public TextRenderer method_19329() {
		return this.field_20847.getTextRenderer();
	}

	public boolean method_12410(T blockEntity) {
		return false;
	}

	protected void method_19326(T blockEntity, String string, double d, double e, double f, int i) {
		Entity entity = this.field_20847.entity;
		double g = blockEntity.getSquaredDistance(entity.x, entity.y, entity.z);
		if (!(g > (double)(i * i))) {
			float h = this.field_20847.cameraYaw;
			float j = this.field_20847.cameraPitch;
			boolean bl = false;
			class_4218.method_19068(this.method_19329(), string, (float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F, 0, h, j, false, false);
		}
	}
}
