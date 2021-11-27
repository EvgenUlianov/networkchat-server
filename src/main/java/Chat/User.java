package Chat;

import lombok.Data;

@Data
public class User {
    String name;
    volatile boolean isAlive;
    public static final User ADMIN;
    private static final String ADMIN_NAME = "admin";

    static {
        ADMIN = new User(ADMIN_NAME);
    }

    private User(String name) {
        this.name = name;
        isAlive = true;
    }

    public static User addUser(String userName) {
        if (ADMIN_NAME.equals(userName))
            return null;

        User user = new User(userName);
        Chat chat = Chat.get();
        chat.addMessage(User.ADMIN, String.format("К нам присоединился %s", userName));
        return user;
    }

}
