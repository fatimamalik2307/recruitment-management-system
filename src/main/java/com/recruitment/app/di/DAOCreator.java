package com.recruitment.app.di;

import com.recruitment.app.dao.*;
import com.recruitment.app.utils.DBConnection;

public class DAOCreator {
    private static DAOCreator instance;

    // DAO instances
    private final UserDAO userDAO;
    private final JobDAO jobDAO;
    private final ApplicationDAO applicationDAO;
    private final ShortlistingCriteriaDAO criteriaDAO;
    private final ShortlistDAO shortlistDAO;
    private final AssessmentResultDAO assessmentDAO;
    private final FinalRankingCriteriaDAO finalRankingDAO;
    private final FinalRankedCandidateDAO  finalRankedCandidateDAO;
    private final JobDescriptionDAO jobDescriptionDAO;
    private final PersonSpecificationDAO personSpecificationDAO;
private final ApplicantNoteDAO applicantNoteDAO;
    private DAOCreator() {
        // Initialize all DAOs (adjust constructors as needed)
        this.userDAO = new UserDAOImpl();
        this.jobDAO = new JobDAOImpl(DBConnection.getConnection());
        this.applicationDAO = new ApplicationDAOImpl(DBConnection.getConnection());
        this.criteriaDAO = new ShortlistingCriteriaDAOImpl(DBConnection.getConnection());
        this.shortlistDAO = new ShortlistDAOImpl(DBConnection.getConnection());
        this.assessmentDAO = new AssessmentResultDAOImpl(DBConnection.getConnection());
        this.finalRankingDAO = new FinalRankingCriteriaDAOImpl(DBConnection.getConnection());
        this.finalRankedCandidateDAO = new FinalRankedCandidateDAOImpl(DBConnection.getConnection());
        this.applicantNoteDAO = new ApplicantNoteDAOImpl(DBConnection.getConnection());
        this.jobDescriptionDAO = new JobDescriptionDAOImpl(DBConnection.getConnection());
        this.personSpecificationDAO = new PersonSpecificationDAOImpl(DBConnection.getConnection());
    }

    public static DAOCreator getInstance() {
        if (instance == null) {
            instance = new DAOCreator();
        }
        return instance;
    }

    // Getters for DAOs
    public UserDAO getUserDAO() { return userDAO; }
    public JobDAO getJobDAO() { return jobDAO; }
    public ApplicationDAO getApplicationDAO() { return applicationDAO; }
    public ShortlistingCriteriaDAO getCriteriaDAO() { return criteriaDAO; }
    public ShortlistDAO getShortlistDAO() { return shortlistDAO; }
    public AssessmentResultDAO getAssessmentDAO() { return assessmentDAO; }
    public FinalRankingCriteriaDAO getFinalRankingDAO() { return finalRankingDAO; }
    public  FinalRankedCandidateDAO getFinalRankedCandidateDAO() { return finalRankedCandidateDAO; }
    public ApplicantNoteDAO getApplicantNoteDAO() { return applicantNoteDAO; }
    public JobDescriptionDAO getJobDescriptionDAO() { return jobDescriptionDAO; }
    public PersonSpecificationDAO getPersonSpecificationDAO() { return personSpecificationDAO; }
}
