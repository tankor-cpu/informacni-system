package cz.vsb.is.gui;

import lombok.Getter;

@Getter
public class StudentDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String gender;
    private String phone;


    public StudentDto(Long id, String firstName, String lastName, String email, String dateOfBirth, String gender, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
    }

}
