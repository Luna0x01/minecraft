package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

public class DebugHud extends DrawableHelper {
	private static final int TEXT_COLOR = 14737632;
	private static final int field_32188 = 2;
	private static final int field_32189 = 2;
	private static final int field_32190 = 2;
	private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Util.make(new EnumMap(Heightmap.Type.class), types -> {
		types.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
		types.put(Heightmap.Type.WORLD_SURFACE, "S");
		types.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
		types.put(Heightmap.Type.OCEAN_FLOOR, "O");
		types.put(Heightmap.Type.MOTION_BLOCKING, "M");
		types.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
	});
	private final MinecraftClient client;
	private final TextRenderer textRenderer;
	private HitResult blockHit;
	private HitResult fluidHit;
	@Nullable
	private ChunkPos pos;
	@Nullable
	private WorldChunk chunk;
	@Nullable
	private CompletableFuture<WorldChunk> chunkFuture;
	private static final int field_32191 = -65536;
	private static final int field_32192 = -256;
	private static final int field_32193 = -16711936;

	public DebugHud(MinecraftClient client) {
		this.client = client;
		this.textRenderer = client.textRenderer;
	}

	public void resetChunk() {
		this.chunkFuture = null;
		this.chunk = null;
	}

	public void render(MatrixStack matrices) {
		this.client.getProfiler().push("debug");
		Entity entity = this.client.getCameraEntity();
		this.blockHit = entity.raycast(20.0, 0.0F, false);
		this.fluidHit = entity.raycast(20.0, 0.0F, true);
		this.renderLeftText(matrices);
		this.renderRightText(matrices);
		if (this.client.options.debugTpsEnabled) {
			int i = this.client.getWindow().getScaledWidth();
			this.drawMetricsData(matrices, this.client.getMetricsData(), 0, i / 2, true);
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				this.drawMetricsData(matrices, integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
			}
		}

		this.client.getProfiler().pop();
	}

