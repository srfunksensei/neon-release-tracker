package com.mb.neonreleasetracker.repository.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.util.SpecSearchCriteria;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomReleaseSpecification implements Specification<Release> {

	private static final long serialVersionUID = 7012076206604215272L;

	private final SpecSearchCriteria criteria;

	@Override
	public Predicate toPredicate(Root<Release> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		switch (criteria.getOperation()) {
		case EQUALITY:
			return cb.equal(root.get(criteria.getKey()), criteria.getValue());
		case NEGATION:
			return cb.notEqual(root.get(criteria.getKey()), criteria.getValue());
		case GREATER_THAN:
			return cb.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LESS_THAN:
			return cb.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LIKE:
			return cb.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
		case STARTS_WITH:
			return cb.like(root.get(criteria.getKey()), criteria.getValue() + "%");
		case ENDS_WITH:
			return cb.like(root.get(criteria.getKey()), "%" + criteria.getValue());
		case CONTAINS:
			return cb.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
		default:
			return null;
		}
	}
}
