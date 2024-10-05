package auction.data.persistence;

import auction.config.DatabaseConnection;
import auction.data.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserPersistence {

    public UserPersistence() {
    }


    public Optional<User> findById(int id) {
        String sql = "SELECT id, name, birthdate, email, password FROM users WHERE users.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    User user = new User(
                            rs.getString("name"),
                            rs.getTimestamp("birthdate").toLocalDateTime(),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    user.setId(rs.getInt("id"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find by id error: " + e.getMessage());
        }
        return Optional.empty();
    }


    public User save(User user) {
        String sql = "INSERT INTO users (name, birthdate, email, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(user.getBirthdate()));
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }

        return user;
    }



    public User findDefaultUser() {
        String sql = "SELECT id, name, birthdate, email, password FROM users LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getTimestamp("birthdate").toLocalDateTime(),
                        rs.getString("email"),
                        rs.getString("password")
                );
                user.setId(rs.getInt("id"));
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding default user: " + e.getMessage());
        }
        return null;
    }


    public List<User> listAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, birthdate, email, password FROM users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getTimestamp("birthdate").toLocalDateTime(),
                        rs.getString("email"),
                        rs.getString("password")
                );
                user.setId(rs.getInt("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing users: " + e.getMessage());
        }

        return users;
    }

    public void update(User user) {
        String sql = "UPDATE users SET name = ?, birthdate = ?, email = ?, password = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(user.getBirthdate()));
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setInt(5, user.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }
}
