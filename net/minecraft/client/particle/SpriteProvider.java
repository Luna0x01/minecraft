package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.texture.Sprite;

public interface SpriteProvider {
	Sprite getSprite(int i, int j);

	Sprite getSprite(Random random);
}
