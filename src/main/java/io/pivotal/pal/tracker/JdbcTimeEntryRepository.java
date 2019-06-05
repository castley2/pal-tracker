package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {


    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {


        String sql = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection)
                            throws SQLException {

                        PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS);

                        statement.setLong(1, timeEntry.getProjectId());
                        statement.setLong(2, timeEntry.getUserId());
                        statement.setDate(3, Date.valueOf(timeEntry.getDate()));
                        statement.setInt(4, timeEntry.getHours());

                        return statement;
                    }
                }, generatedKeyHolder);



        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long id) {
        String sql ="SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?";
        return jdbcTemplate.query(sql
                ,
                new Object[]{id},
                extractor);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        String sql = "UPDATE time_entries " +
                "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);

        return find(id);
    }

    @Override
    public List<TimeEntry> list() {
        String sql = "SELECT id, project_id, user_id, date, hours FROM time_entries";
        return jdbcTemplate.query(sql, mapper);

    }

    @Override
    public void delete(long id) {
        String sql ="DELETE FROM time_entries WHERE id = ?";
                jdbcTemplate.update(sql, id);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );


    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;


}
