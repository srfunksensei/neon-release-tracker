package com.mb.neonreleasetracker.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class SpecSearchCriteria {

	private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;
    
	public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value, final Optional<String> orPredicate) {
        this(key, operation, value);
        this.orPredicate = orPredicate.isPresent() && orPredicate.get().equals(SearchOperation.OR_PREDICATE_FLAG);
    }

    public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value, final String prefix, final String suffix) {
        SearchOperation op = operation;
        if (op != null) {
            if (SearchOperation.EQUALITY.equals(op)) { // the operation may be complex operation
                final boolean startWithAsterisk = StringUtils.isNotBlank(prefix) && prefix.startsWith(SearchOperation.ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = StringUtils.isNotBlank(suffix) && suffix.startsWith(SearchOperation.ZERO_OR_MORE_REGEX);

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
