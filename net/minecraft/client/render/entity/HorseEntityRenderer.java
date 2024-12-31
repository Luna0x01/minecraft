package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;

public final class HorseEntityRenderer extends HorseBaseEntityRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
	private static final Map<String, Identifier> SKINS = Maps.newHashMap();

	public HorseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new HorseEntityModel<>(0.0F), 1.1F);
		this.addFeature(new HorseArmorFeatureRenderer(this));
	}

	protected Identifier method_3983(HorseEntity horseEntity) {
		String string = horseEntity.getTextureLocation();
		Identifier identifier = (Identifier)SKINS.get(string);
		if (identifier == null) {
			identifier = new Identifier(string);
			MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new LayeredTexture(horseEntity.getTextureLayers()));
			SKINS.put(string, identifier);
		}

		return identifier;
	}
}
