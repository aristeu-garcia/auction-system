package auction.business.services;

import auction.data.models.User;
import auction.data.persistence.UserPersistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserService {

    private UserPersistence userPersistence;

    public UserService(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }


    public User create(String name, String email, String password, LocalDateTime birthdate) {
        User user = new User(name, birthdate, email, password);
        User newUser = this.userPersistence.save(user);
        return newUser;
    }

    public User findById(int id){
        Optional<User> user = this.userPersistence.findById(id);
        if(user.isEmpty()){
            System.out.println("User with ID: " + id + " not found");
            return null;
        }
        return user.get();
    }
    public User createUserDefaultIfNotExists() {
        User defaultUser = userPersistence.findDefaultUser();

        if (defaultUser == null) {
            User newUser = new User("DefaultUser", LocalDateTime.now(), "default@example.com", "senhaPadrao");
            userPersistence.save(newUser);
            return newUser;
        }
        return defaultUser;
    }

    public List<User> listAllUsers (){
       return this.userPersistence.listAllUsers();
    }

    public void update(User user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }

        Optional<User> existingUser = this.userPersistence.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User with ID: " + user.getId() + " not found.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        this.userPersistence.update(user);
    }

}
