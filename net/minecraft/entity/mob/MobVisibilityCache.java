package net.minecraft.entity.mob;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;

public class MobVisibilityCache {
	MobEntity owner;
	List<Entity> visibleEntities = Lists.newArrayList();
	List<Entity> invisibleEntities = Lists.newArrayList();

	public MobVisibilityCache(MobEntity mobEntity) {
		this.owner = mobEntity;
	}

	public void clear() {
		this.visibleEntities.clear();
		this.invisibleEntities.clear();
	}

	public boolean canSee(Entity entity) {
		if (this.visibleEntities.contains(entity)) {
			return true;
		} else if (this.invisibleEntities.contains(entity)) {
			return false;
		} else {
			this.owner.world.profiler.push("canSee");
			boolean bl = this.owner.canSee(entity);
			this.owner.world.profiler.pop();
			if (bl) {
				this.visibleEntities.add(entity);
			} else {
				this.invisibleEntities.add(entity);
			}

			return bl;
		}
	}
}
