package net.minecraft.client;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class class_2903 implements BakedModel {
	private final Map<Predicate<BlockState>, BakedModel> field_13672;
	protected final boolean field_13667;
	protected final boolean field_13668;
	protected final Sprite field_13669;
	protected final ModelTransformation field_13670;
	protected final class_2876 field_13671;

	public class_2903(Map<Predicate<BlockState>, BakedModel> map) {
		this.field_13672 = map;
		BakedModel bakedModel = (BakedModel)map.values().iterator().next();
		this.field_13667 = bakedModel.useAmbientOcclusion();
		this.field_13668 = bakedModel.hasDepth();
		this.field_13669 = bakedModel.getParticleSprite();
		this.field_13670 = bakedModel.getTransformation();
		this.field_13671 = bakedModel.method_12503();
	}

	@Override
	public List<BakedQuad> method_12502(@Nullable BlockState blockState, @Nullable Direction direction, long l) {
		List<BakedQuad> list = Lists.newArrayList();
		if (blockState != null) {
			for (Entry<Predicate<BlockState>, BakedModel> entry : this.field_13672.entrySet()) {
				if (((Predicate)entry.getKey()).apply(blockState)) {
					list.addAll(((BakedModel)entry.getValue()).method_12502(blockState, direction, l++));
				}
			}
		}

		return list;
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
		private Map<Predicate<BlockState>, BakedModel> field_13673 = Maps.newLinkedHashMap();

		public void method_12518(Predicate<BlockState> predicate, BakedModel bakedModel) {
			this.field_13673.put(predicate, bakedModel);
		}

		public BakedModel method_12517() {
			return new class_2903(this.field_13673);
		}
	}
}
