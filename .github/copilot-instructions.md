# Copilot Code Review Instructions

When reviewing pull requests, evaluate whether the changes may violate or negatively impact documented requirements, expected behaviors, constraints, invariants, or critical system properties relevant to this repository.

Use the source documents located in:

**`pr-reviewer-benchmark/corpus/`**

All specification files are in this single folder (flat layout). See `pr-reviewer-benchmark/README.md` for the mapping between corpus filenames and their original repository paths.

For each relevant issue:

* cite the source document;
* cite the most precise section, requirement, or heading you can identify;
* cite the impacted file and line;
* explain why the PR introduces a negative impact;
* suggest a concrete fix.

Do not invent requirement IDs, clause numbers, or references.

If you cannot identify an exact requirement or section from the documents, say so explicitly.

Avoid generic code quality comments unless they directly affect a documented requirement, expected behavior, critical constraint, or important system property.

Prefer fewer, high-confidence findings over speculative ones.
