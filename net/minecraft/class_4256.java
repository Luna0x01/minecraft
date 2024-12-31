package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class class_4256 extends ZombieBaseEntityRenderer {
	private static final Identifier field_20920 = new Identifier("textures/entity/zombie/drowned.png");
	private float field_20921;

	public class_4256(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4185(0.0F, 0.0F, 64, 64));
		this.addFeature(new class_4269(this));
	}

	@Override
	protected ArmorRenderer method_19431() {
		return new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new class_4185(0.5F, true);
				this.firstLayer = new class_4185(1.0F, true);
			}
		};
	}

	@Nullable
	@Override
	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return field_20920;
	}

	@Override
	protected void method_5777(ZombieEntity zombieEntity, float f, float g, float h) {
		float i = zombieEntity.method_15642(h);
		super.method_5777(zombieEntity, f, g, h);
		if (i > 0.0F) {
			float j = this.method_19367(zombieEntity.pitch, -10.0F - zombieEntity.pitch, i);
			if (!zombieEntity.method_15584()) {
				j = this.method_5769(this.field_20921, 0.0F, 1.0F - i);
			}

			GlStateManager.rotate(j, 1.0F, 0.0F, 0.0F);
			if (zombieEntity.method_15584()) {
				this.field_20921 = j;
			}
		}
	}

	private float method_19367(float f, float g, float h) {
		return f + (g - f) * h;
	}
}
