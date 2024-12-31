package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity> {
	private static final Identifier PARTICLES = new Identifier("textures/particle/particles.png");

	public FishingBobberEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(FishingBobberEntity fishingBobberEntity, double d, double e, double f, float g, float h) {
		PlayerEntity playerEntity = fishingBobberEntity.getThrower();
		if (playerEntity != null && !this.field_13631) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)d, (float)e, (float)f);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			this.bindTexture(fishingBobberEntity);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			int i = 1;
			int j = 2;
			float k = 0.03125F;
			float l = 0.0625F;
			float m = 0.0625F;
			float n = 0.09375F;
			float o = 1.0F;
			float p = 0.5F;
			float q = 0.5F;
			GlStateManager.rotate(180.0F - this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float)(this.dispatcher.options.perspective == 2 ? -1 : 1) * -this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
			if (this.field_13631) {
				GlStateManager.enableColorMaterial();
				GlStateManager.method_12309(this.method_12454(fishingBobberEntity));
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_NORMAL);
			bufferBuilder.vertex(-0.5, -0.5, 0.0).texture(0.03125, 0.09375).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, -0.5, 0.0).texture(0.0625, 0.09375).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, 0.5, 0.0).texture(0.0625, 0.0625).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(-0.5, 0.5, 0.0).texture(0.03125, 0.0625).normal(0.0F, 1.0F, 0.0F).next();
			tessellator.draw();
			if (this.field_13631) {
				GlStateManager.method_12315();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			int r = playerEntity.getDurability() == HandOption.RIGHT ? 1 : -1;
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (itemStack.getItem() != Items.FISHING_ROD) {
				r = -r;
			}

			float s = playerEntity.getHandSwingProgress(h);
			float t = MathHelper.sin(MathHelper.sqrt(s) * (float) Math.PI);
			float u = (playerEntity.prevBodyYaw + (playerEntity.bodyYaw - playerEntity.prevBodyYaw) * h) * (float) (Math.PI / 180.0);
			double v = (double)MathHelper.sin(u);
			double w = (double)MathHelper.cos(u);
			double x = (double)r * 0.35;
			double y = 0.8;
			double ae;
			double af;
			double ag;
			double ah;
			if ((this.dispatcher.options == null || this.dispatcher.options.perspective <= 0) && playerEntity == MinecraftClient.getInstance().player) {
				double ad = this.dispatcher.options.field_19984;
				ad /= 100.0;
				Vec3d vec3d = new Vec3d((double)r * -0.36 * ad, -0.045 * ad, 0.4);
				vec3d = vec3d.rotateX(-(playerEntity.prevPitch + (playerEntity.pitch - playerEntity.prevPitch) * h) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-(playerEntity.prevYaw + (playerEntity.yaw - playerEntity.prevYaw) * h) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(t * 0.5F);
				vec3d = vec3d.rotateX(-t * 0.7F);
				ae = playerEntity.prevX + (playerEntity.x - playerEntity.prevX) * (double)h + vec3d.x;
				af = playerEntity.prevY + (playerEntity.y - playerEntity.prevY) * (double)h + vec3d.y;
				ag = playerEntity.prevZ + (playerEntity.z - playerEntity.prevZ) * (double)h + vec3d.z;
				ah = (double)playerEntity.getEyeHeight();
			} else {
				ae = playerEntity.prevX + (playerEntity.x - playerEntity.prevX) * (double)h - w * x - v * 0.8;
				af = playerEntity.prevY + (double)playerEntity.getEyeHeight() + (playerEntity.y - playerEntity.prevY) * (double)h - 0.45;
				ag = playerEntity.prevZ + (playerEntity.z - playerEntity.prevZ) * (double)h - v * x + w * 0.8;
				ah = playerEntity.isSneaking() ? -0.1875 : 0.0;
			}

			double ai = fishingBobberEntity.prevX + (fishingBobberEntity.x - fishingBobberEntity.prevX) * (double)h;
			double aj = fishingBobberEntity.prevY + (fishingBobberEntity.y - fishingBobberEntity.prevY) * (double)h + 0.25;
			double ak = fishingBobberEntity.prevZ + (fishingBobberEntity.z - fishingBobberEntity.prevZ) * (double)h;
			double al = (double)((float)(ae - ai));
			double am = (double)((float)(af - aj)) + ah;
			double an = (double)((float)(ag - ak));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
			int ao = 16;

			for (int ap = 0; ap <= 16; ap++) {
				float aq = (float)ap / 16.0F;
				bufferBuilder.vertex(d + al * (double)aq, e + am * (double)(aq * aq + aq) * 0.5 + 0.25, f + an * (double)aq).color(0, 0, 0, 255).next();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			super.render(fishingBobberEntity, d, e, f, g, h);
		}
	}

	protected Identifier getTexture(FishingBobberEntity fishingBobberEntity) {
		return PARTICLES;
	}
}
