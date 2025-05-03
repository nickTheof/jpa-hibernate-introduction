package gr.aueb.cf.model;

import gr.aueb.cf.core.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "teachers_more_info")
public class TeacherMoreInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_birth")
    private LocalDateTime birthDate;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @OneToOne(mappedBy = "teacherMoreInfo", fetch = FetchType.LAZY)
    private Teacher teacher;

    @Override
    public String toString() {
        return "TeacherMoreInfo{" +
                "id=" + id +
                ", birthDate=" + birthDate +
                ", gender=" + gender +
                '}';
    }
}
