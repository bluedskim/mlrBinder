/**
 * Miller Java Binder: build {@code mlr} command lines from Java and run them via {@link net.shed.mlrbinder.Mlr}.
 *
 * <h2>Binder-only convenience (“sugar”) on {@link net.shed.mlrbinder.Mlr}</h2>
 * <p>
 * Some {@link net.shed.mlrbinder.Mlr} methods bundle common Miller <strong>verb options</strong> into one call.
 * They are <strong>not</strong> Miller CLI features, subcommands, or DSL keywords — the child process is always
 * standard {@code mlr} with normal verbs and flags. Each sugar method is documented on {@code Mlr} with a
 * <strong>Miller CLI equivalent</strong> block so you can compare and avoid confusing binder helpers with upstream
 * Miller documentation.
 * </p>
 * <p>Consolidated map (see {@code Mlr} Javadoc for details and examples):</p>
 * <table border="1">
 * <caption>Mlr convenience vs Miller CLI</caption>
 * <tr><th>{@code Mlr} method</th><th>Miller CLI equivalent (verb segment)</th></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#head(int) head(count)}</td><td>{@code head -n <count>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#tail(int) tail(count)}</td><td>{@code tail -n <count>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#headOneGroupedBy(String) headOneGroupedBy(fields)}</td><td>{@code head -n 1 -g <fields>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#cutFields(String) cutFields(fields)}</td><td>{@code cut -f <fields>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#cutOrdered(String) cutOrdered(fields)}</td><td>{@code cut -o -f <fields>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#cutExcept(String) cutExcept(fields)}</td><td>{@code cut -x -f <fields>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#stats1(String, String) stats1(agg, field)}</td><td>{@code stats1 -a <agg> -f <field>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#stats1(String, String, String) stats1(agg, field, groupBy)}</td><td>{@code stats1 -a <agg> -f <field> -g <groupBy>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#splitBy(String) splitBy(field)}</td><td>{@code split -g <field>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#putQuiet(net.shed.mlrbinder.Objective) putQuiet(expr)}</td><td>{@code put -q '<expr>'}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#catNumbered()}</td><td>{@code cat -n}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#renameGlobalRegex(String) renameGlobalRegex(pattern)}</td><td>{@code rename -g -r '<pattern>'}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#putFromFile(String) putFromFile(path)}</td><td>{@code put -f <path>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#putQuietFromFile(String) putQuietFromFile(path)}</td><td>{@code put -q -f <path>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#countDistinctFields(String) countDistinctFields(fields)}</td><td>{@code count-distinct -f <fields>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#joinUnsorted(String, String) joinUnsorted(key, leftFile)}</td><td>{@code join -u -j <key> -f <leftFile>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#joinLeftRightUnpaired(String, String) joinLeftRightUnpaired(key, leftFile)}</td><td>{@code join -j <key> --ul --ur -f <leftFile>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#joinFile(String, String) joinFile(leftFile, key)}</td><td>{@code join -f <leftFile> -j <key>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#unsparsifyFillWith(String) unsparsifyFillWith(value)}</td><td>{@code unsparsify --fill-with <value>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#stepDelta(String) stepDelta(field)}</td><td>{@code step -a delta -f <field>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#uniqCountBy(String) uniqCountBy(fields)}</td><td>{@code uniq -c -g <fields>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#sampleK(int) sampleK(k)}</td><td>{@code sample -k <k>}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#filterVerbTyped(net.shed.mlrbinder.Objective) filterVerbTyped(expr)}</td><td>{@code filter -S '<expr>'}</td></tr>
 * <tr><td>{@link net.shed.mlrbinder.Mlr#sortFields(String...) sortFields(f1, f2, …)}</td><td>{@code sort -f <f1> -f <f2>} … (one {@code -f} per argument)</td></tr>
 * </table>
 */
package net.shed.mlrbinder;
