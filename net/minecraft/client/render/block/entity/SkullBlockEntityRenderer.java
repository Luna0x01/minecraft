package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3685;
import net.minecraft.class_3734;
import net.minecraft.class_4239;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SkullBlockModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public class SkullBlockEntityRenderer extends class_4239<SkullBlockEntity> {
	public static SkullBlockEntityRenderer instance;
	private static final Map<SkullBlock.class_3722, EntityModel> field_20865 = Util.make(Maps.newHashMap(), hashMap -> {
		SkullEntityModel skullEntityModel = new SkullEntityModel(0, 0, 64, 32);
		SkullEntityModel skullEntityModel2 = new SkullOverlayEntityModel();
		SkullBlockModel skullBlockModel = new SkullBlockModel(0.0F);
		hashMap.put(SkullBlock.class_3723.SKELETON, skullEntityModel);
		hashMap.put(SkullBlock.class_3723.WITHER_SKELETON, skullEntityModel);
		hashMap.put(SkullBlock.class_3723.PLAYER, skullEntityModel2);
		hashMap.put(SkullBlock.class_3723.ZOMBIE, skullEntityModel2);
		hashMap.put(SkullBlock.class_3723.CREEPER, skullEntityModel);
		hashMap.put(SkullBlock.class_3723.DRAGON, skullBlockModel);
	});
	private static final Map<SkullBlock.class_3722, Identifier> field_20866 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(SkullBlock.class_3723.SKELETON, new Identifier("textures/entity/skeleton/skeleton.png"));
		hashMap.put(SkullBlock.class_3723.WITHER_SKELETON, new Identifier("textures/entity/skeleton/wither_skeleton.png"));
		hashMap.put(SkullBlock.class_3723.ZOMBIE, new Identifier("textures/entity/zombie/zombie.png"));
		hashMap.put(SkullBlock.class_3723.CREEPER, new Identifier("textures/entity/creeper/creeper.png"));
		hashMap.put(SkullBlock.class_3723.DRAGON, new Identifier("textures/entity/enderdragon/dragon.png"));
		hashMap.put(SkullBlock.class_3723.PLAYER, DefaultSkinHelper.getTexture());
	});

	public void method_1631(SkullBlockEntity skullBlockEntity, double d, double e, double f, float g, int i) {
		float h = skullBlockEntity.method_11664(g);
		BlockState blockState = skullBlockEntity.method_16783();
		boolean bl = blockState.getBlock() instanceof class_3734;
		Direction direction = bl ? blockState.getProperty(class_3734.field_18580) : null;
		float j = 22.5F * (float)(bl ? (2 + direction.getHorizontal()) * 4 : (Integer)blockState.getProperty(SkullBlock.field_18477));
		this.method_10108((float)d, (float)e, (float)f, direction, j, ((class_3685)blockState.getBlock()).method_16548(), skullBlockEntity.getOwner(), i, h);
	}

	@Override
	public void method_1632(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super.method_1632(blockEntityRenderDispatcher);
		instance = this;
	}

	public void method_10108(
		float f, float g, float h, @Nullable Direction direction, float i, SkullBlock.class_3722 arg, @Nullable GameProfile gameProfile, int j, float k
	) {
		EntityModel entityModel = (EntityModel)field_20865.get(arg);
		if (j >= 0) {
			this.method_19327(field_20846[j]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.method_19327(this.method_19335(arg, gameProfile));
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		if (direction == null) {
			GlStateManager.translate(f + 0.5F, g, h + 0.5F);
		} else {
			switch (direction) {
				case NORTH:
					GlStateManager.translate(f + 0.5F, g + 0.25F, h + 0.74F);
					break;
				case SOUTH:
					GlStateManager.translate(f + 0.5F, g + 0.25F, h + 0.26F);
					break;
				case WEST:
					GlStateManager.translate(f + 0.74F, g + 0.25F, h + 0.5F);
					break;
				case EAST:
				default:
					GlStateManager.translate(f + 0.26F, g + 0.25F, h + 0.5F);
			}
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		if (arg == SkullBlock.class_3723.PLAYER) {
			GlStateManager.method_12286(GlStateManager.class_2869.PLAYER_SKIN);
		}

		entityModel.render(null, k, 0.0F, 0.0F, i, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		if (j >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private Identifier method_19335(SkullBlock.class_3722 arg, @Nullable GameProfile gameProfile) {
		Identifier identifier = (Identifier)field_20866.get(arg);
		if (arg == SkullBlock.class_3723.PLAYER && gameProfile != null) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
			if (map.containsKey(Type.SKIN)) {
				identifier = minecraftClient.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
			} else {
				identifier = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
			}
		}

		return identifier;
	}
}
