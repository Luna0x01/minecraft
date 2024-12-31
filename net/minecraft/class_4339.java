package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.PacketByteBuf;

public class class_4339 implements ParticleEffect {
	public static final ParticleEffect.class_4341<class_4339> field_21345 = new ParticleEffect.class_4341<class_4339>() {
		public class_4339 method_19981(ParticleType<class_4339> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			class_4312 lv = new class_4312(stringReader, false).method_19717();
			ItemStack itemStack = new class_4311(lv.method_19708(), lv.method_19710()).method_19702(1, false);
			return new class_4339(particleType, itemStack);
		}

		public class_4339 method_19982(ParticleType<class_4339> particleType, PacketByteBuf packetByteBuf) {
			return new class_4339(particleType, packetByteBuf.readItemStack());
		}
	};
	private final ParticleType<class_4339> field_21346;
	private final ItemStack field_21347;

	public class_4339(ParticleType<class_4339> particleType, ItemStack itemStack) {
		this.field_21346 = particleType;
		this.field_21347 = itemStack;
	}

	@Override
	public void method_19979(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeItemStack(this.field_21347);
	}

	@Override
	public String method_19978() {
		return this.particleType().method_19986() + " " + new class_4311(this.field_21347.getItem(), this.field_21347.getNbt()).method_19705();
	}

	@Override
	public ParticleType<class_4339> particleType() {
		return this.field_21346;
	}

	public ItemStack method_19975() {
		return this.field_21347;
	}
}
