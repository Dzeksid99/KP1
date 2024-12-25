package user;

import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createNewUser() {
        return userRepository.createUser();
    }

    public boolean userExists(UUID userId) {
        return (userRepository.findUser(userId) != null);
    }
}
