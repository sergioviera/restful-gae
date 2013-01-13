package restful.server.resource;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.appengine.api.datastore.EntityNotFoundException;

import restful.server.dao.CategoryDao;
import restful.server.domain.Category;

@Path("/categories")
public class CategoryResource {
	
	private Logger logger = Logger.getLogger(CategoryResource.class.toString());
	
	CategoryDao categoryDao;
	public CategoryResource() {
		categoryDao = new CategoryDao();
	}
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Category get(@PathParam("id") long id, @Context UriInfo uriInfo) {
		logger.info("requested uri: "+uriInfo.getAbsolutePath());
		Category retrievedCategory = null;
		try {
			retrievedCategory = categoryDao.get(id);
			logger.info("retrieved category: "+retrievedCategory.toString());
		} catch(EntityNotFoundException enfe) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return retrievedCategory;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Category> get(@Context UriInfo uriInfo) {
		logger.info("requested uri: "+uriInfo.getAbsolutePath());
		List<Category> categoryList;
		categoryList = categoryDao.getByRange(0, categoryDao.countAll());
		String strCategoryList = "";
		for(Category category : categoryList) {
			strCategoryList = category.toString() + ", " + strCategoryList;
		}
		logger.info("retrieved category list: " + strCategoryList);
		return categoryList;
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response post(Category category, @Context UriInfo uriInfo) {
		logger.info("requested uri: "+uriInfo.getAbsolutePath());
		logger.info("received category: "+category.toString());
		Category newCategory = null;
		if(categoryDao.getByField("name", category.getName()).iterator().hasNext()) {
			logger.info("category by name: "+category.getName()+" already exists");
			throw new WebApplicationException(Response.Status.CONFLICT);
		}
		
		newCategory = new Category();
		newCategory.setId(categoryDao.countAll()+1);
		newCategory.setName(category.getName());
		logger.info("persist new category: "+newCategory.toString());
		categoryDao.put(newCategory);
		return Response.created(URI.create("/categories/" + newCategory.getId())).build();		
	}
	
	@PUT
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response put(@PathParam("id") long id, Category category, @Context UriInfo uriInfo) {
		logger.info("requested uri: "+uriInfo.getAbsolutePath());
		Category retrievedCategory = null;
		try {
			retrievedCategory = categoryDao.get(id);
			logger.info("retrieved category: "+retrievedCategory.toString());
		} catch(EntityNotFoundException enfe) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		retrievedCategory.setName(category.getName());
		categoryDao.put(retrievedCategory);
		logger.info("updated category: "+retrievedCategory.toString());
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") long id, @Context UriInfo uriInfo) {
		logger.info("requested uri: "+uriInfo.getAbsolutePath());
		Category retrievedCategory = null;
		try {
			retrievedCategory = categoryDao.get(id);
			logger.info("retrieved category: "+retrievedCategory.toString());
		} catch(EntityNotFoundException enfe) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		categoryDao.delete(retrievedCategory);
		logger.info("deleted category: "+retrievedCategory.toString());
		return Response.noContent().build();
	}
}
