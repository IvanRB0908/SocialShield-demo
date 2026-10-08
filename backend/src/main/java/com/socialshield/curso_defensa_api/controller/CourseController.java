package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.Course;
import com.socialshield.curso_defensa_api.model.HomePageResponse;
import com.socialshield.curso_defensa_api.service.CourseService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/home")
    public HomePageResponse getHomePage() {
        return courseService.getHomePage();
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return courseService.getCourses();
    }
}
