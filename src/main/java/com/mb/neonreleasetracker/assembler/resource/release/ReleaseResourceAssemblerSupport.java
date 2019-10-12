package com.mb.neonreleasetracker.assembler.resource.release;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.mb.neonreleasetracker.controller.ReleaseController;
import com.mb.neonreleasetracker.model.Release;

@Component
public class ReleaseResourceAssemblerSupport extends ResourceAssemblerSupport<Release, ReleaseResource>{

	public ReleaseResourceAssemblerSupport() {
		super(ReleaseController.class, ReleaseResource.class);
	}

	@Override
	public ReleaseResource toResource(Release entity) {
		ReleaseResource resource = createResourceWithId(entity.getId(), entity);
		resource.setReleaseId(entity.getId());
		resource.setTitle(entity.getTitle());
		resource.setDescription(entity.getDescription());
		resource.setStatus(entity.getStatus());
		resource.setCreatedDate(entity.getCreatedDate());
		resource.setUpdatedDate(entity.getUpdatedDate());
		resource.setReleaseDate(entity.getReleaseDate());
		return resource;
	}
}
