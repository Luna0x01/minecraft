package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityRenderDispatcher {
	private Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = Maps.newHashMap();
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

	public void updateCamera(World world, TextureManager textureManager, TextRenderer textRenderer, Entity camera, float tickDelta) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.textureManager = textureManager;
		this.entity = camera;
		this.textRenderer = textRenderer;
		this.cameraYaw = camera.prevYaw + (camera.yaw - camera.prevYaw) * tickDelta;
		this.cameraPitch = camera.prevPitch + (camera.pitch - camera.prevPitch) * tickDelta;
		this.cameraX = camera.prevTickX + (camera.x - camera.prevTickX) * (double)tickDelta;
		this.cameraY = camera.prevTickY + (camera.y - camera.prevTickY) * (double)tickDelta;
		this.cameraZ = camera.prevTickZ + (camera.z - camera.prevTickZ) * (double)tickDelta;
	}

	public void renderEntity(BlockEntity blockEntity, float tickDelta, int destroyProgress) {
		if (blockEntity.getSquaredDistance(this.cameraX, this.cameraY, this.cameraZ) < blockEntity.getSquaredRenderDistance()) {
			int i = this.world.getLight(blockEntity.getPos(), 0);
			int j = i % 65536;
			int k = i / 65536;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)j, (float)k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos blockPos = blockEntity.getPos();
			this.renderEntity(
				blockEntity, (double)blockPos.getX() - CAMERA_X, (double)blockPos.getY() - CAMERA_Y, (double)blockPos.getZ() - CAMERA_Z, tickDelta, destroyProgress
			);
		}
	}

	public void renderBlockEntity(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
		this.renderEntity(blockEntity, x, y, z, tickDelta, -1);
	}

	public void renderEntity(BlockEntity entity, double x, double y, double z, float tickDelta, int destroyProgress) {
		BlockEntityRenderer<BlockEntity> blockEntityRenderer = this.getRenderer(entity);
		if (blockEntityRenderer != null) {
			try {
				blockEntityRenderer.render(entity, x, y, z, tickDelta, destroyProgress);
			} catch (Throwable var14) {
				CrashReport crashReport = CrashReport.create(var14, "Rendering Block Entity");
				CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
				entity.populateCrashReport(crashReportSection);
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
