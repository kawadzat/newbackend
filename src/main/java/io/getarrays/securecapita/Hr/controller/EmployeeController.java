package io.getarrays.securecapita.Hr.controller;

import io.getarrays.securecapita.Hr.dto.EmployeeCsvDto;
import io.getarrays.securecapita.Hr.service.EmployeeCsvService;
import io.getarrays.securecapita.domain.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "https://processmanagement.tanawellnesscoaching.com", "http://172.18.5.36:9092", "http://securecapita.org", "http://172.18.5.56:8081"})
public class EmployeeController {

    private final EmployeeCsvService employeeCsvService;

    /**
     * Upload CSV file containing employee data
     * Expected CSV format:
     * - Header row with columns: Employee Name (or Name) and EC Number (or EC)
     * - Data rows with employee name and EC number
     * 
     * @param file CSV file to upload
     * @return List of employees with name and EC number
     */
    @PostMapping(value = "/upload-csv", consumes = {"multipart/form-data"})
    public ResponseEntity<HttpResponse> uploadCsvFile(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            log.info("Received CSV file upload request");
            
            // Check if file is null
            if (file == null) {
                log.error("File parameter is null");
                return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("File parameter is required. Please select a CSV file to upload.")
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
                );
            }
            
            String fileName = file.getOriginalFilename();
            log.info("File name: {}, Size: {} bytes, Content Type: {}", 
                fileName != null ? fileName : "null", 
                file.getSize(), 
                file.getContentType());
            
            // Check if file is empty
            if (file.isEmpty()) {
                log.error("File is empty");
                return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("CSV file is empty. Please select a valid CSV file.")
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
                );
            }
            
            // Check file extension (more lenient - accept if extension is .csv or content type suggests CSV)
            boolean isValidCsv = false;
            if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
                isValidCsv = true;
            } else if (file.getContentType() != null) {
                String contentType = file.getContentType().toLowerCase();
                isValidCsv = contentType.contains("csv") || 
                             contentType.equals("text/plain") || 
                             contentType.equals("application/vnd.ms-excel");
            }
            
            if (!isValidCsv) {
                log.error("Invalid file type. File name: {}, Content Type: {}", fileName, file.getContentType());
                return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("File must be a CSV file. Received: " + (fileName != null ? fileName : "unknown") + 
                                " (Content-Type: " + file.getContentType() + ")")
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
                );
            }
            
            List<EmployeeCsvDto> employees = employeeCsvService.parseCsvFile(file);
            
            log.info("Successfully parsed {} employees from CSV file", employees.size());
            
            return ResponseEntity.ok(
                HttpResponse.builder()
                    .timeStamp(now().toString())
                    .data(of("employees", employees, "totalCount", employees.size()))
                    .message("CSV file uploaded and parsed successfully")
                    .status(OK)
                    .statusCode(OK.value())
                    .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid CSV file: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                    .timeStamp(now().toString())
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build()
            );
        } catch (Exception e) {
            log.error("Error processing CSV file: {}", e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse.builder()
                    .timeStamp(now().toString())
                    .message("Error processing CSV file: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build()
            );
        }
    }
}

