package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SkullBlockModel;
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
	private static final Identifier DRAGON_TEXTURE = new Identifier("textures/entity/enderdragon/dragon.png");
	private final SkullBlockModel blockModel = new SkullBlockModel(0.0F);
	public static SkullBlockEntityRenderer instance;
	private final SkullEntityModel model = new SkullEntityModel(0, 0, 64, 32);
	private final SkullEntityModel overlayModel = new SkullOverlayEntityModel();

	public void render(SkullBlockEntity skullBlockEntity, double d, double e, double f, float g, int i, float h) {
		Direction direction = Direction.getById(skullBlockEntity.getDataValue() & 7);
		float j = skullBlockEntity.method_11664(g);
		this.method_10108(
			(float)d,
			(float)e,
			(float)f,
			direction,
			(float)(skullBlockEntity.getRotation() * 360) / 16.0F,
			skullBlockEntity.getSkullType(),
			skullBlockEntity.getOwner(),
			i,
			j
		);
	}

	@Override
	public void setDispatcher(BlockEntityRenderDispatcher dispatcher) {
		super.setDispatcher(dispatcher);
		instance = this;
	}

	public void method_10108(float f, float g, float h, Direction direction, float i, int j, @Nullable GameProfile gameProfile, int k, float l) {
		EntityModel entityModel = this.model;
		if (k >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURE[k]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			switch (j) {
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
					if (gameProfile != null) {
						MinecraftClient minecraftClient = MinecraftClient.getInstance();
						Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
						if (map.containsKey(Type.SKIN)) {
							identifier = minecraftClient.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
						} else {
							UUID uUID = PlayerEntity.getUuidFromProfile(gameProfile);
							identifier = DefaultSkinHelper.getTexture(uUID);
						}
					}

					this.bindTexture(identifier);
					break;
				case 4:
					this.bindTexture(CREEPER_TEXTURE);
					break;
				case 5:
					this.bindTexture(DRAGON_TEXTURE);
					entityModel = this.blockModel;
			}
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		if (direction == Direction.UP) {
			GlStateManager.translate(f + 0.5F, g, h + 0.5F);
		} else {
			switch (direction) {
				case NORTH:
					GlStateManager.translate(f + 0.5F, g + 0.25F, h + 0.74F);
					break;
				case SOUTH:
					GlStateManager.translate(f + 0.5F, g + 0.25F, h + 0.26F);
					i = 180.0F;
					break;
				case WEST:
					GlStateManager.translate(f + 0.74F, g + 0.25F, h + 0.5F);
					i = 270.0F;
					break;
				case EAST:
				default:
					GlStateManager.translate(f + 0.26F, g + 0.25F, h + 0.5F);
					i = 90.0F;
			}
		}

		float m = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		if (j == 3) {
			GlStateManager.method_12286(GlStateManager.class_2869.PLAYER_SKIN);
		}

		entityModel.render(null, l, 0.0F, 0.0F, i, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		if (k >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}
