package business.services;

import auction.business.services.UserService;
import auction.config.DatabaseConnection;
import auction.data.models.User;
import auction.data.persistence.UserPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTest {


    private UserService userService;
    private UserPersistence userPersistence;

    @BeforeEach
    public void setUp() throws SQLException {
        this.userPersistence = new UserPersistence();
        this.userService = new UserService(userPersistence);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        DatabaseConnection.getConnection().prepareStatement("delete from users").execute();
    }

    @Test
    public void testCreateAuction() {
        String name = "Aristeu";
        String email = "aristeu.dev@gmail.com";
        String password = "pass";
        LocalDateTime birthdate = LocalDateTime.now();

        this.userService.create(name, email, password, birthdate);

        List<User> users = this.userPersistence.listAllUsers();

        Optional<User> createdUser = users.stream()
                .filter(user -> user.getName().equals(name) && user.getEmail().equals(email))
                .findFirst();

        assertTrue(createdUser.isPresent(), "User not found");
        assertEquals(name, createdUser.get().getName());
        assertEquals(email, createdUser.get().getEmail());
    }

    @Test
    public void testUpdateUserSuccess() {
        User user = new User("John Doe", LocalDateTime.now(), "john@example.com", "password");
        User createdUser = userService.create(user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate());

        createdUser.setName("John Smith");
        createdUser.setEmail("john.smith@example.com");
        userService.update(createdUser);

        User updatedUser = userService.findById(createdUser.getId());
        assertNotNull(updatedUser);
        assertEquals("John Smith", updatedUser.getName());
        assertEquals("john.smith@example.com", updatedUser.getEmail());
    }

    @Test
    public void testFindUserById() {
        User user = new User("Alice", LocalDateTime.now(), "alice@example.com", "securepassword");
        User createdUser = userService.create(user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate());

        User foundUser = userService.findById(createdUser.getId());

        assertNotNull(foundUser, "User should be found");
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(createdUser.getName(), foundUser.getName());
        assertEquals(createdUser.getEmail(), foundUser.getEmail());
    }

    @Test
    public void testListAllUsers() {
        User user1 = new User("Bob", LocalDateTime.now(), "bob@example.com", "password123");
        User user2 = new User("Charlie", LocalDateTime.now(), "charlie@example.com", "password456");
        userService.create(user1.getName(), user1.getEmail(), user1.getPassword(), user1.getBirthdate());
        userService.create(user2.getName(), user2.getEmail(), user2.getPassword(), user2.getBirthdate());

        List<User> users = userService.listAllUsers();

        assertEquals(2, users.size(), "There should be 2 users in the list");

        assertTrue(users.stream().anyMatch(u -> u.getName().equals(user1.getName())), "User1 should be in the list");
        assertTrue(users.stream().anyMatch(u -> u.getName().equals(user2.getName())), "User2 should be in the list");
    }


}
