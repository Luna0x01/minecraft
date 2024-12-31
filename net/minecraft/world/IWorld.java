package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3601;
import net.minecraft.class_3602;
import net.minecraft.class_3604;
import net.minecraft.class_3781;
import net.minecraft.block.Block;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.Sound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public interface IWorld extends RenderBlockView, class_3601, class_3602 {
	long method_3581();

	default float method_16344() {
		return Dimension.field_18952[this.method_16393().getMoonPhase(this.method_3588().getTimeOfDay())];
	}

	default float method_16349(float f) {
		return this.method_16393().getSkyAngle(this.method_3588().getTimeOfDay(), f);
	}

	default int method_16345() {
		return this.method_16393().getMoonPhase(this.method_3588().getTimeOfDay());
	}

	class_3604<Block> getBlockTickScheduler();

	class_3604<Fluid> method_16340();

	default class_3781 method_16351(BlockPos blockPos) {
		return this.method_16347(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	class_3781 method_16347(int i, int j);

	World method_16348();

	LevelProperties method_3588();

	LocalDifficulty method_8482(BlockPos blockPos);

	default Difficulty method_16346() {
		return this.method_3588().getDifficulty();
	}

	ChunkProvider method_3586();

	SaveHandler method_3587();

	Random getRandom();

	void method_16342(BlockPos blockPos, Block block);

	BlockPos method_3585();

	void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Sound sound, SoundCategory soundCategory, float f, float g);

	void method_16343(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i);
}
