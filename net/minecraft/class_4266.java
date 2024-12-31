package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.SkeletonHorseEntity;
import net.minecraft.entity.ZombieHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

public class class_4266 extends class_3099<HorseBaseEntity> {
	private static final Map<Class<?>, Identifier> field_20951 = Maps.newHashMap(
		ImmutableMap.of(
			ZombieHorseEntity.class,
			new Identifier("textures/entity/horse/horse_zombie.png"),
			SkeletonHorseEntity.class,
			new Identifier("textures/entity/horse/horse_skeleton.png")
		)
	);

	public class_4266(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4180(), 1.0F);
	}

	protected Identifier getTexture(AbstractHorseEntity abstractHorseEntity) {
		return (Identifier)field_20951.get(abstractHorseEntity.getClass());
	}
}
