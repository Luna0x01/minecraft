package net.minecraft.client.texture;

import java.util.stream.Stream;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StatusEffectSpriteManager extends SpriteAtlasHolder {
	public StatusEffectSpriteManager(TextureManager textureManager) {
		super(textureManager, new Identifier("textures/atlas/mob_effects.png"), "mob_effect");
	}

	@Override
	protected Stream<Identifier> getSprites() {
		return Registry.STATUS_EFFECT.getIds().stream();
	}

	public Sprite getSprite(StatusEffect effect) {
		return this.getSprite(Registry.STATUS_EFFECT.getId(effect));
	}
}
