package Medco.PremuimFinancing.utils.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtility {

    public static Instant convert(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString,formatter);

        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.now());

        ZoneId zoneId = ZoneId.of("UTC+3"); // Replace with desired time zone
        return localDateTime.atZone(zoneId).toInstant();
    }

    public static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        return dateFormat.parse(date);
    }

    public static String convertDateToString(Date date) {
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String dateString = localDate.format(formatter);

        return dateString;
    }
}
