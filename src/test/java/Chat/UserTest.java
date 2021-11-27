package Chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void addUserAdmin() {

        String userName = "admin";
        User user = User.addUser(userName);
        assertNull(user);

    }

    @Test
    void addUser() {

        String userName = "userName";
        User user = User.addUser(userName);
        assertEquals(userName, user.getName());

    }
}