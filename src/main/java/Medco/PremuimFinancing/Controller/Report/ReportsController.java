package Medco.PremuimFinancing.Controller.Report;

import Medco.PremuimFinancing.Dto.Response.Report.DashboardReportResponse;
import Medco.PremuimFinancing.Service.Report.DashboardReportService;
import Medco.PremuimFinancing.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sebna/reports")
public class ReportsController {

    private final DashboardReportService dashboardReportService;

    public ReportsController(DashboardReportService dashboardReportService) {
        this.dashboardReportService = dashboardReportService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_read_reports')")
    public ResponseEntity<DashboardReportResponse> dashboard(
            @RequestParam(value = "range", defaultValue = "week") String range
    ) {
        var auth = SecurityUtils.getAuthenticatedUser();
        return ResponseEntity.ok(dashboardReportService.dashboard(range, auth.getUserUuid()));
    }
}
