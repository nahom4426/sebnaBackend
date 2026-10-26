package Medco.PremuimFinancing.Entity.UserRole;

import Medco.PremuimFinancing.utils.Audit;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges", uniqueConstraints = { @UniqueConstraint(columnNames = "privilegeName"),
        @UniqueConstraint(columnNames = "privilegeUuid")

})
public class Privilege extends Audit {
    @Serial
    private static final long serialVersionUID = 2369844719759914085L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 50,nullable = false)
    private String privilegeName;

    @Size(min = 36, max = 40)
    private String privilegeUuid = UUID.randomUUID().toString();

    @Size(max = 100)
    private String privilegeDescription;

    @Column(length = 50)
    private String privilegeCategory;

    @Column(length = 20)
    private String scope; // global or institution


    @JsonBackReference
    @ManyToMany(mappedBy = "privileges")
    private List<Role> roles= new ArrayList<>();
    public Privilege(String privilegeName, String privilegeDescription, String category) {
        this.privilegeName = privilegeName;
        this.privilegeDescription = privilegeDescription;
        this.privilegeCategory = category;
    }

}

