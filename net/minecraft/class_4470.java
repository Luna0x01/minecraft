package net.minecraft;

import java.util.Collection;

public interface class_4470 {
	class_4470 AND = collection -> {
		String[][] strings = new String[collection.size()][];
		int i = 0;

		for (String string : collection) {
			strings[i++] = new String[]{string};
		}

		return strings;
	};
	class_4470 field_21947 = collection -> new String[][]{(String[])collection.toArray(new String[0])};

	String[][] createRequirements(Collection<String> collection);
}
