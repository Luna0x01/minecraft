package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.feature.ZombieVillagerArmorRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class ZombieBaseEntityRenderer extends BipedEntityRenderer<ZombieEntity> {
	private static final Identifier ZOMBIE = new Identifier("textures/entity/zombie/zombie.png");
	private static final Identifier ZOMBIE_VILLAGER = new Identifier("textures/entity/zombie/zombie_villager.png");
	private final BiPedModel field_5219;
	private final ZombieVillagerEntityModel field_5220;
	private final List<FeatureRenderer<ZombieEntity>> field_11139;
	private final List<FeatureRenderer<ZombieEntity>> field_11140;

	public ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new AbstractZombieModel(), 0.5F, 1.0F);
		FeatureRenderer featureRenderer = (FeatureRenderer)this.features.get(0);
		this.field_5219 = this.bipedModel;
		this.field_5220 = new ZombieVillagerEntityModel();
		this.addFeature(new HeldItemRenderer(this));
		ArmorRenderer armorRenderer = new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new AbstractZombieModel(0.5F, true);
				this.firstLayer = new AbstractZombieModel(1.0F, true);
			}
		};
		this.addFeature(armorRenderer);
		this.field_11140 = Lists.newArrayList(this.features);
		if (featureRenderer instanceof HeadFeatureRenderer) {
			this.removeFeature(featureRenderer);
			this.addFeature(new HeadFeatureRenderer(this.field_5220.head));
		}

		this.removeFeature(armorRenderer);
		this.addFeature(new ZombieVillagerArmorRenderer(this));
		this.field_11139 = Lists.newArrayList(this.features);
	}

	public void render(ZombieEntity zombieEntity, double d, double e, double f, float g, float h) {
		this.method_4356(zombieEntity);
		super.render(zombieEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return zombieEntity.isVillager() ? ZOMBIE_VILLAGER : ZOMBIE;
	}

	private void method_4356(ZombieEntity zombieEntity) {
		if (zombieEntity.isVillager()) {
			this.model = this.field_5220;
			this.features = this.field_11139;
		} else {
			this.model = this.field_5219;
			this.features = this.field_11140;
		}

		this.bipedModel = (BiPedModel)this.model;
	}

	protected void method_5777(ZombieEntity zombieEntity, float f, float g, float h) {
		if (zombieEntity.getConversionType()) {
			g += (float)(Math.cos((double)zombieEntity.ticksAlive * 3.25) * Math.PI * 0.25);
		}

		super.method_5777(zombieEntity, f, g, h);
	}
}
