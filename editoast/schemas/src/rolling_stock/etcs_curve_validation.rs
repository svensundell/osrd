//! Validation helpers for ETCS braking curve payloads.
//!
//! Curves follow the speed-interval representation described in ERA Subset-026.

/// Returns true when the number of interval values matches the number of boundaries.
pub fn curve_length_is_consistent(boundary_count: usize, value_count: usize) -> bool {
    value_count == boundary_count + 1
}

/// Returns true when all speeds are non-negative.
pub fn speeds_are_non_negative(boundaries: &[f64]) -> bool {
    boundaries.iter().all(|speed| *speed >= 0.0)
}

/// Returns true when all interval values are non-negative.
pub fn values_are_non_negative(values: &[f64]) -> bool {
    values.iter().all(|value| *value >= 0.0)
}

#[cfg(test)]
mod tests {
    use super::curve_length_is_consistent;

    #[test]
    fn consistent_curve_lengths() {
        assert!(curve_length_is_consistent(2, 3));
        assert!(!curve_length_is_consistent(2, 2));
    }
}
