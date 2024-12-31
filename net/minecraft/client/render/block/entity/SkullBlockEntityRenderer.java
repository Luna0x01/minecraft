package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SkullBlockEntityRenderer extends BlockEntityRenderer<SkullBlockEntity> {
	private static final Identifier SKELETON_TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");
	private static final Identifier WITHER_SKELETON_TEXTURE = new Identifier("textures/entity/skeleton/wither_skeleton.png");
	private static final Identifier ZOMBIE_TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
	private static final Identifier CREEPER_TEXTURE = new Identifier("textures/entity/creeper/creeper.png");
	public static SkullBlockEntityRenderer instance;
	private final SkullEntityModel model = new SkullEntityModel(0, 0, 64, 32);
	private final SkullEntityModel overlayModel = new SkullOverlayEntityModel();

	public void render(SkullBlockEntity skullBlockEntity, double d, double e, double f, float g, int i) {
		Direction direction = Direction.getById(skullBlockEntity.getDataValue() & 7);
		this.render(
			(float)d,
			(float)e,
			(float)f,
			direction,
			(float)(skullBlockEntity.getRotation() * 360) / 16.0F,
			skullBlockEntity.getSkullType(),
			skullBlockEntity.getOwner(),
			i
		);
	}

	@Override
	public void setDispatcher(BlockEntityRenderDispatcher dispatcher) {
		super.setDispatcher(dispatcher);
		instance = this;
	}

	public void render(float x, float y, float z, Direction direction, float yaw, int type, GameProfile profile, int i) {
		EntityModel entityModel = this.model;
		if (i >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			switch (type) {
				case 0:
				default:
					this.bindTexture(SKELETON_TEXTURE);
					break;
				case 1:
					this.bindTexture(WITHER_SKELETON_TEXTURE);
					break;
				case 2:
					this.bindTexture(ZOMBIE_TEXTURE);
					entityModel = this.overlayModel;
					break;
				case 3:
					entityModel = this.overlayModel;
					Identifier identifier = DefaultSkinHelper.getTexture();
					if (profile != null) {
						MinecraftClient minecraftClient = MinecraftClient.getInstance();
						Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(profile);
						if (map.containsKey(Type.SKIN)) {
							identifier = minecraftClient.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
						} else {
							UUID uUID = PlayerEntity.getUuidFromProfile(profile);
							identifier = DefaultSkinHelper.getTexture(uUID);
						}
					}

					this.bindTexture(identifier);
					break;
				case 4:
					this.bindTexture(CREEPER_TEXTURE);
			}
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		if (direction != Direction.UP) {
			switch (direction) {
				case NORTH:
					GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
					break;
				case SOUTH:
					GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
					yaw = 180.0F;
					break;
				case WEST:
					GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
					yaw = 270.0F;
					break;
				case EAST:
				default:
					GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
					yaw = 90.0F;
			}
		} else {
			GlStateManager.translate(x + 0.5F, y, z + 0.5F);
		}

		float f = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		entityModel.render(null, 0.0F, 0.0F, 0.0F, yaw, 0.0F, f);
		GlStateManager.popMatrix();
		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}
