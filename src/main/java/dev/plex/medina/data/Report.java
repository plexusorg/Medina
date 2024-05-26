package dev.plex.medina.data;

import com.google.gson.GsonBuilder;
import dev.plex.medina.storage.annotation.PrimaryKey;
import dev.plex.medina.storage.annotation.TableName;
import dev.plex.medina.storage.annotation.VarcharLimit;
import dev.plex.medina.util.adapter.ZonedDateTimeAdapter;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@TableName("reports")
public class Report
{
    @PrimaryKey
    private final int reportId; // This will be automatically set from addReport

    @Getter
    private final UUID reporterUUID;
    private final String reporterName;

    @Getter
    private final UUID reportedUUID;
    private final String reportedName;

    @Getter
    private final ZonedDateTime timestamp;

    @VarcharLimit(2000)
    @Getter
    private final String reason;

    @Getter
    private final boolean deleted;

    public String toJSON()
    {
        return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create().toJson(this);
    }
}
