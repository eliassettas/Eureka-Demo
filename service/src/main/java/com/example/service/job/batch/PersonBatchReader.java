package com.example.service.job.batch;

import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.service.model.Person;

@Component
public class PersonBatchReader implements ItemReader<Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonBatchReader.class);

    private List<Person> persons;

    @BeforeStep
    public void beforeStep() throws Exception {
        ClassPathResource resource = new ClassPathResource("MOCK_DATA.csv");

        try (Stream<String> lines = Files.lines(resource.getFile().toPath())) {
            persons = Collections.synchronizedList(new LinkedList<>());

            lines.skip(1).forEach(row -> {
                String[] values = row.split(",");
                Person person = new Person();
                try {
                    person.setFirstName(values[0]);
                    person.setLastName(values[1]);
                    person.setEmail(values[2]);
                    person.setGender(values[3]);
                } catch (Exception exception) {
                    LOGGER.error("Error while parsing CSV row", exception);
                }
                persons.add(person);
            });
        } catch (Exception exception) {
            throw new JobExecutionException(exception);
        }

        LOGGER.info("Read a total of {} persons", persons.size());
    }

    @Override
    public Person read() {
        return persons.isEmpty() ? null : persons.remove(0);
    }
}
