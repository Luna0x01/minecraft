package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ParrotEntityRenderer extends MobEntityRenderer<ParrotEntity> {
	public static final Identifier[] TEXTURES = new Identifier[]{
		new Identifier("textures/entity/parrot/parrot_red_blue.png"),
		new Identifier("textures/entity/parrot/parrot_blue.png"),
		new Identifier("textures/entity/parrot/parrot_green.png"),
		new Identifier("textures/entity/parrot/parrot_yellow_blue.png"),
		new Identifier("textures/entity/parrot/parrot_grey.png")
	};

	public ParrotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ParrotEntityModel(), 0.3F);
	}

	protected Identifier getTexture(ParrotEntity parrotEntity) {
		return TEXTURES[parrotEntity.method_14107()];
	}

	public float method_5783(ParrotEntity parrotEntity, float f) {
		return this.method_14695(parrotEntity, f);
	}

	private float method_14695(ParrotEntity parrotEntity, float f) {
		float g = parrotEntity.field_15560 + (parrotEntity.field_15557 - parrotEntity.field_15560) * f;
		float h = parrotEntity.field_15559 + (parrotEntity.field_15558 - parrotEntity.field_15559) * f;
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}
