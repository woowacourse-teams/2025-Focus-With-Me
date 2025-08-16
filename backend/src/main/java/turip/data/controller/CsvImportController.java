package turip.data.controller;

import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.data.service.CsvDataImportService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/csv")
public class CsvImportController {

    private final CsvDataImportService csvDataImportService;

    @PostMapping("/import")
    public ResponseEntity<String> importCsvData(@RequestParam String fileName) {
        try {
            // resources 폴더에서 CSV 파일 찾기
            ClassPathResource resource = new ClassPathResource(fileName);
            File csvFile = resource.getFile();

            if (!csvFile.exists()) {
                return ResponseEntity.badRequest().body("CSV 파일을 찾을 수 없습니다: " + fileName);
            }

            csvDataImportService.importCsvData(csvFile.getAbsolutePath());
            return ResponseEntity.ok("CSV 데이터 import 완료되었습니다.");

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("CSV 파일 읽기 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("CSV 데이터 import 실패: " + e.getMessage());
        }
    }
} 
