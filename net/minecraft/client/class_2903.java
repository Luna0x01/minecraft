package net.minecraft.client;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;

public class class_2903 implements BakedModel {
	private final List<Pair<Predicate<BlockState>, BakedModel>> field_21092;
	protected final boolean field_13667;
	protected final boolean field_13668;
	protected final Sprite field_13669;
	protected final ModelTransformation field_13670;
	protected final class_2876 field_13671;
	private final Map<BlockState, BitSet> field_21093 = new Object2ObjectOpenCustomHashMap(Util.method_20233());

	public class_2903(List<Pair<Predicate<BlockState>, BakedModel>> list) {
		this.field_21092 = list;
		BakedModel bakedModel = (BakedModel)((Pair)list.iterator().next()).getRight();
		this.field_13667 = bakedModel.useAmbientOcclusion();
		this.field_13668 = bakedModel.hasDepth();
		this.field_13669 = bakedModel.getParticleSprite();
		this.field_13670 = bakedModel.getTransformation();
		this.field_13671 = bakedModel.method_12503();
	}

	@Override
	public List<BakedQuad> method_19561(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		if (blockState == null) {
			return Collections.emptyList();
		} else {
			BitSet bitSet = (BitSet)this.field_21093.get(blockState);
			if (bitSet == null) {
				bitSet = new BitSet();

				for (int i = 0; i < this.field_21092.size(); i++) {
					Pair<Predicate<BlockState>, BakedModel> pair = (Pair<Predicate<BlockState>, BakedModel>)this.field_21092.get(i);
					if (((Predicate)pair.getLeft()).test(blockState)) {
						bitSet.set(i);
					}
				}

				this.field_21093.put(blockState, bitSet);
			}

			List<BakedQuad> list = Lists.newArrayList();
			long l = random.nextLong();

			for (int j = 0; j < bitSet.length(); j++) {
				if (bitSet.get(j)) {
					list.addAll(((BakedModel)((Pair)this.field_21092.get(j)).getRight()).method_19561(blockState, direction, new Random(l)));
				}
			}

			return list;
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.field_13667;
	}

	@Override
	public boolean hasDepth() {
		return this.field_13668;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return this.field_13669;
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.field_13670;
	}

	@Override
	public class_2876 method_12503() {
		return this.field_13671;
	}

	public static class class_2904 {
		private final List<Pair<Predicate<BlockState>, BakedModel>> field_21094 = Lists.newArrayList();

		public void method_19597(Predicate<BlockState> predicate, BakedModel bakedModel) {
			this.field_21094.add(Pair.of(predicate, bakedModel));
		}

		public BakedModel method_12517() {
			return new class_2903(this.field_21094);
		}
	}
}
