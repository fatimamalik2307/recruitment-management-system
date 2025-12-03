package com.recruitment.app.di;

import com.recruitment.app.services.*;


public class ServiceContainer {
    private static ServiceContainer instance;

    // Service instances with proper DAO dependencies
    private final JobService jobService;
    private final ShortlistingCriteriaService criteriaService;
    private final ShortlistService shortlistService;
    private final RecruiterService recruiterService;
    private final AssessmentService assessmentService;
    private final FinalRankingService finalRankingService;
    private final HMService hmService;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final NoteService noteService;
    private final JobDescriptionService jobDescriptionService;
    private final PersonSpecificationService personSpecificationService;
    private final RecruitmentReportService  recruitmentReportService;
    private final NotificationService notificationService;
    private ServiceContainer() {
        // Get DAO instances
        DAOCreator daoCreator = DAOCreator.getInstance();
        this.personSpecificationService = new PersonSpecificationServiceImpl(daoCreator.getPersonSpecificationDAO());
        this.jobDescriptionService=new JobDescriptionServiceImpl(daoCreator.getJobDescriptionDAO());
        this.applicationService=new ApplicationServiceImpl(daoCreator.getApplicationDAO());
        this.noteService = new NoteServiceImpl(daoCreator.getApplicantNoteDAO());
        this.recruitmentReportService = new RecruitmentReportServiceImpl();
        this.notificationService = new NotificationServiceImpl(daoCreator.getApplicationDAO());
        // Initialize services with their required DAOs
        this.jobService = new JobServiceImpl(daoCreator.getJobDAO(),daoCreator.getUserDAO());
        this.criteriaService = new ShortlistingCriteriaServiceImpl(
                daoCreator.getCriteriaDAO()
        );
        this.shortlistService = new ShortlistServiceImpl(
                daoCreator.getShortlistDAO(),
                daoCreator.getCriteriaDAO(),
                daoCreator.getApplicationDAO(),
                daoCreator.getFinalRankedCandidateDAO(),
                daoCreator.getJobDAO()
        );
        this.recruiterService = new RecruiterServiceImpl(
                daoCreator.getJobDAO(),
                daoCreator.getApplicationDAO(),
                daoCreator.getCriteriaDAO()

        );
        this.assessmentService = new AssessmentServiceImpl(
                daoCreator.getAssessmentDAO()
        );
        this.finalRankingService = new FinalRankingServiceImpl(
                daoCreator.getFinalRankedCandidateDAO(),
                daoCreator.getFinalRankingDAO(),
                daoCreator.getAssessmentDAO(),
                daoCreator.getJobDAO(),
                daoCreator.getShortlistDAO(),
                daoCreator.getUserDAO()

        );
        this.hmService = new HMServiceImpl(
                daoCreator.getFinalRankedCandidateDAO(),
                daoCreator.getApplicationDAO(),
                daoCreator.getJobDAO()
        );
        this.userService = new UserServiceImpl();
    }

    public static ServiceContainer getInstance() {
        if (instance == null) {
            instance = new ServiceContainer();
        }
        return instance;
    }

    // Getters
    public JobService getJobService() { return jobService; }
    public ShortlistingCriteriaService getCriteriaService() { return criteriaService; }
    public ShortlistService getShortlistService() { return shortlistService; }
    public RecruiterService getRecruiterService() { return recruiterService; }
    public AssessmentService getAssessmentService() { return assessmentService; }
    public FinalRankingService getFinalRankingService() { return finalRankingService; }
    public HMService getHmService() { return hmService; }
    public ApplicationService getApplicationService() { return applicationService; }
    public UserService getUserService() { return userService; }
    public NoteService getNoteService() { return noteService; }
    public JobDescriptionService getJobDescriptionService() { return jobDescriptionService; }
    public PersonSpecificationService getPersonSpecificationService() { return personSpecificationService;}
    public RecruitmentReportService   getRecruitmentReportService() { return recruitmentReportService;}
    public NotificationService getNotificationService() { return notificationService;}
}