package net.minecraft;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class class_3289 implements CommandSource {
	private final CommandSource field_16084;
	@Nullable
	private final Vec3d field_16085;
	@Nullable
	private final BlockPos field_16086;
	@Nullable
	private final Integer field_16087;
	@Nullable
	private final Entity field_16088;
	@Nullable
	private final Boolean field_16089;

	public class_3289(
		CommandSource commandSource,
		@Nullable Vec3d vec3d,
		@Nullable BlockPos blockPos,
		@Nullable Integer integer,
		@Nullable Entity entity,
		@Nullable Boolean boolean_
	) {
		this.field_16084 = commandSource;
		this.field_16085 = vec3d;
		this.field_16086 = blockPos;
		this.field_16087 = integer;
		this.field_16088 = entity;
		this.field_16089 = boolean_;
	}

	public static class_3289 method_14640(CommandSource commandSource) {
		return commandSource instanceof class_3289 ? (class_3289)commandSource : new class_3289(commandSource, null, null, null, null, null);
	}

	public class_3289 method_14641(Entity entity, Vec3d vec3d) {
		return this.field_16088 == entity && Objects.equals(this.field_16085, vec3d)
			? this
			: new class_3289(this.field_16084, vec3d, new BlockPos(vec3d), this.field_16087, entity, this.field_16089);
	}

	public class_3289 method_14639(int i) {
		return this.field_16087 != null && this.field_16087 <= i
			? this
			: new class_3289(this.field_16084, this.field_16085, this.field_16086, i, this.field_16088, this.field_16089);
	}

	public class_3289 method_14642(boolean bl) {
		return this.field_16089 == null || this.field_16089 && !bl
			? new class_3289(this.field_16084, this.field_16085, this.field_16086, this.field_16087, this.field_16088, bl)
			: this;
	}

	public class_3289 method_14643() {
		return this.field_16085 != null
			? this
			: new class_3289(this.field_16084, this.getPos(), this.getBlockPos(), this.field_16087, this.field_16088, this.field_16089);
	}

	@Override
	public String getTranslationKey() {
		return this.field_16088 != null ? this.field_16088.getTranslationKey() : this.field_16084.getTranslationKey();
	}

	@Override
	public Text getName() {
		return this.field_16088 != null ? this.field_16088.getName() : this.field_16084.getName();
	}

	@Override
	public void sendMessage(Text text) {
		if (this.field_16089 == null || this.field_16089) {
			this.field_16084.sendMessage(text);
		}
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return this.field_16087 != null && this.field_16087 < permissionLevel ? false : this.field_16084.canUseCommand(permissionLevel, commandLiteral);
	}

	@Override
	public BlockPos getBlockPos() {
		if (this.field_16086 != null) {
			return this.field_16086;
		} else {
			return this.field_16088 != null ? this.field_16088.getBlockPos() : this.field_16084.getBlockPos();
		}
	}

	@Override
	public Vec3d getPos() {
		if (this.field_16085 != null) {
			return this.field_16085;
		} else {
			return this.field_16088 != null ? this.field_16088.getPos() : this.field_16084.getPos();
		}
	}

	@Override
	public World getWorld() {
		return this.field_16088 != null ? this.field_16088.getWorld() : this.field_16084.getWorld();
	}

	@Nullable
	@Override
	public Entity getEntity() {
		return this.field_16088 != null ? this.field_16088.getEntity() : this.field_16084.getEntity();
	}

	@Override
	public boolean sendCommandFeedback() {
		return this.field_16089 != null ? this.field_16089 : this.field_16084.sendCommandFeedback();
	}

	@Override
	public void setStat(CommandStats.Type statsType, int value) {
		if (this.field_16088 != null) {
			this.field_16088.setStat(statsType, value);
		} else {
			this.field_16084.setStat(statsType, value);
		}
	}

	@Nullable
	@Override
	public MinecraftServer getMinecraftServer() {
		return this.field_16084.getMinecraftServer();
	}
}
