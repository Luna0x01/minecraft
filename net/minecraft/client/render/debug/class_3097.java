package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class class_3097 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;
	private PlayerEntity player;
	private double field_15283;
	private double field_15284;
	private double field_15285;

	public class_3097(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		this.player = this.client.player;
		this.field_15283 = this.player.prevTickX + (this.player.x - this.player.prevTickX) * (double)tickDelta;
		this.field_15284 = this.player.prevTickY + (this.player.y - this.player.prevTickY) * (double)tickDelta;
		this.field_15285 = this.player.prevTickZ + (this.player.z - this.player.prevTickZ) * (double)tickDelta;
		World world = this.client.player.world;
		List<Box> list = world.doesBoxCollide(this.player, this.player.getBoundingBox().expand(4.0, 4.0, 4.0));
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.method_12304(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (Box box : list) {
			WorldRenderer.drawBox(box.expand(0.002).offset(-this.field_15283, -this.field_15284, -this.field_15285), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
