package net.minecraft.client.toast;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class ToastManager extends DrawableHelper {
	private static final int field_32220 = 5;
	final MinecraftClient client;
	private final ToastManager.Entry<?>[] visibleEntries = new ToastManager.Entry[5];
	private final Deque<Toast> toastQueue = Queues.newArrayDeque();

	public ToastManager(MinecraftClient client) {
		this.client = client;
	}

	public void draw(MatrixStack matrices) {
		if (!this.client.options.hudHidden) {
			for (int i = 0; i < this.visibleEntries.length; i++) {
				ToastManager.Entry<?> entry = this.visibleEntries[i];
				if (entry != null && entry.draw(this.client.getWindow().getScaledWidth(), i, matrices)) {
					this.visibleEntries[i] = null;
				}

				if (this.visibleEntries[i] == null && !this.toastQueue.isEmpty()) {
					this.visibleEntries[i] = new ToastManager.Entry((Toast)this.toastQueue.removeFirst());
				}
			}
		}
	}

	@Nullable
	public <T extends Toast> T getToast(Class<? extends T> toastClass, Object type) {
		for (ToastManager.Entry<?> entry : this.visibleEntries) {
			if (entry != null && toastClass.isAssignableFrom(entry.getInstance().getClass()) && entry.getInstance().getType().equals(type)) {
				return (T)entry.getInstance();
			}
		}

		for (Toast toast : this.toastQueue) {
			if (toastClass.isAssignableFrom(toast.getClass()) && toast.getType().equals(type)) {
				return (T)toast;
			}
		}

		return null;
	}

	public void clear() {
		Arrays.fill(this.visibleEntries, null);
		this.toastQueue.clear();
	}

	public void add(Toast toast) {
		this.toastQueue.add(toast);
	}

	public MinecraftClient getGame() {
		return this.client;
	}

	class Entry<T extends Toast> {
		private static final long field_32221 = 600L;
		private final T instance;
		private long field_2243 = -1L;
		private long field_2242 = -1L;
		private Toast.Visibility visibility = Toast.Visibility.SHOW;

		Entry(T toast) {
			this.instance = toast;
		}

		public T getInstance() {
			return this.instance;
		}

		private float getDisappearProgress(long time) {
			float f = MathHelper.clamp((float)(time - this.field_2243) / 600.0F, 0.0F, 1.0F);
			f *= f;
			return this.visibility == Toast.Visibility.HIDE ? 1.0F - f : f;
		}

		public boolean draw(int x, int y, MatrixStack matrices) {
			long l = Util.getMeasuringTimeMs();
			if (this.field_2243 == -1L) {
				this.field_2243 = l;
				this.visibility.playSound(ToastManager.this.client.getSoundManager());
			}

			if (this.visibility == Toast.Visibility.SHOW && l - this.field_2243 <= 600L) {
				this.field_2242 = l;
			}

			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.push();
			matrixStack.translate(
				(double)((float)x - (float)this.instance.getWidth() * this.getDisappearProgress(l)), (double)(y * this.instance.getHeight()), (double)(800 + y)
			);
			RenderSystem.applyModelViewMatrix();
			Toast.Visibility visibility = this.instance.draw(matrices, ToastManager.this, l - this.field_2242);
			matrixStack.pop();
			RenderSystem.applyModelViewMatrix();
			if (visibility != this.visibility) {
				this.field_2243 = l - (long)((int)((1.0F - this.getDisappearProgress(l)) * 600.0F));
				this.visibility = visibility;
				this.visibility.playSound(ToastManager.this.client.getSoundManager());
			}

			return this.visibility == Toast.Visibility.HIDE && l - this.field_2243 > 600L;
		}
	}
}
