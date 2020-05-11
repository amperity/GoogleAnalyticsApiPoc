package com.amperity.app;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;


import com.google.api.services.analyticsreporting.v4.model.*;


public class GoogleAnalyticsReportingApiPoc {
    private static final String APPLICATION_NAME = "Hello Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "/Users/andrea/code/google-analytics-api-prova/src/main/java/com/amperity/app/client_secrets.json";
//    private static final String VIEW_ID = "215449332";
    private static final String VIEW_ID = "140553412";

    public static void main(String[] args) {
        try {
            AnalyticsReporting service = initializeAnalyticsReporting();

            GetReportsResponse response = getActiveUsersReport(service);
            printResponse(response);

            printResponse(getSessionsReport(service));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes an Analytics Reporting API V4 service object.
     *
     * @return An authorized Analytics Reporting API V4 service object.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsReportingScopes.all());

        // Construct the Analytics Reporting service object.
        return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Queries the Analytics Reporting API V4.
     *
     * @param service An authorized Analytics Reporting API V4 service object.
     * @return GetReportResponse The Analytics Reporting API V4 response.
     * @throws IOException
     */
    private static GetReportsResponse getActiveUsersReport(AnalyticsReporting service) throws IOException {
        // Create the DateRange object.
        DateRange dateRange = new DateRange();
        dateRange.setStartDate("1DaysAgo");
        dateRange.setEndDate("yesterday");

        Metric users = new Metric()
                .setExpression("ga:28dayUsers")
                .setAlias("28 day active users");
        Dimension gaDate = new Dimension().setName("ga:date");

        ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(Arrays.asList(dateRange))
                .setMetrics(Arrays.asList(users))
                .setDimensions(Arrays.asList(gaDate));
        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        GetReportsResponse response = service.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }


    private static GetReportsResponse getSessionsReport(AnalyticsReporting service) throws IOException {
        // Create the DateRange object.
        DateRange dateRange = new DateRange();
        dateRange.setStartDate("30DaysAgo");
        dateRange.setEndDate("yesterday");

        Metric sessions = new Metric()
                .setExpression("ga:sessions")
                .setAlias("sessions");

        Dimension gaDate = new Dimension().setName("ga:dimension1");

        ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(Arrays.asList(dateRange))
                .setMetrics(Arrays.asList(sessions))
                .setDimensions(Arrays.asList(gaDate));

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        GetReportsResponse response = service.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }

    /**
     * Parses and prints the Analytics Reporting API V4 response.
     *
     * @param response An Analytics Reporting API V4 response.
     */
    private static void printResponse(GetReportsResponse response) {

        for (Report report: response.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<String> dimensionHeaders = header.getDimensions();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();
            String measure = "";

            if (rows == null) {
                System.out.println("No data found for " + VIEW_ID);
                return;
            }
            Integer total = 0;
            for (ReportRow row: rows) {
                List<String> dimensions = row.getDimensions();
                List<DateRangeValues> metrics = row.getMetrics();

                for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                    System.out.println(dimensionHeaders.get(i) + ": " + dimensions.get(i));
                }

                for (int j = 0; j < metrics.size(); j++) {
                    System.out.print("Date Range (" + j + "): ");
                    DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                        System.out.println(metricHeaders.get(k).getName() + ": " + values.getValues().get(k));
                        total = total + Integer.parseInt(values.getValues().get(k));
                        measure = metricHeaders.get(k).getName();

                    }
                }
            }
                System.out.println("Total " + measure  + ": "+ total);

        }
    }
}