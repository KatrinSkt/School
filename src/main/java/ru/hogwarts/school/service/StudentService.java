package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student create(Student student) {
        Faculty faculty = null;
        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> new FacultyNotFoundException(student.getFaculty().getId()));
        }
        student.setFaculty(faculty);
        student.setId(null);
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public void update(long id, Student student) {
        logger.error("There is not student with id = " + id);
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        Faculty faculty = null;
        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> new FacultyNotFoundException(student.getFaculty().getId()));
        }
        oldStudent.setName(student.getName());
        oldStudent.setAge(student.getAge());
        oldStudent.setFaculty(faculty);
        studentRepository.save(oldStudent);
    }

    public Student get(long id) {
        logger.error("There is not student with id = " + id);
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student remove(long id) {
        logger.error("There is not student with id = " + id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
        return student;
    }

    public List<Student> filterByAge(int age) {
        logger.info("Was invoke method for filter students by age");
        return studentRepository.findAllByAge(age);
    }

    public List<Student> filterByRangeAge(int minAge, int maxAge) {
        logger.info("Was invoke method for filter students by range age");
        return studentRepository.findAllByAgeBetween(minAge, maxAge);
    }

    public Faculty findStudentsFaculty(long id) {
        logger.debug("Was invoke method for find students by faculty");
        logger.debug("Students faculty is {}", id);
        return get(id).getFaculty();
    }

    public long countStudents() {
        logger.info("Was invoke method for count students");
        return studentRepository.countStudents();
    }


    public double averageAge() {
        logger.info("Was invoke method for get average age");
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .summaryStatistics()
                .getAverage();
    }


    public List<Student> getLastFiveStudents() {
        logger.info("Was invoke method for get last five students");
        return studentRepository.getLastFiveStudents();
    }

    public List<String> getNameOfStudentsWhichStartsWith(char startWith) {
        logger.info("Was invoke method for get name of students which starts with char");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith(Character.toString(startWith).toUpperCase()))
                .sorted()
                .collect(Collectors.toList());
    }
}
