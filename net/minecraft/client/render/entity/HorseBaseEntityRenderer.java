package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.HorseBaseEntityModel;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.entity.HorseType;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class HorseBaseEntityRenderer extends MobEntityRenderer<HorseBaseEntity> {
	private static final Map<String, Identifier> MODEL_IDENTIFIERS = Maps.newHashMap();

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, HorseBaseEntityModel horseBaseEntityModel, float f) {
		super(entityRenderDispatcher, horseBaseEntityModel, f);
	}

	protected void scale(HorseBaseEntity horseBaseEntity, float f) {
		float g = 1.0F;
		HorseType horseType = horseBaseEntity.method_13129();
		if (horseType == HorseType.DONKEY) {
			g *= 0.87F;
		} else if (horseType == HorseType.MULE) {
			g *= 0.92F;
		}

		GlStateManager.scale(g, g, g);
		super.scale(horseBaseEntity, f);
	}

	protected Identifier getTexture(HorseBaseEntity horseBaseEntity) {
		return !horseBaseEntity.method_6271() ? horseBaseEntity.method_13129().getTexturePath() : this.method_5756(horseBaseEntity);
	}

	@Nullable
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
