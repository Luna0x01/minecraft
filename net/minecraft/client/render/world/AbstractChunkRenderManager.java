package net.minecraft.client.render.world;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractChunkRenderManager {
	private double viewX;
	private double viewY;
	private double viewZ;
	protected List<BuiltChunk> helpers = Lists.newArrayListWithCapacity(17424);
	protected boolean field_10667;

	public void setViewPos(double viewX, double viewY, double viewZ) {
		this.field_10667 = true;
		this.helpers.clear();
		this.viewX = viewX;
		this.viewY = viewY;
		this.viewZ = viewZ;
	}

	public void method_9770(BuiltChunk helper) {
		BlockPos blockPos = helper.getPos();
		GlStateManager.translate(
			(float)((double)blockPos.getX() - this.viewX), (float)((double)blockPos.getY() - this.viewY), (float)((double)blockPos.getZ() - this.viewZ)
		);
	}

	public void method_9771(BuiltChunk helper, RenderLayer layer) {
		this.helpers.add(helper);
	}

	public abstract void render(RenderLayer layer);
}
