package restful.server.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import restful.server.domain.Category;

@Path("/hello")
public class HelloResource {
	
	public HelloResource() {}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response sayHello() {
		String output = "Say hello world via response";
		ResponseBuilder responseBuilder = Response.ok(output);
		responseBuilder.language("fr")
					   .header("varmansvn", "a man of nowhere");
		return responseBuilder.build();
	}
	
	@GET
	@Path("{id}")
	@Produces({"application/xml","application/json"})
	public Category get(@PathParam("id") String id) {
		Category category = new Category();
		category.setId(Long.parseLong(id));
		category.setName("Tablet");
		return category;
	}

	
} 