#!/usr/bin/env bash
# Regenerate golden expected outputs for MillerDocs617E2eTest using Miller 6.17.0.
# Usage: MLR=/path/to/mlr-6.17.0 ./utils/gen_miller_docs_617_goldens.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
RES="$ROOT/lib/src/test/resources/miller-docs-6.17"
DATA="$RES/data"
OUT="$RES/expected"
MLR="${MLR:-mlr}"
mkdir -p "$OUT"
ver="$("$MLR" --version 2>/dev/null | head -1 || true)"
echo "Using: $ver"
run() {
  local name="$1"
  shift
  "$@" >"$OUT/$name.txt"
  sed -i 's/\r$//' "$OUT/$name.txt" 2>/dev/null || true
}

run_sh() {
  local name="$1"
  shift
  bash -c "$1" >"$OUT/$name.txt"
  sed -i 's/\r$//' "$OUT/$name.txt" 2>/dev/null || true
}

# --- csv-with-and-without-headers ---
run csv_implicit_header_cat "$MLR" --csv --implicit-csv-header cat "$DATA/headerless.csv"
run csv_implicit_header_label "$MLR" --csv --implicit-csv-header cat then label name,age,status "$DATA/headerless.csv"
run dkvp_head5_headerless_csv "$MLR" --idkvp --ocsv --headerless-csv-output cat "$DATA/colored-shapes-head5.dkvp"
run nidx_cut_13_headerless "$MLR" --inidx --ifs comma --oxtab cut -f 1,3 "$DATA/headerless.csv"
run nas_hi_cat "$MLR" --csv --hi cat "$DATA/nas.csv"
run nas_inidx_ocsv_cat "$MLR" --inidx --ifs comma --ocsv cat "$DATA/nas.csv"
run nas_hi_label_chain "$MLR" --csv --hi cat then label xsn,ysn,x,y,t,a,e29,e31,e32 "$DATA/nas.csv"
run ragged_pad_put "$MLR" --from "$DATA/ragged.csv" --fs comma --nidx put '
@maxnf = max(@maxnf, NF);
while(NF < @maxnf) {
  $[NF+1] = "";
}
'

# --- shapes-of-data ---
run colours_cut_semicolon "$MLR" --csv --ifs semicolon cut -f KEY,PL,TO "$DATA/colours.csv"
run example_cut_ordered_rate_shape_flag "$MLR" --csv cut -o -f rate,shape,flag "$DATA/example.csv"
run example_filter_yellow_cat_n "$MLR" --csv filter '$color == "yellow"' then cat -n "$DATA/example.csv"
run rect_emit_put "$MLR" --from "$DATA/rect.txt" put -q '
is_present($outer) {
  unset @r
}
for (k, v in $*) {
  @r[k] = v
}
is_present($inner1) {
  emit @r
}
'

# --- operating-on-all-fields ---
run spaces_rename_g_r "$MLR" --csv rename -g -r ' ,_' "$DATA/spaces.csv"
run header_lf_normalize "$MLR" --csv --from "$DATA/header-lf.csv" put '
map inrec = $*;
$* = {};
for (oldkey, value in inrec) {
  newkey = clean_whitespace(gsub(oldkey, "\n", " "));
  $[newkey] = value;
}
'
run sar_gsub_all_fields "$MLR" --csv put -f "$DATA/sar.mlr" "$DATA/sar.csv"
run small_put_reassign "$MLR" put '
begin {
  @i_cumu = 0;
}
@i_cumu += $i;
$* = {
  "z": $x + $y,
  "KEYFIELD": $a,
  "i": @i_cumu,
  "b": $b,
  "y": $x,
  "x": $y,
};
' "$DATA/small"

# --- operating-on-all-records ---
run short_sum_put_q "$MLR" --icsv --ojson --from "$DATA/short.csv" put -q '
begin {
  @count = 0;
  @sum = 0;
}
@count += 1;
@sum += $value;
end {
  emit (@count, @sum);
}
'

# --- questions-about-then-chaining ---
run then_count_distinct "$MLR" --from "$DATA/then-example.csv" --c2p count-distinct -f Status,Payment_Type
run then_count_distinct_sort "$MLR" --from "$DATA/then-example.csv" --c2p count-distinct -f Status,Payment_Type then sort -nr count
run small_filter_then_cat_n "$MLR" filter '$x > 0.5' then cat -n "$DATA/small"