	protected void renderLeftText(MatrixStack matrices) {
		List<String> list = this.getLeftText();
		list.add("");
		boolean bl = this.client.getServer() != null;
		list.add(
			"Debug: Pie [shift]: "
				+ (this.client.options.debugProfilerEnabled ? "visible" : "hidden")
				+ (bl ? " FPS + TPS" : " FPS")
				+ " [alt]: "
				+ (this.client.options.debugTpsEnabled ? "visible" : "hidden")
		);
		list.add("For help: press F3 + Q");

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = 9;
				int k = this.textRenderer.getWidth(string);
				int l = 2;
				int m = 2 + j * i;
				fill(matrices, 1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
				this.textRenderer.draw(matrices, string, 2.0F, (float)m, 14737632);
			}
		}
	}

	protected void renderRightText(MatrixStack matrices) {
		List<String> list = this.getRightText();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = 9;
				int k = this.textRenderer.getWidth(string);
				int l = this.client.getWindow().getScaledWidth() - 2 - k;
				int m = 2 + j * i;
				fill(matrices, l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
				this.textRenderer.draw(matrices, string, (float)l, (float)m, 14737632);
			}
		}
	}

	protected List<String> getLeftText() {
		IntegratedServer integratedServer = this.client.getServer();
		ClientConnection clientConnection = this.client.getNetworkHandler().getConnection();
		float f = clientConnection.getAveragePacketsSent();
		float g = clientConnection.getAveragePacketsReceived();
		String string;
		if (integratedServer != null) {
			string = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer.getTickTime(), f, g);
		} else {
			string = String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), f, g);
		}

		BlockPos blockPos = this.client.getCameraEntity().getBlockPos();
		if (this.client.hasReducedDebugInfo()) {
			return Lists.newArrayList(
				new String[]{
					"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
					this.client.fpsDebugString,
					string,
					this.client.worldRenderer.getChunksDebugString(),
					this.client.worldRenderer.getEntitiesDebugString(),
					"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(),
					this.client.world.asString(),
					"",
					String.format("Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
				}
			);
		} else {
			Entity entity = this.client.getCameraEntity();
			Direction direction = entity.getHorizontalFacing();

			String string7 = switch (direction) {
				case NORTH -> "Towards negative Z";
				case SOUTH -> "Towards positive Z";
				case WEST -> "Towards negative X";
				case EAST -> "Towards positive X";
				default -> "Invalid";
			};
			ChunkPos chunkPos = new ChunkPos(blockPos);
			if (!Objects.equals(this.pos, chunkPos)) {
				this.pos = chunkPos;
				this.resetChunk();
			}

			World world = this.getWorld();
			LongSet longSet = (LongSet)(world instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET);
			List<String> list = Lists.newArrayList(
				new String[]{
					"Minecraft "
						+ SharedConstants.getGameVersion().getName()
						+ " ("
						+ this.client.getGameVersion()
						+ "/"
						+ ClientBrandRetriever.getClientModName()
						+ ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType())
						+ ")",
					this.client.fpsDebugString,
					string,
					this.client.worldRenderer.getChunksDebugString(),
					this.client.worldRenderer.getEntitiesDebugString(),
					"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(),
					this.client.world.asString()
				}
			);
			String string8 = this.getServerWorldDebugString();
			if (string8 != null) {
				list.add(string8);
			}

			list.add(this.client.world.getRegistryKey().getValue() + " FC: " + longSet.size());
			list.add("");
			list.add(
				String.format(
					Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()
				)
			);
			list.add(String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
			list.add(
				String.format(
					"Chunk: %d %d %d in %d %d %d",
					blockPos.getX() & 15,
					blockPos.getY() & 15,
					blockPos.getZ() & 15,
					ChunkSectionPos.getSectionCoord(blockPos.getX()),
					ChunkSectionPos.getSectionCoord(blockPos.getY()),
					ChunkSectionPos.getSectionCoord(blockPos.getZ())
				)
			);
			list.add(
				String.format(
					Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string7, MathHelper.wrapDegrees(entity.getYaw()), MathHelper.wrapDegrees(entity.getPitch())
				)
			);
			WorldChunk worldChunk = this.getClientChunk();
			if (worldChunk.isEmpty()) {
				list.add("Waiting for chunk...");
			} else {
				int i = this.client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
				int j = this.client.world.getLightLevel(LightType.SKY, blockPos);
				int k = this.client.world.getLightLevel(LightType.BLOCK, blockPos);
				list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
				WorldChunk worldChunk2 = this.getChunk();
				StringBuilder stringBuilder = new StringBuilder("CH");

				for (Heightmap.Type type : Heightmap.Type.values()) {
					if (type.shouldSendToClient()) {
						stringBuilder.append(" ")
							.append((String)HEIGHT_MAP_TYPES.get(type))
							.append(": ")
							.append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
					}
				}

				list.add(stringBuilder.toString());
				stringBuilder.setLength(0);
				stringBuilder.append("SH");

				for (Heightmap.Type type2 : Heightmap.Type.values()) {
					if (type2.isStoredServerSide()) {
						stringBuilder.append(" ").append((String)HEIGHT_MAP_TYPES.get(type2)).append(": ");
						if (worldChunk2 != null) {
							stringBuilder.append(worldChunk2.sampleHeightmap(type2, blockPos.getX(), blockPos.getZ()));
						} else {
							stringBuilder.append("??");
						}
					}
				}

				list.add(stringBuilder.toString());
				if (blockPos.getY() >= this.client.world.getBottomY() && blockPos.getY() < this.client.world.getTopY()) {
					list.add("Biome: " + this.client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(this.client.world.getBiome(blockPos)));
					long l = 0L;
					float h = 0.0F;
					if (worldChunk2 != null) {
						h = world.getMoonSize();
						l = worldChunk2.getInhabitedTime();
					}

					LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
					list.add(
						String.format(
							Locale.ROOT,
							"Local Difficulty: %.2f // %.2f (Day %d)",
							localDifficulty.getLocalDifficulty(),
							localDifficulty.getClampedLocalDifficulty(),
							this.client.world.getTimeOfDay() / 24000L
						)
					);
				}
			}

			ServerWorld serverWorld = this.getServerWorld();
			if (serverWorld != null) {
				SpawnHelper.Info info = serverWorld.getChunkManager().getSpawnInfo();
				if (info != null) {
					Object2IntMap<SpawnGroup> object2IntMap = info.getGroupToCount();
					int m = info.getSpawningChunkCount();
					list.add(
						"SC: "
							+ m
							+ ", "
							+ (String)Stream.of(SpawnGroup.values())
								.map(spawnGroup -> Character.toUpperCase(spawnGroup.getName().charAt(0)) + ": " + object2IntMap.getInt(spawnGroup))
								.collect(Collectors.joining(", "))
					);
				} else {
					list.add("SC: N/A");
				}
			}

			ShaderEffect shaderEffect = this.client.gameRenderer.getShader();
			if (shaderEffect != null) {
				list.add("Shader: " + shaderEffect.getName());
			}

			list.add(this.client.getSoundManager().getDebugString() + String.format(" (Mood %d%%)", Math.round(this.client.player.getMoodPercentage() * 100.0F)));
			return list;
		}
	}

	@Nullable
	private ServerWorld getServerWorld() {
		IntegratedServer integratedServer = this.client.getServer();
		return integratedServer != null ? integratedServer.getWorld(this.client.world.getRegistryKey()) : null;
	}

	@Nullable
	private String getServerWorldDebugString() {
		ServerWorld serverWorld = this.getServerWorld();
		return serverWorld != null ? serverWorld.asString() : null;
	}

	private World getWorld() {
		return (World)DataFixUtils.orElse(
			Optional.ofNullable(this.client.getServer()).flatMap(integratedServer -> Optional.ofNullable(integratedServer.getWorld(this.client.world.getRegistryKey()))),
			this.client.world
		);
	}

	@Nullable
	private WorldChunk getChunk() {
		if (this.chunkFuture == null) {
			ServerWorld serverWorld = this.getServerWorld();
			if (serverWorld != null) {
				this.chunkFuture = serverWorld.getChunkManager()
					.getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false)
					.thenApply(either -> (WorldChunk)either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
			}

			if (this.chunkFuture == null) {
				this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
			}
		}

		return (WorldChunk)this.chunkFuture.getNow(null);
	}

	private WorldChunk getClientChunk() {
		if (this.chunk == null) {
			this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
		}

		return this.chunk;
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
				String.format("CPU: %s", GlDebugInfo.getCpuInfo()),
				"",
				String.format(
					"Display: %dx%d (%s)",
					MinecraftClient.getInstance().getWindow().getFramebufferWidth(),
					MinecraftClient.getInstance().getWindow().getFramebufferHeight(),
					GlDebugInfo.getVendor()
				),
				GlDebugInfo.getRenderer(),
				GlDebugInfo.getVersion()
			}
		);
		if (this.client.hasReducedDebugInfo()) {
			return list;
		} else {
			if (this.blockHit.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult)this.blockHit).getBlockPos();
				BlockState blockState = this.client.world.getBlockState(blockPos);
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
				list.add(String.valueOf(Registry.BLOCK.getId(blockState.getBlock())));
				UnmodifiableIterator var12 = blockState.getEntries().entrySet().iterator();

				while (var12.hasNext()) {
					Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var12.next();
					list.add(this.propertyToString(entry));
				}

				for (Identifier identifier : this.client.getNetworkHandler().getTagManager().getOrCreateTagGroup(Registry.BLOCK_KEY).getTagsFor(blockState.getBlock())) {
					list.add("#" + identifier);
				}
			}

			if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos2 = ((BlockHitResult)this.fluidHit).getBlockPos();
				FluidState fluidState = this.client.world.getFluidState(blockPos2);
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Fluid: " + blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ());
				list.add(String.valueOf(Registry.FLUID.getId(fluidState.getFluid())));
				UnmodifiableIterator var18 = fluidState.getEntries().entrySet().iterator();

				while (var18.hasNext()) {
					Entry<Property<?>, Comparable<?>> entry2 = (Entry<Property<?>, Comparable<?>>)var18.next();
					list.add(this.propertyToString(entry2));
				}

				for (Identifier identifier2 : this.client.getNetworkHandler().getTagManager().getOrCreateTagGroup(Registry.FLUID_KEY).getTagsFor(fluidState.getFluid())) {
					list.add("#" + identifier2);
				}
			}

			Entity entity = this.client.targetedEntity;
			if (entity != null) {
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Entity");
				list.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity.getType())));
			}

			return list;
		}
	}

	private String propertyToString(Entry<Property<?>, Comparable<?>> propEntry) {
		Property<?> property = (Property<?>)propEntry.getKey();
		Comparable<?> comparable = (Comparable<?>)propEntry.getValue();
		String string = Util.getValueAsString(property, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			string = Formatting.GREEN + string;
		} else if (Boolean.FALSE.equals(comparable)) {
			string = Formatting.RED + string;
		}

		return property.getName() + ": " + string;
	}

	private void drawMetricsData(MatrixStack matrices, MetricsData metricsData, int x, int width, boolean showFps) {
		RenderSystem.disableDepthTest();
		int i = metricsData.getStartIndex();
		int j = metricsData.getCurrentIndex();
		long[] ls = metricsData.getSamples();
		int l = x;
		int m = Math.max(0, ls.length - width);
		int n = ls.length - m;
		int k = metricsData.wrapIndex(i + m);
		long o = 0L;
		int p = Integer.MAX_VALUE;
		int q = Integer.MIN_VALUE;

		for (int r = 0; r < n; r++) {
			int s = (int)(ls[metricsData.wrapIndex(k + r)] / 1000000L);
			p = Math.min(p, s);
			q = Math.max(q, s);
			o += (long)s;
		}

		int t = this.client.getWindow().getScaledHeight();
		fill(matrices, x, t - 60, x + n, t, -1873784752);
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

		for (Matrix4f matrix4f = AffineTransformation.identity().getMatrix(); k != j; k = metricsData.wrapIndex(k + 1)) {
			int u = metricsData.method_15248(ls[k], showFps ? 30 : 60, showFps ? 60 : 20);
			int v = showFps ? 100 : 60;
			int w = this.getMetricsLineColor(MathHelper.clamp(u, 0, v), 0, v / 2, v);
			int y = w >> 24 & 0xFF;
			int z = w >> 16 & 0xFF;
			int aa = w >> 8 & 0xFF;
			int ab = w & 0xFF;
			bufferBuilder.vertex(matrix4f, (float)(l + 1), (float)t, 0.0F).color(z, aa, ab, y).next();
			bufferBuilder.vertex(matrix4f, (float)(l + 1), (float)(t - u + 1), 0.0F).color(z, aa, ab, y).next();
			bufferBuilder.vertex(matrix4f, (float)l, (float)(t - u + 1), 0.0F).color(z, aa, ab, y).next();
			bufferBuilder.vertex(matrix4f, (float)l, (float)t, 0.0F).color(z, aa, ab, y).next();
			l++;
		}

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		if (showFps) {
			fill(matrices, x + 1, t - 30 + 1, x + 14, t - 30 + 10, -1873784752);
			this.textRenderer.draw(matrices, "60 FPS", (float)(x + 2), (float)(t - 30 + 2), 14737632);
			this.drawHorizontalLine(matrices, x, x + n - 1, t - 30, -1);
			fill(matrices, x + 1, t - 60 + 1, x + 14, t - 60 + 10, -1873784752);
			this.textRenderer.draw(matrices, "30 FPS", (float)(x + 2), (float)(t - 60 + 2), 14737632);
			this.drawHorizontalLine(matrices, x, x + n - 1, t - 60, -1);
		} else {
			fill(matrices, x + 1, t - 60 + 1, x + 14, t - 60 + 10, -1873784752);
			this.textRenderer.draw(matrices, "20 TPS", (float)(x + 2), (float)(t - 60 + 2), 14737632);
			this.drawHorizontalLine(matrices, x, x + n - 1, t - 60, -1);
		}

		this.drawHorizontalLine(matrices, x, x + n - 1, t - 1, -1);
		this.drawVerticalLine(matrices, x, t - 60, t, -1);
		this.drawVerticalLine(matrices, x + n - 1, t - 60, t, -1);
		if (showFps && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
			this.drawHorizontalLine(matrices, x, x + n - 1, t - 1 - (int)(1800.0 / (double)this.client.options.maxFps), -16711681);
		}

		String string = p + " ms min";
		String string2 = o / (long)n + " ms avg";
		String string3 = q + " ms max";
		this.textRenderer.drawWithShadow(matrices, string, (float)(x + 2), (float)(t - 60 - 9), 14737632);
		this.textRenderer.drawWithShadow(matrices, string2, (float)(x + n / 2 - this.textRenderer.getWidth(string2) / 2), (float)(t - 60 - 9), 14737632);
		this.textRenderer.drawWithShadow(matrices, string3, (float)(x + n - this.textRenderer.getWidth(string3)), (float)(t - 60 - 9), 14737632);
		RenderSystem.enableDepthTest();
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
		int q = MathHelper.clamp((int)MathHelper.lerp(dt, (float)i, (float)m), 0, 255);
		int r = MathHelper.clamp((int)MathHelper.lerp(dt, (float)j, (float)n), 0, 255);
		int s = MathHelper.clamp((int)MathHelper.lerp(dt, (float)k, (float)o), 0, 255);
		int t = MathHelper.clamp((int)MathHelper.lerp(dt, (float)l, (float)p), 0, 255);
		return q << 24 | r << 16 | s << 8 | t;
	}

	private static long toMiB(long bytes) {
		return bytes / 1024L / 1024L;
	}
}
