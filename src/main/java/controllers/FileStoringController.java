package main.java.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import main.java.services.FileStoringService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "File Storage API", description = "API для загрузки и получения файлов")
public class FileStoringController {

    @Autowired
    private final FileStoringService fileStoringService;
    
    public FileStoringController(FileStoringService fileStoringService) {
        this.fileStoringService = fileStoringService;
    }
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Загрузить файл",
        description = "Загружает файл на сервер и возвращает идентификатор файла и URL для анализа",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Файл успешно загружен",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        type = "object",
                        example = """
                        {
                            "fileId": "a1b2c3d4e5",
                            "analysisUrl": "http://localhost:8080/api/analysis/stats?fileId=a1b2c3d4e5"
                        }
                        """
                    )
                ),
                headers = @io.swagger.v3.oas.annotations.headers.Header(
                    name = HttpHeaders.LOCATION,
                    description = "URL для анализа файла",
                    schema = @Schema(type = "string")
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Ошибка загрузки файла",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "error: Не удалось сохранить файл")
                )
            )
        }
    )
    public ResponseEntity<?> uploadFile(
        @Parameter(
            description = "Файл для загрузки",
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        @RequestParam("file") MultipartFile file) {
        
        try {
            String fileId = fileStoringService.storeFile(file);
            String analysisUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/analysis/stats")
                    .queryParam("fileId", fileId)
                    .toUriString();

            return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, analysisUrl)
                .body(Map.of(
                    "fileId", fileId,
                    "analysisUrl", analysisUrl
                ));
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("error: " + e.getMessage());
        }
    }
    
    @GetMapping("/getById")
    @Operation(
        summary = "Получить содержимое файла по ID",
        description = "Возвращает содержимое файла по его идентификатору",
        parameters = {
            @Parameter(
                name = "id",
                description = "Идентификатор файла",
                required = true,
                example = "a1b2c3d4e5"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Содержимое файла",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string")
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Файл не найден",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "error: Файл с айди a1b2c3d4e5 не найден")
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Внутренняя ошибка сервера",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "error: Внутренняя ошибка сервера")
                )
            )
        }
    )
    public ResponseEntity<?> getContentById(@RequestParam("id") String hashCode) {
        try {
            String content = fileStoringService.getContent(hashCode);
            return ResponseEntity.ok(content);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("error: Файл с айди " + hashCode + " не найден");
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                   .body("error: Внутренняя ошибка сервера");
        }
    }
}