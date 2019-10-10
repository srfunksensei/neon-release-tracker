package com.mb.neonreleasetracker.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;

import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.util.SearchOperation;
import com.mb.neonreleasetracker.util.SpecSearchCriteria;

public class ReleaseSpecificationsBuilder {

	private final List<SpecSearchCriteria> criterias;

	public ReleaseSpecificationsBuilder() {
		criterias = new ArrayList<>();
	}

	public final ReleaseSpecificationsBuilder with(final SpecSearchCriteria criteria) {
		criterias.add(criteria);
		return this;
	}

	public final ReleaseSpecificationsBuilder with(final ReleaseSpecification spec) {
		criterias.add(spec.getCriteria());
		return this;
	}

	public final ReleaseSpecificationsBuilder with(final String key, final String operation, final Object value,
			final String prefix, final String suffix) {
		return with(key, operation, value, prefix, suffix, null);
	}

	public final ReleaseSpecificationsBuilder with(final String key, final String operation, final Object value,
			final String prefix, final String suffix, final Optional<String> orPredicate) {
		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null) {
			if (op == SearchOperation.EQUALITY) { // the operation may be complex operation
				final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
				final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

				if (startWithAsterisk && endWithAsterisk) {
					op = SearchOperation.CONTAINS;
				} else if (startWithAsterisk) {
					op = SearchOperation.ENDS_WITH;
				} else if (endWithAsterisk) {
					op = SearchOperation.STARTS_WITH;
				}
			}
			criterias.add(new SpecSearchCriteria(key, op, value, orPredicate));
		}
		return this;
	}

	public Specification<Release> build() {
		if (criterias.size() == 0) {
			return null;
		}

		Specification<Release> result = new ReleaseSpecification(criterias.get(0));

		for (int i = 1; i < criterias.size(); i++) {
			result = criterias.get(i).isOrPredicate()
					? Specification.where(result).or(new ReleaseSpecification(criterias.get(i)))
					: Specification.where(result).and(new ReleaseSpecification(criterias.get(i)));
		}

		return result;
	}
}
