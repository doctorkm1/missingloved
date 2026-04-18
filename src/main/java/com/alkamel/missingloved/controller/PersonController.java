package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Person;
import com.alkamel.missingloved.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person) {
        return personService.savePerson(person);
    }
}