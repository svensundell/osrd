use std::collections::HashMap;

use rangemap::RangeMap;

use schemas::infra::ApplicableDirections;
use schemas::infra::Direction;

/// Key used to index speed section ranges when checking overlaps.
#[derive(Debug, Clone, PartialEq, Eq, Hash)]
pub struct SpeedSectionOverlapKey {
    pub track_id: String,
    pub tag: Option<String>,
    pub direction: Direction,
}

/// Converts a track range to the integer range used by overlap detection.
pub fn track_range_to_overlap_range(begin: f64, end: f64) -> std::ops::Range<u64> {
    (begin * 100.) as u64..(end * 100.) as u64
}

pub fn get_directions(directions: ApplicableDirections) -> Vec<Direction> {
    match directions {
        ApplicableDirections::Both => vec![Direction::StartToStop, Direction::StopToStart],
        ApplicableDirections::StartToStop => vec![Direction::StartToStop],
        ApplicableDirections::StopToStart => vec![Direction::StopToStart],
    }
}

pub fn overlap_range_maps() -> HashMap<SpeedSectionOverlapKey, RangeMap<u64, String>> {
    HashMap::new()
}

pub fn overlap_key(
    track_id: &str,
    tag: Option<String>,
    direction: Direction,
) -> SpeedSectionOverlapKey {
    SpeedSectionOverlapKey {
        track_id: track_id.to_string(),
        tag,
        direction,
    }
}

#[cfg(test)]
mod tests {
    use super::track_range_to_overlap_range;

    #[test]
    fn overlap_range_conversion() {
        let range = track_range_to_overlap_range(1.5, 42.0);
        assert_eq!(range, 150..4200);
    }
}
