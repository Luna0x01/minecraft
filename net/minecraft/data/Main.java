package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.class_4344;
import net.minecraft.class_4347;
import net.minecraft.class_4353;
import net.minecraft.class_4354;
import net.minecraft.class_4355;
import net.minecraft.class_4357;
import net.minecraft.class_4365;
import net.minecraft.class_4366;
import net.minecraft.class_4367;
import net.minecraft.class_4368;
import net.minecraft.class_4369;

public class Main {
	public static void main(String[] strings) throws IOException {
		OptionParser optionParser = new OptionParser();
		AbstractOptionSpec<Void> abstractOptionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
		OptionSpecBuilder optionSpecBuilder = optionParser.accepts("server", "Include server generators");
		OptionSpecBuilder optionSpecBuilder2 = optionParser.accepts("client", "Include client generators");
		OptionSpecBuilder optionSpecBuilder3 = optionParser.accepts("dev", "Include development tools");
		OptionSpecBuilder optionSpecBuilder4 = optionParser.accepts("reports", "Include data reports");
		OptionSpecBuilder optionSpecBuilder5 = optionParser.accepts("all", "Include all generators");
		ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec = optionParser.accepts("output", "Output folder")
			.withRequiredArg()
			.defaultsTo("generated", new String[0]);
		ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec2 = optionParser.accepts("input", "Input folder").withRequiredArg();
		OptionSet optionSet = optionParser.parse(strings);
		if (!optionSet.has(abstractOptionSpec) && optionSet.hasOptions()) {
			Path path = Paths.get((String)argumentAcceptingOptionSpec.value(optionSet));
			boolean bl = optionSet.has(optionSpecBuilder2) || optionSet.has(optionSpecBuilder5);
			boolean bl2 = optionSet.has(optionSpecBuilder) || optionSet.has(optionSpecBuilder5);
			boolean bl3 = optionSet.has(optionSpecBuilder3) || optionSet.has(optionSpecBuilder5);
			boolean bl4 = optionSet.has(optionSpecBuilder4) || optionSet.has(optionSpecBuilder5);
			class_4344 lv = method_20306(
				path,
				(Collection<Path>)optionSet.valuesOf(argumentAcceptingOptionSpec2).stream().map(string -> Paths.get(string)).collect(Collectors.toList()),
				bl,
				bl2,
				bl3,
				bl4
			);
			lv.method_19994();
		} else {
			optionParser.printHelpOn(System.out);
		}
	}

	public static class_4344 method_20306(Path path, Collection<Path> collection, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		class_4344 lv = new class_4344(path, collection);
		if (bl || bl2) {
			lv.method_19992(new class_4366(lv));
		}

		if (bl2) {
			lv.method_19992(new class_4368(lv));
			lv.method_19992(new class_4367(lv));
			lv.method_19992(new class_4369(lv));
			lv.method_19992(new class_4357(lv));
			lv.method_19992(new class_4347(lv));
		}

		if (bl3) {
			lv.method_19992(new class_4365(lv));
		}

		if (bl4) {
			lv.method_19992(new class_4353(lv));
			lv.method_19992(new class_4355(lv));
			lv.method_19992(new class_4354(lv));
		}

		return lv;
	}
}
