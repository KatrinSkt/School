SELECT student.name, student.age, faculties.name
FROM student
         INNER JOIN faculties ON student.faculty_id = faculties.id;
SELECT student.name, student.age
FROM student
         INNER JOIN avatar ON student.id = avatar.student_id;