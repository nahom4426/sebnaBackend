package Medco.PremuimFinancing.ServiceImpl.User.UsersLoader;

import Medco.PremuimFinancing.Entity.UserRole.Privilege;
import Medco.PremuimFinancing.Repository.Role.PrivilegeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrivilegeLoader  implements CommandLineRunner {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeLoader(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public void run(String... args) {
        if (privilegeRepository.count() == 0) {
            List <Privilege>  privilege=new ArrayList<>();
            privilege.add(new Privilege("create_privilege", "Grants permission to create new privilege.", "privilege"));
            privilege.add( new Privilege("read_privilege", "Grants permission to read privilege.", "privilege"));
            privilege.add( new Privilege("read_privileges", "Grants permission to read privilege.", "privilege"));
            privilege.add( new Privilege("update_privilege", "Grants permission to update privilege.", "privilege"));
            privilege.add( new Privilege("delete_privilege", "Grants permission to delete privilege.", "privilege"));

            privilege.add(new Privilege("create_role", "Grants permission to create role.", "role"));
            privilege.add(new Privilege("read_role", "Grants permission to read role.", "role"));
            privilege.add(new Privilege("read_roles", "Grants permission to read role.", "role"));
            privilege.add( new Privilege("update_role", "Grants permission to update role.", "role"));
            privilege.add(new Privilege("delete_role", "Grants permission to delete role.", "role"));

            privilege.add( new Privilege("create_user", "Grants permission to create user.", "user"));
            privilege.add(new Privilege("read_payer-users", "Grants permission to read user.", "user"));
            privilege.add(new Privilege("read_user", "Grants permission to read user.", "user"));
            privilege.add(new Privilege("read_users", "Grants permission to read user.", "user"));
            privilege.add( new Privilege("update_user", "Grants permission to update user.", "user"));
            privilege.add( new Privilege("change_password", "Grants permission to update user.", "user"));
            privilege.add( new Privilege("delete_user", "Grants permission to delete user.", "user"));


//            privilege.add( new Privilege("create_contract", "Grants permission to create contract.", "contract"));
//            privilege.add(new Privilege("read_contract", "Grants permission to read contract.", "contract"));
//            privilege.add(new Privilege("read_contract_by_status", "Grants permission to read read_contract_by_status.", "contract"));
//            privilege.add(new Privilege("read_image", "Grants permission to read contract files.", "contract"));
//            privilege.add( new Privilege("update_contract_status", "Grants permission to update contract status.", "contract"));
//            privilege.add( new Privilege("update_signed_contract", "Grants permission to update signed contract .", "contract"));
//            privilege.add( new Privilege("update_contract", "Grants permission to update contract.", "contract"));
//            privilege.add( new Privilege("delete_contract", "Grants permission to delete contract.", "contract"));

//
//            privilege.add(new Privilege("read_document", "Grants permission to read document.", "document"));
//            privilege.add( new Privilege("update_document", "Grants permission to update document.", "document"));
//            privilege.add( new Privilege("delete_document", "Grants permission to delete document.", "document"));
//
//            privilege.add( new Privilege("create_program", "Grants permission to create program.", "program"));
//            privilege.add(new Privilege("read_program", "Grants permission to read program.", "program"));
//            privilege.add(new Privilege("read_programs", "Grants permission to read contract files.", "program"));
//            privilege.add( new Privilege("update_program", "Grants permission to update program .", "program"));
//            privilege.add( new Privilege("delete_program", "Grants permission to delete program.", "program"));

//            privilege.add( new Privilege("create_student_upload", "Grants permission to  upload student.", "student"));
//            privilege.add( new Privilege("create_student", "Grants permission to create student.", "student"));
//            privilege.add( new Privilege("create_student_document", "Grants permission to create_student_document .", "student"));
//            privilege.add(new Privilege("read_student", "Grants permission to read student.", "student"));
//            privilege.add(new Privilege("read_students", "Grants permission to read students.", "student"));
//            privilege.add(new Privilege("read_batches", "Grants permission to read  batches .", "student"));
//            privilege.add(new Privilege("read_batch_students", "Grants permission to read student  batches .", "student"));
//            privilege.add(new Privilege("read_university_students", "Grants permission to read university students .", "student"));
//            privilege.add(new Privilege("read_university_students_by_status", "Grants permission to read_university_students_by_status .", "student"));
//            privilege.add(new Privilege("read_university_students_by_academic_year", "Grants permission to read_university_students_by_academic_year .", "student"));
//            privilege.add(new Privilege("read_university_students_by_campus_status", "Grants permission to read_university_students_by_status .", "student"));
//            privilege.add(new Privilege("read_university_students_by_registration_status", "Grants permission to read_university_students_by_status .", "student"));
//            privilege.add( new Privilege("update_student", "Grants permission to update student .", "student"));
//            privilege.add( new Privilege("update_student_status", "Grants permission to update_student_status  .", "student"));
//            privilege.add( new Privilege("update_campus_status", "Grants permission to update_campus_status  .", "student"));
//            privilege.add( new Privilege("update_pass_to_next_year", "Grants permission to update_pass_to_next_year  .", "student"));
//            privilege.add( new Privilege("update_undo_pass_to_next_year", "Grants permission to update_undo_pass_to_next_year  .", "student"));
//            privilege.add( new Privilege("delete_student", "Grants permission to delete student.", "student"));
//

//            privilege.add( new Privilege("create_university", "Grants permission to create university.", "university"));
//            privilege.add(new Privilege("read_university", "Grants permission to read university .", "university"));
//            privilege.add( new Privilege("update_university", "Grants permission to update university .", "university"));
//            privilege.add( new Privilege("update_university_program", "Grants permission to update_university_program  .", "university"));
//            privilege.add( new Privilege("delete_university", "Grants permission to delete university.", "university"));



            privilegeRepository.saveAll(privilege);
        }
    }
}