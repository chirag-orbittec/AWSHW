package com.cmpe.server;

import java.util.Iterator;

import org.json.JSONObject;

import com.mongodb.DBCursor;


public class Project {
	
	int id;
	String name;
	Float budget;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getBudget() {
		return budget;
	}
	public void setBudget(Float budget) {
		this.budget = budget;
	}
	
	public String getJSON()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(" \"id\":").append(id).append(",");
		sb.append("\"name\":").append("\""+name+"\"").append(",");
		sb.append("\"budget\":").append("\""+budget+"\"");
		sb.append("}");
		return sb.toString();
	}

	public Project(String json)
	{

		JSONObject jsonObject = new JSONObject(json);
		if(jsonObject.has("id"))
		{
			this.setId(jsonObject.getInt("id"));
		}
		if(jsonObject.has("name"))
		{
			this.setName(jsonObject.getString("name"));
		}
		if(jsonObject.has("budget"))
		{
			this.setBudget(((Number)jsonObject.get("budget")).floatValue());
		}
	}
	public Project() {
		// TODO Auto-generated constructor stub
	}

	public static Object createListJSON(DBCursor cursor) {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("ProjectList : [");

		while(cursor.hasNext())
		{
			Project e = new Project(cursor.next().toString());
			sb.append("{");
			sb.append(" \"id\":").append(e.getId()).append(",");
			sb.append("\"name\":").append("\""+e.getName()+"\"").append(",");
			sb.append("\"budget\":").append("\""+e.getBudget()+"\"");
			sb.append("},");
		}

		sb.deleteCharAt(sb.length()-1);

		sb.append("]}");
		return sb.toString();	
	}

}
