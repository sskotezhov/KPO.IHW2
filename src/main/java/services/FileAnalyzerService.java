package main.java.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import main.java.builders.StoredAnalyseBuilder;
import main.java.models.StoredAnalyse;
import main.java.repositories.AnalyseRepository;

@Service
public class FileAnalyzerService {
	@Autowired
	private final AnalyseRepository analyseRepository;
	
	private final static WebClient webClient = WebClient.create("http://nginx/filestoring/api");
	
	public FileAnalyzerService(AnalyseRepository analyseRepository)
	{
		this.analyseRepository = analyseRepository;
	}
	
	public StoredAnalyse storeAnalyse(String hashCode)
	{
		StoredAnalyse storedAnalyse = analyseRepository.findByHashCode(hashCode).orElseGet(
				() -> {
					StoredAnalyse _storedAnalyse = StoredAnalyseBuilder.buildFromHashAndContent(hashCode, getContentFromApi(hashCode));
					analyseRepository.save(_storedAnalyse);
					return _storedAnalyse;
				}
				);
		return storedAnalyse;
	}
	
	private String getContentFromApi(String hashCode)
	{
        return webClient.get()
                .uri("/getById?id={hashCode}", hashCode)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(String.class)
                .block();
	}
}
