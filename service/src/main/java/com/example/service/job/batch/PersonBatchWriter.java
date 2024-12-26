package com.example.service.job.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.service.model.Person;
import com.example.service.repository.PersonRepository;

@Component
public class PersonBatchWriter implements ItemWriter<Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonBatchWriter.class);

    private final PersonRepository personRepository;

    public PersonBatchWriter(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void write(List<? extends Person> persons) {
        personRepository.saveAll(persons);
        LOGGER.info("Saved {} persons in the DB", persons.size());
    }
}
