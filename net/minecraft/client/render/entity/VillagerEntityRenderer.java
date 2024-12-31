package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerEntityModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

public class VillagerEntityRenderer extends MobEntityRenderer<VillagerEntity> {
	private static final Identifier UNEMPLOYED = new Identifier("textures/entity/villager/villager.png");
	private static final Identifier FARMER = new Identifier("textures/entity/villager/farmer.png");
	private static final Identifier LIBRARIAN = new Identifier("textures/entity/villager/librarian.png");
	private static final Identifier PRIEST = new Identifier("textures/entity/villager/priest.png");
	private static final Identifier BLACKSMITH = new Identifier("textures/entity/villager/smith.png");
	private static final Identifier BUTCHER = new Identifier("textures/entity/villager/butcher.png");

	public VillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VillagerEntityModel(0.0F), 0.5F);
		this.addFeature(new HeadFeatureRenderer(this.getModel().field_1557));
	}

	public VillagerEntityModel getModel() {
		return (VillagerEntityModel)super.getModel();
	}

	protected Identifier getTexture(VillagerEntity villagerEntity) {
		switch (villagerEntity.profession()) {
			case 0:
				return FARMER;
			case 1:
				return LIBRARIAN;
			case 2:
				return PRIEST;
			case 3:
				return BLACKSMITH;
			case 4:
				return BUTCHER;
			default:
				return UNEMPLOYED;
		}
	}

	protected void scale(VillagerEntity villagerEntity, float f) {
		float g = 0.9375F;
		if (villagerEntity.age() < 0) {
			g = (float)((double)g * 0.5);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		GlStateManager.scale(g, g, g);
	}
}
