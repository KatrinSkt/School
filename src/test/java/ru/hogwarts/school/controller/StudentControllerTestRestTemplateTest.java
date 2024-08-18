package ru.hogwarts.school.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentController studentController;
    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();
    private final List<Student> students = new ArrayList<>(10);


    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        Faculty faculty1 = createFaculty();
        Faculty faculty2 = createFaculty();

        createStudents(faculty1, faculty2);
    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return facultyRepository.save(faculty);
    }

    private void createStudents(Faculty... faculties) {
        students.clear();
        Stream.of(faculties)
                .forEach(faculty ->
                        students.addAll(
                                studentRepository.saveAll(Stream.generate(() -> {
                                            Student student = new Student();
                                            student.setFaculty(faculty);
                                            student.setName(faker.harryPotter().character());
                                            student.setAge(faker.random().nextInt(11, 18));
                                            return student;
                                        })
                                        .limit(5)
                                        .collect(Collectors.toList()))
                        )
                );
    }

    private String buildUrl(String UrlStartsWithSlash) {
        return "http://localhost:%d%s".formatted(port, UrlStartsWithSlash);
    }

    @Test
    void contextLoads() throws Exception {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void getStudentTest() throws Exception {
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/student", String.class))
                .isNotNull();
    }

    @Test
    public void createStudentWithoutFacultyPositive() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));

        createStudentPositive(student);
    }

    @Test
    public void createStudentWithFacultyPositive() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        Faculty randomFacultyFromDb = facultyRepository.findAll(PageRequest.of(faker.random().nextInt(0, 1), 1)).
                getContent().
                get(0);
        student.setFaculty(randomFacultyFromDb);

        createStudentPositive(student);
    }

    private void createStudentPositive(Student student) {
        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/student"),
                student,
                Student.class
        );
        Student created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
        assertThat(created.getId()).isNotNull();
        Optional<Student> fromDb = studentRepository.findById(created.getId());

        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .isEqualTo(created);
    }

    @Test
    public void createStudentWithoutFacultyWhichNotExistsNegative() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));

        Faculty faculty = new Faculty();
        faculty.setId(-1L);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/student"),
                student,
                String.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Факультет с id = %d не найден".formatted(-1));
    }

    @Test
    void get() {
        Faculty faculty = createFaculty();
        Student student1 = new Student();
        student1.setFaculty(faculty);
        Student student2 = studentRepository.save(student1);
        assertThat(studentRepository.findAll()).isNotEmpty();
        long id = student2.getId();
        //test
        ResponseEntity<Student> responseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + id,
                Student.class);
        //check
        assertThat(responseEntity.getBody()).isEqualTo(student1);
    }

    @Test
    public void getStudentNegative() {
        //data
        long id = 0;
        //test
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + id,
                String.class);
        //check
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Студент с id = %d не найден".formatted(id));
    }

    @Test
    void update() {
        //data
        Faculty faculty1 = createFaculty();
        Student student1 = new Student();
        student1.setFaculty(faculty1);
        Student student2 = studentRepository.save(student1);
        long id = student2.getId();
        Student student3 = new Student();
        student3.setFaculty(faculty1);
        //test
        testRestTemplate.put("http://localhost:" + port + "/student/" + id,
                student3);
        //check
        Optional<Student> actual = studentRepository.findById(id);
        Student actualNew = actual.orElseThrow();
        assertThat(actualNew)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student3);
    }

    @Test
    void remove() {
        //data
        studentRepository.deleteAll();
        assertThat(studentRepository.findAll()).isEmpty();
        Faculty faculty1 = createFaculty();
        Student student1 = new Student();
        student1.setFaculty(faculty1);
        Student student2 = studentRepository.save(student1);
        long id = student2.getId();
        Student studentInBase = studentRepository.findById(student2.getId()).orElseThrow();
        Assertions.assertEquals(student1, studentInBase);
        //test
        testRestTemplate.delete("http://localhost:" + port + "/student/" + id);
        //check
        assertThat(studentRepository.findAll()).isEmpty();
    }

    @Test
    void filterByAge() {
        int age = faker.random().nextInt(11, 18);
        List<Student> expected = students.stream()
                .filter(student -> student.getAge() == age)
                .toList();

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                buildUrl("/student?age={age}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("age", age)
        );
        List<Student> actual = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void filterByRangeAge() {
        int minAge = faker.random().nextInt(11, 18);
        int maxAge = faker.random().nextInt(minAge, 18);
        List<Student> expected = students.stream()
                .filter(student -> student.getAge() >= minAge && student.getAge() <= maxAge)
                .toList();

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                buildUrl("/student?minAge={minAge}&maxAge={maxAge}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("minAge", minAge, "maxAge", maxAge)
        );
        List<Student> actual = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void findStudentsFacultyPositive() {
        Student student = students.get(faker.random().nextInt(students.size()));
        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/student/{id}/faculty"),
                Faculty.class,
                Map.of("id", student.getId())
        );
        Faculty actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(student.getFaculty());
    }

    @Test
    void findStudentsFacultyNegative() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/student/{id}/faculty"),
                String.class,
                Map.of("id", -1)
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Студент с id = %d не найден".formatted(-1));
    }
}