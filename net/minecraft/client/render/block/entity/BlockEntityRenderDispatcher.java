package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BlockEntityRenderDispatcher {
	private final Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = Maps.newHashMap();
	public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
	private final BufferBuilder bufferBuilder = new BufferBuilder(256);
	private TextRenderer textRenderer;
	public TextureManager textureManager;
	public World world;
	public Camera camera;
	public HitResult crosshairTarget;

	private BlockEntityRenderDispatcher() {
		this.register(BlockEntityType.field_11911, new SignBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11889, new MobSpawnerBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11897, new PistonBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11914, new ChestBlockEntityRenderer<>(this));
		this.register(BlockEntityType.field_11901, new ChestBlockEntityRenderer<>(this));
		this.register(BlockEntityType.field_11891, new ChestBlockEntityRenderer<>(this));
		this.register(BlockEntityType.field_11912, new EnchantingTableBlockEntityRenderer(this));
		this.register(BlockEntityType.field_16412, new LecternBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11898, new EndPortalBlockEntityRenderer<>(this));
		this.register(BlockEntityType.field_11906, new EndGatewayBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11890, new BeaconBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11913, new SkullBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11905, new BannerBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11895, new StructureBlockBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11896, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel(), this));
		this.register(BlockEntityType.field_11910, new BedBlockEntityRenderer(this));
		this.register(BlockEntityType.field_11902, new ConduitBlockEntityRenderer(this));
		this.register(BlockEntityType.field_16413, new BellBlockEntityRenderer(this));
		this.register(BlockEntityType.field_17380, new CampfireBlockEntityRenderer(this));
	}

	private <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer) {
		this.renderers.put(blockEntityType, blockEntityRenderer);
	}

	@Nullable
	public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
		return (BlockEntityRenderer<E>)this.renderers.get(blockEntity.getType());
	}

	public void configure(World world, TextureManager textureManager, TextRenderer textRenderer, Camera camera, HitResult hitResult) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.textureManager = textureManager;
		this.camera = camera;
		this.textRenderer = textRenderer;
		this.crosshairTarget = hitResult;
	}

	public <E extends BlockEntity> void render(E blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
		if (blockEntity.getSquaredDistance(this.camera.getPos().x, this.camera.getPos().y, this.camera.getPos().z) < blockEntity.getSquaredRenderDistance()) {
			BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
			if (blockEntityRenderer != null) {
				if (blockEntity.hasWorld() && blockEntity.getType().supports(blockEntity.getCachedState().getBlock())) {
					runReported(blockEntity, () -> render(blockEntityRenderer, blockEntity, f, matrixStack, vertexConsumerProvider));
				}
			}
		}
	}

	private static <T extends BlockEntity> void render(
		BlockEntityRenderer<T> blockEntityRenderer, T blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider
	) {
		World world = blockEntity.getWorld();
		int i;
		if (world != null) {
			i = WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos());
		} else {
			i = 15728880;
		}

		blockEntityRenderer.render(blockEntity, f, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
	}

	public <E extends BlockEntity> boolean renderEntity(E blockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
		if (blockEntityRenderer == null) {
			return true;
		} else {
			runReported(blockEntity, () -> blockEntityRenderer.render(blockEntity, 0.0F, matrixStack, vertexConsumerProvider, i, j));
			return false;
		}
	}

	private static void runReported(BlockEntity blockEntity, Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Rendering Block Entity");
			CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
			blockEntity.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.camera = null;
		}
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}
}
