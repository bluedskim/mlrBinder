package net.shed.mlrbinder.verb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.MlrBinder;

public class Verb implements Arg {	
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	public static final Verb altkv(Arg... args) {return new Verb("altkv", args);}
	public static final Verb bar(Arg... args) {return new Verb("bar", args);}
	public static final Verb bootstrap(Arg... args) {return new Verb("bootstrap", args);}
	public static final Verb cat(Arg... args) {return new Verb("cat", args);}
	public static final Verb check(Arg... args) {return new Verb("check", args);}
	public static final Verb cleanWhitespace(Arg... args) {return new Verb("clean-whitespace", args);}
	public static final Verb count(Arg... args) {return new Verb("count", args);}
	public static final Verb countDistinct(Arg... args) {return new Verb("count-distinct", args);}
	public static final Verb countSimilar(Arg... args) {return new Verb("count-similar", args);}
	public static final Verb cut(Arg... args) {return new Verb("cut", args);}
	public static final Verb decimate(Arg... args) {return new Verb("decimate", args);}
	public static final Verb fillDown(Arg... args) {return new Verb("fill-down", args);}
	public static final Verb fillEmpty(Arg... args) {return new Verb("fill-empty", args);}
	public static final Verb filter(Arg... args) {return new Verb("filter", args);}
	public static final Verb flatten(Arg... args) {return new Verb("flatten", args);}
	public static final Verb formatValues(Arg... args) {return new Verb("format-values", args);}
	public static final Verb fraction(Arg... args) {return new Verb("fraction", args);}
	public static final Verb gap(Arg... args) {return new Verb("gap", args);}
	public static final Verb grep(Arg... args) {return new Verb("grep", args);}
	public static final Verb groupBy(Arg... args) {return new Verb("group-by", args);}
	public static final Verb groupLike(Arg... args) {return new Verb("group-like", args);}
	public static final Verb havingFields(Arg... args) {return new Verb("having-fields", args);}
	public static final Verb head(Arg... args) {return new Verb("head", args);}
	public static final Verb histogram(Arg... args) {return new Verb("histogram", args);}
	public static final Verb join(Arg... args) {return new Verb("join", args);}
	public static final Verb jsonParse(Arg... args) {return new Verb("json-parse", args);}
	public static final Verb jsonStringify(Arg... args) {return new Verb("json-stringify", args);}
	public static final Verb label(Arg... args) {return new Verb("label", args);}
	public static final Verb latin1ToUtf8(Arg... args) {return new Verb("latin1-to-utf8", args);}
	public static final Verb utf8ToLatin1(Arg... args) {return new Verb("utf8-to-latin1", args);}
	public static final Verb leastFrequent(Arg... args) {return new Verb("least-frequent", args);}
	public static final Verb mergeFields(Arg... args) {return new Verb("merge-fields", args);}
	public static final Verb mostFrequent(Arg... args) {return new Verb("most-frequent", args);}
	public static final Verb nest(Arg... args) {return new Verb("nest", args);}
	public static final Verb nothing(Arg... args) {return new Verb("nothing", args);}
	public static final Verb put(Arg... args) {return new Verb("put", args);}
	public static final Verb regularize(Arg... args) {return new Verb("regularize", args);}
	public static final Verb removeEmptyColumns(Arg... args) {return new Verb("remove-empty-columns", args);}
	public static final Verb rename(Arg... args) {return new Verb("rename", args);}
	public static final Verb reorder(Arg... args) {return new Verb("reorder", args);}
	public static final Verb repeat(Arg... args) {return new Verb("repeat", args);}
	public static final Verb reshape(Arg... args) {return new Verb("reshape", args);}
	public static final Verb sample(Arg... args) {return new Verb("sample", args);}
	public static final Verb sec2Gmt(Arg... args) {return new Verb("sec2gmt", args);}
	public static final Verb sec2Gmtdate(Arg... args) {return new Verb("sec2gmtdate", args);}
	public static final Verb seqgen(Arg... args) {return new Verb("seqgen", args);}
	public static final Verb shuffle(Arg... args) {return new Verb("shuffle", args);}
	public static final Verb skipTrivialRecords(Arg... args) {return new Verb("skip-trivial-records", args);}
	public static final Verb sort(Arg... args) {return new Verb("sort", args);}
	public static final Verb sortWithinRecords(Arg... args) {return new Verb("sort-within-records", args);}
	public static final Verb split(Arg... args) {return new Verb("split", args);}
	public static final Verb stats1(Arg... args) {return new Verb("stats1", args);}
	public static final Verb stats2(Arg... args) {return new Verb("stats2", args);}
	public static final Verb step(Arg... args) {return new Verb("step", args);}
	public static final Verb summary(Arg... args) {return new Verb("summary", args);}
	public static final Verb tac(Arg... args) {return new Verb("tac", args);}
	public static final Verb tail(Arg... args) {return new Verb("tail", args);}
	public static final Verb tee(Arg... args) {return new Verb("tee", args);}
	public static final Verb template(Arg... args) {return new Verb("template", args);}
	public static final Verb top(Arg... args) {return new Verb("top", args);}
	public static final Verb unflatten(Arg... args) {return new Verb("unflatten", args);}
	public static final Verb uniq(Arg... args) {return new Verb("uniq", args);}
	public static final Verb unsparsify(Arg... args) {return new Verb("unsparsify", args);}
	
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
