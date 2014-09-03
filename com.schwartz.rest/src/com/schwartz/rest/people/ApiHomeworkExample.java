package com.schwartz.rest.people;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

// using Jersey REST implementation of JAX-RS

@Path("/people")
public class ApiHomeworkExample {
	
	// An inner class to define the Person type
	class Person{
		String name;
		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
	}
	
	// An inner class to define the PhoneNumber type in the Person class
	// 	TODO:  make this variables private and add getters and setters
	class PhoneNumber{
		String type;
		String number;
	}
	
	// An array of persons to which we add persons and retrieve persons from
	private static ArrayList<Person>  personArray= new ArrayList<Person>();
	
	// Uses default Path ("/people")
	// Method to build an array of persons in JSON format
	// and then return the array of personObjects in String format
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String returnAllPeople(){
		JSONArray peopleArray = new JSONArray();
		try{
		    for ( int i = 0; i < personArray.size(); i++ ) {
			    Person person = personArray.get(i);
			    JSONObject personObject = new JSONObject();
			    personObject.put("name", person.name);
			    personObject.put("id", i);
			    peopleArray.put(personObject);
		    }
		}catch(JSONException e ) {
			System.out.println(e.getMessage());
		}
		return peopleArray.toString();
	}
	
	// Uses default Path ("/people")
	// Method to add a person to the array of persons
	// and return a person in JSON format
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createPerson(String params) {
	    try {
		    JSONObject personJSON = new JSONObject(params);
		    Person person = new Person();
		    person.name = personJSON.getString("name");
		    personArray.add(person);
		    // now add id to JSON object
		    personJSON.put("id", personArray.size() - 1);
            return personJSON.toString();
	    } catch(JSONException e) {
		    return "{ \"exception\": " + e + "}";
	    }
	}
	
	// Method to delete a person
	@DELETE @Path("/{personId}/")
	public Response deletePerson(@PathParam("personId") String personId) {
		int personIdInteger = Integer.valueOf(personId);
		try {
		   personArray.remove(personIdInteger);
		} catch (IndexOutOfBoundsException iobException) {
			return Response.noContent().status(404).build();
		}
		return Response.ok().build(); 	
	}
	
	// Utility method to return one person indicated by the subscript contained in personId
	// Called to find person to get phone number for and
	// to find the person to add a phoneNumber to
	// converts personId String to an integer subscript 
	private Person getPerson(String personId) {
		int subscript;		
		subscript = Integer.valueOf(personId); 
		Person person = personArray.get(subscript);
		return person;
	}
	
	
	// Method to add a phone number to the phone number array for the 
    // person identified by personId
	// produces JSON for Postman output
		
	@POST @Path("/{personId}/phone_numbers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//Using @PathParam annotation to bind personId to method argument
	public String createPhoneNumber(@PathParam("personId")  String personId, String params) {
		try {
			// parsing a string containing a JSON representation of a phone number 
			JSONObject phoneNumberJSON = new JSONObject(params);  
			PhoneNumber phoneNumber = phoneNumberFromJSON(phoneNumberJSON);
			Person person = getPerson(personId);
			person.phoneNumbers.add(phoneNumber);
			
			// now add person id to phoneNumberJSON object  
			phoneNumberJSON.put("personId", personId);
			
			// then add phone number's index to phoneNumberJSON object
			// re-use phoneNumberJSON because it already has almost all the
			// data we want to render in response to this request.
			phoneNumberJSON.put("id", person.phoneNumbers.size() - 1);
			
	        return phoneNumberJSON.toString();
		  } catch(JSONException e) {
			    return "{ \"exception\": " + e + "}";
		  }
		}

	private PhoneNumber phoneNumberFromJSON(JSONObject phoneNumberJSON)
			throws JSONException {
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.type = phoneNumberJSON.getString("type");
		phoneNumber.number = phoneNumberJSON.getString("number");
		return phoneNumber;
	}
	
	@PUT @Path("/{personId}/phone_numbers/{phoneNumberId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String replacePhoneNumber(@PathParam("personId")  String personId, @PathParam("phoneNumberId")  String phoneNumberId, String params) {
		try {
		    JSONObject phoneNumberJSON = new JSONObject(params);
		    PhoneNumber number = phoneNumberFromJSON(phoneNumberJSON);
		    Person person = getPerson(personId);
		    Integer index = Integer.valueOf(phoneNumberId);
		    person.phoneNumbers.set(index, number);   
		    phoneNumberJSON.put("id", index);
		    
		    return phoneNumberJSON.toString();
		} catch(JSONException jse) {
			return "{ \"exception\": " + jse + "}";
		}
		
	}
	
	// Method which returns the phone numbers for the personId and 
	// puts them in JSON format
    // returns the array of phone numbers for a person
	@GET @Path("/{personId}/phone_numbers")
		
	@Produces(MediaType.APPLICATION_JSON)
	public String getPhoneNumbers(@PathParam("personId") String personId) {
		Person person = getPerson(personId);
		JSONArray phoneNumberArray = new JSONArray();
		try{
		    for ( int i = 0; i < person.phoneNumbers.size(); i++ ) {
		    	 PhoneNumber phoneNumber = (PhoneNumber) person.phoneNumbers.get(i);
				 JSONObject phoneNumberObject = new JSONObject();
				 phoneNumberObject.put("id", i);
				 phoneNumberObject.put("type", phoneNumber.type);
				 phoneNumberObject.put("number", phoneNumber.number);
				 phoneNumberArray.put(phoneNumberObject);
		    	
		    }
		} catch (JSONException e){
		    return "{ \"exception\": " + e + "}";
		}
		
	   return phoneNumberArray.toString();	
	}
	
	// Method to delete a phone number
	@DELETE @Path("/{personId}/phone_numbers/{phoneNumberId}")
	public Response deletePhoneNumber(@PathParam("personId") String personId, @PathParam("phoneNumberId") String phoneNumberId) {
		Person person = getPerson(personId);
		int phoneNumberInt = Integer.valueOf(phoneNumberId);
		try {
		    person.phoneNumbers.remove(phoneNumberInt);
		} catch (IndexOutOfBoundsException iobException) {
		    return Response.noContent().status(404).build();
		}
		return Response.ok().build(); 
		
	}
	
    
	
}
