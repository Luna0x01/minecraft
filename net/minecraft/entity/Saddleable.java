package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.sound.SoundCategory;

public interface Saddleable {
	boolean canBeSaddled();

	void saddle(@Nullable SoundCategory sound);

	boolean isSaddled();
}
