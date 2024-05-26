package dev.plex.medina.storage;

import com.google.common.collect.Lists;
import dev.plex.medina.Medina;
import dev.plex.medina.MedinaBase;
import dev.plex.medina.data.Report;
import dev.plex.medina.util.MedinaUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLReports implements MedinaBase
{
    private static final String SELECT = "SELECT * FROM `reports` WHERE reportedUUID=?";
    private static final String INSERT = "INSERT INTO `reports` (`reportId`, `reporterUUID`, `reporterName`, `reportedUUID`, `reportedName`, `timestamp`, `reason`, `deleted`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM `reports` WHERE reportId=? AND reportedUUID=?";

    public CompletableFuture<List<Report>> getReports(UUID reportedUUID)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            List<Report> reports = Lists.newArrayList();
            try (Connection con = plugin.getSqlConnection().getCon())
            {
                PreparedStatement statement = con.prepareStatement(SELECT);
                statement.setString(1, reportedUUID.toString());
                ResultSet set = statement.executeQuery();
                while (set.next())
                {
                    Report report = new Report(
                            UUID.fromString(set.getString("reporterUUID")),
                            set.getString("reporterName"),
                            reportedUUID,
                            set.getString("reportedName"),
                            ZonedDateTime.ofInstant(Instant.ofEpochMilli(set.getLong("timestamp")), ZoneId.of(MedinaUtils.TIMEZONE)),
                            set.getString("reason"),
                            set.getBoolean("deleted"));
                    reports.add(report);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return reports;
            }
            return reports;
        });
    }

    public CompletableFuture<Void> deleteReport(int reportId, UUID reportedUUID)
    {
        return CompletableFuture.runAsync(() ->
        {
            try (Connection con = plugin.getSqlConnection().getCon())
            {
                PreparedStatement statement = con.prepareStatement(DELETE);
                statement.setInt(1, reportId);
                statement.setString(2, reportedUUID.toString());
                statement.execute();
            }
            catch (SQLException e)
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
                    statement.setString(1, report.getReporterUUID().toString());
                    statement.setString(2, report.getReporterName());
                    statement.setString(3, report.getReportedUUID().toString());
                    statement.setString(4, report.getReportedName());
                    statement.setLong(5, report.getTimestamp().toInstant().toEpochMilli());
                    statement.setString(6, report.getReason());
                    statement.setBoolean(7, report.isDeleted());
                    statement.execute();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            });
        });
    }
}