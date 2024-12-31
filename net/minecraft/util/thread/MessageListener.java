package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MessageListener<Msg> extends AutoCloseable {
	String getName();

	void send(Msg object);

	default void close() {
	}

	default <Source> CompletableFuture<Source> ask(Function<? super MessageListener<Source>, ? extends Msg> function) {
		CompletableFuture<Source> completableFuture = new CompletableFuture();
		Msg object = (Msg)function.apply(create("ask future procesor handle", completableFuture::complete));
		this.send(object);
		return completableFuture;
	}

	static <Msg> MessageListener<Msg> create(String string, Consumer<Msg> consumer) {
		return new MessageListener<Msg>() {
			@Override
			public String getName() {
				return string;
			}

			@Override
			public void send(Msg object) {
				consumer.accept(object);
			}

			public String toString() {
				return string;
			}
		};
	}
}
