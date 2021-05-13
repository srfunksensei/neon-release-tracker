package com.mb.neonreleasetracker.repository.specification;

import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.Release_;
import com.mb.neonreleasetracker.dto.ReleaseSearchDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ReleaseSpecificationBuilder {

    public static Specification<Release> build(final ReleaseSearchDto searchDto) {
        Specification<Release> searchSpec = GenericQuerySpecs.all();

        if (StringUtils.isNotBlank(searchDto.getTitle())) {
            searchSpec = searchSpec.and(GenericQuerySpecs.like(Release_.title, searchDto.getTitle()));
        }
        if (StringUtils.isNotBlank(searchDto.getDescription())) {
            searchSpec = searchSpec.and(GenericQuerySpecs.startsWith(Release_.description, searchDto.getDescription()));
        }

        if (searchDto.getCreatedDateAfter() != null) {
            searchSpec = searchSpec.and(GenericQuerySpecs.withDateAfter(Release_.createdDate, searchDto.getCreatedDateAfter()));
        }
        if (searchDto.getCreatedDateBefore() != null) {
            searchSpec = searchSpec.and(GenericQuerySpecs.withDateBefore(Release_.createdDate, searchDto.getCreatedDateBefore()));
        }

        if (searchDto.getUpdatedDateAfter() != null) {
            searchSpec = searchSpec.and(GenericQuerySpecs.withDateAfter(Release_.updatedDate, searchDto.getUpdatedDateAfter()));
        }
        if (searchDto.getCreatedDateBefore() != null) {
            searchSpec = searchSpec.and(GenericQuerySpecs.withDateBefore(Release_.updatedDate, searchDto.getCreatedDateBefore()));
        }

        if (searchDto.getReleaseDateAfter() != null) {
            searchSpec = searchSpec.and(GenericQuerySpecs.withDateAfter(Release_.releaseDate, searchDto.getReleaseDateAfter()));
        }
        if (searchDto.getReleaseDateBefore() != null) {
            searchSpec = searchSpec.and(GenericQuerySpecs.withDateBefore(Release_.releaseDate, searchDto.getReleaseDateBefore()));
        }

        if (searchDto.getInStatus() != null && !searchDto.getInStatus().isEmpty()) {
            searchSpec = searchSpec.and(GenericQuerySpecs.inList(Release_.status, searchDto.getInStatus()));
        }
        if (searchDto.getNotInStatus() != null && !searchDto.getNotInStatus().isEmpty()) {
            searchSpec = searchSpec.and(Specification.not(GenericQuerySpecs.inList(Release_.status, searchDto.getInStatus())));
        }

        return searchSpec;
    }
}
