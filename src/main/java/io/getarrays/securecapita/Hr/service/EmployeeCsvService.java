package io.getarrays.securecapita.Hr.service;

import io.getarrays.securecapita.Hr.dto.EmployeeCsvDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmployeeCsvService {

    public List<EmployeeCsvDto> parseCsvFile(MultipartFile file) throws Exception {
        List<EmployeeCsvDto> employees = new ArrayList<>();
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty or null");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.toLowerCase().endsWith(".csv") && 
            !file.getContentType().equals("text/csv") && 
            !file.getContentType().equals("application/vnd.ms-excel"))) {
            throw new IllegalArgumentException("File must be a CSV file. Received: " + (fileName != null ? fileName : "unknown"));
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;
            int nameColumnIndex = -1;
            int ecNumberColumnIndex = -1;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parse CSV line (handling quoted values)
                String[] columns = parseCsvLine(line);
                
                if (isFirstLine) {
                    // Header row - find column indices
                    for (int i = 0; i < columns.length; i++) {
                        String header = columns[i].trim().toLowerCase();
                        // More flexible name detection
                        if (nameColumnIndex == -1 && (header.contains("name") || header.contains("employee"))) {
                            nameColumnIndex = i;
                            log.debug("Found name column at index {}: {}", i, columns[i]);
                        }
                        // More flexible EC number detection
                        if (ecNumberColumnIndex == -1 && (header.contains("ec") || header.contains("number"))) {
                            // Check if it's specifically EC number, not just any number
                            if (header.contains("ec") || header.startsWith("ec") || header.endsWith("ec")) {
                                ecNumberColumnIndex = i;
                                log.debug("Found EC number column at index {}: {}", i, columns[i]);
                            }
                        }
                    }
                    
                    if (nameColumnIndex == -1 || ecNumberColumnIndex == -1) {
                        String errorMsg = String.format(
                            "CSV must contain 'Employee Name' (or 'Name') and 'EC Number' (or 'EC') columns. " +
                            "Found columns: %s. Name column found: %s, EC column found: %s",
                            String.join(", ", columns),
                            nameColumnIndex != -1 ? "Yes" : "No",
                            ecNumberColumnIndex != -1 ? "Yes" : "No"
                        );
                        log.error(errorMsg);
                        throw new IllegalArgumentException(errorMsg);
                    }
                    
                    isFirstLine = false;
                    continue;
                }
                
                // Data row
                if (columns.length > Math.max(nameColumnIndex, ecNumberColumnIndex)) {
                    String employeeName = columns[nameColumnIndex].trim();
                    String ecNumber = columns[ecNumberColumnIndex].trim();
                    
                    if (!employeeName.isEmpty() || !ecNumber.isEmpty()) {
                        employees.add(EmployeeCsvDto.builder()
                                .employeeName(employeeName)
                                .ecNumber(ecNumber)
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing CSV file: {}", e.getMessage(), e);
            throw new Exception("Failed to parse CSV file: " + e.getMessage(), e);
        }
        
        if (employees.isEmpty()) {
            throw new IllegalArgumentException("No employee data found in CSV file");
        }
        
        log.info("Successfully parsed {} employees from CSV file", employees.size());
        return employees;
    }
    
    /**
     * Parse a CSV line handling quoted values
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    current.append('"');
                    i++;
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // End of field
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // Add last field
        result.add(current.toString().trim());
        
        return result.toArray(new String[0]);
    }
}

