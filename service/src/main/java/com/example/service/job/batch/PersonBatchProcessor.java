package com.example.service.job.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.service.model.Person;

@Component
public class PersonBatchProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) {
        if (!"Male".equals(person.getGender()) && !"Female".equals(person.getGender())) {
            person.setGender("Gay as fuck");
        }
        return person;
    }
}
