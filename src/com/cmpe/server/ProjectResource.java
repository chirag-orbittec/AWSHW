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

@Path("/project")
public class ProjectResource {

	@Path("/")
	@POST
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response createProject(@Context UriInfo uriInfo,String ProjectJSON) throws URISyntaxException, UnknownHostException
	{
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		Project Project = new Project(ProjectJSON);
		DBCollection table = db.getCollection("Project");

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", Project.getId());

		DBCursor cursor = table.find(searchQuery);

		if(cursor.hasNext())
		{
			URI location = new URI(uriInfo.getAbsolutePath() + "/" + Project.getId());
			return Response.status(Status.CONFLICT).location(location).build();
		}



		BasicDBObject document = new BasicDBObject();
		document.put("id", Project.getId());
		document.put("name", Project.getName());
		document.put("budget", Project.getBudget());
		table.insert(document);

		URI location = new URI(uriInfo.getAbsolutePath() + "/" + Project.getId());
		return Response.ok(Project.getJSON(), MediaType.APPLICATION_JSON).status(Status.CREATED).location(location).build();
	}



	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProject(@PathParam("id") int id) throws UnknownHostException
	{

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		DBCollection table = db.getCollection("Project");

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", id);

		DBCursor cursor = table.find(searchQuery);


		URI location = null;
		Project Project = null;
		if(cursor.hasNext())
		{
			Project = new Project(cursor.next().toString());
		}

		return Response.ok(Project.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).build();
	}

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getProject() throws UnknownHostException
		{

			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB("cmpe281");
			DBCollection table = db.getCollection("Project");

			BasicDBObject searchQuery = new BasicDBObject();

			DBCursor cursor = table.find();
			
			
			return Response.ok(Project.createListJSON(cursor), MediaType.APPLICATION_JSON).status(Status.OK).build();
		}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(@Context UriInfo uriInfo,@PathParam("id") int id,String ProjectJSON) throws URISyntaxException, UnknownHostException
	{
		Project Project = new Project(ProjectJSON);

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("cmpe281");
		DBCollection table = db.getCollection("Project");

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("id", id);

		DBCursor cursor = table.find(searchQuery);
		URI location = null;
		if(cursor.hasNext())
		{
			BasicDBObject newDocument = new BasicDBObject();

			if(Project.getName()!=null)
			{
				newDocument.put("name", Project.getName());
			}
			if(Project.getBudget()!=null)
			{
				newDocument.put("budget", Project.getBudget()); 
			}

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newDocument);

			table.update(searchQuery, updateObj);
			location = new URI(uriInfo.getAbsolutePath() + "/" + Project.getId()); 
			return Response.ok(Project.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).location(location).build();
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
		DBCollection table = db.getCollection("Project");
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
