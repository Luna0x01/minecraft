package net.minecraft.client.resource;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;

public class AnimationMetadata implements ResourceMetadataProvider {
	private final List<AnimationFrameResourceMetadata> metadataList;
	private final int width;
	private final int height;
	private final int time;
	private final boolean interpolate;

	public AnimationMetadata(List<AnimationFrameResourceMetadata> list, int i, int j, int k, boolean bl) {
		this.metadataList = list;
		this.width = i;
		this.height = j;
		this.time = k;
		this.interpolate = bl;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getMetadataListSize() {
		return this.metadataList.size();
	}

	public int getTime() {
		return this.time;
	}

	public boolean shouldInterpolate() {
		return this.interpolate;
	}

	private AnimationFrameResourceMetadata get(int i) {
		return (AnimationFrameResourceMetadata)this.metadataList.get(i);
	}

	public int getTime(int i) {
		AnimationFrameResourceMetadata animationFrameResourceMetadata = this.get(i);
		return animationFrameResourceMetadata.usesDefaultFrameTime() ? this.time : animationFrameResourceMetadata.getTime();
	}

	public boolean method_5964(int i) {
		return !((AnimationFrameResourceMetadata)this.metadataList.get(i)).usesDefaultFrameTime();
	}

	public int getIndex(int i) {
		return ((AnimationFrameResourceMetadata)this.metadataList.get(i)).getIndex();
	}

	public Set<Integer> getIndices() {
		Set<Integer> set = Sets.newHashSet();

		for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.metadataList) {
			set.add(animationFrameResourceMetadata.getIndex());
		}

		return set;
	}
}
