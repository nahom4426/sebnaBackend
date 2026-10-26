package Medco.PremuimFinancing.ServiceImpl.Report;

import Medco.PremuimFinancing.Dto.Response.Report.*;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Enums.Message.MessageType;
import Medco.PremuimFinancing.Enums.Post.PostCategory;
import Medco.PremuimFinancing.Repository.Message.MessageRepository;
import Medco.PremuimFinancing.Repository.Post.CommentRepository;
import Medco.PremuimFinancing.Repository.Post.PostRepository;
import Medco.PremuimFinancing.Repository.Security.LoginLogRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Report.DashboardReportService;
import Medco.PremuimFinancing.Service.Settings.AppSettingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardReportServiceImpl implements DashboardReportService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MessageRepository messageRepository;
    private final LoginLogRepository loginLogRepository;
    private final AppSettingService appSettingService;

    public DashboardReportServiceImpl(
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            MessageRepository messageRepository,
            LoginLogRepository loginLogRepository,
            AppSettingService appSettingService
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.messageRepository = messageRepository;
        this.loginLogRepository = loginLogRepository;
        this.appSettingService = appSettingService;
    }

    @Override
    public DashboardReportResponse dashboard(String range, String requesterUserUuid) {
        String normalizedRange = range == null ? "week" : range.trim().toLowerCase();
        int days = "month".equals(normalizedRange) ? 30 : 7;

        UserEntity requester = userRepository.findByUserUuid(requesterUserUuid).orElseThrow();
        boolean superAdmin = true;

        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        Instant start = today.minusDays(days - 1L).atStartOfDay(zone).toInstant();
        Instant end = Instant.now();

        Instant prevStart = today.minusDays((days - 1L) + days).atStartOfDay(zone).toInstant();
        Instant prevEnd = start.minusMillis(1);

        DashboardReportResponse res = new DashboardReportResponse();
        res.setRange(normalizedRange);
        res.setGeneratedAt(Instant.now());
        res.setScope("global");

        res.setFinance(buildFinanceKpis());

        // KPI cards
        List<DashboardCardResponse> cards = new ArrayList<>();
        cards.addAll(buildFinanceCards(res.getFinance()));
        cards.add(buildUsersCard(start, end, prevStart, prevEnd));
        cards.add(buildPostsCard(start, end, prevStart, prevEnd));
        cards.add(buildCommentsCard(start, end, prevStart, prevEnd));
        cards.add(buildMessagesCard(start, end, prevStart, prevEnd));
        cards.add(buildLoginsCard(start, end, prevStart, prevEnd));
        res.setCards(cards);

        // Bar series
        List<BarSeriesResponse> bar = new ArrayList<>();
        bar.add(buildUsersBar(start, end, zone, days));
        bar.add(buildPostsBar(start, end, zone, days));
        res.setBarSeries(bar);

        // Pie series
        List<PieSeriesResponse> pies = new ArrayList<>();
        pies.add(buildShareholderPaymentStatusPie(res.getFinance()));
        pies.add(buildPostsByCategoryPie(start, end));
        pies.add(buildMessagesByTypePie(start, end));
        pies.add(buildLoginSuccessPie(start, end));
        res.setPieSeries(pies);

        return res;
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private DashboardFinanceResponse buildFinanceKpis() {
        DashboardFinanceResponse f = new DashboardFinanceResponse();

        BigDecimal pricePerShare = safe(appSettingService.getPricePerShare());
        BigDecimal totalSharesSold = safe(userRepository.sumSharesBoughtAmountForShareholders());
        BigDecimal totalDeposited = safe(userRepository.sumAmountPaidForShareholders());
        BigDecimal costBasisExpected = safe(userRepository.sumCostBasisExpectedAmountForShareholders(pricePerShare));
        BigDecimal costBasisRemaining = costBasisExpected.subtract(totalDeposited);
        if (costBasisRemaining.compareTo(BigDecimal.ZERO) < 0) {
            costBasisRemaining = BigDecimal.ZERO;
        }

        BigDecimal totalExpected = totalSharesSold.multiply(pricePerShare);
        BigDecimal remaining = totalExpected.subtract(totalDeposited);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        long shareholdersCount = userRepository.countShareholders();
        long fullyPaidCount = userRepository.countFullyPaidShareholders(pricePerShare);
        long outstandingCount = userRepository.countOutstandingShareholders(pricePerShare);

        BigDecimal collectionRate;
        if (costBasisExpected.compareTo(BigDecimal.ZERO) <= 0) {
            collectionRate = BigDecimal.ZERO;
        } else {
            collectionRate = totalDeposited
                    .multiply(new BigDecimal("100"))
                    .divide(costBasisExpected, 2, RoundingMode.HALF_UP);
        }

        BigDecimal avgShares = shareholdersCount <= 0
                ? BigDecimal.ZERO
                : totalSharesSold.divide(new BigDecimal(shareholdersCount), 2, RoundingMode.HALF_UP);

        BigDecimal avgPaid = shareholdersCount <= 0
                ? BigDecimal.ZERO
                : totalDeposited.divide(new BigDecimal(shareholdersCount), 2, RoundingMode.HALF_UP);

        f.setPricePerShare(pricePerShare);
        f.setTotalSharesSold(totalSharesSold);
        f.setTotalExpectedAmount(totalExpected);
        f.setTotalDepositedAmount(totalDeposited);
        f.setTotalRemainingAmount(remaining);

        f.setCostBasisTotalExpectedAmount(costBasisExpected);
        f.setCostBasisTotalRemainingAmount(costBasisRemaining);

        BigDecimal marketTotalValue = totalExpected;
        BigDecimal unrealizedGainAmount = marketTotalValue.subtract(costBasisExpected);
        BigDecimal unrealizedGainPercent;
        if (costBasisExpected.compareTo(BigDecimal.ZERO) <= 0) {
            unrealizedGainPercent = BigDecimal.ZERO;
        } else {
            unrealizedGainPercent = unrealizedGainAmount
                    .multiply(new BigDecimal("100"))
                    .divide(costBasisExpected, 2, RoundingMode.HALF_UP);
        }
        f.setMarketTotalValue(marketTotalValue);
        f.setMarketUnrealizedGainAmount(unrealizedGainAmount);
        f.setMarketUnrealizedGainPercent(unrealizedGainPercent);

        f.setShareholdersCount(shareholdersCount);
        f.setFullyPaidShareholdersCount(fullyPaidCount);
        f.setOutstandingShareholdersCount(outstandingCount);

        f.setCollectionRatePercent(collectionRate);
        f.setAvgSharesPerShareholder(avgShares);
        f.setAvgPaidPerShareholder(avgPaid);

        return f;
    }

    private List<DashboardCardResponse> buildFinanceCards(DashboardFinanceResponse finance) {
        if (finance == null) {
            return List.of();
        }

        DashboardCardResponse shares = cardNoChange(
                "total_shares_sold",
                "Total Shares Sold",
                safe(finance.getTotalSharesSold()).setScale(0, RoundingMode.HALF_UP).longValue()
        );
        DashboardCardResponse deposited = cardNoChange(
                "total_deposited",
                "Total Deposited",
                safe(finance.getTotalDepositedAmount()).setScale(0, RoundingMode.HALF_UP).longValue()
        );
        DashboardCardResponse remaining = cardNoChange(
                "total_remaining",
                "Total Remaining",
                safe(finance.getTotalRemainingAmount()).setScale(0, RoundingMode.HALF_UP).longValue()
        );

        return List.of(shares, deposited, remaining);
    }

    private DashboardCardResponse cardNoChange(String key, String title, long value) {
        DashboardCardResponse c = new DashboardCardResponse();
        c.setKey(key);
        c.setTitle(title);
        c.setValue(value);
        c.setPreviousValue(null);
        c.setChangePercent(null);
        return c;
    }

    private PieSeriesResponse buildShareholderPaymentStatusPie(DashboardFinanceResponse finance) {
        PieSeriesResponse p = new PieSeriesResponse();
        p.setKey("shareholder_payment_status");
        p.setTitle("Shareholders Payment Status");

        long fullyPaid = finance == null ? 0L : finance.getFullyPaidShareholdersCount();
        long outstanding = finance == null ? 0L : finance.getOutstandingShareholdersCount();

        PieSliceResponse s1 = new PieSliceResponse();
        s1.setLabel("FULLY_PAID");
        s1.setValue(fullyPaid);

        PieSliceResponse s2 = new PieSliceResponse();
        s2.setLabel("OUTSTANDING");
        s2.setValue(outstanding);

        p.setSlices(List.of(s1, s2));
        return p;
    }

    private DashboardCardResponse buildUsersCard(Instant start, Instant end, Instant prevStart, Instant prevEnd) {
        long current = userRepository.countCreatedBetween(start, end);
        long previous = userRepository.countCreatedBetween(prevStart, prevEnd);

        return card("users", "Today's Users", current, previous);
    }

    private DashboardCardResponse buildPostsCard(Instant start, Instant end, Instant prevStart, Instant prevEnd) {
        long current = postRepository.countCreatedBetweenGlobal(start, end);
        long previous = postRepository.countCreatedBetweenGlobal(prevStart, prevEnd);

        return card("posts", "Posts", current, previous);
    }

    private DashboardCardResponse buildCommentsCard(Instant start, Instant end, Instant prevStart, Instant prevEnd) {
        long current = commentRepository.countCreatedBetweenGlobal(start, end);
        long previous = commentRepository.countCreatedBetweenGlobal(prevStart, prevEnd);

        return card("comments", "Comments", current, previous);
    }

    private DashboardCardResponse buildMessagesCard(Instant start, Instant end, Instant prevStart, Instant prevEnd) {
        long current = messageRepository.countCreatedBetweenGlobal(start, end);
        long previous = messageRepository.countCreatedBetweenGlobal(prevStart, prevEnd);

        return card("messages", "Messages", current, previous);
    }

    private DashboardCardResponse buildLoginsCard(Instant start, Instant end, Instant prevStart, Instant prevEnd) {
        long current = loginLogRepository.countCreatedBetween(start, end);
        long previous = loginLogRepository.countCreatedBetween(prevStart, prevEnd);
        return card("logins", "Logins", current, previous);
    }

    private BarSeriesResponse buildUsersBar(Instant start, Instant end, ZoneId zone, int days) {
        List<Object[]> rows = userRepository.countDailyCreatedBetween(start, end);
        return barSeries("users_daily", "Users (Daily)", rows, zone, days);
    }

    private BarSeriesResponse buildPostsBar(Instant start, Instant end, ZoneId zone, int days) {
        List<Object[]> rows = postRepository.countDailyCreatedBetweenGlobal(start, end);
        return barSeries("posts_daily", "Posts (Daily)", rows, zone, days);
    }

    private PieSeriesResponse buildPostsByCategoryPie(Instant start, Instant end) {
        List<Object[]> rows = postRepository.countByCategoryBetweenGlobal(start, end);
        Map<String, Long> counts = new LinkedHashMap<>();
        for (PostCategory c : PostCategory.values()) {
            counts.put(c.name(), 0L);
        }
        for (Object[] r : rows) {
            String label = r[0] == null ? "UNKNOWN" : String.valueOf(r[0]);
            long v = ((Number) r[1]).longValue();
            counts.put(label, v);
        }

        PieSeriesResponse p = new PieSeriesResponse();
        p.setKey("posts_by_category");
        p.setTitle("Posts by Category");
        p.setSlices(counts.entrySet().stream().map(e -> {
            PieSliceResponse s = new PieSliceResponse();
            s.setLabel(e.getKey());
            s.setValue(e.getValue());
            return s;
        }).collect(Collectors.toList()));
        return p;
    }

    private PieSeriesResponse buildMessagesByTypePie(Instant start, Instant end) {
        List<Object[]> rows = messageRepository.countByTypeBetweenGlobal(start, end);
        Map<String, Long> counts = new LinkedHashMap<>();
        for (MessageType t : MessageType.values()) {
            counts.put(t.name(), 0L);
        }
        for (Object[] r : rows) {
            String label = r[0] == null ? "UNKNOWN" : String.valueOf(r[0]);
            long v = ((Number) r[1]).longValue();
            counts.put(label, v);
        }

        PieSeriesResponse p = new PieSeriesResponse();
        p.setKey("messages_by_type");
        p.setTitle("Messages by Type");
        p.setSlices(counts.entrySet().stream().map(e -> {
            PieSliceResponse s = new PieSliceResponse();
            s.setLabel(e.getKey());
            s.setValue(e.getValue());
            return s;
        }).collect(Collectors.toList()));
        return p;
    }

    private PieSeriesResponse buildLoginSuccessPie(Instant start, Instant end) {
        long success = loginLogRepository.countSuccessBetween(true, start, end);
        long fail = loginLogRepository.countSuccessBetween(false, start, end);

        PieSeriesResponse p = new PieSeriesResponse();
        p.setKey("login_success");
        p.setTitle("Login Success vs Fail");

        PieSliceResponse s1 = new PieSliceResponse();
        s1.setLabel("SUCCESS");
        s1.setValue(success);

        PieSliceResponse s2 = new PieSliceResponse();
        s2.setLabel("FAIL");
        s2.setValue(fail);

        p.setSlices(List.of(s1, s2));
        return p;
    }

    private DashboardCardResponse card(String key, String title, long value, long previousValue) {
        DashboardCardResponse c = new DashboardCardResponse();
        c.setKey(key);
        c.setTitle(title);
        c.setValue(value);
        c.setPreviousValue(previousValue);

        if (previousValue <= 0) {
            c.setChangePercent(value > 0 ? 100.0 : 0.0);
        } else {
            c.setChangePercent(((double) (value - previousValue) / (double) previousValue) * 100.0);
        }
        return c;
    }

    private BarSeriesResponse barSeries(String key, String title, List<Object[]> rows, ZoneId zone, int days) {
        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] r : rows) {
            Object dateObj = r[0];
            LocalDate d;
            if (dateObj instanceof java.sql.Date sqlDate) {
                d = sqlDate.toLocalDate();
            } else if (dateObj instanceof java.sql.Timestamp ts) {
                d = ts.toInstant().atZone(zone).toLocalDate();
            } else {
                d = LocalDate.parse(String.valueOf(dateObj));
            }
            map.put(d, ((Number) r[1]).longValue());
        }

        LocalDate today = LocalDate.now(zone);
        List<ChartPointResponse> points = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            ChartPointResponse p = new ChartPointResponse();
            p.setLabel(days == 7 ? String.valueOf(d.getDayOfWeek().name().charAt(0)) : String.valueOf(d.getDayOfMonth()));
            p.setValue(map.getOrDefault(d, 0L));
            points.add(p);
        }

        BarSeriesResponse series = new BarSeriesResponse();
        series.setKey(key);
        series.setTitle(title);
        series.setPoints(points);
        return series;
    }

    private BarSeriesResponse emptyBar(String key, String title, int days) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        List<ChartPointResponse> points = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            ChartPointResponse p = new ChartPointResponse();
            p.setLabel(days == 7 ? String.valueOf(d.getDayOfWeek().name().charAt(0)) : String.valueOf(d.getDayOfMonth()));
            p.setValue(0L);
            points.add(p);
        }
        BarSeriesResponse series = new BarSeriesResponse();
        series.setKey(key);
        series.setTitle(title);
        series.setPoints(points);
        return series;
    }

    private PieSeriesResponse emptyPostsByCategoryPie() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (PostCategory c : PostCategory.values()) {
            counts.put(c.name(), 0L);
        }
        PieSeriesResponse p = new PieSeriesResponse();
        p.setKey("posts_by_category");
        p.setTitle("Posts by Category");
        p.setSlices(counts.entrySet().stream().map(e -> {
            PieSliceResponse s = new PieSliceResponse();
            s.setLabel(e.getKey());
            s.setValue(e.getValue());
            return s;
        }).collect(Collectors.toList()));
        return p;
    }

    private PieSeriesResponse emptyMessagesByTypePie() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (MessageType t : MessageType.values()) {
            counts.put(t.name(), 0L);
        }
        PieSeriesResponse p = new PieSeriesResponse();
        p.setKey("messages_by_type");
        p.setTitle("Messages by Type");
        p.setSlices(counts.entrySet().stream().map(e -> {
            PieSliceResponse s = new PieSliceResponse();
            s.setLabel(e.getKey());
            s.setValue(e.getValue());
            return s;
        }).collect(Collectors.toList()));
        return p;
    }
}
