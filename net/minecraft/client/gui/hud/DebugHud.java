package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.class_3595;
import net.minecraft.class_4079;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;

public class DebugHud extends DrawableHelper {
	private final MinecraftClient client;
	private final TextRenderer renderer;
	private BlockHitResult field_20065;
	private BlockHitResult field_20066;

	public DebugHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.renderer = minecraftClient.textRenderer;
	}

	public void method_18382() {
		this.client.profiler.push("debug");
		GlStateManager.pushMatrix();
		Entity entity = this.client.getCameraEntity();
		this.field_20065 = entity.method_10931(20.0, 0.0F, class_4079.NEVER);
		this.field_20066 = entity.method_10931(20.0, 0.0F, class_4079.ALWAYS);
		this.renderLeftText();
		this.method_18384();
		GlStateManager.popMatrix();
		if (this.client.options.field_19983) {
			this.drawMetricsData();
		}

		this.client.profiler.pop();
	}

	protected void renderLeftText() {
		List<String> list = this.getLeftText();
		list.add("");
		list.add(
			"Debug: Pie [shift]: "
				+ (this.client.options.field_19982 ? "visible" : "hidden")
				+ " FPS [alt]: "
				+ (this.client.options.field_19983 ? "visible" : "hidden")
		);
		list.add("For help: press F3 + Q");

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.renderer.fontHeight;
				int k = this.renderer.getStringWidth(string);
				int l = 2;
				int m = 2 + j * i;
				fill(1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
				this.renderer.method_18355(string, 2.0F, (float)m, 14737632);
			}
		}
	}

	protected void method_18384() {
		List<String> list = this.getRightText();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.renderer.fontHeight;
				int k = this.renderer.getStringWidth(string);
				int l = this.client.field_19944.method_18321() - 2 - k;
				int m = 2 + j * i;
				fill(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
				this.renderer.method_18355(string, (float)l, (float)m, 14737632);
			}
		}
	}

	protected List<String> getLeftText() {
		IntegratedServer integratedServer = this.client.getServer();
		ClientConnection clientConnection = this.client.getNetworkHandler().getClientConnection();
		float f = clientConnection.method_20163();
		float g = clientConnection.method_20162();
		String string;
		if (integratedServer != null) {
			string = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer.method_20338(), f, g);
		} else {
			string = String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), f, g);
		}

		BlockPos blockPos = new BlockPos(this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z);
		if (this.client.hasReducedDebugInfo()) {
			return Lists.newArrayList(
				new String[]{
					"Minecraft 1.13.2 (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
					this.client.fpsDebugString,
					string,
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
			String string3 = "Invalid";
			switch (direction) {
				case NORTH:
					string3 = "Towards negative Z";
					break;
				case SOUTH:
					string3 = "Towards positive Z";
					break;
				case WEST:
					string3 = "Towards negative X";
					break;
				case EAST:
					string3 = "Towards positive X";
			}

			DimensionType dimensionType = this.client.world.dimension.method_11789();
			World world;
			if (integratedServer != null && integratedServer.method_20312(dimensionType) != null) {
				world = integratedServer.method_20312(dimensionType);
			} else {
				world = this.client.world;
			}

			class_3595 lv = world.method_16398(dimensionType, class_3595::new, "chunks");
			List<String> list = Lists.newArrayList(
				new String[]{
					"Minecraft 1.13.2 ("
						+ this.client.getGameVersion()
						+ "/"
						+ ClientBrandRetriever.getClientModName()
						+ ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType())
						+ ")",
					this.client.fpsDebugString,
					string,
					this.client.worldRenderer.getChunksDebugString(),
					this.client.worldRenderer.getEntitiesDebugString(),
					"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.addDetailsToCrashReport(),
					this.client.world.getDebugString(),
					DimensionType.method_17196(dimensionType).toString() + " FC: " + (lv == null ? "n/a" : Integer.toString(lv.method_16296().size())),
					"",
					String.format(
						Locale.ROOT,
						"XYZ: %.3f / %.5f / %.3f",
						this.client.getCameraEntity().x,
						this.client.getCameraEntity().getBoundingBox().minY,
						this.client.getCameraEntity().z
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
					String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string3, MathHelper.wrapDegrees(entity.yaw), MathHelper.wrapDegrees(entity.pitch))
				}
			);
			if (this.client.world != null) {
				Chunk chunk = this.client.world.getChunk(blockPos);
				if (!this.client.world.method_16359(blockPos) || blockPos.getY() < 0 || blockPos.getY() >= 256) {
					list.add("Outside of world...");
				} else if (!chunk.isEmpty()) {
					list.add("Biome: " + Registry.BIOME.getId(chunk.method_17088(blockPos)));
					list.add(
						"Light: "
							+ chunk.method_16993(blockPos, 0, chunk.getWorld().dimension.isOverworld())
							+ " ("
							+ chunk.method_9132(LightType.SKY, blockPos, chunk.getWorld().dimension.isOverworld())
							+ " sky, "
							+ chunk.method_9132(LightType.BLOCK, blockPos, chunk.getWorld().dimension.isOverworld())
							+ " block)"
					);
					LocalDifficulty localDifficulty = this.client.world.method_8482(blockPos);
					if (this.client.isIntegratedServerRunning() && integratedServer != null) {
						ServerPlayerEntity serverPlayerEntity = integratedServer.getPlayerManager().getPlayer(this.client.player.getUuid());
						if (serverPlayerEntity != null) {
							localDifficulty = serverPlayerEntity.world.method_8482(new BlockPos(serverPlayerEntity));
						}
					}

					list.add(
						String.format(
							Locale.ROOT,
							"Local Difficulty: %.2f // %.2f (Day %d)",
							localDifficulty.getLocalDifficulty(),
							localDifficulty.getClampedLocalDifficulty(),
							this.client.world.getTimeOfDay() / 24000L
						)
					);
				} else {
					list.add("Waiting for chunk...");
				}
			}

			if (this.client.field_3818 != null && this.client.field_3818.method_19058()) {
				list.add("Shader: " + this.client.field_3818.method_19082().getName());
			}

			if (this.field_20065 != null && this.field_20065.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos2 = this.field_20065.getBlockPos();
				list.add(String.format("Looking at block: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
			}

			if (this.field_20066 != null && this.field_20066.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos3 = this.field_20066.getBlockPos();
				list.add(String.format("Looking at liquid: %d %d %d", blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()));
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
				String.format(
					"Display: %dx%d (%s)",
					MinecraftClient.getInstance().field_19944.method_18317(),
					MinecraftClient.getInstance().field_19944.method_18318(),
					GlStateManager.method_12320(7936)
				),
				GlStateManager.method_12320(7937),
				GlStateManager.method_12320(7938)
			}
		);
		if (this.client.hasReducedDebugInfo()) {
			return list;
		} else {
			if (this.field_20065 != null && this.field_20065.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = this.field_20065.getBlockPos();
				BlockState blockState = this.client.world.getBlockState(blockPos);
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Block");
				list.add(String.valueOf(Registry.BLOCK.getId(blockState.getBlock())));
				UnmodifiableIterator var12 = blockState.getEntries().entrySet().iterator();

				while (var12.hasNext()) {
					Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var12.next();
					list.add(this.method_18383(entry));
				}

				for (Identifier identifier : this.client.getNetworkHandler().method_18965().method_21492().method_21484(blockState.getBlock())) {
					list.add("#" + identifier);
				}
			}

			if (this.field_20066 != null && this.field_20066.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos2 = this.field_20066.getBlockPos();
				FluidState fluidState = this.client.world.getFluidState(blockPos2);
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Fluid");
				list.add(String.valueOf(Registry.FLUID.getId(fluidState.getFluid())));
				UnmodifiableIterator var18 = fluidState.getEntries().entrySet().iterator();

				while (var18.hasNext()) {
					Entry<Property<?>, Comparable<?>> entry2 = (Entry<Property<?>, Comparable<?>>)var18.next();
					list.add(this.method_18383(entry2));
				}

				for (Identifier identifier2 : this.client.getNetworkHandler().method_18965().method_21496().method_21484(fluidState.getFluid())) {
					list.add("#" + identifier2);
				}
			}

			Entity entity = this.client.targetedEntity;
			if (entity != null) {
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Entity");
				list.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity.method_15557())));
			}

			return list;
		}
	}

	private String method_18383(Entry<Property<?>, Comparable<?>> entry) {
		Property<?> property = (Property<?>)entry.getKey();
		Comparable<?> comparable = (Comparable<?>)entry.getValue();
		String string = Util.method_20219(property, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			string = Formatting.GREEN + string;
		} else if (Boolean.FALSE.equals(comparable)) {
			string = Formatting.RED + string;
		}

		return property.getName() + ": " + string;
	}

	private void drawMetricsData() {
		GlStateManager.disableDepthTest();
		MetricsData metricsData = this.client.getMetricsData();
		int i = metricsData.getStartIndex();
		int j = metricsData.getCurrentIndex();
		long[] ls = metricsData.getSamples();
		int k = i;
		int l = 0;
		int m = this.client.field_19944.method_18322();
		fill(0, m - 60, 240, m, -1873784752);

		while (k != j) {
			int n = metricsData.getFps(ls[k], 30);
			int o = this.getMetricsLineColor(MathHelper.clamp(n, 0, 60), 0, 30, 60);
			this.drawVerticalLine(l, m, m - n, o);
			l++;
			k = metricsData.wrapIndex(k + 1);
		}

		fill(1, m - 30 + 1, 14, m - 30 + 10, -1873784752);
		this.renderer.method_18355("60", 2.0F, (float)(m - 30 + 2), 14737632);
		this.drawHorizontalLine(0, 239, m - 30, -1);
		fill(1, m - 60 + 1, 14, m - 60 + 10, -1873784752);
		this.renderer.method_18355("30", 2.0F, (float)(m - 60 + 2), 14737632);
		this.drawHorizontalLine(0, 239, m - 60, -1);
		this.drawHorizontalLine(0, 239, m - 1, -1);
		this.drawVerticalLine(0, m - 60, m, -1);
		this.drawVerticalLine(239, m - 60, m, -1);
		if (this.client.options.maxFramerate <= 120) {
			this.drawHorizontalLine(0, 239, m - 60 + this.client.options.maxFramerate / 2, -16711681);
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
