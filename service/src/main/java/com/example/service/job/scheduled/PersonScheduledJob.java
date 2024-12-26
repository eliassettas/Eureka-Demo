package com.example.service.job.scheduled;

import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.service.job.QuartzScheduledJob;
import com.example.service.model.Person;
import com.example.service.repository.PersonRepository;

@Component
public class PersonScheduledJob extends QuartzScheduledJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonScheduledJob.class);

    private final PersonRepository personRepository;

    public PersonScheduledJob(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public String getJobName() {
        return "person-job";
    }

    @Override
    public String triggerCron() {
        return "0 0 0 * * ?";
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
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

        LOGGER.info("Finished job");
    }
}
