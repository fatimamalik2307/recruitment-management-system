
package com.recruitment.app.services;

import com.recruitment.app.models.User;

public interface UserService {

    // ----------------- REGISTER APPLICANT -----------------
    RegistrationResult registerUser(User user, String companyName);

    User login(String username, String rawPassword);

    boolean updateProfile(User user);

    boolean updatePassword(int userId, String newPassword);

    User getUserById(int userId);

    boolean checkPassword(String oldPlain, String password);

    String hashPassword(String newPass);

    class RegistrationResult {
        private final boolean success;
        private final String message;
        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
