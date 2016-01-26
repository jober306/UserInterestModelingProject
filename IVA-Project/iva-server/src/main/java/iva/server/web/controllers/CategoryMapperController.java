package iva.server.web.controllers;

import iva.server.categoryextractor.services.CategoryMapperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/interestModels/mapToCategory")
public class CategoryMapperController {

	private final CategoryMapperService service;
	
	@Autowired
	public CategoryMapperController(CategoryMapperService service) {
		this.service = service;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public String mapToCategory(@RequestParam String term) {
		return service.mapToCategory(term).orElse(null);
	}

}
