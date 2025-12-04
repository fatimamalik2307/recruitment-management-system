package com.recruitment.app.di;

import com.recruitment.app.dao.*;
import com.recruitment.app.services.*;
import com.recruitment.app.utils.EmailService;

public class ServiceContainer {

    private static ServiceContainer instance;

    // ---- SERVICES ----
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
    private final RecruitmentReportService recruitmentReportService;
    private final NotificationService notificationService;

    private final EmailService emailService = new EmailService(); // required

    private ServiceContainer() {

        // Get DAO creator
        DAOCreator dao = DAOCreator.getInstance();

        // USER SERVICE first (required by many others)
        this.userService = new UserServiceImpl();

        // FIRST create NotificationDAO → NotificationService requires it
        NotificationDAO notificationDAO = new NotificationDAOImpl(dao.getConnection());

        this.notificationService = new NotificationServiceImpl(
                notificationDAO,
                this.userService,
                this.emailService
        );

        // OTHER SERVICES
        this.jobDescriptionService = new JobDescriptionServiceImpl(dao.getJobDescriptionDAO());
        this.personSpecificationService = new PersonSpecificationServiceImpl(dao.getPersonSpecificationDAO());
        this.applicationService = new ApplicationServiceImpl(dao.getApplicationDAO());
        this.noteService = new NoteServiceImpl(dao.getApplicantNoteDAO());
        this.recruitmentReportService = new RecruitmentReportServiceImpl();

        this.jobService = new JobServiceImpl(dao.getJobDAO(), dao.getUserDAO());

        this.criteriaService = new ShortlistingCriteriaServiceImpl(
                dao.getCriteriaDAO()
        );

        this.shortlistService = new ShortlistServiceImpl(
                dao.getShortlistDAO(),
                dao.getCriteriaDAO(),
                dao.getApplicationDAO(),
                dao.getFinalRankedCandidateDAO(),
                dao.getJobDAO()
        );

        this.recruiterService = new RecruiterServiceImpl(
                dao.getJobDAO(),
                dao.getApplicationDAO(),
                dao.getCriteriaDAO()
        );

        this.assessmentService = new AssessmentServiceImpl(
                dao.getAssessmentDAO()
        );

        this.finalRankingService = new FinalRankingServiceImpl(
                dao.getFinalRankedCandidateDAO(),
                dao.getFinalRankingDAO(),
                dao.getAssessmentDAO(),
                dao.getJobDAO(),
                dao.getShortlistDAO(),
                dao.getUserDAO()
        );

        this.hmService = new HMServiceImpl(
                dao.getFinalRankedCandidateDAO(),
                dao.getApplicationDAO(),
                dao.getJobDAO(),
                this.notificationService
        );

    }

    public static ServiceContainer getInstance() {
        if (instance == null) {
            instance = new ServiceContainer();
        }
        return instance;
    }

    // ----- GETTERS (Required by ControllerFactory) -----

    public JobService getJobService() { return jobService; }
    public ShortlistingCriteriaService getCriteriaService() { return criteriaService; }
    public ShortlistService getShortlistService() { return shortlistService; }
    public RecruiterService getRecruiterService() { return recruiterService; }
    public AssessmentService getAssessmentService() { return assessmentService; }
    public FinalRankingService getFinalRankingService() { return finalRankingService; }
    public HMService getHmService() { return hmService; }
    public UserService getUserService() { return userService; }
    public ApplicationService getApplicationService() { return applicationService; }
    public NoteService getNoteService() { return noteService; }
    public JobDescriptionService getJobDescriptionService() { return jobDescriptionService; }
    public PersonSpecificationService getPersonSpecificationService() { return personSpecificationService; }
    public RecruitmentReportService getRecruitmentReportService() { return recruitmentReportService; }
    public NotificationService getNotificationService() { return notificationService; }
}
