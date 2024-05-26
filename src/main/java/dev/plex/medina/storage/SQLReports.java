package dev.plex.medina.storage;

import com.google.common.collect.Lists;
import dev.plex.medina.MedinaBase;
import dev.plex.medina.data.Report;
import dev.plex.medina.util.MedinaLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLReports implements MedinaBase
{
    private static final String SELECT = "SELECT * FROM `reports` WHERE reportedUUID=?";
    private static final String SELECT_ID = "SELECT * FROM `reports` WHERE reportId=?";
    private static final String INSERT = "INSERT INTO `reports` (`reportId`, `reporterUUID`, `reporterName`, `reportedUUID`, `reportedName`, `timestamp`, `reason`, `deleted`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE = "UPDATE `reports` SET `deleted`=true WHERE reportId=? AND reportedUUID=?";

    public CompletableFuture<Report> getReports(int reportedId)
    {
        MedinaLog.log("getting reports for: " + reportedId);
        return CompletableFuture.supplyAsync(() ->
        {
            Report report;
            MedinaLog.log("initialized List<Report>");
            try (Connection con = plugin.getSqlConnection().getCon())
            {
                MedinaLog.log("opened connection");
                PreparedStatement statement = con.prepareStatement(SELECT_ID);
                MedinaLog.log("prepared select statement");
                statement.setInt(1, reportedId);
                MedinaLog.log("set reportedUUID to " + reportedId);
                ResultSet set = statement.executeQuery();
                MedinaLog.log("executing query");
                MedinaLog.log("adding a report...");
                report = new Report(
                        reportedId,
                        UUID.fromString(set.getString("reporterUUID")),
                        set.getString("reporterName"),
                        UUID.fromString(set.getString("reportedUUID")),
                        set.getString("reportedName"),
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(set.getLong("timestamp")), ZoneId.systemDefault()),
                        set.getString("reason"),
                        set.getBoolean("deleted"));
                MedinaLog.log("added a report, id is " + report.getReportId());
                return report;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<List<Report>> getReports(UUID reportedUUID)
    {
        MedinaLog.log("getting reports for: " + reportedUUID);
        return CompletableFuture.supplyAsync(() ->
        {
            List<Report> reports = Lists.newArrayList();
            MedinaLog.log("initialized List<Report>");
            try (Connection con = plugin.getSqlConnection().getCon())
            {
                MedinaLog.log("opened connection");
                PreparedStatement statement = con.prepareStatement(SELECT);
                MedinaLog.log("prepared select statement");
                statement.setString(1, reportedUUID.toString());
                MedinaLog.log("set reportedUUID to " + reportedUUID);
                ResultSet set = statement.executeQuery();
                MedinaLog.log("executing query");
                while (set.next())
                {
                    MedinaLog.log("adding a report...");
                    Report report = new Report(
                            set.getInt("reportId"),
                            UUID.fromString(set.getString("reporterUUID")),
                            set.getString("reporterName"),
                            reportedUUID,
                            set.getString("reportedName"),
                            ZonedDateTime.ofInstant(Instant.ofEpochMilli(set.getLong("timestamp")), ZoneId.systemDefault()),
                            set.getString("reason"),
                            set.getBoolean("deleted"));
                    reports.add(report);
                    MedinaLog.log("added a report, id is " + report.getReportId());
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return reports;
            }
            return reports;
        });
    }

    public CompletableFuture<Void> deleteReport(int reportId, UUID reportedUUID)
    {
        MedinaLog.log("deleting report");
        return CompletableFuture.runAsync(() ->
        {
            MedinaLog.log("running async");
            try (Connection con = plugin.getSqlConnection().getCon())
            {
                MedinaLog.log("established connection");
                PreparedStatement statement = con.prepareStatement(DELETE);
                statement.setInt(1, reportId);
                statement.setString(2, reportedUUID.toString());
                statement.execute();
                MedinaLog.log("deleted report");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> addReport(Report report)
    {
        return CompletableFuture.runAsync(() ->
        {
            getReports(report.getReportedUUID()).whenComplete((reports, throwable) ->
            {
                try (Connection con = plugin.getSqlConnection().getCon())
                {
                    PreparedStatement statement = con.prepareStatement(INSERT);
                    statement.setInt(1, reports.size() + 1);
                    statement.setString(2, report.getReporterUUID().toString());
                    statement.setString(3, report.getReporterName());
                    statement.setString(4, report.getReportedUUID().toString());
                    statement.setString(5, report.getReportedName());
                    statement.setLong(6, report.getTimestamp().toInstant().toEpochMilli());
                    statement.setString(7, report.getReason());
                    statement.setBoolean(8, report.isDeleted());
                    statement.execute();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            });
        });
    }
}
