package com.alkamel.missingloved.service;

import com.alkamel.missingloved.model.Person;
import com.alkamel.missingloved.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Get all persons
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    // Get person by ID
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    // Save a person
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    // Delete a person
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}

