import java.util.HashMap;

public class Bank {
    private HashMap<String, User> users;

    public Bank() {
        users = new HashMap<>();
        // Adding some users for testing
        users.put("user1", new User("user1", "1234"));
        users.put("user2", new User("user2", "5678"));
    }

    public User authenticateUser(String userId, String userPin) {
        User user = users.get(userId);
        if (user != null && user.validatePin(userPin)) {
            return user;
        }
        return null;
    }
}
