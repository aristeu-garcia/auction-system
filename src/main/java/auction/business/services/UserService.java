package auction.business.services;

import auction.data.models.User;
import auction.data.persistence.UserPersistence;

import java.time.LocalDateTime;

public class UserService {

    private UserPersistence userPersistence;

    public UserService(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }
    public User create(String name, String email, String password){
        User newUser = new User("DefaultUser", LocalDateTime.now(), "default@example.com", "enhaPadrao");
        this.userPersistence.save(newUser);
        return newUser;
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
}
