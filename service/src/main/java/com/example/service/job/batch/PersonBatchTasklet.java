package com.example.service.job.batch;

import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.service.model.Person;
import com.example.service.repository.PersonRepository;

@Component
public class PersonBatchTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonBatchTasklet.class);

    private final PersonRepository personRepository;

    public PersonBatchTasklet(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        ClassPathResource resource = new ClassPathResource("MOCK_DATA.csv");
        List<Person> persons = new LinkedList<>();

        try (Stream<String> lines = Files.lines(resource.getFile().toPath())) {
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

        persons.forEach(person -> {
            if (!"Male".equals(person.getGender()) && !"Female".equals(person.getGender())) {
                person.setGender("Gay as fuck");
            }
        });

        personRepository.saveAll(persons);
        LOGGER.info("Saved {} persons in the DB", persons.size());

        return RepeatStatus.FINISHED;
    }
}
