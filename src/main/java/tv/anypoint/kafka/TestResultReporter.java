package tv.anypoint.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import tv.anypoint.utils.FileUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by shcho on 10/23/15.
 */
@Slf4j
public class TestResultReporter {

    /*public static void main(String[] argv) {

        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("jetstream-spring-test.xml");

        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");

        report(jdbcTemplate);

    }*/

    private static void report(JdbcTemplate jdbcTemplate) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("SELECT ");
        countSql.append("Genre.totalCount,");
        countSql.append("CampaignGenre.totalCount,");
        countSql.append("Region.totalCount,");
        countSql.append("CampaignRegion.totalCount,");
        countSql.append("Period.totalCount,");
        countSql.append("CampaignPeriod.totalCount ");
        countSql.append("FROM ");
        countSql.append("(SELECT COUNT(*) as totalCount FROM ImpressionLogCampaignGenre) as CampaignGenre,");
        countSql.append("(SELECT COUNT(*) as totalCount FROM ImpressionLogCampaignPeriod) as CampaignPeriod,");
        countSql.append("(SELECT COUNT(*) as totalCount FROM ImpressionLogCampaignRegion) as CampaignRegion,");
        countSql.append("(SELECT COUNT(*) as totalCount FROM ImpressionLogGenre) as Genre,");
        countSql.append("(SELECT COUNT(*) as totalCount FROM ImpressionLogPeriod) as Period,");
        countSql.append("(SELECT COUNT(*) as totalCount FROM ImpressionLogRegion) as Region;");

        SqlRowSet countSqlRowSet = jdbcTemplate.queryForRowSet(countSql.toString());

        if (countSqlRowSet.next()) {

            log.debug("---------------------------");
            log.debug("     Row Count Summary     ");
            log.debug("---------------------------");
            log.debug("Genre          :: {}", String.format("%8s", countSqlRowSet.getString(1)));
            log.debug("CampaignGenre  :: {}", String.format("%8s", countSqlRowSet.getString(2)));
            log.debug("Region         :: {}", String.format("%8s", countSqlRowSet.getString(3)));
            log.debug("CampaignRegion :: {}", String.format("%8s", countSqlRowSet.getString(4)));
            log.debug("Period         :: {}", String.format("%8s", countSqlRowSet.getString(5)));
            log.debug("CampaignPeriod :: {}", String.format("%8s", countSqlRowSet.getString(6)));
        }

        StringBuilder sumSql = new StringBuilder();

        sumSql.append("SELECT ");
        sumSql.append("Genre.totalSum,");
        sumSql.append("CampaignGenre.totalSum,");
        sumSql.append("Region.totalSum,");
        sumSql.append("CampaignRegion.totalSum,");
        sumSql.append("Period.totalSum,");
        sumSql.append("CampaignPeriod.totalSum ");
        sumSql.append("FROM ");
        sumSql.append("(SELECT SUM(totalImpression) as totalSum FROM ImpressionLogCampaignGenre) as CampaignGenre,");
        sumSql.append("(SELECT SUM(totalImpression) as totalSum FROM ImpressionLogCampaignPeriod) as CampaignPeriod,");
        sumSql.append("(SELECT SUM(totalImpression) as totalSum FROM ImpressionLogCampaignRegion) as CampaignRegion,");
        sumSql.append("(SELECT SUM(totalImpression) as totalSum FROM ImpressionLogGenre) as Genre,");
        sumSql.append("(SELECT SUM(totalImpression) as totalSum FROM ImpressionLogPeriod) as Period,");
        sumSql.append("(SELECT SUM(totalImpression) as totalSum FROM ImpressionLogRegion) as Region;");

        SqlRowSet sumSqlRowSet = jdbcTemplate.queryForRowSet(sumSql.toString());

        if (sumSqlRowSet.next()) {

            log.debug("---------------------------");
            log.debug("      Row Sum Summary      ");
            log.debug("---------------------------");
            log.debug("Total Impression :: {}", (sumSqlRowSet.getString(1) == null)
                    ? String.format("%6s", 0) : String.format("%6s", sumSqlRowSet.getString(1)));
        }

    }

    public static void truncateTables(DataSource dataSource) {

        String sqlScriptPath;

        String userDir = System.getProperty("user.dir");

        if (FileUtils.existsDir(userDir + "/build/classes/main/config/sql")) {
            sqlScriptPath = userDir + "/build/classes/main/config/sql/truncate.sql";
        } else {
            sqlScriptPath = userDir + "/config/sql/truncate.sql";
        }

        try(Connection conn = dataSource.getConnection()) {

            log.debug("Table Truncate Job :: Start...");

            ScriptUtils.executeSqlScript(conn, new FileSystemResource(sqlScriptPath));

            log.debug("Table Truncate Job :: End...");

        } catch (SQLException e) {
            log.error(e.getMessage());
        }


    }

}
