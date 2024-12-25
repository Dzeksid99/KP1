package user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRepository {
    private final Map<UUID, User> storage = new HashMap<>();

    public User createUser() {
        UUID newId = UUID.randomUUID();
        User user = new User(newId);
        storage.put(newId, user);
        return user;
    }

    public User findUser(UUID userId) {
        return storage.get(userId);
    }
}
