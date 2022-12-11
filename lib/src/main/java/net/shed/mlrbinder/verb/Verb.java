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
	public static final String CHAINING_ADVERB = "then";
	
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	public static final Verb altkv() {return new Verb("altkv");}
	public static final Verb bar() {return new Verb("bar");}
	public static final Verb bootstrap() {return new Verb("bootstrap");}
	public static final Verb cat() {return new Verb("cat");}
	public static final Verb check() {return new Verb("check");}
	public static final Verb cleanWhitespace() {return new Verb("clean-whitespace");}
	public static final Verb count() {return new Verb("count");}
	public static final Verb countDistinct() {return new Verb("count-distinct");}
	public static final Verb countSimilar() {return new Verb("count-similar");}
	public static final Verb cut() {return new Verb("cut");}
	public static final Verb decimate() {return new Verb("decimate");}
	public static final Verb fillDown() {return new Verb("fill-down");}
	public static final Verb fillEmpty() {return new Verb("fill-empty");}
	public static final Verb filter() {return new Verb("filter");}
	public static final Verb flatten() {return new Verb("flatten");}
	public static final Verb formatValues() {return new Verb("format-values");}
	public static final Verb fraction() {return new Verb("fraction");}
	public static final Verb gap() {return new Verb("gap");}
	public static final Verb grep() {return new Verb("grep");}
	public static final Verb groupBy() {return new Verb("group-by");}
	public static final Verb groupLike() {return new Verb("group-like");}
	public static final Verb havingFields() {return new Verb("having-fields");}
	public static final Verb head() {return new Verb("head");}
	public static final Verb histogram() {return new Verb("histogram");}
	public static final Verb join() {return new Verb("join");}
	public static final Verb jsonParse() {return new Verb("json-parse");}
	public static final Verb jsonStringify() {return new Verb("json-stringify");}
	public static final Verb label() {return new Verb("label");}
	public static final Verb latin1ToUtf8() {return new Verb("latin1-to-utf8");}
	public static final Verb utf8ToLatin1() {return new Verb("utf8-to-latin1");}
	public static final Verb leastFrequent() {return new Verb("least-frequent");}
	public static final Verb mergeFields() {return new Verb("merge-fields");}
	public static final Verb mostFrequent() {return new Verb("most-frequent");}
	public static final Verb nest() {return new Verb("nest");}
	public static final Verb nothing() {return new Verb("nothing");}
	public static final Verb put() {return new Verb("put");}
	public static final Verb regularize() {return new Verb("regularize");}
	public static final Verb removeEmptyColumns() {return new Verb("remove-empty-columns");}
	public static final Verb rename() {return new Verb("rename");}
	public static final Verb reorder() {return new Verb("reorder");}
	public static final Verb repeat() {return new Verb("repeat");}
	public static final Verb reshape() {return new Verb("reshape");}
	public static final Verb sample() {return new Verb("sample");}
	public static final Verb sec2Gmt() {return new Verb("sec2gmt");}
	public static final Verb sec2Gmtdate() {return new Verb("sec2gmtdate");}
	public static final Verb seqgen() {return new Verb("seqgen");}
	public static final Verb shuffle() {return new Verb("shuffle");}
	public static final Verb skipTrivialRecords() {return new Verb("skip-trivial-records");}
	public static final Verb sort(Arg... args) {return new Verb("sort", args);}
	public static final Verb sortWithinRecords() {return new Verb("sort-within-records");}
	public static final Verb split() {return new Verb("split");}
	public static final Verb stats1() {return new Verb("stats1");}
	public static final Verb stats2() {return new Verb("stats2");}
	public static final Verb step() {return new Verb("step");}
	public static final Verb summary() {return new Verb("summary");}
	public static final Verb tac() {return new Verb("tac");}
	public static final Verb tail() {return new Verb("tail");}
	public static final Verb tee() {return new Verb("tee");}
	public static final Verb template() {return new Verb("template");}
	public static final Verb top() {return new Verb("top");}
	public static final Verb unflatten() {return new Verb("unflatten");}
	public static final Verb uniq() {return new Verb("uniq");}
	public static final Verb unsparsify() {return new Verb("unsparsify");}
	
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
