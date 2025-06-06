package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String CREATE_USER = """

            INSERT INTO users(name, email) VALUES (?, ?)""";
    private static final String UPDATE_USER = """

            UPDATE users SET name = ?, email = ? WHERE user_id = ?""";
    private static final String FIND_ONE_USER = """

            SELECT * FROM users WHERE user_id = ?""";
    private static final String DELETE = """

            DELETE FROM users WHERE user_id = ?""";

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(FIND_ALL, userRowMapper);
    }

    @Override
    public User save(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_USER, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId == null) {
            throw new InternalServerException("Не удалось сохранить пользователя");
        }
        user.setId(generatedId);
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(UPDATE_USER, user.getName(), user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        try {
            User user = jdbcTemplate.queryForObject(FIND_ONE_USER, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(DELETE, id);
    }
}