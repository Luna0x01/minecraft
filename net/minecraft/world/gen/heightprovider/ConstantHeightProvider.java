package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public class ConstantHeightProvider extends HeightProvider {
	public static final ConstantHeightProvider ZERO = new ConstantHeightProvider(YOffset.fixed(0));
	public static final Codec<ConstantHeightProvider> CONSTANT_CODEC = Codec.either(
			YOffset.OFFSET_CODEC,
			RecordCodecBuilder.create(
				instance -> instance.group(YOffset.OFFSET_CODEC.fieldOf("value").forGetter(constantHeightProvider -> constantHeightProvider.offset))
						.apply(instance, ConstantHeightProvider::new)
			)
		)
		.xmap(
			either -> (ConstantHeightProvider)either.map(ConstantHeightProvider::create, constantHeightProvider -> constantHeightProvider),
			constantHeightProvider -> Either.left(constantHeightProvider.offset)
		);
	private final YOffset offset;

	public static ConstantHeightProvider create(YOffset offset) {
		return new ConstantHeightProvider(offset);
	}

	private ConstantHeightProvider(YOffset offset) {
		this.offset = offset;
	}

	public YOffset getOffset() {
		return this.offset;
	}

	@Override
	public int get(Random random, HeightContext context) {
		return this.offset.getY(context);
	}

	@Override
	public HeightProviderType<?> getType() {
		return HeightProviderType.CONSTANT;
	}

	public String toString() {
		return this.offset.toString();
	}
}
