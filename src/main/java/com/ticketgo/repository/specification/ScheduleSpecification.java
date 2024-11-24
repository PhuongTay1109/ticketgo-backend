package com.ticketgo.repository.specification;

import com.ticketgo.model.Route;
import com.ticketgo.model.Schedule;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ScheduleSpecification {
    public static Specification<Schedule> hasVisibility() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isVisible"));
    }

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

    public static Specification<Schedule> hasDepartureDate(LocalDate departureDate) {
        return (root, query, criteriaBuilder) -> {
            if (departureDate == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDateTime startOfDay = departureDate.atStartOfDay();
            LocalDateTime endOfDay = departureDate.atTime(23, 59, 59);
            return criteriaBuilder.between(root.get("departureTime"), startOfDay, endOfDay);
        };
    }


    public static Specification<Schedule> withSorting(String sortBy, String sortDirection) {
        return (root, query, criteriaBuilder) -> {
            if (sortBy == null || sortDirection == null) {
                return criteriaBuilder.conjunction();
            }

            if ("price".equalsIgnoreCase(sortBy)) {
                query.orderBy("asc".equalsIgnoreCase(sortDirection)
                        ? criteriaBuilder.asc(root.get("price"))
                        : criteriaBuilder.desc(root.get("price")));
            } else if ("departureDate".equalsIgnoreCase(sortBy)) {
                query.orderBy("asc".equalsIgnoreCase(sortDirection)
                        ? criteriaBuilder.asc(root.get("departureTime"))
                        : criteriaBuilder.desc(root.get("departureTime")));
            }
            return criteriaBuilder.conjunction();
        };
    }

}
