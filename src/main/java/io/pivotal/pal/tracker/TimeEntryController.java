package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    TimeEntryRepository repo;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {
        this.repo = timeEntryRepository;
    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {

        return new ResponseEntity<>(repo.create(timeEntryToCreate), HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity read(@PathVariable long id) {

        TimeEntry match = repo.find(id);

        if(match == null) {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable long id, @RequestBody TimeEntry expected) {

        if(expected.getId() == 0 && expected.getHours() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(repo.update(id, expected), HttpStatus.OK);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {

        return new ResponseEntity<>(repo.list(), HttpStatus.OK);

    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity delete(@PathVariable long id) {

        repo.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
