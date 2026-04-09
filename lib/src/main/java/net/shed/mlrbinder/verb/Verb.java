package net.shed.mlrbinder.verb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.MlrBinder;

/**
 * Miller verb segment for the command line. Static factories delegate to {@link Verbs}; add new verbs there and add a
 * matching delegating method here so existing {@code Verb.foo()} call sites keep working.
 */
public class Verb implements Arg {
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	public static Verb altkv(Arg... args) {
		return Verbs.altkv(args);
	}

	public static Verb bar(Arg... args) {
		return Verbs.bar(args);
	}

	public static Verb bootstrap(Arg... args) {
		return Verbs.bootstrap(args);
	}

	/**
	 * Delegates to {@link Verbs#caseVerb}; Miller's verb is {@code case}.
	 */
	public static Verb caseVerb(Arg... args) {
		return Verbs.caseVerb(args);
	}

	public static Verb cat(Arg... args) {
		return Verbs.cat(args);
	}

	public static Verb check(Arg... args) {
		return Verbs.check(args);
	}

	public static Verb cleanWhitespace(Arg... args) {
		return Verbs.cleanWhitespace(args);
	}

	public static Verb count(Arg... args) {
		return Verbs.count(args);
	}

	public static Verb countDistinct(Arg... args) {
		return Verbs.countDistinct(args);
	}

	public static Verb countSimilar(Arg... args) {
		return Verbs.countSimilar(args);
	}

	public static Verb cut(Arg... args) {
		return Verbs.cut(args);
	}

	public static Verb decimate(Arg... args) {
		return Verbs.decimate(args);
	}

	public static Verb fillDown(Arg... args) {
		return Verbs.fillDown(args);
	}

	public static Verb fillEmpty(Arg... args) {
		return Verbs.fillEmpty(args);
	}

	public static Verb filter(Arg... args) {
		return Verbs.filter(args);
	}

	public static Verb flatten(Arg... args) {
		return Verbs.flatten(args);
	}

	public static Verb formatValues(Arg... args) {
		return Verbs.formatValues(args);
	}

	public static Verb fraction(Arg... args) {
		return Verbs.fraction(args);
	}

	public static Verb gap(Arg... args) {
		return Verbs.gap(args);
	}

	public static Verb grep(Arg... args) {
		return Verbs.grep(args);
	}

	public static Verb gsub(Arg... args) {
		return Verbs.gsub(args);
	}

	public static Verb groupBy(Arg... args) {
		return Verbs.groupBy(args);
	}

	public static Verb groupLike(Arg... args) {
		return Verbs.groupLike(args);
	}

	public static Verb havingFields(Arg... args) {
		return Verbs.havingFields(args);
	}

	public static Verb head(Arg... args) {
		return Verbs.head(args);
	}

	public static Verb histogram(Arg... args) {
		return Verbs.histogram(args);
	}

	public static Verb join(Arg... args) {
		return Verbs.join(args);
	}

	public static Verb jsonParse(Arg... args) {
		return Verbs.jsonParse(args);
	}

	public static Verb jsonStringify(Arg... args) {
		return Verbs.jsonStringify(args);
	}

	public static Verb label(Arg... args) {
		return Verbs.label(args);
	}

	public static Verb latin1ToUtf8(Arg... args) {
		return Verbs.latin1ToUtf8(args);
	}

	public static Verb utf8ToLatin1(Arg... args) {
		return Verbs.utf8ToLatin1(args);
	}

	public static Verb leastFrequent(Arg... args) {
		return Verbs.leastFrequent(args);
	}

	public static Verb mergeFields(Arg... args) {
		return Verbs.mergeFields(args);
	}

	public static Verb mostFrequent(Arg... args) {
		return Verbs.mostFrequent(args);
	}

	public static Verb nest(Arg... args) {
		return Verbs.nest(args);
	}

	public static Verb nothing(Arg... args) {
		return Verbs.nothing(args);
	}

	public static Verb put(Arg... args) {
		return Verbs.put(args);
	}

