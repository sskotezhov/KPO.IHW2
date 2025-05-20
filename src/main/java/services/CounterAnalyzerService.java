package main.java.services;

import org.springframework.stereotype.Service;

@Service
public class CounterAnalyzerService {
		public static Integer countWords(String text)
		{
			return text.split("\\s+").length;
		}
		public static Integer countParagraphs(String text)
		{
			return text.split("\n\n").length;
		}
		public static Integer countLetters(String text)
		{
			return text.replaceAll("\\s", "").length();
		}
}
