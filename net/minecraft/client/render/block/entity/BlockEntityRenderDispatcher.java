package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3096;
import net.minecraft.class_3298;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityRenderDispatcher {
	private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = Maps.newHashMap();
	public static BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
	private TextRenderer textRenderer;
	public static double CAMERA_X;
	public static double CAMERA_Y;
	public static double CAMERA_Z;
	public TextureManager textureManager;
	public World world;
	public Entity entity;
	public float cameraYaw;
	public float cameraPitch;
	public BlockHitResult field_14963;
	public double cameraX;
	public double cameraY;
	public double cameraZ;

	private BlockEntityRenderDispatcher() {
		this.renderers.put(SignBlockEntity.class, new SignBlockEntityRenderer());
		this.renderers.put(MobSpawnerBlockEntity.class, new MobSpawnerBlockEntityRenderer());
		this.renderers.put(PistonBlockEntity.class, new PistonBlockEntityRenderer());
		this.renderers.put(ChestBlockEntity.class, new ChestBlockEntityRenderer());
		this.renderers.put(EnderChestBlockEntity.class, new EnderChestBlockEntityRenderer());
		this.renderers.put(EnchantingTableBlockEntity.class, new EnchantingTableBlockEntityRenderer());
		this.renderers.put(EndPortalBlockEntity.class, new EndPortalBlockEntityRenderer());
		this.renderers.put(EndGatewayBlockEntity.class, new EndGatewayBlockEntityRenderer());
		this.renderers.put(BeaconBlockEntity.class, new BeaconBlockEntityRenderer());
		this.renderers.put(SkullBlockEntity.class, new SkullBlockEntityRenderer());
		this.renderers.put(BannerBlockEntity.class, new BannerBlockEntityRenderer());
		this.renderers.put(StructureBlockEntity.class, new StructureBlockEntityRenderer());
		this.renderers.put(ShulkerBoxBlockEntity.class, new class_3096(new ShulkerEntityModel()));
		this.renderers.put(BedBlockEntity.class, new class_3298());

		for (BlockEntityRenderer<?> blockEntityRenderer : this.renderers.values()) {
			blockEntityRenderer.setDispatcher(this);
		}
	}

	public <T extends BlockEntity> BlockEntityRenderer<T> render(Class<? extends BlockEntity> clazz) {
		BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer = (BlockEntityRenderer<? extends BlockEntity>)this.renderers.get(clazz);
		if (blockEntityRenderer == null && clazz != BlockEntity.class) {
			blockEntityRenderer = this.render(clazz.getSuperclass());
			this.renderers.put(clazz, blockEntityRenderer);
		}

		return (BlockEntityRenderer<T>)blockEntityRenderer;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(@Nullable BlockEntity entity) {
		return entity == null ? null : this.render(entity.getClass());
	}

	public void method_1629(World world, TextureManager textureManager, TextRenderer textRenderer, Entity entity, BlockHitResult blockHitResult, float f) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.textureManager = textureManager;
		this.entity = entity;
		this.textRenderer = textRenderer;
		this.field_14963 = blockHitResult;
		this.cameraYaw = entity.prevYaw + (entity.yaw - entity.prevYaw) * f;
		this.cameraPitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * f;
		this.cameraX = entity.prevTickX + (entity.x - entity.prevTickX) * (double)f;
		this.cameraY = entity.prevTickY + (entity.y - entity.prevTickY) * (double)f;
		this.cameraZ = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)f;
	}

	public void renderEntity(BlockEntity blockEntity, float tickDelta, int destroyProgress) {
		if (blockEntity.getSquaredDistance(this.cameraX, this.cameraY, this.cameraZ) < blockEntity.getSquaredRenderDistance()) {
			DiffuseLighting.enableNormally();
			int i = this.world.getLight(blockEntity.getPos(), 0);
			int j = i % 65536;
			int k = i / 65536;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)j, (float)k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos blockPos = blockEntity.getPos();
			this.renderBlockEntity(
				blockEntity, (double)blockPos.getX() - CAMERA_X, (double)blockPos.getY() - CAMERA_Y, (double)blockPos.getZ() - CAMERA_Z, tickDelta, destroyProgress, 1.0F
			);
		}
	}

	public void renderBlockEntity(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
		this.renderBlockEntity(blockEntity, x, y, z, tickDelta, 1.0F);
	}

	public void renderBlockEntity(BlockEntity blockEntity, double x, double y, double z, float tickDelta, float f) {
		this.renderBlockEntity(blockEntity, x, y, z, tickDelta, -1, f);
	}

	public void renderBlockEntity(BlockEntity blockEntity, double x, double y, double z, float tickDelta, int i, float f) {
		BlockEntityRenderer<BlockEntity> blockEntityRenderer = this.getRenderer(blockEntity);
		if (blockEntityRenderer != null) {
			try {
				blockEntityRenderer.render(blockEntity, x, y, z, tickDelta, i, f);
			} catch (Throwable var15) {
				CrashReport crashReport = CrashReport.create(var15, "Rendering Block Entity");
				CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
				blockEntity.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}
		}
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.entity = null;
		}
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}
}
