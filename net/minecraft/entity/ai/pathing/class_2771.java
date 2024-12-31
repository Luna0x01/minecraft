package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public abstract class class_2771 {
	protected BlockView field_13075;
	protected MobEntity field_13076;
	protected final IntObjectStorage<PathNode> field_13077 = new IntObjectStorage<>();
	protected int field_13078;
	protected int field_13079;
	protected int field_13080;
	protected boolean field_13081;
	protected boolean field_13082;
	protected boolean field_13083;

	public void method_11915(BlockView blockView, MobEntity mobEntity) {
		this.field_13075 = blockView;
		this.field_13076 = mobEntity;
		this.field_13077.clear();
		this.field_13078 = MathHelper.floor(mobEntity.width + 1.0F);
		this.field_13079 = MathHelper.floor(mobEntity.height + 1.0F);
		this.field_13080 = MathHelper.floor(mobEntity.width + 1.0F);
	}

	public void method_11910() {
		this.field_13075 = null;
		this.field_13076 = null;
	}

	protected PathNode method_11912(int i, int j, int k) {
		int l = PathNode.hash(i, j, k);
		PathNode pathNode = this.field_13077.get(l);
		if (pathNode == null) {
			pathNode = new PathNode(i, j, k);
			this.field_13077.set(l, pathNode);
		}

		return pathNode;
	}

	public abstract PathNode method_11918();

	public abstract PathNode method_11911(double d, double e, double f);

	public abstract int method_11917(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f);

	public abstract LandType method_11914(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2);

	public abstract LandType method_11913(BlockView blockView, int i, int j, int k);

	public void method_11916(boolean bl) {
		this.field_13081 = bl;
	}

	public void method_11919(boolean bl) {
		this.field_13082 = bl;
	}

	public void method_11921(boolean bl) {
		this.field_13083 = bl;
	}

	public boolean method_11920() {
		return this.field_13081;
	}

	public boolean method_11922() {
		return this.field_13082;
	}

	public boolean method_11923() {
		return this.field_13083;
	}
}
