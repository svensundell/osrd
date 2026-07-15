use std::collections::HashMap;
use std::collections::HashSet;

use crate::generated_data::infra_error::InfraError;
use crate::infra_cache::Graph;
use crate::infra_cache::InfraCache;
use crate::infra_cache::ObjectCache;
use schemas::infra::SwitchDirection;
use schemas::primitives::Identifier;
use schemas::primitives::OSRDIdentified;
use schemas::primitives::OSRDObject;
use schemas::primitives::ObjectRef;
use schemas::primitives::ObjectType;

use super::routes::Context;

/// Track interval on a route, expressed in meters on a given track section.
#[derive(Debug, Clone, Copy)]
pub struct RouteTrackInterval {
    pub begin: f64,
    pub end: f64,
}

/// Returns whether a detector position lies on a route track interval.
///
/// Release detectors must be located on the topological path of the route they
/// help release. Endpoints are validated against the computed track ranges.
pub fn detector_on_route_interval(position: f64, interval: RouteTrackInterval) -> bool {
    (interval.begin..interval.end).contains(&position)
}

pub fn collect_route_track_intervals(
    route_path: &schemas::infra::RoutePath,
) -> HashMap<String, RouteTrackInterval> {
    route_path
        .track_ranges
        .iter()
        .map(|track| {
            (
                track.track.0.clone(),
                RouteTrackInterval {
                    begin: track.begin,
                    end: track.end,
                },
            )
        })
        .collect()
}

pub fn switches_out_of_path(
    route: &schemas::infra::Route,
    route_path: &schemas::infra::RoutePath,
) -> Vec<InfraError> {
    let switches_hashset: HashSet<Identifier> = HashSet::from_iter(
        route_path
            .switches_directions
            .iter()
            .map(|SwitchDirection { switch_id, .. }| switch_id.clone()),
    );

    let mut res = vec![];
    for switch in route.switches_directions.keys() {
        if !switches_hashset.contains(switch) {
            res.push(InfraError::new_object_out_of_path(
                route,
                format!("switches_directions.{switch}"),
                ObjectRef::new(ObjectType::Switch, switch),
            ));
        }
    }
    res
}

pub fn release_detectors_out_of_path(
    route: &schemas::infra::Route,
    infra_cache: &InfraCache,
    track_intervals: &HashMap<String, RouteTrackInterval>,
) -> Vec<InfraError> {
    let mut res = vec![];
    for (index, detector_id) in route.release_detectors.iter().enumerate() {
        let detector = infra_cache.detectors().get::<String>(detector_id).unwrap();
        let detector = detector.unwrap_detector();
        let track_interval = track_intervals.get(&detector.track);
        if let Some(track_interval) = track_interval
            && detector_on_route_interval(detector.position, *track_interval)
        {
            continue;
        }

        res.push(InfraError::new_object_out_of_path(
            route,
            format!("release_detectors.{index}"),
            detector.get_ref(),
        ));
    }
    res
}

/// Check for all routes if they have a consistent path.
/// We also retrieve track sections that are not used by any route.
pub fn check_path(
    route: &ObjectCache,
    infra_cache: &InfraCache,
    graph: &Graph,
    mut context: Context,
) -> (Vec<InfraError>, Context) {
    let route = route.unwrap_route();

    let route_path = match infra_cache.compute_track_ranges_on_route(route, graph) {
        Some(path) => path,
        None => return (vec![InfraError::new_invalid_path(route)], context),
    };

    let tracks_on_route = route_path
        .track_ranges
        .iter()
        .map(|track| (*track.track).clone());
    context.tracks_on_routes.extend(tracks_on_route);

    let track_intervals = collect_route_track_intervals(&route_path);
    let mut res = switches_out_of_path(route, &route_path);
    res.extend(release_detectors_out_of_path(
        route,
        infra_cache,
        &track_intervals,
    ));

    (res, context)
}

/// Check that all track sections are covered by a route
pub fn check_missing(
    infra_cache: &InfraCache,
    _: &Graph,
    context: Context,
) -> (Vec<InfraError>, Context) {
    let mut res = vec![];
    for track in infra_cache
        .track_sections()
        .keys()
        .filter(|e| !context.tracks_on_routes.contains(*e))
    {
        res.push(InfraError::new_missing_route(track));
    }

    (res, context)
}

#[cfg(test)]
mod tests {
    use super::RouteTrackInterval;
    use super::detector_on_route_interval;

    #[test]
    fn detector_on_interval_endpoints() {
        let interval = RouteTrackInterval { begin: 0.0, end: 100.0 };
        assert!(detector_on_route_interval(0.0, interval));
        assert!(!detector_on_route_interval(100.0, interval));
        assert!(detector_on_route_interval(50.0, interval));
    }
}
