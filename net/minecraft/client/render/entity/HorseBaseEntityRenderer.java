package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.HorseBaseEntityModel;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class HorseBaseEntityRenderer extends MobEntityRenderer<HorseBaseEntity> {
	private static final Map<String, Identifier> MODEL_IDENTIFIERS = Maps.newHashMap();

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new HorseBaseEntityModel(), 0.75F);
	}

	protected Identifier getTexture(HorseBaseEntity horseBaseEntity) {
		String string = horseBaseEntity.method_6272();
		Identifier identifier = (Identifier)MODEL_IDENTIFIERS.get(string);
		if (identifier == null) {
			identifier = new Identifier(string);
			MinecraftClient.getInstance().getTextureManager().loadTexture(identifier, new LayeredTexture(horseBaseEntity.method_6273()));
			MODEL_IDENTIFIERS.put(string, identifier);
		}

		return identifier;
	}
}
