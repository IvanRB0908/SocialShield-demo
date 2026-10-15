package com.socialshield.curso_defensa_api.model;

import java.util.List;

public record UserProgressResponse(
    String userId,
    int enrolledCourses,
    int completedCourses,
    double averageProgress,
    List<EnrollmentResponse> enrollments
) {
}
