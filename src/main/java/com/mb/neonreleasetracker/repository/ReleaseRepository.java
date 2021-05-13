package com.mb.neonreleasetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.mb.neonreleasetracker.model.Release;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, String>, JpaSpecificationExecutor<Release> {
}
