package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class ZombieHorseEntityRenderer extends HorseBaseEntityRenderer<HorseBaseEntity, HorseEntityModel<HorseBaseEntity>> {
	private static final Map<Class<?>, Identifier> TEXTURES = Maps.newHashMap(
		ImmutableMap.of(
			ZombieHorseEntity.class,
			new Identifier("textures/entity/horse/horse_zombie.png"),
			SkeletonHorseEntity.class,
			new Identifier("textures/entity/horse/horse_skeleton.png")
		)
	);

	public ZombieHorseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new HorseEntityModel<>(0.0F), 1.0F);
	}

	protected Identifier method_4145(HorseBaseEntity horseBaseEntity) {
		return (Identifier)TEXTURES.get(horseBaseEntity.getClass());
	}
}
