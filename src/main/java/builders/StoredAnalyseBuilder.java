package main.java.builders;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import main.java.models.StoredAnalyse;
import main.java.services.CounterAnalyzerService;

public class StoredAnalyseBuilder {
	
	private final static WebClient webClient = WebClient.create("https://quickchart.io");
	
	private static final String UPLOAD_DIR = "cloudword/";
	
	public static StoredAnalyse buildFromHashAndContent(String hashCode, String content)
	{
		StoredAnalyse storedAnalyse = new StoredAnalyse();
		storedAnalyse.setHashCode(hashCode);
		storedAnalyse.setLocationimage(getCloudWord(hashCode, content));
		storedAnalyse.setLetters(CounterAnalyzerService.countLetters(content));
		storedAnalyse.setWords(CounterAnalyzerService.countWords(content));
		storedAnalyse.setParagraphs(CounterAnalyzerService.countParagraphs(content));
		return storedAnalyse;
	}
	
	public static String getCloudWord(String hashCode, String content)
	{
        try {
            byte[] imageBytes = webClient.get()
                    .uri("/wordcloud?text={text}&format=png", content)
                    .accept(MediaType.IMAGE_PNG)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            Path path = Paths.get(UPLOAD_DIR+hashCode+".png");
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(
                    path,
                    imageBytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            return path.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при скачивании изображения", e);
        }
	}
}
