package com.mb.neonreleasetracker.repository.specification;

import com.mb.neonreleasetracker.model.Release;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.List;

public class GenericQuerySpecs {

    public static Specification<Release> all() {
        return (Specification<Release>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static <T, E> Specification<T> equal(final SingularAttribute<T, String> attribute, final E value) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.lower(root.get(attribute)), value);
    }

    public static <T> Specification<T> startsWith(final SingularAttribute<T, String> attribute, final String value) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get(attribute)), value.toLowerCase() + "%");
    }

    public static <T> Specification<T> endsWith(final SingularAttribute<T, String> attribute, final String value) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get(attribute)), value.toLowerCase() + "%");
    }

    public static <T> Specification<T> like(final SingularAttribute<T, String> attribute, final String value) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get(attribute)), "%" + value.toLowerCase() + "%");
    }

    public static <T, E extends Enum<E>> Specification<T> inList(final SingularAttribute<T, E> attribute, final List<E> items) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In in = criteriaBuilder.in(root.get(attribute));
            for (final E item : items) {
                if (item != null) {
                    in = in.value(item);
                }
            }
            return in;
        };
    }

    public static <T> Specification<T> withDateBefore(final SingularAttribute<T, LocalDate> attribute, final LocalDate date) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get(attribute), date);
    }

    public static <T> Specification<T>  withDateAfter(final SingularAttribute<T, LocalDate> attribute, final LocalDate date) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get(attribute), date);
    }
}
