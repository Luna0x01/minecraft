package net.minecraft;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class class_2925 extends class_2957 {
	private final Set<ServerPlayerEntity> field_13847 = Sets.newHashSet();
	private final Set<ServerPlayerEntity> field_13848 = Collections.unmodifiableSet(this.field_13847);
	private boolean field_13849 = true;

	public class_2925(Text text, class_2957.Color color, class_2957.Division division) {
		super(MathHelper.randomUuid(), text, color, division);
	}

	@Override
	public void setHealth(float health) {
		if (health != this.health) {
			super.setHealth(health);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_PCT);
		}
	}

	@Override
	public void setColor(class_2957.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_STYLE);
		}
	}

	@Override
	public void setDivision(class_2957.Division division) {
		if (division != this.division) {
			super.setDivision(division);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_STYLE);
		}
	}

	@Override
	public class_2957 method_12921(boolean bl) {
		if (bl != this.field_14418) {
			super.method_12921(bl);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_PROPERTIES);
		}

		return this;
	}

	@Override
	public class_2957 method_12922(boolean bl) {
		if (bl != this.field_14419) {
			super.method_12922(bl);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_PROPERTIES);
		}

		return this;
	}

	@Override
	public class_2957 method_12923(boolean bl) {
		if (bl != this.field_14420) {
			super.method_12923(bl);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_PROPERTIES);
		}

		return this;
	}

	@Override
	public void setTitle(Text title) {
		if (!Objects.equal(title, this.title)) {
			super.setTitle(title);
			this.updateClients(BossBarS2CPacket.Action.UPDATE_NAME);
		}
	}

	private void updateClients(BossBarS2CPacket.Action action) {
		if (this.field_13849) {
			BossBarS2CPacket bossBarS2CPacket = new BossBarS2CPacket(action, this);

			for (ServerPlayerEntity serverPlayerEntity : this.field_13847) {
				serverPlayerEntity.networkHandler.sendPacket(bossBarS2CPacket);
			}
		}
	}

	public void method_12768(ServerPlayerEntity player) {
		if (this.field_13847.add(player) && this.field_13849) {
			player.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Action.ADD, this));
		}
	}

	public void method_12769(ServerPlayerEntity player) {
		if (this.field_13847.remove(player) && this.field_13849) {
			player.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Action.REMOVE, this));
		}
	}

	public void method_21246() {
		if (!this.field_13847.isEmpty()) {
			for (ServerPlayerEntity serverPlayerEntity : this.field_13847) {
				this.method_12769(serverPlayerEntity);
			}
		}
	}

	public boolean method_21247() {
		return this.field_13849;
	}

	public void method_12771(boolean bl) {
		if (bl != this.field_13849) {
			this.field_13849 = bl;

			for (ServerPlayerEntity serverPlayerEntity : this.field_13847) {
				serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(bl ? BossBarS2CPacket.Action.ADD : BossBarS2CPacket.Action.REMOVE, this));
			}
		}
	}

	public Collection<ServerPlayerEntity> method_12770() {
		return this.field_13848;
	}
}
