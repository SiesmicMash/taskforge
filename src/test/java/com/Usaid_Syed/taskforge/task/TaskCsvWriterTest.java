package com.Usaid_Syed.taskforge.task;

import com.Usaid_Syed.taskforge.CSV.TaskCsvWriter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskCsvWriterTest {
    @Test
    void escape_plainValue_isUnchanged() {
        assertThat(TaskCsvWriter.escape("hello")).isEqualTo("hello");
    }

    @Test
    void escape_null_becomesEmpty() {
        assertThat(TaskCsvWriter.escape(null)).isEmpty();
    }

    @Test
    void escape_commaAndQuotes_areQuotedAndDoubled() {
        assertThat(TaskCsvWriter.escape("a,\"b\"")).isEqualTo("\"a,\"\"b\"\"\"");
    }

    @Test
    void escape_newline_isQuoted() {
        assertThat(TaskCsvWriter.escape("line1\nline2")).isEqualTo("\"line1\nline2\"");
    }

    @Test
    void escape_formulaPrefix_isNeutralized() {
        assertThat(TaskCsvWriter.escape("=1+1")).isEqualTo("'=1+1");
        assertThat(TaskCsvWriter.escape("@SUM(A1)")).isEqualTo("'@SUM(A1)");
    }

    @Test
    void toCsv_includesHeaderAndRows() {
        TaskResponse task = new TaskResponse(1L, "Write, test", null, TaskStatus.TODO,
                Instant.parse("2026-11-10T00:00:00Z"));

        String csv = TaskCsvWriter.toCsv(List.of(task));

        assertThat(csv).startsWith("id,title,description,status,createdAt\r\n");
        assertThat(csv).contains("1,\"Write, test\",,TODO,2026-11-10T00:00:00Z");
    }
}
