package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class class_4379 implements Packet<ClientPlayPacketListener> {
	private double field_21548;
	private double field_21549;
	private double field_21550;
	private int field_21551;
	private class_4048.class_4049 field_21552;
	private class_4048.class_4049 field_21553;
	private boolean field_21554;

	public class_4379() {
	}

	public class_4379(class_4048.class_4049 arg, double d, double e, double f) {
		this.field_21552 = arg;
		this.field_21548 = d;
		this.field_21549 = e;
		this.field_21550 = f;
	}

	public class_4379(class_4048.class_4049 arg, Entity entity, class_4048.class_4049 arg2) {
		this.field_21552 = arg;
		this.field_21551 = entity.getEntityId();
		this.field_21553 = arg2;
		Vec3d vec3d = arg2.method_17870(entity);
		this.field_21548 = vec3d.x;
		this.field_21549 = vec3d.y;
		this.field_21550 = vec3d.z;
		this.field_21554 = true;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21552 = buf.readEnumConstant(class_4048.class_4049.class);
		this.field_21548 = buf.readDouble();
		this.field_21549 = buf.readDouble();
		this.field_21550 = buf.readDouble();
		if (buf.readBoolean()) {
			this.field_21554 = true;
			this.field_21551 = buf.readVarInt();
			this.field_21553 = buf.readEnumConstant(class_4048.class_4049.class);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.field_21552);
		buf.writeDouble(this.field_21548);
		buf.writeDouble(this.field_21549);
		buf.writeDouble(this.field_21550);
		buf.writeBoolean(this.field_21554);
		if (this.field_21554) {
			buf.writeVarInt(this.field_21551);
			buf.writeEnumConstant(this.field_21553);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_20200(this);
	}

	public class_4048.class_4049 method_20244() {
		return this.field_21552;
	}

	@Nullable
	public Vec3d method_20242(World world) {
		if (this.field_21554) {
			Entity entity = world.getEntityById(this.field_21551);
			return entity == null ? new Vec3d(this.field_21548, this.field_21549, this.field_21550) : this.field_21553.method_17870(entity);
		} else {
			return new Vec3d(this.field_21548, this.field_21549, this.field_21550);
		}
	}
}
