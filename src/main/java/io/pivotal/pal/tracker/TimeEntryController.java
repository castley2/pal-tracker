package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    TimeEntryRepository repo;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    public TimeEntryController(TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry) {
        this.repo = timeEntryRepository;

        this.timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        this.actionCounter = meterRegistry.counter("timeEntry.actionCounter");

    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {


        ResponseEntity<TimeEntry> entity = new ResponseEntity<>(repo.create(timeEntryToCreate), HttpStatus.CREATED);

        actionCounter.increment();
        timeEntrySummary.record(repo.list().size());

        return entity;
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity read(@PathVariable long id) {

        TimeEntry match = repo.find(id);

        if(match == null) {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        actionCounter.increment();
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable long id, @RequestBody TimeEntry expected) {

        if(expected.getId() == 0 && expected.getHours() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        actionCounter.increment();
        return new ResponseEntity<>(repo.update(id, expected), HttpStatus.OK);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {

        actionCounter.increment();

        return new ResponseEntity<>(repo.list(), HttpStatus.OK);

    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity delete(@PathVariable long id) {

        repo.delete(id);

        actionCounter.increment();
        timeEntrySummary.record(repo.list().size());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
