package net.minecraft;

import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3454<K, T extends class_3455<K, T>, R> {
	private static final Logger field_16648 = LogManager.getLogger();
	private final class_3452<K, T, R> field_16649;
	private boolean field_16650;
	private int field_16651 = 1000;

	public class_3454(class_3452<K, T, R> arg) {
		this.field_16649 = arg;
	}

	public void method_15508() throws InterruptedException {
		this.field_16649.method_15491();
	}

	public void method_15510() {
		if (this.field_16650) {
			throw new RuntimeException("Batch already started.");
		} else {
			this.field_16651 = 1000;
			this.field_16650 = true;
		}
	}

	public CompletableFuture<R> method_15509(K object) {
		if (!this.field_16650) {
			throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
		} else {
			CompletableFuture<R> completableFuture = this.field_16649.method_15482(object);
			this.field_16651--;
			if (this.field_16651 == 0) {
				completableFuture = this.field_16649.method_15480();
				this.field_16651 = 1000;
			}

			return completableFuture;
		}
	}

	public CompletableFuture<R> method_15511() {
		if (!this.field_16650) {
			throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
		} else {
			if (this.field_16651 != 1000) {
				this.field_16649.method_15480();
			}

			this.field_16650 = false;
			return this.field_16649.method_15496();
		}
	}
}
