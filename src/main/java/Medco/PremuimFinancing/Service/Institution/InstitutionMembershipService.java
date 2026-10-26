package Medco.PremuimFinancing.Service.Institution;

import java.util.UUID;

public interface InstitutionMembershipService {
    void assignUser(UUID institutionId, String userUuid);
    void unassignUser(UUID institutionId, String userUuid);
}
