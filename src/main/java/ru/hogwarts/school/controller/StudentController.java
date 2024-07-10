package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return studentService.create(student);
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable long id) {
        return studentService.get(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Student student) {
        studentService.update(id, student);
    }
    @DeleteMapping("/{id}")
    public Student remove(@PathVariable long id){
        return studentService.remove(id);
    }
//    @GetMapping
//    public Student
//    //1. Добавить фильтрацию студентов по возрасту.
    //
    //Для этого в StudentController добавить эндпоинт, который принимает число (возраст — поле age)
    // и возвращает список студентов, у которых совпал возраст с переданным числом.

}