	public static Verb regularize(Arg... args) {
		return Verbs.regularize(args);
	}

	public static Verb removeEmptyColumns(Arg... args) {
		return Verbs.removeEmptyColumns(args);
	}

	public static Verb rename(Arg... args) {
		return Verbs.rename(args);
	}

	public static Verb reorder(Arg... args) {
		return Verbs.reorder(args);
	}

	public static Verb repeat(Arg... args) {
		return Verbs.repeat(args);
	}

	public static Verb reshape(Arg... args) {
		return Verbs.reshape(args);
	}

	public static Verb sample(Arg... args) {
		return Verbs.sample(args);
	}

	public static Verb sec2Gmt(Arg... args) {
		return Verbs.sec2Gmt(args);
	}

	public static Verb sec2Gmtdate(Arg... args) {
		return Verbs.sec2Gmtdate(args);
	}

	public static Verb seqgen(Arg... args) {
		return Verbs.seqgen(args);
	}

	public static Verb shuffle(Arg... args) {
		return Verbs.shuffle(args);
	}

	public static Verb sparsify(Arg... args) {
		return Verbs.sparsify(args);
	}

	public static Verb skipTrivialRecords(Arg... args) {
		return Verbs.skipTrivialRecords(args);
	}

	public static Verb sort(Arg... args) {
		return Verbs.sort(args);
	}

	public static Verb sortWithinRecords(Arg... args) {
		return Verbs.sortWithinRecords(args);
	}

	public static Verb split(Arg... args) {
		return Verbs.split(args);
	}

	public static Verb ssub(Arg... args) {
		return Verbs.ssub(args);
	}

	public static Verb stats1(Arg... args) {
		return Verbs.stats1(args);
	}

	public static Verb stats2(Arg... args) {
		return Verbs.stats2(args);
	}

	public static Verb step(Arg... args) {
		return Verbs.step(args);
	}

	public static Verb sub(Arg... args) {
		return Verbs.sub(args);
	}

	public static Verb summary(Arg... args) {
		return Verbs.summary(args);
	}

	public static Verb surv(Arg... args) {
		return Verbs.surv(args);
	}

	public static Verb tac(Arg... args) {
		return Verbs.tac(args);
	}

	public static Verb tail(Arg... args) {
		return Verbs.tail(args);
	}

	public static Verb tee(Arg... args) {
		return Verbs.tee(args);
	}

	public static Verb template(Arg... args) {
		return Verbs.template(args);
	}

	public static Verb top(Arg... args) {
		return Verbs.top(args);
	}

	public static Verb unflatten(Arg... args) {
		return Verbs.unflatten(args);
	}

	public static Verb uniq(Arg... args) {
		return Verbs.uniq(args);
	}

	public static Verb unspace(Arg... args) {
		return Verbs.unspace(args);
	}

	public static Verb unsparsify(Arg... args) {
		return Verbs.unsparsify(args);
	}
	
	String verbName;

	/**
	 * argument list
	 */
	private List<Arg> args = new ArrayList<>();

	/**
	 * return flag list
	 * @return
	 */
	public List<Arg> getArgs() {
		return args;
	}

	public Verb addArg(Arg arg) {
		args.add(arg);
		return this;
	}

	public Verb(String verbName) {
		super();
		this.verbName = verbName;
	}

	public Verb(String verbName, Arg... args) {
		this(verbName);
		this.args = Arrays.asList(args);
	}	

	@Override
	public String toString() {
		return verbName + toStringArgs();
	}

	private String toStringArgs() {
		StringBuilder concatenatedOptions = new StringBuilder();
		for(Arg arg : args) {
			concatenatedOptions.append(MlrBinder.SPACER + arg);
		}
		return concatenatedOptions.toString();
	}

	/**
	 * to command line executable arguments list
	 */
	@Override
	public List<String> toStringList() {
		List<String> stringList = new ArrayList<>();
		stringList.add(verbName);
		args.stream().forEach(o -> stringList.addAll(o.toStringList()));
		return stringList;
	}
}
