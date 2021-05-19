package com.mb.neonreleasetracker.assembler.resource.release;

import com.mb.neonreleasetracker.controller.ReleaseController;
import com.mb.neonreleasetracker.model.Release;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ReleaseResourceAssemblerSupport extends RepresentationModelAssemblerSupport<Release, ReleaseModel> {

	public ReleaseResourceAssemblerSupport() {
		super(ReleaseController.class, ReleaseModel.class);
	}

	@Override
	public ReleaseModel toModel(final Release entity) {
		final ReleaseModel resource = createModelWithId(entity.getId(), entity);
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
