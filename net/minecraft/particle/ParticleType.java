package net.minecraft.particle;

import com.mojang.serialization.Codec;

public abstract class ParticleType<T extends ParticleEffect> {
	private final boolean alwaysShow;
	private final ParticleEffect.Factory<T> parametersFactory;

	protected ParticleType(boolean alwaysShow, ParticleEffect.Factory<T> parametersFactory) {
		this.alwaysShow = alwaysShow;
		this.parametersFactory = parametersFactory;
	}

	public boolean shouldAlwaysSpawn() {
		return this.alwaysShow;
	}

	public ParticleEffect.Factory<T> getParametersFactory() {
		return this.parametersFactory;
	}

	public abstract Codec<T> getCodec();
}
