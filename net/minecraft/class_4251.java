package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.DonkeyEntity;
import net.minecraft.entity.MuleEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class class_4251 extends class_3099<HorseBaseEntity> {
	private static final Map<Class<?>, Identifier> field_20910 = Maps.newHashMap(
		ImmutableMap.of(DonkeyEntity.class, new Identifier("textures/entity/horse/donkey.png"), MuleEntity.class, new Identifier("textures/entity/horse/mule.png"))
	);

	public class_4251(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher, new class_4182(), f);
	}

	protected Identifier getTexture(AbstractHorseEntity abstractHorseEntity) {
		return (Identifier)field_20910.get(abstractHorseEntity.getClass());
	}
}
