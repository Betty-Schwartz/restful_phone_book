package com.schwartz.rest.people;

import java.util.ArrayList;
import java.util.List;

//A class to define the Person type
	class Person{
		 // id of the Person   
	    private Long id;
	    
	    // Name of the person
		String name;
		
		// An ArrayList of PhoneNumber belonging to a Person
		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
	}