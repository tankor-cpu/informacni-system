package cz.vsb.is.controller;

import cz.vsb.is.entity.Course;
import cz.vsb.is.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updated) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setName(updated.getName());
                    course.setCode(updated.getCode());
                    course.setCredits(updated.getCredits());
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Kurz s ID " + id + " nebyl nalezen"));
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
}
