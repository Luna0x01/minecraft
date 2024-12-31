package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class RabbitEntityRenderer extends MobEntityRenderer<RabbitEntity> {
	private static final Identifier BROWN_RABBIT = new Identifier("textures/entity/rabbit/brown.png");
	private static final Identifier WHITE_RABBIT = new Identifier("textures/entity/rabbit/white.png");
	private static final Identifier BLACK_RABBIT = new Identifier("textures/entity/rabbit/black.png");
	private static final Identifier GOLD_RABBIT = new Identifier("textures/entity/rabbit/gold.png");
	private static final Identifier SALT_RABBIT = new Identifier("textures/entity/rabbit/salt.png");
	private static final Identifier SPLOTCHED_WHITE_RABBIT = new Identifier("textures/entity/rabbit/white_splotched.png");
	private static final Identifier TOAST_RABBIT = new Identifier("textures/entity/rabbit/toast.png");
	private static final Identifier CAERBANNOG_RABBIT = new Identifier("textures/entity/rabbit/caerbannog.png");

	public RabbitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected Identifier getTexture(RabbitEntity rabbitEntity) {
		String string = Formatting.strip(rabbitEntity.getTranslationKey());
		if (string != null && "Toast".equals(string)) {
			return TOAST_RABBIT;
		} else {
			switch (rabbitEntity.getRabbitType()) {
				case 0:
				default:
					return BROWN_RABBIT;
				case 1:
					return WHITE_RABBIT;
				case 2:
					return BLACK_RABBIT;
				case 3:
					return SPLOTCHED_WHITE_RABBIT;
				case 4:
					return GOLD_RABBIT;
				case 5:
					return SALT_RABBIT;
				case 99:
					return CAERBANNOG_RABBIT;
			}
		}
	}
}
