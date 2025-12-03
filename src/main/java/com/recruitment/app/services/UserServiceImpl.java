package com.recruitment.app.services;

import com.recruitment.app.dao.CompanyDAO;
import com.recruitment.app.dao.CompanyDAOImpl;
import com.recruitment.app.dao.UserDAO;
import com.recruitment.app.dao.UserDAOImpl;
import com.recruitment.app.models.Applicant;
import com.recruitment.app.models.HiringManager;
import com.recruitment.app.models.Recruiter;
import com.recruitment.app.models.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final CompanyDAO companyDAO = new CompanyDAOImpl();

    private static final int BCRYPT_WORKLOAD = 12;

    // ----------------- REGISTER APPLICANT -----------------
    @Override
    public RegistrationResult registerUser(User user, String companyName) {
        if (user instanceof Applicant) {
            // Applicant doesn't need a company
            user.setStatus("pending_verification");
            user.setPassword(hashPassword(user.getPassword()));
            boolean ok = userDAO.register(user);
            return ok ? new RegistrationResult(true, "Registration successful!")
                    : new RegistrationResult(false, "Database error");
        } else if (user instanceof Recruiter || user instanceof HiringManager) {
            if (companyName == null || companyName.isBlank()) {
                return new RegistrationResult(false, "Company name required.");
            }

            int companyId = companyDAO.getOrCreateCompanyId(companyName.trim());

            if (companyId <= 0) {
                return new RegistrationResult(false, "Company not found.");
            }

            if (userDAO.roleExistsForCompany(user.getRole(), companyId)) {
                return new RegistrationResult(false, user.getRole() + " already exists.");
            }

            user.setCompanyId(companyId);
            user.setStatus("pending_verification");
            user.setPassword(hashPassword(user.getPassword()));

            boolean ok = userDAO.register(user);
            return ok ? new RegistrationResult(true, "Registration successful.")
                    : new RegistrationResult(false, "Database error");
        } else {
            return new RegistrationResult(false, "Unknown user type");
        }
    }


    // ----------------- LOGIN -----------------
    @Override
    public User login(String username, String rawPassword) {
        User u = userDAO.login(username, rawPassword);
        if (u == null) return null;

        if (!checkPassword(rawPassword, u.getPassword())) return null;
        return u; // DO NOT REMOVE PASSWORD
    }


    // ----------------- UPDATE PROFILE -----------------
    @Override
    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }

    // ----------------- UPDATE PASSWORD -----------------
    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String hashed = hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashed);
    }

    // ----------------- HASHING -----------------
    public String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(BCRYPT_WORKLOAD));
    }

    // ----------------- CHECK PASSWORD -----------------
    @Override
    public boolean checkPassword(String plain, String hashed) {
        if (hashed == null || !hashed.startsWith("$2")) return false;
        return BCrypt.checkpw(plain, hashed);
    }

    // ----------------- GET USER BY ID -----------------
    @Override
    public User getUserById(int id) {
        return userDAO.getById(id);
    }
}
