//package com.recruitment.app.services;
//
//import com.recruitment.app.dao.*;
//import com.recruitment.app.models.FinalRankedCandidate;
//import com.recruitment.app.models.JobPosting;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class HMDashboardServiceImpl implements HMDashboardService {
//    private final JobDAO jobDAO;
//    private final FinalRankedCandidateDAO candidateDAO;
//    private final ApplicationDAO applicationDAO;
//
//    public HMDashboardServiceImpl(JobDAO jobDAO,
//                                  FinalRankedCandidateDAO candidateDAO,
//                                  ApplicationDAO applicationDAO) {
//        this.jobDAO = jobDAO;
//        this.candidateDAO = candidateDAO;
//        this.applicationDAO = applicationDAO;
//    }
//
////    @Override
////    public Map<String, Object> getDashboardOverview(Long hiringManagerId) {
////        try {
////            Map<String, Object> overview = new HashMap<>();
////
////            // Get active job postings
////            List<JobPosting> activeJobs = getActiveJobPostings(hiringManagerId);
////            overview.put("activeJobs", activeJobs.size());
////
////            // Get pending reviews count
////            overview.put("pendingReviews", getPendingReviewsCount(hiringManagerId));
////
////            // Get candidate pipeline
////            overview.put("candidatePipeline", getCandidatePipeline(hiringManagerId));
////
////            // Get recent activities
////            overview.put("recentActivities", getRecentActivities(hiringManagerId));
////
////            return overview;
////        } catch (Exception e) {
////            throw new RuntimeException("Failed to fetch dashboard overview", e);
////        }
////    }
//
//    @Override
//    public List<JobPosting> getActiveJobPostings(Long hiringManagerId) {
//        try {
//            return jobDAO.findByHiringManagerId(Math.toIntExact(hiringManagerId))
//                    .stream()
//                    .filter(job -> "ACTIVE".equals(job.getStatus()) || "OPEN".equals(job.getStatus()))
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch active job postings", e);
//        }
//    }
//
//    @Override
//    public Map<String, Integer> getCandidatePipeline(Long hiringManagerId) {
//        try {
//            Map<String, Integer> pipeline = new HashMap<>();
//
//            List<JobPosting> jobs = jobDAO.findByHiringManagerId(Math.toIntExact(hiringManagerId));
//            int totalCandidates = 0;
//            int pendingReview = 0;
//            int reviewed = 0;
//
//            for (JobPosting job : jobs) {
//                List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(job.getId());
//                totalCandidates += candidates.size();
//                pendingReview += candidates.stream()
//                        .filter(c -> "SENT_TO_HM".equals(c.getStatus()))
//                        .count();
//                reviewed += candidates.stream()
//                        .filter(c -> "HM_REVIEWED".equals(c.getStatus()))
//                        .count();
//            }
//
//            pipeline.put("TOTAL", totalCandidates);
//            pipeline.put("PENDING_REVIEW", pendingReview);
//            pipeline.put("REVIEWED", reviewed);
//            pipeline.put("SELECTED", 0); // You would get this from applications table
//
//            return pipeline;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch candidate pipeline", e);
//        }
//    }
//
//    @Override
//    public List<Map<String, Object>> getRecentActivities(Long hiringManagerId) {
//        try {
//            List<Map<String, Object>> activities = new ArrayList<>();
//
//            // This would typically come from an activity log table
//            // For now, return placeholder data
//            Map<String, Object> activity1 = new HashMap<>();
//            activity1.put("type", "CANDIDATE_REVIEW");
//            activity1.put("description", "Reviewed candidate John Doe");
//            activity1.put("timestamp", new Date());
//            activity1.put("jobTitle", "Senior Developer");
//            activities.add(activity1);
//
//            Map<String, Object> activity2 = new HashMap<>();
//            activity2.put("type", "DECISION_MADE");
//            activity2.put("description", "Selected candidate Jane Smith");
//            activity2.put("timestamp", new Date());
//            activity2.put("jobTitle", "Product Manager");
//            activities.add(activity2);
//
//            return activities;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch recent activities", e);
//        }
//    }
//
//    @Override
//    public int getPendingReviewsCount(Long hiringManagerId) {
//        try {
//            List<JobPosting> jobs = jobDAO.findByHiringManagerId(Math.toIntExact(hiringManagerId));
//            int pendingCount = 0;
//
//            for (JobPosting job : jobs) {
//                List<FinalRankedCandidate> pendingCandidates =
//                        candidateDAO.findByJobPostingIdAndStatus(job.getId(), "SENT_TO_HM");
//                pendingCount += pendingCandidates.size();
//            }
//
//            return pendingCount;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch pending reviews count", e);
//        }
//    }
//}