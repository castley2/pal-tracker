package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    Map<Long, TimeEntry> repo;
    private long id;

    public InMemoryTimeEntryRepository() {
        repo = new HashMap<>();
        this.id = 0;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        this.id++;

        timeEntry.setId(this.id);

        repo.put(this.id, timeEntry);

        return timeEntry;
    }

    @Override
    public TimeEntry find(Long id) {
        return repo.get(id);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {


        if(repo.get(id) == null) {
            return null;
        }

        timeEntry.setId(id);
        repo.put(id, timeEntry);

        return repo.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(repo.values());
    }

    @Override
    public void delete(long id) {
        repo.remove(id);
    }
}
