package net.minecraft.entity.boss;

import net.minecraft.text.Text;

public interface BossBarProvider {
	float getMaxHealth();

	float getHealth();

	Text getName();
}
