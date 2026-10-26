package Medco.PremuimFinancing.Repository.User;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    Optional <UserEntity> findByUserUuid(String userUuid);
    UserEntity findByUserUuidAndFirstName(String userUuid,String name);

    boolean existsByMobilePhone(String mobilePhone);

    UserEntity findByEmailAndPasswordResetCode(String email, String passwordResetCode);

    UserEntity findByEmailVerificationToken(String emailVerificationToken);

    @Query("""
             SELECT u FROM UserEntity u
             WHERE lower(u.firstName) LIKE lower(concat('%', :searchKey, '%'))
                OR lower(u.fatherName) LIKE lower(concat('%', :searchKey, '%'))
                OR lower(u.grandFatherName) LIKE lower(concat('%', :searchKey, '%'))
                OR lower(u.mobilePhone) LIKE lower(concat('%', :searchKey, '%'))
                OR lower(u.email) LIKE lower(concat('%', :searchKey, '%'))
                OR lower(str(u.userStatus)) LIKE lower(concat('%', :searchKey, '%'))
                OR lower(str(u.userType)) LIKE lower(concat('%', :searchKey, '%'))
             """)
    Page<UserEntity> searchUsersIgnoreCase(@Param("searchKey") String searchKey, Pageable pageableRequest);

    Page<UserEntity> findByFirstNameContainingIgnoreCaseAndFatherNameContainingIgnoreCase(String firstName, String fatherName,
                                                                  Pageable pageableRequest);

    Page<UserEntity> findByFirstNameContainingIgnoreCaseAndFatherNameContainingIgnoreCaseAndGrandFatherNameContainingIgnoreCase(String firstName,
                                                                                            String lastName, String grandFatherName, Pageable pageableRequest);

    Page<UserEntity> findByFirstNameContainingIgnoreCaseOrFatherNameContainingIgnoreCaseOrGrandFatherNameContainingIgnoreCaseOrMobilePhoneContainingOrEmailContainingIgnoreCase(
            String searchKey, String searchKey2, String searchKey3, String searchKey4, String searchKey5, Pageable pageableRequest);

    UserEntity findByMobilePhone(String phoneNumber);


    UserEntity findByEmailOrMobilePhone(String email,String phoneNumber);

    Page<UserEntity> findByMobilePhoneStartingWithOrFirstNameStartingWithIgnoreCase(String search,String search1, Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt BETWEEN :start AND :end")
    long countCreatedBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
            SELECT COUNT(u) FROM UserEntity u
            WHERE u.id IN (
                SELECT ui.user.id FROM Medco.PremuimFinancing.Entity.Institution.UserInstitution ui
                WHERE ui.institution.id = :institutionId
            )
            AND u.createdAt BETWEEN :start AND :end
            """)
    long countCreatedBetweenByInstitution(@Param("institutionId") UUID institutionId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("""
            SELECT function('date', u.createdAt) as d, COUNT(u)
            FROM UserEntity u
            WHERE u.createdAt BETWEEN :start AND :end
            GROUP BY function('date', u.createdAt)
            ORDER BY d
            """)
    List<Object[]> countDailyCreatedBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
            SELECT function('date', u.createdAt) as d, COUNT(u)
            FROM UserEntity u
            WHERE u.id IN (
                SELECT ui.user.id FROM Medco.PremuimFinancing.Entity.Institution.UserInstitution ui
                WHERE ui.institution.id = :institutionId
            )
            AND u.createdAt BETWEEN :start AND :end
            GROUP BY function('date', u.createdAt)
            ORDER BY d
            """)
    List<Object[]> countDailyCreatedBetweenByInstitution(@Param("institutionId") UUID institutionId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("""
            SELECT COALESCE(SUM(u.sharesBoughtAmount), 0)
            FROM UserEntity u
            WHERE u.isDeleted = false
              AND u.role IS NOT NULL
              AND lower(u.role.roleName) = 'shareholders'
            """)
    BigDecimal sumSharesBoughtAmountForShareholders();

    @Query("""
            SELECT COALESCE(SUM(u.amountPaid), 0)
            FROM UserEntity u
            WHERE u.isDeleted = false
              AND u.role IS NOT NULL
              AND lower(u.role.roleName) = 'shareholders'
            """)
    BigDecimal sumAmountPaidForShareholders();

    @Query("""
            SELECT COALESCE(SUM(COALESCE(u.sharesBoughtAmount, 0) * COALESCE(u.purchasePricePerShare, :fallbackPricePerShare)), 0)
            FROM UserEntity u
            WHERE u.isDeleted = false
              AND u.role IS NOT NULL
              AND lower(u.role.roleName) = 'shareholders'
            """)
    BigDecimal sumCostBasisExpectedAmountForShareholders(@Param("fallbackPricePerShare") BigDecimal fallbackPricePerShare);

    @Query("""
            SELECT COUNT(u)
            FROM UserEntity u
            WHERE u.isDeleted = false
              AND u.role IS NOT NULL
              AND lower(u.role.roleName) = 'shareholders'
            """)
    long countShareholders();

    @Query("""
            SELECT COUNT(u)
            FROM UserEntity u
            WHERE u.isDeleted = false
              AND u.role IS NOT NULL
              AND lower(u.role.roleName) = 'shareholders'
              AND COALESCE(u.amountPaid, 0) >= (COALESCE(u.sharesBoughtAmount, 0) * COALESCE(u.purchasePricePerShare, :pricePerShare))
            """)
    long countFullyPaidShareholders(@Param("pricePerShare") BigDecimal pricePerShare);

    @Query("""
            SELECT COUNT(u)
            FROM UserEntity u
            WHERE u.isDeleted = false
              AND u.role IS NOT NULL
              AND lower(u.role.roleName) = 'shareholders'
              AND COALESCE(u.amountPaid, 0) < (COALESCE(u.sharesBoughtAmount, 0) * COALESCE(u.purchasePricePerShare, :pricePerShare))
            """)
    long countOutstandingShareholders(@Param("pricePerShare") BigDecimal pricePerShare);

//    boolean isAnyUserEntityPresent();
}
