package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public abstract class PathNodeMaker {
	protected BlockView blockView;
	protected IntObjectStorage<PathNode> pathNodeCache = new IntObjectStorage<>();
	protected int entityBlockXSize;
	protected int entityBlockYSize;
	protected int entityBlockZSize;

	public void init(BlockView blockView, Entity entity) {
		this.blockView = blockView;
		this.pathNodeCache.clear();
		this.entityBlockXSize = MathHelper.floor(entity.width + 1.0F);
		this.entityBlockYSize = MathHelper.floor(entity.height + 1.0F);
		this.entityBlockZSize = MathHelper.floor(entity.width + 1.0F);
	}

	public void clear() {
	}

	protected PathNode getNode(int x, int y, int z) {
		int i = PathNode.hash(x, y, z);
		PathNode pathNode = this.pathNodeCache.get(i);
		if (pathNode == null) {
			pathNode = new PathNode(x, y, z);
			this.pathNodeCache.set(i, pathNode);
		}

		return pathNode;
	}

	public abstract PathNode getStart(Entity entity);

	public abstract PathNode getNode(Entity entity, double x, double y, double z);

	public abstract int getSuccessors(PathNode[] nodes, Entity entity, PathNode currentNode, PathNode endNode, float maxDistance);
}
