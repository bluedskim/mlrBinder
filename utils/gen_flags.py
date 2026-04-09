#!/usr/bin/env python3
"""Generate Flags.java from Miller reference-main-flag-list.md (upstream)."""
import re
import urllib.request
from pathlib import Path

URL = "https://raw.githubusercontent.com/johnkerl/miller/main/docs/src/reference-main-flag-list.md"

# Doc bullets omit "{string}" for some flags that still take a value on the CLI.
FORCE_NEEDS_ARG = frozenset(
    {
        "--gen-field-name",
        "--gen-start",
        "--gen-step",
        "--gen-stop",
        "--fail-color",
        "--help-color",
        "--key-color",
        "--pass-color",
        "--value-color",
        "--repl-ps1-color",
        "--repl-ps2-color",
        "--flatsep",
    }
)

# Miller --mfrom / --mload use varargs + "--"; use MlrBinder#mfrom / #mload instead.
SKIP_FLAGS = frozenset({"--mfrom", "--mload"})

JAVA_KEYWORDS = frozenset(
    {
        "abstract",
        "assert",
        "boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extends",
        "final",
        "finally",
        "float",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "try",
        "void",
        "volatile",
        "while",
        "record",
        "sealed",
        "permits",
        "yield",
        "var",
    }
)


def parse_spec(s: str) -> tuple[str, bool]:
    s = s.strip()
    m = re.match(r"^(.+?)\s+\{[^}]+\}\s*$", s)
    if m:
        return m.group(1).strip(), True
    return s, False


def short_flag_method(flag: str) -> str:
    return {
        "-N": "N",
        "-A": "inferIntAsFloatShort",
        "-S": "inferNoneShort",
        "-O": "inferOctalShort",
        "-I": "inPlaceShort",
        "-n": "noInput",
        "-x": "failOnErrorValue",
        "-C": "alwaysColorShort",
        "-M": "noColorShort",
        "-c": "cShort",
        "-t": "tShort",
        "-j": "jShort",
        "-p": "pShort",
        "-T": "TShort",
        "-i": "inputFormat",
        "-o": "outputFormat",
        "-s": "flagsFromFile",
    }[flag]


def long_flag_to_camel(flag: str) -> str:
    if not flag.startswith("--"):
        raise ValueError(flag)
    body = flag[2:]
    parts = body.split("-")
    name = parts[0] + "".join(p[:1].upper() + p[1:] for p in parts[1:] if p)
    if not name:
        raise ValueError(flag)
    if name in JAVA_KEYWORDS:
        name = name + "Flag"
    return name


def main() -> None:
    text = urllib.request.urlopen(URL, timeout=60).read().decode()

    items = re.findall(r"\* `([^`]+)`:", text)
    seen: dict[str, bool] = {}
    for it in items:
        it = it.strip()
        parts = re.split(r"\s+or\s+", it) if " or " in it else [it]
        for p in parts:
            p = p.strip()
            if not (p.startswith("--") or (p.startswith("-") and len(p) > 1)):
                continue
            flag, needs = parse_spec(p)
            if flag in SKIP_FLAGS:
                continue
            if flag in FORCE_NEEDS_ARG:
                needs = True
            if flag not in seen:
                seen[flag] = needs

    for f in sorted(set(re.findall(r"`(--[a-z]2[a-z])`", text))):
        if f not in seen:
            seen[f] = False

    entries: list[tuple[str, bool, str]] = []
    used: dict[str, str] = {}
    for flag, needs in sorted(seen.items(), key=lambda x: x[0]):
        if flag.startswith("-") and not flag.startswith("--"):
            m = short_flag_method(flag)
        else:
            m = long_flag_to_camel(flag)

        if m in used:
            suffix = flag.lstrip("-").replace("-", "_")
            m = f"{m}_{suffix}"
        used[m] = flag
        entries.append((flag, needs, m))

    methods = [e[2] for e in entries]
    dupes = [m for m in methods if methods.count(m) > 1]
    if dupes:
        raise SystemExit(f"duplicate method names: {dupes}")

    out = Path(__file__).resolve().parent.parent / "lib/src/main/java/net/shed/mlrbinder/Flags.java"
    lines: list[str] = [
        "package net.shed.mlrbinder;",
        "",
        "/**",
        " * Static factories for Miller <strong>global</strong> CLI flags.",
        " * <p>",
        " * Sourced from Miller upstream",
        ' * <a href="https://github.com/johnkerl/miller/blob/main/docs/src/reference-main-flag-list.md">reference-main-flag-list.md</a>.',
        " * Regenerate with {@code python3 utils/gen_flags.py}.",
        " * Use {@link #raw(String)} / {@link #raw(String, String)} for flags not yet listed or added in newer Miller releases.",
        " * Verb-local options remain on {@link net.shed.mlrbinder.verb.Option} / {@link Flag}.",
        " * </p>",
        " * <p>",
        " * Some flags must be the first tokens after {@code mlr} (e.g. {@code --cpuprofile});",
        " * add them in the order Miller expects when combining with other flags.",
        " * </p>",
        " * <p>",
        " * Miller options {@code --mfrom} and {@code --mload} take a variable argument list",
        " * terminated by {@code --}; use {@link MlrBinder#mfrom(String...)} and {@link MlrBinder#mload(String...)}.",
        " * </p>",
        " */",
        "public final class Flags {",
        "\tprivate Flags() {",
        "\t}",
        "",
        "\t/** Pass-through for a global flag with no separate value token. */",
        "\tpublic static Flag raw(String flagName) {",
        "\t\treturn new Flag(flagName);",
        "\t}",
        "",
        "\t/** Pass-through for {@code flagName} then {@code value} (two argv entries). */",
        "\tpublic static Flag raw(String flagName, String value) {",
        "\t\treturn new Flag(flagName).objective(value);",
        "\t}",
        "",
    ]

    for flag, needs_arg, meth in sorted(entries, key=lambda e: e[2]):
        esc = flag.replace("*/", "* /")
        if needs_arg:
            lines.append(f"\t/** Miller `{esc}` value. */")
            lines.append(f"\tpublic static Flag {meth}(String value) {{")
            lines.append(f'\t\treturn new Flag("{esc}").objective(value);')
            lines.append("\t}")
        else:
            lines.append(f"\t/** Miller `{esc}`. */")
            lines.append(f"\tpublic static Flag {meth}() {{")
            lines.append(f'\t\treturn new Flag("{esc}");')
            lines.append("\t}")
        lines.append("")

    lines.append("}")
    out.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {out} ({len(entries)} factories)")


if __name__ == "__main__":
    main()
