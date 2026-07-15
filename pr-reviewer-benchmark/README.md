# PR reviewer specification corpus

This folder contains copies of internal specification and requirement documents used as reference material for automated pull request review.

## Corpus

All source documents are under **`corpus/`**, with paths mirroring their location in the repository.

Topics covered:

1. RailJSON infra schema and infra validation rules
2. SNCF signaling drivers and transitions
3. Speed limit tags and resolution
4. STDCM / last-minute requests
5. Driver behaviour and spacing requirements
6. ETCS braking parameters and curves

External reference for ETCS braking: [ERA ETCS Subset-026 v4.0.0](https://www.era.europa.eu/system/files/2023-09/index004_-_SUBSET-026_v400.zip)

## Copilot integration

GitHub Copilot Code Review is configured to use these documents via `.github/copilot-instructions.md`.
