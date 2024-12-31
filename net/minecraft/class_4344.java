package net.minecraft;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4344 {
	private static final Logger field_21402 = LogManager.getLogger();
	private final Collection<Path> field_21403;
	private final Path field_21404;
	private final List<class_4345> field_21405 = Lists.newArrayList();

	public class_4344(Path path, Collection<Path> collection) {
		this.field_21404 = path;
		this.field_21403 = collection;
	}

	public Collection<Path> method_19991() {
		return this.field_21403;
	}

	public Path method_19993() {
		return this.field_21404;
	}

	public void method_19994() throws IOException {
		class_4346 lv = new class_4346(this.field_21404, "cache");
		Stopwatch stopwatch = Stopwatch.createUnstarted();

		for (class_4345 lv2 : this.field_21405) {
			field_21402.info("Starting provider: {}", lv2.method_19995());
			stopwatch.start();
			lv2.method_19996(lv);
			stopwatch.stop();
			field_21402.info("{} finished after {} ms", lv2.method_19995(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
			stopwatch.reset();
		}

		lv.method_19997();
	}

	public void method_19992(class_4345 arg) {
		this.field_21405.add(arg);
	}

	static {
		Bootstrap.initialize();
	}
}
