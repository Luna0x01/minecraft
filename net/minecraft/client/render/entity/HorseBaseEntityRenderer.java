package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.HorseBaseEntityModel;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class HorseBaseEntityRenderer extends MobEntityRenderer<HorseBaseEntity> {
	private static final Map<String, Identifier> MODEL_IDENTIFIERS = Maps.newHashMap();
	private static final Identifier WHITE_HORSE = new Identifier("textures/entity/horse/horse_white.png");
	private static final Identifier MULE = new Identifier("textures/entity/horse/mule.png");
	private static final Identifier DONKEY = new Identifier("textures/entity/horse/donkey.png");
	private static final Identifier ZOMBIE_HORSE = new Identifier("textures/entity/horse/horse_zombie.png");
	private static final Identifier SKELETON_HORSE = new Identifier("textures/entity/horse/horse_skeleton.png");

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, HorseBaseEntityModel horseBaseEntityModel, float f) {
		super(entityRenderDispatcher, horseBaseEntityModel, f);
	}

	protected void scale(HorseBaseEntity horseBaseEntity, float f) {
		float g = 1.0F;
		int i = horseBaseEntity.getType();
		if (i == 1) {
			g *= 0.87F;
		} else if (i == 2) {
			g *= 0.92F;
		}

		GlStateManager.scale(g, g, g);
		super.scale(horseBaseEntity, f);
	}

	protected Identifier getTexture(HorseBaseEntity horseBaseEntity) {
		if (!horseBaseEntity.method_6271()) {
			switch (horseBaseEntity.getType()) {
				case 0:
				default:
					return WHITE_HORSE;
				case 1:
					return DONKEY;
				case 2:
					return MULE;
				case 3:
					return ZOMBIE_HORSE;
				case 4:
					return SKELETON_HORSE;
			}
		} else {
			return this.method_5756(horseBaseEntity);
		}
	}

	private Identifier method_5756(HorseBaseEntity horse) {
		String string = horse.method_6272();
		if (!horse.method_11071()) {
			return null;
		} else {
			Identifier identifier = (Identifier)MODEL_IDENTIFIERS.get(string);
			if (identifier == null) {
				identifier = new Identifier(string);
				MinecraftClient.getInstance().getTextureManager().loadTexture(identifier, new LayeredTexture(horse.method_6273()));
				MODEL_IDENTIFIERS.put(string, identifier);
			}

			return identifier;
		}
	}
}