# --- questions-about-joins ---
run join_u_ipaddr "$MLR" --icsvlite --opprint join -u -j ipaddr -f "$DATA/join-u-left.csv" "$DATA/join-u-right.csv"
run join_color_ul_unsparsify "$MLR" --csv join -j color --ul --ur -f "$DATA/prevtemp.csv" then unsparsify --fill-with 0 then put '$count_delta = int($current_count) - int($previous_count)' "$DATA/currtemp.csv"
run multi_join_chain "$MLR" --icsv --opprint join -f "$DATA/multi-join/name-lookup.csv" -j id then join -f "$DATA/multi-join/status-lookup.csv" -j id "$DATA/multi-join/input.csv"

# --- date-time-examples ---
run dates_filter_strptime "$MLR" --csv filter '
strptime($date, "%Y-%m-%d") > strptime("2018-03-03", "%Y-%m-%d")
' "$DATA/dates.csv"
run_sh miss_date_step_head10 "$MLR --from \"$DATA/miss-date.csv\" --icsv cat -n then put '\$datestamp = strptime(\$date, \"%Y-%m-%d\")' then step -a delta -f datestamp | head -n 10"
run miss_date_gaps_filter "$MLR" --from "$DATA/miss-date.csv" --icsv cat -n then put '$datestamp = strptime($date, "%Y-%m-%d")' then step -a delta -f datestamp then filter '$datestamp_delta != 86400 && $n != 1'

# --- special-symbols-and-formatting ---
run commas_icsv_ojson "$MLR" --icsv --ojson cat "$DATA/commas.csv"
run commas_icsv_odkvp_ofs_pipe "$MLR" --icsv --odkvp --ofs pipe cat "$DATA/commas.csv"
run question_gsub_bracket "$MLR" --oxtab put '$c = gsub($a, "[?]"," ...")' "$DATA/question.dat"
run latin1_utf8_print "$MLR" -n put 'end {
  name = "Ka\xf0l\xedn og \xdeormundr";
  name = gssub(name, "\xde", "\u00de");
  name = gssub(name, "\xf0", "\u00f0");
  name = gssub(name, "\xed", "\u00ed");
  print name;
}'

# --- shell-commands ---
run small_system_echo "$MLR" --opprint put '$o = system("echo hello world")' "$DATA/small"

# --- data-cleaning-examples ---
run het_bool_boolean "$MLR" --icsv --opprint put '$reachable = boolean($reachable)' "$DATA/het-bool.csv"

# --- data-diving-examples (uses bundled flins subset) ---
if [[ -f "$DATA/flins-subset.csv" ]]; then
  run flins_head2_c2x "$MLR" --c2x --from "$DATA/flins-subset.csv" head -n 2
  run flins_count_distinct_county "$MLR" --c2p --from "$DATA/flins-subset.csv" count-distinct -f county
fi

# --- log-processing-examples ---
run log_stats1_mean_hit_type "$MLR" --idkvp --opprint stats1 -a mean -f hit -g type then sort -f type "$DATA/log-sample.dkvp"

# --- sql-examples ---
run medium_uniq_c_ab "$MLR" --opprint uniq -c -g a,b then sort -nr count "$DATA/medium-subset.csv"

# --- kubectl-and-helm ---
run kubectl_pods_json_head1 "$MLR" --ipprint --ojson head -n 1 "$DATA/kubectl-pods-sample.txt"

# --- dkvp-examples ---
run dkvp_stdin_one_line "$MLR" --dkvp cat "$DATA/stdin-dkvp.dkvp"
run dkvp_curly_product "$MLR" --dkvp put '${product.all} = ${x.a} * ${y:b} * ${z/c}' "$DATA/dkvp-curly-in.dkvp"

# --- statistics-examples ---
run medium_subset_stats1_iqr "$MLR" --oxtab stats1 -f x -a p25,p75 then put '$x_iqr = $x_p75 - $x_p25' "$DATA/medium-subset.csv"

# --- randomizing-examples ---
run random_sample_seed42 "$MLR" --seed 42 --from "$DATA/english-words-sample.txt" --nidx filter -S 'n=strlen($1);4<=n&&n<=8' then sample -k 10

# --- two-pass-algorithms ---
run maxrows_put_q "$MLR" --itsv --opprint put -q -f "$DATA/maxrows.mlr" "$DATA/maxrows.tsv"
run features_json_put_q "$MLR" --ijson --opprint put -q -f "$DATA/feature-count.mlr" "$DATA/features.json"

# --- programming-examples ---
run sieve_n_put "$MLR" -n put -q -f "$DATA/sieve.mlr"

echo "Wrote goldens under $OUT"
