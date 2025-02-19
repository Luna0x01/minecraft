package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlazeEntityRenderer extends MobEntityRenderer<BlazeEntity, BlazeEntityModel<BlazeEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/blaze.png");

	public BlazeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BlazeEntityModel<>(context.getPart(EntityModelLayers.BLAZE)), 0.5F);
	}

	protected int getBlockLight(BlazeEntity blazeEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(BlazeEntity blazeEntity) {
		return TEXTURE;
	}
}
