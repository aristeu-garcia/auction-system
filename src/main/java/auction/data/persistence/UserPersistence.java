package auction.data.persistence;

import auction.data.models.User;
import java.util.ArrayList;
import java.util.List;

public class UserPersistence {
    private List<User> users;

    public UserPersistence() {
        users = new ArrayList<>();
    }

    public void save(User user) {
        users.add(user);
    }

    public User findDefaultUser() {
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    public void listAllUsers() {
        for (User user : users) {
            System.out.println(user);
        }
    }
}
