# PR reviewer specification corpus

Flat copies of internal specification and requirement documents for automated pull request review.

All files are in **`corpus/`** (single folder, no subdirectories).

## File index

| Corpus file | Original repository path |
|-------------|-------------------------|
| `osrd_schemas_infra.py` | `osrd_schemas/osrd_schemas/infra.py` |
| `osrd_schemas_infra_editor.py` | `osrd_schemas/osrd_schemas/infra_editor.py` |
| `osrd_schemas_switch_type.py` | `osrd_schemas/osrd_schemas/switch_type.py` |
| `editoast_error_routes.rs` | `editoast/src/generated_data/error/routes.rs` |
| `editoast_error_speed_sections.rs` | `editoast/src/generated_data/error/speed_sections.rs` |
| `editoast_error_electrifications.rs` | `editoast/src/generated_data/error/electrifications.rs` |
| `editoast_error_switches.rs` | `editoast/src/generated_data/error/switches.rs` |
| `editoast_error_signals.rs` | `editoast/src/generated_data/error/signals.rs` |
| `signaling_BAL.kt` | `core/kt-osrd-sncf-signaling/.../bal/BAL.kt` |
| `signaling_BAPR.kt` | `core/kt-osrd-sncf-signaling/.../bapr/BAPR.kt` |
| `signaling_TVM300.kt` | `core/kt-osrd-sncf-signaling/.../tvm300/TVM300.kt` |
| `signaling_TVM430.kt` | `core/kt-osrd-sncf-signaling/.../tvm430/TVM430.kt` |
| `signaling_BALtoTVM300.kt` | `core/kt-osrd-sncf-signaling/.../bal/BALtoTVM300.kt` |
| `speed_limit_tags.yml` | `assets/static_resources/speed_limit_tags.yml` |
| `sim_infra_RawInfraImpl.kt` | `core/kt-osrd-sim-infra/.../RawInfraImpl.kt` |
| `stdcm_debugging_tips.md` | `core/stdcm_debugging_tips.md` |
| `stdcm_STDCMEndpoint.kt` | `core/src/main/kotlin/fr/sncf/osrd/api/stdcm/STDCMEndpoint.kt` |
| `stdcm_search_environment.rs` | `editoast/editoast_models/src/stdcm_search_environment.rs` |
| `sim_DriverBehaviour.kt` | `core/envelope-sim/.../DriverBehaviour.kt` |
| `conflicts_SpacingResourceGenerator.kt` | `core/src/main/kotlin/fr/sncf/osrd/conflicts/SpacingResourceGenerator.kt` |
| `sim_ScheduleMetadataExtractor.kt` | `core/src/main/kotlin/fr/sncf/osrd/standalone_sim/ScheduleMetadataExtractor.kt` |
| `rolling_stock_etcs_brake_params.rs` | `editoast/schemas/src/rolling_stock/etcs_brake_params.rs` |
| `etcs_ETCSBrakingCurves.kt` | `core/envelope-sim/.../etcs/ETCSBrakingCurves.kt` |
| `etcs_ETCSBrakingSimulator.kt` | `core/envelope-sim/.../etcs/ETCSBrakingSimulator.kt` |

External reference for ETCS braking: [ERA ETCS Subset-026 v4.0.0](https://www.era.europa.eu/system/files/2023-09/index004_-_SUBSET-026_v400.zip)

## Copilot integration

Configured via `.github/copilot-instructions.md`.
