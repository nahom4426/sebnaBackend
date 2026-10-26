package Medco.PremuimFinancing.Entity.Insurance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "insurance")
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String insuranceUuid = UUID.randomUUID().toString();
    private String insuranceName;
    private String accountNumber;
    private Date registrationDate;
    private String registeredBy;
    private Date updatedDate;
    private String updatedBy;
//    private String insurance;


//    @Lob @Basic(fetch= FetchType.LAZY)
//    @Column(name = "Profile", columnDefinition = "LONGBLOB")
//    private byte[] profile;

    @Lob
    private byte[] profile;

//@OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//private List<PremiumRate> premiums;




//    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<UserEntity> clients;

//    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Car> cars;


}
