package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillerBlockFeature;

public class EndBiomeDecorator extends BiomeDecorator {
	protected Feature endStoneFillerFeature = new FillerBlockFeature(Blocks.END_STONE);

	@Override
	protected void generate(Biome biome) {
		this.generateOres();
		if (this.random.nextInt(5) == 0) {
			int i = this.random.nextInt(16) + 8;
			int j = this.random.nextInt(16) + 8;
			this.endStoneFillerFeature.generate(this.world, this.random, this.world.getTopPosition(this.startPos.add(i, 0, j)));
		}

		if (this.startPos.getX() == 0 && this.startPos.getZ() == 0) {
			EnderDragonEntity enderDragonEntity = new EnderDragonEntity(this.world);
			enderDragonEntity.refreshPositionAndAngles(0.0, 128.0, 0.0, this.random.nextFloat() * 360.0F, 0.0F);
			this.world.spawnEntity(enderDragonEntity);
		}
	}
}
