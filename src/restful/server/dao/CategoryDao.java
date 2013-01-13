package restful.server.dao;

import restful.server.domain.Category;

import com.googlecode.objectify.ObjectifyService;

public class CategoryDao extends ObjectifyDao<Category>{
	
	  static
	  {
	    ObjectifyService.register(Category.class);
	  }

	  public CategoryDao() {
		  
		  super(Category.class);
	  
	  }
}
