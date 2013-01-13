package restful.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import restful.server.dao.CategoryDao;
import restful.server.domain.Category;
import restful.server.resource.CategoryResource;
import restful.server.resource.HelloResource;

public class RestfulApplication extends Application{
	 
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	public RestfulApplication() {
		
		CategoryDao categoryDao = new CategoryDao();
		if(categoryDao.countAll() <= 0) {
			Category category = null;
			for(int i = 1; i < 100; i++) {
				category = new Category();
				category.setId(i);
				category.setName("Category "+i);
				categoryDao.put(category);
			}
		}
		
		singletons.add(new HelloResource());
		singletons.add(new CategoryResource());
		
	}
	
	/*
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resourceSet = new HashSet<Class<?>>();
        resourceSet.add(HelloResource.class);
        //resourceSet.add(CategoryResource.class);
        return resourceSet;
    }
    */
    
    @Override
    public Set<Class<?>> getClasses() {
    	return empty;
    }
    
    @Override
    public Set<Object> getSingletons() {
    	return singletons;
    }
    
}
