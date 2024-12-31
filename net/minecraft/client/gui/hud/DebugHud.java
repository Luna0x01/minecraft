package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.MetricsData;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.LevelGeneratorType;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class DebugHud extends DrawableHelper {
	private final MinecraftClient client;
	private final TextRenderer renderer;

	public DebugHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.renderer = minecraftClient.textRenderer;
	}

	public void render(Window window) {
		this.client.profiler.push("debug");
		GlStateManager.pushMatrix();
		this.renderLeftText();
		this.renderRightText(window);
		GlStateManager.popMatrix();
		if (this.client.options.debugFpsEnabled) {
			this.drawMetricsData();
		}

		this.client.profiler.pop();
	}

	private boolean hasReducedDebugInfo() {
		return this.client.player.getReducedDebugInfo() || this.client.options.reducedDebugInfo;
	}

	protected void renderLeftText() {
		List<String> list = this.getLeftText();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.renderer.fontHeight;
				int k = this.renderer.getStringWidth(string);
				int l = 2;
				int m = 2 + j * i;
				fill(1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
				this.renderer.draw(string, 2, m, 14737632);
			}
		}
	}

	protected void renderRightText(Window window) {
		List<String> list = this.getRightText();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.renderer.fontHeight;
				int k = this.renderer.getStringWidth(string);
				int l = window.getWidth() - 2 - k;
				int m = 2 + j * i;
				fill(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
				this.renderer.draw(string, l, m, 14737632);
			}
		}
	}

	protected List<String> getLeftText() {
		BlockPos blockPos = new BlockPos(this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z);
		if (this.hasReducedDebugInfo()) {
			return Lists.newArrayList(
				new String[]{
					"Minecraft 1.8.9 (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
					this.client.fpsDebugString,
					this.client.worldRenderer.getChunksDebugString(),
					this.client.worldRenderer.getEntitiesDebugString(),
					"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.addDetailsToCrashReport(),
					this.client.world.getDebugString(),
					"",
					String.format("Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
				}
			);
		} else {
			Entity entity = this.client.getCameraEntity();
			Direction direction = entity.getHorizontalDirection();
			String string = "Invalid";
			switch (direction) {
				case NORTH:
					string = "Towards negative Z";
					break;
				case SOUTH:
					string = "Towards positive Z";
					break;
				case WEST:
					string = "Towards negative X";
					break;
				case EAST:
					string = "Towards positive X";
			}

			List<String> list = Lists.newArrayList(
				new String[]{
					"Minecraft 1.8.9 (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
					this.client.fpsDebugString,
					this.client.worldRenderer.getChunksDebugString(),
					this.client.worldRenderer.getEntitiesDebugString(),
					"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.addDetailsToCrashReport(),
					this.client.world.getDebugString(),
					"",
					String.format(
						"XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z
					),
					String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()),
					String.format(
						"Chunk: %d %d %d in %d %d %d",
						blockPos.getX() & 15,
						blockPos.getY() & 15,
						blockPos.getZ() & 15,
						blockPos.getX() >> 4,
						blockPos.getY() >> 4,
						blockPos.getZ() >> 4
					),
					String.format("Facing: %s (%s) (%.1f / %.1f)", direction, string, MathHelper.wrapDegrees(entity.yaw), MathHelper.wrapDegrees(entity.pitch))
				}
			);
			if (this.client.world != null && this.client.world.blockExists(blockPos)) {
				Chunk chunk = this.client.world.getChunk(blockPos);
				list.add("Biome: " + chunk.getBiomeAt(blockPos, this.client.world.getBiomeSource()).name);
				list.add(
					"Light: "
						+ chunk.getLightLevel(blockPos, 0)
						+ " ("
						+ chunk.getLightAtPos(LightType.SKY, blockPos)
						+ " sky, "
						+ chunk.getLightAtPos(LightType.BLOCK, blockPos)
						+ " block)"
				);
				LocalDifficulty localDifficulty = this.client.world.getLocalDifficulty(blockPos);
				if (this.client.isIntegratedServerRunning() && this.client.getServer() != null) {
					ServerPlayerEntity serverPlayerEntity = this.client.getServer().getPlayerManager().getPlayer(this.client.player.getUuid());
					if (serverPlayerEntity != null) {
						localDifficulty = serverPlayerEntity.world.getLocalDifficulty(new BlockPos(serverPlayerEntity));
					}
				}

				list.add(String.format("Local Difficulty: %.2f (Day %d)", localDifficulty.getLocalDifficulty(), this.client.world.getTimeOfDay() / 24000L));
			}

			if (this.client.gameRenderer != null && this.client.gameRenderer.areShadersSupported()) {
				list.add("Shader: " + this.client.gameRenderer.getShader().getName());
			}

			if (this.client.result != null && this.client.result.type == BlockHitResult.Type.BLOCK && this.client.result.getBlockPos() != null) {
				BlockPos blockPos2 = this.client.result.getBlockPos();
				list.add(String.format("Looking at: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
			}

			return list;
		}
	}

	protected List<String> getRightText() {
		long l = Runtime.getRuntime().maxMemory();
		long m = Runtime.getRuntime().totalMemory();
		long n = Runtime.getRuntime().freeMemory();
		long o = m - n;
		List<String> list = Lists.newArrayList(
			new String[]{
				String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32),
				String.format("Mem: % 2d%% %03d/%03dMB", o * 100L / l, toMiB(o), toMiB(l)),
				String.format("Allocated: % 2d%% %03dMB", m * 100L / l, toMiB(m)),
				"",
				String.format("CPU: %s", GLX.getProcessor()),
				"",
				String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)),
				GL11.glGetString(7937),
				GL11.glGetString(7938)
			}
		);
		if (this.hasReducedDebugInfo()) {
			return list;
		} else {
			if (this.client.result != null && this.client.result.type == BlockHitResult.Type.BLOCK && this.client.result.getBlockPos() != null) {
				BlockPos blockPos = this.client.result.getBlockPos();
				BlockState blockState = this.client.world.getBlockState(blockPos);
				if (this.client.world.getGeneratorType() != LevelGeneratorType.DEBUG) {
					blockState = blockState.getBlock().getBlockState(blockState, this.client.world, blockPos);
				}

				list.add("");
				list.add(String.valueOf(Block.REGISTRY.getIdentifier(blockState.getBlock())));

				for (Entry<Property, Comparable> entry : blockState.getPropertyMap().entrySet()) {
					String string = ((Comparable)entry.getValue()).toString();
					if (entry.getValue() == Boolean.TRUE) {
						string = Formatting.GREEN + string;
					} else if (entry.getValue() == Boolean.FALSE) {
						string = Formatting.RED + string;
					}

					list.add(((Property)entry.getKey()).getName() + ": " + string);
				}
			}

			return list;
		}
	}

	private void drawMetricsData() {
		GlStateManager.disableDepthTest();
		MetricsData metricsData = this.client.getMetricsData();
		int i = metricsData.getStartIndex();
		int j = metricsData.getCurrentIndex();
		long[] ls = metricsData.getSamples();
		Window window = new Window(this.client);
		int k = i;
		int l = 0;
		fill(0, window.getHeight() - 60, 240, window.getHeight(), -1873784752);

		while (k != j) {
			int m = metricsData.getFps(ls[k], 30);
			int n = this.getMetricsLineColor(MathHelper.clamp(m, 0, 60), 0, 30, 60);
			this.drawVerticalLine(l, window.getHeight(), window.getHeight() - m, n);
			l++;
			k = metricsData.wrapIndex(k + 1);
		}

		fill(1, window.getHeight() - 30 + 1, 14, window.getHeight() - 30 + 10, -1873784752);
		this.renderer.draw("60", 2, window.getHeight() - 30 + 2, 14737632);
		this.drawHorizontalLine(0, 239, window.getHeight() - 30, -1);
		fill(1, window.getHeight() - 60 + 1, 14, window.getHeight() - 60 + 10, -1873784752);
		this.renderer.draw("30", 2, window.getHeight() - 60 + 2, 14737632);
		this.drawHorizontalLine(0, 239, window.getHeight() - 60, -1);
		this.drawHorizontalLine(0, 239, window.getHeight() - 1, -1);
		this.drawVerticalLine(0, window.getHeight() - 60, window.getHeight(), -1);
		this.drawVerticalLine(239, window.getHeight() - 60, window.getHeight(), -1);
		if (this.client.options.maxFramerate <= 120) {
			this.drawHorizontalLine(0, 239, window.getHeight() - 60 + this.client.options.maxFramerate / 2, -16711681);
		}

		GlStateManager.enableDepthTest();
	}

	private int getMetricsLineColor(int value, int greenValue, int yellowValue, int redValue) {
		return value < yellowValue
			? this.interpolateColor(-16711936, -256, (float)value / (float)yellowValue)
			: this.interpolateColor(-256, -65536, (float)(value - yellowValue) / (float)(redValue - yellowValue));
	}

	private int interpolateColor(int color1, int color2, float dt) {
		int i = color1 >> 24 & 0xFF;
		int j = color1 >> 16 & 0xFF;
		int k = color1 >> 8 & 0xFF;
		int l = color1 & 0xFF;
		int m = color2 >> 24 & 0xFF;
		int n = color2 >> 16 & 0xFF;
		int o = color2 >> 8 & 0xFF;
		int p = color2 & 0xFF;
		int q = MathHelper.clamp((int)((float)i + (float)(m - i) * dt), 0, 255);
		int r = MathHelper.clamp((int)((float)j + (float)(n - j) * dt), 0, 255);
		int s = MathHelper.clamp((int)((float)k + (float)(o - k) * dt), 0, 255);
		int t = MathHelper.clamp((int)((float)l + (float)(p - l) * dt), 0, 255);
		return q << 24 | r << 16 | s << 8 | t;
	}

	private static long toMiB(long bytes) {
		return bytes / 1024L / 1024L;
	}
}
