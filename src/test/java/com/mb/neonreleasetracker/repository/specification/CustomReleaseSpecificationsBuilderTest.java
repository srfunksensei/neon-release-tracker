package com.mb.neonreleasetracker.repository.specification;

import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.util.SearchOperation;
import com.mb.neonreleasetracker.util.SpecSearchCriteria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

public class CustomReleaseSpecificationsBuilderTest {

    @Test
    public void with_noSpecs() {
        final Specification<Release> spec = CustomReleaseSpecificationsBuilder.builder().build();
        Assertions.assertNull(spec);
    }

    @Test
    public void with_customReleaseSpecification() {
        final SpecSearchCriteria criteria = new SpecSearchCriteria("status", SearchOperation.EQUALITY, ReleaseStatus.ON_DEV.toString());
        final CustomReleaseSpecification customReleaseSpecification = new CustomReleaseSpecification(criteria);

        final Specification<Release> spec = CustomReleaseSpecificationsBuilder.builder().with(customReleaseSpecification).build();
        Assertions.assertNotNull(spec);
    }

    @Test
    public void with_specSearchCriteria() {
        final SpecSearchCriteria criteria = new SpecSearchCriteria("status", SearchOperation.EQUALITY, ReleaseStatus.ON_DEV.toString());

        final Specification<Release> spec = CustomReleaseSpecificationsBuilder.builder().with(criteria).build();
        Assertions.assertNotNull(spec);
    }

    @Test
    public void with_specSearchCriteria_prefix_suffix() {
        final SpecSearchCriteria criteria = new SpecSearchCriteria("title", SearchOperation.EQUALITY, "v.xx", "*", null);

        final Specification<Release> spec = CustomReleaseSpecificationsBuilder.builder().with(criteria).build();
        Assertions.assertNotNull(spec);
    }

    @Test
    public void with_prefix_suffix() {
        final Specification<Release> spec = CustomReleaseSpecificationsBuilder.builder().with("title", SearchOperation.EQUALITY, "v.xx", null, null).build();
        Assertions.assertNotNull(spec);
    }
}
