package turip.data.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import turip.data.service.CsvDataImportService;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
public class CsvDataInitializer implements CommandLineRunner {

    private final CsvDataImportService csvDataImportService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            // 리소스 패턴으로 CSV 찾기 (resources 하위 모든 폴더 포함)
            Resource[] csvResources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:/**/*.csv");

            if (csvResources.length == 0) {
                log.warn("import 할 CSV 파일이 없습니다.");
            } else {
                log.info("찾은 CSV 파일 개수: {}", csvResources.length);
                for (Resource resource : csvResources) {
                    log.info("import 대상 CSV 파일: {}", resource.getFilename());

                    // 임시 파일로 복사
                    File tempFile = File.createTempFile("csv_import_", ".csv");
                    tempFile.deleteOnExit();

                    try (InputStream inputStream = resource.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        inputStream.transferTo(outputStream);
                    }

                    // CSV 데이터 import 실행
                    csvDataImportService.importCsvData(tempFile.getAbsolutePath());
                    log.info("CSV 파일 import 완료: {}", resource.getFilename());
                }

                log.info("모든 CSV 파일 import 완료되었습니다.");
            }

            // CSV import 완료 후 favorite_content_data.sql 실행
            executeDataSql();

        } catch (Exception e) {
            log.error("CSV import 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void executeDataSql() {
        try {
            log.info("favorite_content_data.sql 실행을 시작합니다.");

            // favorite_content_data.sql 파일 읽기
            Resource dataSqlResource = new PathMatchingResourcePatternResolver()
                    .getResource("classpath:favorite_content_data.sql");

            if (!dataSqlResource.exists()) {
                log.warn("favorite_content_data.sql 파일을 찾을 수 없습니다.");
                return;
            }

            String sqlContent = StreamUtils.copyToString(
                    dataSqlResource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            // SQL 문장들을 세미콜론으로 분리하여 실행
            String[] sqlStatements = sqlContent.split(";");

            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        jdbcTemplate.execute(sql);
                        log.debug("SQL 실행 완료: {}", sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        log.error("SQL 실행 중 오류 발생: {}", e.getMessage());
                        log.error("실행 실패한 SQL: {}", sql);
                    }
                }
            }

            log.info("favorite_content_data.sql 실행이 완료되었습니다.");

        } catch (Exception e) {
            log.error("favorite_content_data.sql 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
