package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3096;
import net.minecraft.class_3298;
import net.minecraft.class_3741;
import net.minecraft.class_4195;
import net.minecraft.class_4239;
import net.minecraft.class_4240;
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
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityRenderDispatcher {
	private final Map<Class<? extends BlockEntity>, class_4239<? extends BlockEntity>> renderers = Maps.newHashMap();
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
		this.renderers.put(EnderChestBlockEntity.class, new ChestBlockEntityRenderer());
		this.renderers.put(EnchantingTableBlockEntity.class, new EnchantingTableBlockEntityRenderer());
		this.renderers.put(EndPortalBlockEntity.class, new EndPortalBlockEntityRenderer());
		this.renderers.put(EndGatewayBlockEntity.class, new EndGatewayBlockEntityRenderer());
		this.renderers.put(BeaconBlockEntity.class, new BeaconBlockEntityRenderer());
		this.renderers.put(SkullBlockEntity.class, new SkullBlockEntityRenderer());
		this.renderers.put(BannerBlockEntity.class, new BannerBlockEntityRenderer());
		this.renderers.put(StructureBlockEntity.class, new StructureBlockEntityRenderer());
		this.renderers.put(ShulkerBoxBlockEntity.class, new class_3096(new class_4195()));
		this.renderers.put(BedBlockEntity.class, new class_3298());
		this.renderers.put(class_3741.class, new class_4240());

		for (class_4239<?> lv : this.renderers.values()) {
			lv.method_1632(this);
		}
	}

	public <T extends BlockEntity> class_4239<T> method_1627(Class<? extends BlockEntity> class_) {
		class_4239<? extends BlockEntity> lv = (class_4239<? extends BlockEntity>)this.renderers.get(class_);
		if (lv == null && class_ != BlockEntity.class) {
			lv = this.method_1627(class_.getSuperclass());
			this.renderers.put(class_, lv);
		}

		return (class_4239<T>)lv;
	}

	@Nullable
	public <T extends BlockEntity> class_4239<T> method_1630(@Nullable BlockEntity blockEntity) {
		return blockEntity == null ? null : this.method_1627(blockEntity.getClass());
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
			int i = this.world.method_8578(blockEntity.getPos(), 0);
			int j = i % 65536;
			int k = i / 65536;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)j, (float)k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos blockPos = blockEntity.getPos();
			this.method_10100(
				blockEntity, (double)blockPos.getX() - CAMERA_X, (double)blockPos.getY() - CAMERA_Y, (double)blockPos.getZ() - CAMERA_Z, tickDelta, destroyProgress, false
			);
		}
	}

	public void renderBlockEntity(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
		this.method_10100(blockEntity, x, y, z, tickDelta, -1, false);
	}

	public void method_19324(BlockEntity blockEntity) {
		this.method_10100(blockEntity, 0.0, 0.0, 0.0, 0.0F, -1, true);
	}

	public void method_10100(BlockEntity blockEntity, double d, double e, double f, float g, int i, boolean bl) {
		class_4239<BlockEntity> lv = this.method_1630(blockEntity);
		if (lv != null) {
			try {
				if (!bl && (!blockEntity.hasWorld() || !blockEntity.method_16783().getBlock().hasBlockEntity())) {
					return;
				}

				lv.method_1631(blockEntity, d, e, f, g, i);
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
