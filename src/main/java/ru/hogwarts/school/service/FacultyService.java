package ru.hogwarts.school.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty create(Faculty faculty) {
        faculty.setId(null);
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public void update(long id, Faculty faculty) {
        logger.error("There is not faculty with id = " + id);
        Faculty oldFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));
        oldFaculty.setName(faculty.getName());
        oldFaculty.setColor(faculty.getColor());
        facultyRepository.save(oldFaculty);
    }

    public Faculty get(long id) {
        logger.error("There is not faculty with id = " + id);
        return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public Faculty remove(long id) {
        logger.error("There is not faculty with id = " + id);
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));
        facultyRepository.delete(faculty);
        return faculty;
    }

    public List<Faculty> filterByColor(String color) {
        logger.info("Was invoke method for filter by color");
        return facultyRepository.findAllByColor(color);
    }

    public List<Faculty> filterByNameOrColor(String nameOrColor) {
        logger.info("Was invoke method for filter by name or color");
        return facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public List<Student> findStudentsByFacultyId(long id) {
        logger.info("Was invoke method for get students by faculty id");
        return studentRepository.findAllByFaculty_Id(id);
    }

    public String findTheLongestFacultyName() {
        logger.info("Was invoke method for find the longest faculty name");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
}
