package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {
    Map<Long, Student> students = new HashMap<>();
    private long idGenerator = 1;

    public Student create(Student student) {
        student.setId(idGenerator++);
        students.put(student.getId(), student);
        return student;
    }

    public void update(long id, Student student) {
        if (!students.containsKey(id)) {
            throw new StudentNotFoundException(id);
        }
        Student oldStudent = students.get(id);
        oldStudent.setName(student.getName());
        oldStudent.setAge(student.getAge());
    }

    public Student get(long id) {
        if (!students.containsKey(id)) {
            throw new StudentNotFoundException(id);
        }
        return students.get(id);
    }

    public Student remove(long id) {
        if (!students.containsKey(id)) {
            throw new FacultyNotFoundException(id);
        }
        return students.remove(id);
    }

    public List<Student> filterByAge(int age) {
        return students.values().stream()
                .filter(student -> student.getAge() == age)
                .toList();
    }
}
