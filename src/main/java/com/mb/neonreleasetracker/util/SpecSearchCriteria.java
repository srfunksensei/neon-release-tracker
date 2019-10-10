package com.mb.neonreleasetracker.util;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpecSearchCriteria {

	private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;
    
	public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value, final Optional<String> orPredicate) {
        this(key, operation, value);
        this.orPredicate = orPredicate.isPresent() && orPredicate.get().equals(SearchOperation.OR_PREDICATE_FLAG);
    }

    public SpecSearchCriteria(final String key, final String operation, final Object value, final String prefix, final String suffix) {
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
        }
        
        this.key = key;
        this.operation = op;
        this.value = value;
    }
}
