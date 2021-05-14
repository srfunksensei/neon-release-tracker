package com.mb.neonreleasetracker.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.util.SearchOperation;
import com.mb.neonreleasetracker.util.SpecSearchCriteria;

public class CustomReleaseSpecificationsBuilder {

	private final List<SpecSearchCriteria> criteria;

	private CustomReleaseSpecificationsBuilder() {
		criteria = new ArrayList<>();
	}

	public static CustomReleaseSpecificationsBuilder builder() {
		return new CustomReleaseSpecificationsBuilder();
	}

	public final CustomReleaseSpecificationsBuilder with(final SpecSearchCriteria criteria) {
		this.criteria.add(criteria);
		return this;
	}

	public final CustomReleaseSpecificationsBuilder with(final CustomReleaseSpecification spec) {
		criteria.add(spec.getCriteria());
		return this;
	}

	public final CustomReleaseSpecificationsBuilder with(final String key, final SearchOperation operation, final Object value,
														 final String prefix, final String suffix) {
		return with(key, operation, value, prefix, suffix, Optional.empty());
	}

	public final CustomReleaseSpecificationsBuilder with(final String key, final SearchOperation operation, final Object value,
														 final String prefix, final String suffix, final Optional<String> orPredicate) {
		SearchOperation op = operation;
		if (op != null) {
			if (op == SearchOperation.EQUALITY) { // the operation may be complex operation
				final boolean startWithAsterisk = StringUtils.isNotBlank(prefix) && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
				final boolean endWithAsterisk = StringUtils.isNotBlank(suffix) && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

				if (startWithAsterisk && endWithAsterisk) {
					op = SearchOperation.CONTAINS;
				} else if (startWithAsterisk) {
					op = SearchOperation.ENDS_WITH;
				} else if (endWithAsterisk) {
					op = SearchOperation.STARTS_WITH;
				}
			}
			criteria.add(new SpecSearchCriteria(key, op, value, orPredicate));
		}
		return this;
	}

	public Specification<Release> build() {
		if (criteria.size() == 0) {
			return null;
		}

		Specification<Release> result = new CustomReleaseSpecification(criteria.get(0));

		for (int i = 1; i < criteria.size(); i++) {
			result = criteria.get(i).isOrPredicate()
					? Specification.where(result).or(new CustomReleaseSpecification(criteria.get(i)))
					: Specification.where(result).and(new CustomReleaseSpecification(criteria.get(i)));
		}

		return result;
	}
}
