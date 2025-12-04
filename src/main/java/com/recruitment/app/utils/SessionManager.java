package com.recruitment.app.utils;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.User;

public class SessionManager {
    public static User loggedInUser = null;
    public static JobPosting selectedJob;

    public static void logout() {
        loggedInUser = null;
    }
}
