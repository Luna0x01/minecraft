package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

public class DonkeyEntityRenderer<T extends AbstractDonkeyEntity> extends HorseBaseEntityRenderer<T, DonkeyEntityModel<T>> {
	private static final Map<EntityType<?>, Identifier> TEXTURES = Maps.newHashMap(
		ImmutableMap.of(EntityType.DONKEY, new Identifier("textures/entity/horse/donkey.png"), EntityType.MULE, new Identifier("textures/entity/horse/mule.png"))
	);

	public DonkeyEntityRenderer(EntityRendererFactory.Context ctx, float scale, EntityModelLayer layer) {
		super(ctx, new DonkeyEntityModel<>(ctx.getPart(layer)), scale);
	}

	public Identifier getTexture(T abstractDonkeyEntity) {
		return (Identifier)TEXTURES.get(abstractDonkeyEntity.getType());
	}
}
