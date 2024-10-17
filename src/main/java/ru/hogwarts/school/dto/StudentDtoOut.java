package ru.hogwarts.school.dto;

public class StudentDtoOut {

    private long id;
    private String name;
    private int age;
    private FacultyDtoOut faculty;
    private AvatarDtoOut avatar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public FacultyDtoOut getFaculty() {
        return faculty;
    }

    public void setFaculty(FacultyDtoOut faculty) {
        this.faculty = faculty;
    }

    public AvatarDtoOut getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarDtoOut avatar) {
        this.avatar = avatar;
    }

}
