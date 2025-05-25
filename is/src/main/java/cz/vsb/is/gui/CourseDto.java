package cz.vsb.is.gui;

import lombok.Getter;

@Getter
public class CourseDto {
    private Long id;
    private String name;
    private String code;
    private int credits;

    public CourseDto(Long id, String name, String code, int credits) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credits = credits;
    }

}
