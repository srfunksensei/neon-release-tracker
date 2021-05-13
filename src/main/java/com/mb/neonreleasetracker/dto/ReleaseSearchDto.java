package com.mb.neonreleasetracker.dto;

import com.mb.neonreleasetracker.model.ReleaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class ReleaseSearchDto {

    private final String title;
    private final String description;

    private final LocalDate createdDateBefore;
    private final LocalDate createdDateAfter;
    private final LocalDate updatedDateBefore;
    private final LocalDate updatedDateAfter;
    private final LocalDate releaseDateBefore;
    private final LocalDate releaseDateAfter;

    private final List<ReleaseStatus> inStatus;
    private final List<ReleaseStatus> notInStatus;
}
