package user;

import java.util.UUID;

public class User {
    private final UUID userId;

    public User(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
