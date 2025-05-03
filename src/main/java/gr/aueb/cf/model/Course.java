package gr.aueb.cf.model;

import gr.aueb.cf.core.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "TINYINT COMMENT='ΕΙΔΟΣ ΜΑΘΗΜΑΤΟΣ: 1. Θεωρία 2. Εργαστήριο 3. Μεικτό'")
    private LessonType lessonType;

    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "courses_teachers")
    private Set<Teacher> teachers = new HashSet<>();

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", comments='" + comments + '\'' +
                ", lessonType=" + lessonType +
                '}';
    }

    public Set<Teacher> getAllTeachers() {
        return Collections.unmodifiableSet(teachers);
    }

    public void addTeacher(Teacher teacher) {
        if (teacher == null) return;
        if (teachers == null) teachers = new HashSet<>();
        teachers.add(teacher);
        teacher.getCourses().add(this);
    }


    public void removeTeacher(Teacher teacher) {
        if (teacher == null || teachers == null) return;
        teachers.remove(teacher);
        teacher.getCourses().remove(this);
    }
}
