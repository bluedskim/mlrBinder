package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.verb.Verb;

/**
 * Every {@link Mlr.Verbs} factory has a matching fluent instance method on {@link Mlr} with the same argv.
 */
class MlrVerbFluentParityTest {

	private static final Map<String, String> MLR_METHOD_FOR_VERB = new HashMap<>();

	static {
		MLR_METHOD_FOR_VERB.put("filter", "filterVerb");
		MLR_METHOD_FOR_VERB.put("split", "splitVerb");
	}

	@Test
	void fluentMethodMatchesVerbsStatic() throws Exception {
		for (Method vm : net.shed.mlrbinder.verb.Verbs.class.getDeclaredMethods()) {
			if (!Modifier.isPublic(vm.getModifiers()) || !Modifier.isStatic(vm.getModifiers())) {
				continue;
			}
			if (vm.getReturnType() != Verb.class) {
				continue;
			}
			if (!Arrays.equals(vm.getParameterTypes(), new Class<?>[] { Arg[].class })) {
				continue;
			}
			String verbName = vm.getName();
			String mlrName = MLR_METHOD_FOR_VERB.getOrDefault(verbName, verbName);
			Method im = Mlr.class.getMethod(mlrName, Arg[].class);
			Mlr chain = Mlr.inDir("wp");
			Mlr viaFluent = (Mlr) im.invoke(chain, (Object) new Arg[0]);
			Mlr viaVerb = Mlr.inDir("wp").verb((Verb) vm.invoke(null, (Object) new Arg[0]));
			assertEquals(viaVerb.toString(), viaFluent.toString(),
					() -> "Mlr." + mlrName + "() vs Verbs." + verbName + "()");
		}
	}

	@Test
	void everyMlrFluentVerbMethodMapsToVerbs() {
		var verbNames = Arrays.stream(net.shed.mlrbinder.verb.Verbs.class.getDeclaredMethods())
				.filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
				.filter(m -> m.getReturnType() == Verb.class)
				.filter(m -> Arrays.equals(m.getParameterTypes(), new Class<?>[] { Arg[].class }))
				.map(Method::getName)
				.collect(Collectors.toSet());

		for (Method m : Mlr.class.getDeclaredMethods()) {
			if (!Modifier.isPublic(m.getModifiers()) || m.getReturnType() != Mlr.class) {
				continue;
			}
			if (!Arrays.equals(m.getParameterTypes(), new Class<?>[] { Arg[].class })) {
				continue;
			}
			String n = m.getName();
			if ("verb".equals(n)) {
				continue;
			}
			String v = MLR_METHOD_FOR_VERB.entrySet().stream()
					.filter(e -> e.getValue().equals(n))
					.map(Map.Entry::getKey)
					.findFirst()
					.orElse(n);
			if (!verbNames.contains(v)) {
				throw new AssertionError("Mlr." + n + "(Arg...) has no matching Verbs." + v);
			}
		}
	}
}
