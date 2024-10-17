package ru.hogwarts.school.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.entity.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByAge(Integer age);

    List<Student> findAllByAgeBetween(Integer ageFrom, Integer ageTo);

    List<Student> findAllByFaculty_Id(Long facultyId);

    //    @Query(value = "SELECT COUNT(*) FROM students", nativeQuery = true)
    @Query("SELECT COUNT(s) FROM Student s")
    int countAllStudentsInTheSchool();

    //    @Query(value = "SELECT AVG(age) FROM students", nativeQuery = true)
    @Query("SELECT AVG(s.age) FROM Student s")
    double getAverageAgeOfStudents();

    @Query("SELECT s FROM Student s ORDER BY s.id DESC")
    List<Student> getLastStudents(Pageable pageable);

}
