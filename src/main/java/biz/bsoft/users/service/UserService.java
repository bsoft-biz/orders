package biz.bsoft.users.service;

import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.users.model.User;
import biz.bsoft.web.dto.UserDto;

import java.util.Set;

public interface UserService {
    User findByUsername(String username);
    User getCurrentUser();
    void setUserPassword(String oldPassword, String newPassword);
    void saveUserPassword(String token, String password);

    User findUserByEmail(String email);

    void createVerificationTokenForUser(User user, String token);

    void createPasswordResetTokenForUser(User user, String token);

    Set<ClientPOS> getUserPoses(String userName);

    void checkUserPos(String userName, Integer PosId);

    User registerNewUser(UserDto userDto);
    void validateVerificationToken(String token);

    void updatePos(ClientPOS clientPOS);

    void updateUser(UserDto userDto);
}
