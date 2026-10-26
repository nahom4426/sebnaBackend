package Medco.PremuimFinancing.Entity.UserRole;//package com.medcoAnalytics.invoiceService.entity.UserRole;
//
//import com.medcoAnalytics.invoiceService.utils.Audit;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.io.Serial;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "role_privileges")
//public class RolePrivilege extends Audit {
//    @Serial
//    private static final long serialVersionUID = -4814904549349101427L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "role_id", referencedColumnName = "id")
//    private Role role;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "privilege_id",referencedColumnName = "id")
//    private Privilege privilege;
//
////    @Column(name = "privilege_id", insertable=false, updatable=false)
////    private Integer privilegeId;
////
////    @Column(name = "id", insertable=false, updatable=false)
////    private Role roleUuid;
//
//}
//
