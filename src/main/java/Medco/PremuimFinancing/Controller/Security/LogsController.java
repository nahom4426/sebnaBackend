package Medco.PremuimFinancing.Controller.Security;

import Medco.PremuimFinancing.Dto.Response.Security.LoginLogPageResponse;
import Medco.PremuimFinancing.Dto.Response.Security.LoginLogResponse;
import Medco.PremuimFinancing.Entity.Security.LoginLog;
import Medco.PremuimFinancing.Repository.Security.LoginLogRepository;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sebna/logs")
public class LogsController {

    private final LoginLogRepository loginLogRepository;

    public LogsController(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    @GetMapping("/login")
    @PreAuthorize("hasAuthority('ROLE_read_logs')")
    public ResponseEntity<LoginLogPageResponse> listLoginLogs(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit
    ) {
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "createdAt", "desc");
        Page<LoginLog> logs = loginLogRepository.findAll(pageable);

        List<LoginLogResponse> responseItems = logs.getContent().stream().map(this::toResponse).toList();

        LoginLogPageResponse response = new LoginLogPageResponse();
        response.setTotalElements(logs.getTotalElements());
        response.setTotalPages(logs.getTotalPages());
        response.setPageNumber(logs.getNumber());
        response.setResponse(responseItems);

        return ResponseEntity.ok(response);
    }

    private LoginLogResponse toResponse(LoginLog log) {
        LoginLogResponse r = new LoginLogResponse();
        r.setId(log.getId());
        r.setEmail(log.getEmail());
        r.setUserUuid(log.getUserUuid());
        r.setSuccess(log.isSuccess());
        r.setIpAddress(log.getIpAddress());
        r.setUserAgent(log.getUserAgent());
        r.setFailureReason(log.getFailureReason());
        r.setCreatedAt(log.getCreatedAt());
        return r;
    }
}
