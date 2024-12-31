package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.class_3099;
import net.minecraft.class_4180;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class HorseBaseEntityRenderer extends class_3099<HorseBaseEntity> {
	private static final Map<String, Identifier> MODEL_IDENTIFIERS = Maps.newHashMap();

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4180(), 1.1F);
	}

	protected Identifier getTexture(AbstractHorseEntity abstractHorseEntity) {
		HorseBaseEntity horseBaseEntity = (HorseBaseEntity)abstractHorseEntity;
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
