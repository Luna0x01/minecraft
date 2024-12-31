package net.minecraft.fluid;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4021;
import net.minecraft.class_4025;
import net.minecraft.class_4027;
import net.minecraft.class_4032;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class Fluid {
	public static final IdList<FluidState> field_19484 = new IdList<>();
	protected final StateManager<Fluid, FluidState> field_19485;
	private FluidState field_19483;

	protected Fluid() {
		StateManager.Builder<Fluid, FluidState> builder = new StateManager.Builder<>(this);
		this.method_17780(builder);
		this.field_19485 = builder.build(class_4025::new);
		this.method_17794(this.field_19485.method_16923());
	}

	protected void method_17780(StateManager.Builder<Fluid, FluidState> builder) {
	}

	public StateManager<Fluid, FluidState> method_17795() {
		return this.field_19485;
	}

	protected final void method_17794(FluidState fluidState) {
		this.field_19483 = fluidState;
	}

	public final FluidState getDefaultState() {
		return this.field_19483;
	}

	protected abstract RenderLayer getRenderLayer();

	public abstract Item method_17787();

	protected void method_17777(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	protected void method_17776(World world, BlockPos blockPos, FluidState fluidState) {
	}

	protected void method_17788(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	@Nullable
	protected ParticleEffect getParticle() {
		return null;
	}

	protected abstract boolean method_17783(FluidState fluidState, Fluid fluid, Direction direction);

	protected abstract Vec3d method_17779(RenderBlockView renderBlockView, BlockPos blockPos, FluidState fluidState);

	public abstract int method_17778(RenderBlockView renderBlockView);

	protected boolean method_17798() {
		return false;
	}

	protected boolean isEmpty() {
		return false;
	}

	protected abstract float getBlastResistance();

	public abstract float method_17782(FluidState fluidState);

	protected abstract BlockState method_17789(FluidState fluidState);

	public abstract boolean isStill(FluidState fluidState);

	public abstract int method_17793(FluidState fluidState);

	public boolean method_17781(Fluid fluid) {
		return fluid == this;
	}

	public boolean method_17786(Tag<Fluid> tag) {
		return tag.contains(this);
	}

	public static void method_17799() {
		method_17785(Registry.FLUID.getDefaultId(), new class_4021());
		method_17784("flowing_water", new class_4032.class_4033());
		method_17784("water", new class_4032.class_4034());
		method_17784("flowing_lava", new class_4027.class_4028());
		method_17784("lava", new class_4027.class_4029());

		for (Fluid fluid : Registry.FLUID) {
			UnmodifiableIterator var2 = fluid.method_17795().getBlockStates().iterator();

			while (var2.hasNext()) {
				FluidState fluidState = (FluidState)var2.next();
				field_19484.method_19952(fluidState);
			}
		}
	}

	private static void method_17784(String string, Fluid fluid) {
		method_17785(new Identifier(string), fluid);
	}

	private static void method_17785(Identifier identifier, Fluid fluid) {
		Registry.FLUID.add(identifier, fluid);
	}
}
