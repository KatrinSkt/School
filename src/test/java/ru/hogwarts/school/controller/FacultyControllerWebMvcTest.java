package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;


    @Test
    public void updatePositiveTest() throws Exception {
        long id = 1;
        String newName = "Gryffindor";
        String newColor = "Red";

        Faculty oldFaculty = new Faculty();
        oldFaculty.setId(id);
        oldFaculty.setName("Slytherin");
        oldFaculty.setColor("Green");

        Faculty newFaculty = new Faculty();
        newFaculty.setId(id);
        newFaculty.setName(newName);
        newFaculty.setColor(newColor);

        JSONObject newFacultyTest = new JSONObject();
        newFacultyTest.put("$.name", newName);
        newFacultyTest.put("$.id", id);
        newFacultyTest.put("$.color", newColor);
        System.out.println(oldFaculty);
        System.out.println(newFaculty);
        when(facultyRepository.findById(id)).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.save(any())).thenReturn(newFaculty);
        //test, check
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/{id}", id)
                        .content(newFacultyTest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.color").value(newColor));
    }

    @Test
    public void createFacultyTest() throws Exception {
        //data
        long id = 1;
        String name = "RRR";
        String color = "red";
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setId(id);
        faculty.setColor(color);
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", name);
        facultyObject.put("id", id);
        facultyObject.put("color", color);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        //test
        //check
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.color").value(color));
        Mockito.verify(facultyRepository, only()).save(any());
    }

    @Test
    void deleteFaculty() throws Exception {
        //data
        long id = 1L;

        /*Faculty oldFaculty = new Faculty();
        oldFaculty.setId(id);
        oldFaculty.setName(faker.harryPotter().house());
        oldFaculty.setColor(faker.color().name());
        Faculty faculty = new Faculty();*/

        //test, check
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isEmpty())
                .andExpect(jsonPath("$.id").isEmpty())
                .andExpect(jsonPath("$.color").isEmpty());
    }
}
