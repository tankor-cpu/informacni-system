package cz.vsb.is.gui;

import lombok.Getter;

@Getter
public class EnrollmentDto {
    private Long id;
    private String courseName;
    private Long studentId;
    private String studentName;

    public EnrollmentDto(Long id, String courseName, Long studentId, String studentName) {
        this.id = id;
        this.courseName = courseName;
        this.studentId = studentId;
        this.studentName = studentName;
    }
}
