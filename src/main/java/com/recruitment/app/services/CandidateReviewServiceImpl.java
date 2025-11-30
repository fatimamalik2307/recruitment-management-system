//package com.recruitment.app.services;
//
//import com.recruitment.app.dao.*;
//import com.recruitment.app.models.FinalRankedCandidate;
//import com.recruitment.app.models.JobPosting;
//import com.recruitment.app.models.ApplicantNote;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class CandidateReviewServiceImpl implements CandidateReviewService {
//    private final FinalRankedCandidateDAO candidateDAO;
//    private final ApplicantNoteDAO noteDAO;
//    private final JobDAO jobDAO;
//
//    public CandidateReviewServiceImpl(FinalRankedCandidateDAO candidateDAO,
//                                      ApplicantNoteDAO noteDAO,
//                                      JobDAO jobDAO) {
//        this.candidateDAO = candidateDAO;
//        this.noteDAO = noteDAO;
//        this.jobDAO = jobDAO;
//    }
//
////    @Override
////    public List<FinalRankedCandidate> getCandidatesForReview(int hiringManagerId) {
////        try {
////            // Get all job postings for this hiring manager
////            return jobDAO.findByHiringManagerId(hiringManagerId)
////                    .stream()
////                    .map(JobPosting::getId) // This returns int
////                    .flatMap(jobId -> candidateDAO.findByJobPostingIdAndStatus(jobId, "SENT_TO_HM").stream())
////                    .collect(Collectors.toList());
////        } catch (Exception e) {
////            throw new RuntimeException("Failed to fetch candidates for review", e);
////        }
////    }
//
//    @Override
//    public List<FinalRankedCandidate> getCandidatesByJobForReview(int jobPostingId, int hiringManagerId) {
//        try {
//            // Verify that the job belongs to this hiring manager
//            boolean isAuthorized = jobDAO.findByHiringManagerId(hiringManagerId)
//                    .stream()
//                    .anyMatch(job -> job.getId() == jobPostingId); // Changed to int comparison
//
//            if (!isAuthorized) {
//                throw new SecurityException("Hiring manager is not authorized to access this job");
//            }
//
//            return candidateDAO.findByJobPostingIdAndStatus(jobPostingId, "SENT_TO_HM");
//        } catch (SecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch candidates by job for review", e);
//        }
//    }
//
//    @Override
//    public boolean addCandidateReviewNote(int candidateId, int hiringManagerId, String note, String noteType) {
//        try {
//            // Get candidate to verify application ID
//            FinalRankedCandidate candidate = candidateDAO.findById(candidateId)
//                    .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
//
//            ApplicantNote applicantNote = new ApplicantNote(
//                    (long) candidate.getApplicationId(),
//                    (long) hiringManagerId,
//                    note,
//                    ApplicantNote.NoteType.valueOf(noteType)
//            );
//
//            noteDAO.save(applicantNote);
//            return true;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to add candidate review note", e);
//        }
//    }
//
//    @Override
//    public List<ApplicantNote> getCandidateReviewNotes(int candidateId) {
//        try {
//            FinalRankedCandidate candidate = candidateDAO.findById(candidateId)
//                    .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
//
//            return noteDAO.findByApplicationId((long) candidate.getApplicationId());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch candidate review notes", e);
//        }
//    }
//
//    @Override
//    public boolean updateCandidateReviewStatus(int candidateId, String status) {
//        try {
//            if (!isValidReviewStatus(status)) {
//                throw new IllegalArgumentException("Invalid review status");
//            }
//
//            return candidateDAO.updateStatus(candidateId, status);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to update candidate review status", e);
//        }
//    }
//
//    private boolean isValidReviewStatus(String status) {
//        return status != null && status.matches("UNDER_REVIEW|REVIEW_COMPLETED|ON_HOLD");
//    }
//}