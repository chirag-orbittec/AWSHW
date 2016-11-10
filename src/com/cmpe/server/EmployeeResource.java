package com.cmpe.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

@Path("/employee")
public class EmployeeResource {

	@Path("/")
	@POST
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response createEmployee(@Context UriInfo uriInfo,String employeeJSON) throws URISyntaxException, UnknownHostException
	{
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		Employee employee = new Employee(employeeJSON);
		DBCollection table = db.getCollection("Employee");

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", employee.getId());

		DBCursor cursor = table.find(searchQuery);

		if(cursor.hasNext())
		{
			URI location = new URI(uriInfo.getAbsolutePath() + "/" + employee.getId());
			return Response.status(Status.CONFLICT).location(location).build();
		}



		BasicDBObject document = new BasicDBObject();
		document.put("id", employee.getId());
		document.put("firstName", employee.getFirstName());
		document.put("lastName", employee.getLastName());
		table.insert(document);

		URI location = new URI(uriInfo.getAbsolutePath() + "/" + employee.getId());
		return Response.ok(employee.getJSON(), MediaType.APPLICATION_JSON).status(Status.CREATED).location(location).build();
	}



	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmployee(@PathParam("id") int id) throws UnknownHostException
	{

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		DBCollection table = db.getCollection("Employee");

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", id);

		DBCursor cursor = table.find(searchQuery);


		URI location = null;
		Employee employee = null;
		if(cursor.hasNext())
		{
			employee = new Employee(cursor.next().toString());
		}

		return Response.ok(employee.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).build();
	}

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getEmployee() throws UnknownHostException
		{

			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB("cmpe281");
			DBCollection table = db.getCollection("Employee");

			BasicDBObject searchQuery = new BasicDBObject();

			DBCursor cursor = table.find();
			
			
			return Response.ok(Employee.createListJSON(cursor), MediaType.APPLICATION_JSON).status(Status.OK).build();
		}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEmployee(@Context UriInfo uriInfo,@PathParam("id") int id,String employeeJSON) throws URISyntaxException, UnknownHostException
	{
		Employee employee = new Employee(employeeJSON);

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		DBCollection table = db.getCollection("Employee");

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", id);

		DBCursor cursor = table.find(searchQuery);
		URI location = null;
		if(cursor.hasNext())
		{
			BasicDBObject newDocument = new BasicDBObject();

			if(employee.getFirstName()!=null)
			{
				newDocument.put("firstName", employee.getFirstName());
			}
			if(employee.getLastName()!=null)
			{
				newDocument.put("lastName", employee.getLastName()); 
			}

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newDocument);

			table.update(searchQuery, updateObj);
			location = new URI(uriInfo.getAbsolutePath() + "/" + employee.getId()); 
			return Response.ok(employee.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).location(location).build();
		}
		else
		{
			location = new URI(uriInfo.getAbsolutePath() + "/" + id); 
			return Response.status(Status.NOT_FOUND).location(location).build();
		}
	}
	
	@DELETE 
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response deleteEmployee(@PathParam("id") int id) throws UnknownHostException {

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		DBCollection table = db.getCollection("Employee");
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", id);
		

		DBCursor cursor = table.find(searchQuery);
		if(!cursor.hasNext())
		{
			return Response.status(Status.NOT_FOUND).build();
		}

		table.remove(searchQuery);
		
		return Response.status(Status.OK).build();
	}
}
