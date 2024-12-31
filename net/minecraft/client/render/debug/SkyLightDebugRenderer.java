package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SkyLightDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public SkyLightDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f) {
		World world = this.client.world;
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		BlockPos blockPos = new BlockPos(d, e, f);
		LongSet longSet = new LongOpenHashSet();

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			int i = world.getLightLevel(LightType.field_9284, blockPos2);
			float g = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(g, 0.9F, 0.9F);
			long l = ChunkSectionPos.fromGlobalPos(blockPos2.asLong());
			if (longSet.add(l)) {
				DebugRenderer.drawString(
					world.getChunkManager().getLightingProvider().method_22876(LightType.field_9284, ChunkSectionPos.from(l)),
					(double)(ChunkSectionPos.getX(l) * 16 + 8),
					(double)(ChunkSectionPos.getY(l) * 16 + 8),
					(double)(ChunkSectionPos.getZ(l) * 16 + 8),
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				DebugRenderer.drawString(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j);
			}
		}

		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}
}
