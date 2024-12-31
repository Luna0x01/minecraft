package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class BlockUpdateDebugRenderer implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;
	private final Map<Long, Map<BlockPos, Integer>> blockUpdates = Maps.newTreeMap(Ordering.natural().reverse());

	BlockUpdateDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void addBlockUpdate(long time, BlockPos position) {
		Map<BlockPos, Integer> map = (Map<BlockPos, Integer>)this.blockUpdates.get(time);
		if (map == null) {
			map = Maps.newHashMap();
			this.blockUpdates.put(time, map);
		}

		Integer integer = (Integer)map.get(position);
		if (integer == null) {
			integer = 0;
		}

		map.put(position, integer + 1);
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		long l = this.client.world.getLastUpdateTime();
		PlayerEntity playerEntity = this.client.player;
		double d = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double e = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double f = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		World world = this.client.player.world;
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.method_12304(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		int i = 200;
		double g = 0.0025;
		Set<BlockPos> set = Sets.newHashSet();
		Map<BlockPos, Integer> map = Maps.newHashMap();
		Iterator<Entry<Long, Map<BlockPos, Integer>>> iterator = this.blockUpdates.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Long, Map<BlockPos, Integer>> entry = (Entry<Long, Map<BlockPos, Integer>>)iterator.next();
			Long long_ = (Long)entry.getKey();
			Map<BlockPos, Integer> map2 = (Map<BlockPos, Integer>)entry.getValue();
			long m = l - long_;
			if (m > 200L) {
				iterator.remove();
			} else {
				for (Entry<BlockPos, Integer> entry2 : map2.entrySet()) {
					BlockPos blockPos = (BlockPos)entry2.getKey();
					Integer integer = (Integer)entry2.getValue();
					if (set.add(blockPos)) {
						WorldRenderer.drawBox(
							new Box(BlockPos.ORIGIN)
								.expand(0.002)
								.contract(0.0025 * (double)m)
								.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ())
								.offset(-d, -e, -f),
							1.0F,
							1.0F,
							1.0F,
							1.0F
						);
						map.put(blockPos, integer);
					}
				}
			}
		}

		for (Entry<BlockPos, Integer> entry3 : map.entrySet()) {
			BlockPos blockPos2 = (BlockPos)entry3.getKey();
			Integer integer2 = (Integer)entry3.getValue();
			DebugRenderer.method_13856(String.valueOf(integer2), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), tickDelta, -1);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
