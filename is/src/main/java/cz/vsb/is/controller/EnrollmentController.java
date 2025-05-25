package cz.vsb.is.controller;

import cz.vsb.is.entity.Course;
import cz.vsb.is.entity.Enrollment;
import cz.vsb.is.entity.Student;
import cz.vsb.is.repository.CourseRepository;
import cz.vsb.is.repository.EnrollmentRepository;
import cz.vsb.is.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @GetMapping
    public List<Map<String, Object>> getAllEnrollments() {
        return enrollmentRepository.findAll().stream().map(enrollment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", enrollment.getId());

            // Полный объект course
            Map<String, Object> course = new HashMap<>();
            course.put("id", enrollment.getCourse().getId());
            course.put("name", enrollment.getCourse().getName());
            map.put("course", course);

            // Полный объект student
            Map<String, Object> student = new HashMap<>();
            student.put("id", enrollment.getStudent().getId());
            student.put("firstName", enrollment.getStudent().getFirstName());
            student.put("lastName", enrollment.getStudent().getLastName());
            map.put("student", student);

            return map;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(@RequestBody Map<String, Long> payload) {
        Long studentId = payload.get("studentId");
        Long courseId = payload.get("courseId");

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
