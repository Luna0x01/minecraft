package net.minecraft.server.command;

import net.minecraft.class_3893;
import net.minecraft.class_3915;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class Console implements class_3893 {
	private final StringBuffer text = new StringBuffer();
	private final MinecraftServer field_13901;

	public Console(MinecraftServer minecraftServer) {
		this.field_13901 = minecraftServer;
	}

	public void destroy() {
		this.text.setLength(0);
	}

	public String getTextAsString() {
		return this.text.toString();
	}

	public class_3915 method_21391() {
		ServerWorld serverWorld = this.field_13901.method_20312(DimensionType.OVERWORLD);
		return new class_3915(this, new Vec3d(serverWorld.method_3585()), Vec2f.ZERO, serverWorld, 4, "Recon", new LiteralText("Rcon"), this.field_13901, null);
	}

	@Override
	public void method_5505(Text text) {
		this.text.append(text.getString());
	}

	@Override
	public boolean method_17413() {
		return true;
	}

	@Override
	public boolean method_17414() {
		return true;
	}

	@Override
	public boolean method_17412() {
		return this.field_13901.shouldBroadcastRconToOps();
	}
}
