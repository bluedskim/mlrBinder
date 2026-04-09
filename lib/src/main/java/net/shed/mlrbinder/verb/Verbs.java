package net.shed.mlrbinder.verb;

import net.shed.mlrbinder.Arg;

/**
 * Prebuilt {@link Verb} factories (v0.02). Prefer importing from here for discoverability.
 */
public final class Verbs {
	private Verbs() {
	}

	public static Verb altkv(Arg... args) {
		return new Verb("altkv", args);
	}

	public static Verb bar(Arg... args) {
		return new Verb("bar", args);
	}

	public static Verb bootstrap(Arg... args) {
		return new Verb("bootstrap", args);
	}

	public static Verb cat(Arg... args) {
		return new Verb("cat", args);
	}

	public static Verb check(Arg... args) {
		return new Verb("check", args);
	}

	public static Verb cleanWhitespace(Arg... args) {
		return new Verb("clean-whitespace", args);
	}

	public static Verb count(Arg... args) {
		return new Verb("count", args);
	}

	public static Verb countDistinct(Arg... args) {
		return new Verb("count-distinct", args);
	}

	public static Verb countSimilar(Arg... args) {
		return new Verb("count-similar", args);
	}

	public static Verb cut(Arg... args) {
		return new Verb("cut", args);
	}

	public static Verb decimate(Arg... args) {
		return new Verb("decimate", args);
	}

	public static Verb fillDown(Arg... args) {
		return new Verb("fill-down", args);
	}

	public static Verb fillEmpty(Arg... args) {
		return new Verb("fill-empty", args);
	}

	public static Verb filter(Arg... args) {
		return new Verb("filter", args);
	}

	public static Verb flatten(Arg... args) {
		return new Verb("flatten", args);
	}

	public static Verb formatValues(Arg... args) {
		return new Verb("format-values", args);
	}

	public static Verb fraction(Arg... args) {
		return new Verb("fraction", args);
	}

	public static Verb gap(Arg... args) {
		return new Verb("gap", args);
	}

	public static Verb grep(Arg... args) {
		return new Verb("grep", args);
	}

	public static Verb groupBy(Arg... args) {
		return new Verb("group-by", args);
	}

	public static Verb groupLike(Arg... args) {
		return new Verb("group-like", args);
	}

	public static Verb havingFields(Arg... args) {
		return new Verb("having-fields", args);
	}

	public static Verb head(Arg... args) {
		return new Verb("head", args);
	}

	public static Verb histogram(Arg... args) {
		return new Verb("histogram", args);
	}

	public static Verb join(Arg... args) {
		return new Verb("join", args);
	}

	public static Verb jsonParse(Arg... args) {
		return new Verb("json-parse", args);
	}

	public static Verb jsonStringify(Arg... args) {
		return new Verb("json-stringify", args);
	}

	public static Verb label(Arg... args) {
		return new Verb("label", args);
	}

	public static Verb latin1ToUtf8(Arg... args) {
		return new Verb("latin1-to-utf8", args);
	}

	public static Verb utf8ToLatin1(Arg... args) {
		return new Verb("utf8-to-latin1", args);
	}

	public static Verb leastFrequent(Arg... args) {
		return new Verb("least-frequent", args);
	}

	public static Verb mergeFields(Arg... args) {
		return new Verb("merge-fields", args);
	}

	public static Verb mostFrequent(Arg... args) {
		return new Verb("most-frequent", args);
	}

	public static Verb nest(Arg... args) {
		return new Verb("nest", args);
	}

	public static Verb nothing(Arg... args) {
		return new Verb("nothing", args);
	}

	public static Verb put(Arg... args) {
		return new Verb("put", args);
	}

	public static Verb regularize(Arg... args) {
		return new Verb("regularize", args);
	}

	public static Verb removeEmptyColumns(Arg... args) {
		return new Verb("remove-empty-columns", args);
	}

	public static Verb rename(Arg... args) {
		return new Verb("rename", args);
	}

	public static Verb reorder(Arg... args) {
		return new Verb("reorder", args);
	}

	public static Verb repeat(Arg... args) {
		return new Verb("repeat", args);
	}

	public static Verb reshape(Arg... args) {
		return new Verb("reshape", args);
	}

	public static Verb sample(Arg... args) {
		return new Verb("sample", args);
	}

	public static Verb sec2Gmt(Arg... args) {
		return new Verb("sec2gmt", args);
	}

	public static Verb sec2Gmtdate(Arg... args) {
		return new Verb("sec2gmtdate", args);
	}

	public static Verb seqgen(Arg... args) {
		return new Verb("seqgen", args);
	}

	public static Verb shuffle(Arg... args) {
		return new Verb("shuffle", args);
	}

	public static Verb skipTrivialRecords(Arg... args) {
		return new Verb("skip-trivial-records", args);
	}

	public static Verb sort(Arg... args) {
		return new Verb("sort", args);
	}

	public static Verb sortWithinRecords(Arg... args) {
		return new Verb("sort-within-records", args);
	}

	public static Verb split(Arg... args) {
		return new Verb("split", args);
	}

	public static Verb stats1(Arg... args) {
		return new Verb("stats1", args);
	}

	public static Verb stats2(Arg... args) {
		return new Verb("stats2", args);
	}

	public static Verb step(Arg... args) {
		return new Verb("step", args);
	}

	public static Verb summary(Arg... args) {
		return new Verb("summary", args);
	}

	public static Verb tac(Arg... args) {
		return new Verb("tac", args);
	}

	public static Verb tail(Arg... args) {
		return new Verb("tail", args);
	}

	public static Verb tee(Arg... args) {
		return new Verb("tee", args);
	}

	public static Verb template(Arg... args) {
		return new Verb("template", args);
	}

	public static Verb top(Arg... args) {
		return new Verb("top", args);
	}

	public static Verb unflatten(Arg... args) {
		return new Verb("unflatten", args);
	}

	public static Verb uniq(Arg... args) {
		return new Verb("uniq", args);
	}

	public static Verb unsparsify(Arg... args) {
		return new Verb("unsparsify", args);
	}
}
