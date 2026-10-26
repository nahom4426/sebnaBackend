package Medco.PremuimFinancing.Service.Report;

import Medco.PremuimFinancing.Dto.Response.Report.DashboardReportResponse;

public interface DashboardReportService {
    DashboardReportResponse dashboard(String range, String requesterUserUuid);
}
