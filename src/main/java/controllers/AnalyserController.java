package main.java.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import main.java.models.StoredAnalyse;
import main.java.services.FileAnalyzerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/analysis")
@Tag(name = "File Analysis API", description = "API для анализа текстовых файлов и генерации облака слов")
public class AnalyserController {
    
    @Autowired
    private final FileAnalyzerService fileAnalyzerService;
    
    public AnalyserController(FileAnalyzerService fileAnalyzerService) {
        this.fileAnalyzerService = fileAnalyzerService;
    }
    
    @GetMapping("/stats")
    @Operation(
        summary = "Получить статистику анализа файла",
        description = "Возвращает количество букв, слов, параграфов и путь к изображению с анализом",
        parameters = {
            @Parameter(
                name = "id",
                description = "Уникальный хэш-код файла",
                required = true,
                example = "a1b2c3d4e5"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Успешный запрос",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        type = "object",
                        example = """
                        {
                            "letters": 1500,
                            "words": 300,
                            "paragraphs": 20,
                            "locationImage": "/images/analysis/a1b2c3d4e5.png"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Файл не найден или ошибка анализа",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        type = "object",
                        example = """
                        {
                            "error": "Ошибка при получении анализа: Файл не найден"
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> getAnalyse(@RequestParam("id") String hashCode) {
        try {
            StoredAnalyse analyse = fileAnalyzerService.storeAnalyse(hashCode);
            
            Map<String, Object> response = Map.of(
                "letters", analyse.getLetters(),
                "words", analyse.getWords(),
                "paragraphs", analyse.getParagraphs(),
                "locationImage", analyse.getLocationimage()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Ошибка при получении анализа: " + e.getMessage()));
        }
    }
    
    @GetMapping("/cloudword/download")
    @Operation(summary = "Загрузка изображения облака слов",
              description = "Загрузка изображения по локации")
    public ResponseEntity<Resource> downloadImage(
        @RequestParam("path") String imagePath) {
        
        Path requestedPath = Paths.get(imagePath).normalize();
        
        if (!requestedPath.startsWith("cloudword/")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                   .body(null);
        }
        
        if (!imagePath.toLowerCase().endsWith(".png")) {
            return ResponseEntity.badRequest()
                   .body(null);
        }
        
        try {
            Resource resource = new InputStreamResource(Files.newInputStream(requestedPath));
            
            String filename = requestedPath.getFileName().toString();
            
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + filename + "\"")
                .body(resource);
                
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}