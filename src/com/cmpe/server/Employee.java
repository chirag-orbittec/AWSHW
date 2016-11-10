package com.cmpe.server;

import java.util.Iterator;

import org.json.JSONObject;

import com.mongodb.DBCursor;


public class Employee {

	int id;
	String firstName;
	String lastName;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJSON()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(" \"id\":").append(id).append(",");
		sb.append("\"firstName\":").append("\""+firstName+"\"").append(",");
		sb.append("\"lastName\":").append("\""+lastName+"\"");
		sb.append("}");
		return sb.toString();
	}

	public Employee(String json)
	{

		JSONObject jsonObject = new JSONObject(json);
		if(jsonObject.has("id"))
		{
			this.setId(jsonObject.getInt("id"));
		}
		if(jsonObject.has("firstName"))
		{
			this.setFirstName(jsonObject.getString("firstName"));
		}
		if(jsonObject.has("lastName"))
		{
			this.setLastName(jsonObject.getString("lastName"));
		}
	}
	
	public Employee() {
		// TODO Auto-generated constructor stub
	}

	public static Object createListJSON(DBCursor cursor) {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("employeeList : [");

		while(cursor.hasNext())
		{
			Employee e = new Employee(cursor.next().toString());
			sb.append("{");
			sb.append(" \"id\":").append(e.getId()).append(",");
			sb.append("\"firstName\":").append("\""+e.getFirstName()+"\"").append(",");
			sb.append("\"lastName\":").append("\""+e.getLastName()+"\"");
			sb.append("},");
		}

		sb.deleteCharAt(sb.length()-1);

		sb.append("]}");
		return sb.toString();	
	}
}
