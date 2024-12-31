package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SpawnerBlockEntityBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class MobSpawnerBlockEntityRenderer extends BlockEntityRenderer<MobSpawnerBlockEntity> {
	public void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d + 0.5F, (float)e, (float)f + 0.5F);
		renderEntity(mobSpawnerBlockEntity.getLogic(), d, e, f, g);
		GlStateManager.popMatrix();
	}

	public static void renderEntity(SpawnerBlockEntityBehavior behavior, double x, double y, double z, float tickDelta) {
		Entity entity = behavior.getRenderedEntity(behavior.getWorld());
		if (entity != null) {
			float f = 0.4375F;
			GlStateManager.translate(0.0F, 0.4F, 0.0F);
			GlStateManager.rotate((float)(behavior.method_8464() + (behavior.method_8463() - behavior.method_8464()) * (double)tickDelta) * 10.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.4F, 0.0F);
			GlStateManager.scale(f, f, f);
			entity.refreshPositionAndAngles(x, y, z, 0.0F, 0.0F);
			MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0, 0.0, 0.0, 0.0F, tickDelta);
		}
	}
}
