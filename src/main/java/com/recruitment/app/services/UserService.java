package com.recruitment.app.services;

import com.recruitment.app.dao.CompanyDAO;
import com.recruitment.app.dao.CompanyDAOImpl;
import com.recruitment.app.dao.UserDAO;
import com.recruitment.app.dao.UserDAOImpl;
import com.recruitment.app.models.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * UserService: business logic layer.
 * - Responsible for password hashing and business rules (SRP).
 * - Depends on DAO interfaces (DIP).
 */
public class UserService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final CompanyDAO companyDAO = new CompanyDAOImpl();

    // Password hashing parameters
    private final int BCRYPT_WORKLOAD = 12;

    /**
     * Register applicant (no company)
     */
    public boolean registerApplicant(User user) {
        // Hash password before persisting
        String hashed = hashPassword(user.getPassword());
        user.setPassword(hashed);

        // default role/status if not set
        if (user.getRole() == null) user.setRole("Applicant");
        if (user.getStatus() == null) user.setStatus("pending_verification");

        return userDAO.register(user);
    }

    /**
     * Register recruiter or hiring manager for a company.
     * Enforces one recruiter/hiring manager per company.
     */
    public RegistrationResult registerRecruiterOrManager(User user, String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return new RegistrationResult(false, "Company name is required.");
        }

        int companyId = companyDAO.getOrCreateCompanyId(companyName.trim());
        if (companyId <= 0) {
            return new RegistrationResult(false, "Unable to create/find company.");
        }

        // Enforce DB-level rule at service-level too
        if (userDAO.roleExistsForCompany(user.getRole(), companyId)) {
            return new RegistrationResult(false, user.getRole() + " already exists for this company.");
        }

        // Hash password and set company id
        String hashed = hashPassword(user.getPassword());
        user.setPassword(hashed);
        user.setCompanyId(companyId);

        if (user.getStatus() == null) user.setStatus("pending_verification");

        boolean ok = userDAO.register(user);
        return ok ? new RegistrationResult(true, "Registration successful.") :
                new RegistrationResult(false, "Registration failed due to DB error.");
    }

    /**
     * Login: verifies username and password.
     * Returns User on success (without exposing raw password) or null on failure.
     */
    public User login(String username, String rawPassword) {
        User u = userDAO.login(username, rawPassword); // DAO returns stored hashed pw
        if (u == null) return null;

        if (checkPassword(rawPassword, u.getPassword())) {
            // Clear password field before returning for safety (we don't want to leak it).
            u.setPassword(null);
            return u;
        }
        return null;
    }

    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }

    // ---------- Password helpers ----------
    private String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(BCRYPT_WORKLOAD));
    }

    private boolean checkPassword(String plain, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$") && !hashed.startsWith("$2b$") && !hashed.startsWith("$2y$")) {
            // Not a bcrypt hash - do not allow
            return false;
        }
        return BCrypt.checkpw(plain, hashed);
    }

    // Simple result wrapper for registration
    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        public RegistrationResult(boolean success, String message) {
            this.success = success; this.message = message;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    public User getUserById(int userId) {
        return userDAO.getById(userId);  // delegate to DAO
    }
}
