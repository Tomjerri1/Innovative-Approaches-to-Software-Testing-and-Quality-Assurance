#!/usr/bin/env bash
# Reproducible benchmark of v0..v3. Run from the project root (Lab3/):  bash bench/run_bench.sh
# Needs only a JDK (javac/java) and python3 (for medians).
set -euo pipefail
export JAVA_TOOL_OPTIONS=
FILE=${1:-src/txt/harry.txt}
OUT=$(mktemp -d)
javac -nowarn -d "$OUT" src/sorcerstone/*.java bench/v0/*.java bench/v1/*.java bench/v2/*.java bench/harness/Bench.java 2>/dev/null

field() { grep "$1" | sed 's/.*: *//'; }
median() { python3 -c 'import sys,statistics as s;print(f"{s.median(map(float,sys.stdin.read().split())):.2f}")'; }

echo "version | cold start, ms (median of fresh JVMs) | warm avg, ms (median of 3 JVMs x 200 runs) | allocated, MB"
for v in v0 v1 v2 v3; do
  if [ "$v" = v0 ]; then COLD_N=5; else COLD_N=15; fi
  cold=$(for i in $(seq $COLD_N); do java -cp "$OUT" sorcerstone.Bench $v "$FILE" 0 0 | field 'Cold start'; done | median)
  alloc=$(java -cp "$OUT" sorcerstone.Bench $v "$FILE" 0 0 | field 'Allocated')
  if [ "$v" = v0 ]; then warm="n/a (>2 s per run)"; else
    warm=$(for i in 1 2 3; do java -cp "$OUT" sorcerstone.Bench $v "$FILE" 30 200 | field 'Warm average'; done | median)
  fi
  echo "$v | $cold | $warm | $alloc"
done
