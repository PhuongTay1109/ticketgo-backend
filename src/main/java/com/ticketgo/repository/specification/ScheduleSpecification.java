package com.ticketgo.repository.specification;

import com.ticketgo.model.Route;
import com.ticketgo.model.Schedule;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ScheduleSpecification {
    public static Specification<Schedule> hasDepartureLocation(String departureLocation) {
        return ((root, query, criteriaBuilder) -> {
            Join<Schedule, Route> route = root.join("route");
            return criteriaBuilder.equal(route.get("departureLocation"), departureLocation);
        });
    }

    public static Specification<Schedule> hasArrivalLocation(String arrivalLocation) {
        return (((root, query, criteriaBuilder) -> {
            Join<Schedule, Route> route = root.join("route");
            return criteriaBuilder.equal(route.get("arrivalLocation"), arrivalLocation);
        }));
    }
}
