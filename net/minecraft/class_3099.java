package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.HorseBaseEntityModel;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.DonkeyEntity;
import net.minecraft.entity.MuleEntity;
import net.minecraft.entity.SkeletonHorseEntity;
import net.minecraft.entity.ZombieHorseEntity;
import net.minecraft.util.Identifier;

public class class_3099 extends MobEntityRenderer<AbstractHorseEntity> {
	private static final Map<Class<?>, Identifier> TEXTURES = Maps.newHashMap();
	private final float field_15293;

	public class_3099(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, 1.0F);
	}

	public class_3099(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher, new HorseBaseEntityModel(), 0.75F);
		this.field_15293 = f;
	}

	protected void scale(AbstractHorseEntity abstractHorseEntity, float f) {
		GlStateManager.scale(this.field_15293, this.field_15293, this.field_15293);
		super.scale(abstractHorseEntity, f);
	}

	protected Identifier getTexture(AbstractHorseEntity abstractHorseEntity) {
		return (Identifier)TEXTURES.get(abstractHorseEntity.getClass());
	}

	static {
		TEXTURES.put(DonkeyEntity.class, new Identifier("textures/entity/horse/donkey.png"));
		TEXTURES.put(MuleEntity.class, new Identifier("textures/entity/horse/mule.png"));
		TEXTURES.put(ZombieHorseEntity.class, new Identifier("textures/entity/horse/horse_zombie.png"));
		TEXTURES.put(SkeletonHorseEntity.class, new Identifier("textures/entity/horse/horse_skeleton.png"));
	}
}
