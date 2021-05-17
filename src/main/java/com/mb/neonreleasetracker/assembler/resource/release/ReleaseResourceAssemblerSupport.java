package com.mb.neonreleasetracker.assembler.resource.release;

import com.mb.neonreleasetracker.controller.ReleaseController;
import com.mb.neonreleasetracker.model.Release;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ReleaseResourceAssemblerSupport extends ResourceAssemblerSupport<Release, ReleaseResource>{

	public ReleaseResourceAssemblerSupport() {
		super(ReleaseController.class, ReleaseResource.class);
	}

	@Override
	public ReleaseResource toResource(final Release entity) {
		final ReleaseResource resource = createResourceWithId(entity.getId(), entity);
		return resource.toBuilder()
				.releaseId(entity.getId())
				.title(entity.getTitle())
				.description(entity.getDescription())
				.status(entity.getStatus())
				.createdDate(entity.getCreatedDate())
				.updatedDate(entity.getUpdatedDate())
				.releaseDate(entity.getReleaseDate())
				.build();
	}
}
