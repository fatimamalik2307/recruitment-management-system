package com.recruitment.app.models;

/**
 * Factory pattern for creating User instances.
 * Keeps controller/service code simpler and open for extension.
 */
public final class UserFactory {

    private UserFactory() {} // static factory only

    public static User createApplicant(String fullName, String email, String username,
                                       String rawPassword, String contact) {
        return new User(fullName, email, username, rawPassword, contact);
    }

    public static User createRecruiterOrManager(String fullName, String email, String username,
                                                String rawPassword, String contact,
                                                String role, Integer companyId) {
        return new User(fullName, email, username, rawPassword, contact, role, companyId);
    }
}
