package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.PacketByteBuf;

public class class_4337 implements ParticleEffect {
	public static final ParticleEffect.class_4341<class_4337> field_21336 = new ParticleEffect.class_4341<class_4337>() {
		public class_4337 method_19981(ParticleType<class_4337> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new class_4337(particleType, new class_4238(stringReader, false).method_19300(false).method_19301());
		}

		public class_4337 method_19982(ParticleType<class_4337> particleType, PacketByteBuf packetByteBuf) {
			return new class_4337(particleType, Block.BLOCK_STATES.fromId(packetByteBuf.readVarInt()));
		}
	};
	private final ParticleType<class_4337> field_21337;
	private final BlockState field_21338;

	public class_4337(ParticleType<class_4337> particleType, BlockState blockState) {
		this.field_21337 = particleType;
		this.field_21338 = blockState;
	}

	@Override
	public void method_19979(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(Block.BLOCK_STATES.getId(this.field_21338));
	}

	@Override
	public String method_19978() {
		return this.particleType().method_19986() + " " + class_4238.method_19289(this.field_21338, null);
	}

	@Override
	public ParticleType<class_4337> particleType() {
		return this.field_21337;
	}

	public BlockState method_19966() {
		return this.field_21338;
	}
}
