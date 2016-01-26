package iva.server.test.runner;

import iva.server.core.model.InterestModel;
import iva.server.core.model.Question;
import iva.server.core.model.User;
import iva.server.persistence.InterestModelRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public class ReportMakerHandler implements ReportMaker {
	
	private static final Comparator<Entry<String, Double>> categoryEntrySorter = 
			Entry.comparingByValue((v1, v2) -> v2.compareTo(v1));
	
	@Autowired
	private InterestModelRepository modelRepo;
	
	private PrintWriter reportWriter;
	
	private Map<String, Double> oldUserCategories;
	
	private String username;
	
	@Override
	public void init(User testUser) {
		username = testUser.getUsername();
		InterestModel model = testUser.getInterestModel();
		Double ageRate = model.getShortTermAgeRate();
		
		try {
			reportWriter = new PrintWriter(Files.newBufferedWriter(Paths.get("log/report.html")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		reportWriter.println("<p><b>"+(new Date().toString())+" Report for "+username+" (AgeRate = "+ageRate+")</b></p>");
		
		reportWriter.println("<table><tr><th>Long Term Categories</th><th>Score</th></tr>");
		model.getLongTermCategories().entrySet().stream().sorted(Entry.comparingByKey())
		.forEachOrdered(category -> {
			reportWriter.println("\t<tr><td>"+category.getKey()+"</td><td>+"+category.getValue()+"</td></tr>");
		});
		reportWriter.println("</table>");
		
		oldUserCategories = Collections.emptyMap();
	}
	
	@Override
	public void append(int index, Question query) {
		InterestModel model = modelRepo.findByOwnerUsername(username);
		
		reportWriter.println("<p><b>"+index+"A. Question</b><br>"+query.getQuestion()+"</p>");
		
		reportWriter.println("<table><tr><th>"+index+"B. Question Categories</th><th>Score</th><th>Source</th></tr>");
		query.getCategories().entrySet().stream().sorted(categoryEntrySorter)
		.forEachOrdered(category -> {
			reportWriter.println("\t<tr><td>"+category.getKey()+"</td><td>+"+category.getValue()+"</td><td>Question</td></tr>");
		});
		
		query.getExtraCategories().entrySet().stream().sorted(categoryEntrySorter)
		.forEachOrdered(extraCategory -> {
			reportWriter.println("\t<tr><td>"+extraCategory.getKey()+"</td><td>+"+extraCategory.getValue()+"</td><td>Graph</td></tr>");
		});
		reportWriter.println("</table>");
		
		Map<String, Double> userCategories = model.getShortTermCategories();
		userCategories.putAll(model.getLongTermCategories());
		
		reportWriter.println("<table><tr><th>"+index+"C. User Categories</th><th>Before</th><th>Increase</th><th>After</th></tr>");
		userCategories.entrySet().stream().sorted(categoryEntrySorter)
		.forEachOrdered(e -> {
			String userCategory = e.getKey();
			Double score = e.getValue();
			
			Double oldScore = oldUserCategories.getOrDefault(userCategory, 0.0);
			Double diff = score - oldScore;
			String diffString = (diff >= 0 ? "+" : "") + diff;
			reportWriter.println("\t<tr><td>"+userCategory+"</td><td>"+oldScore+"</td><td>"+diffString+"</td><td>"+score+"</td></tr>");
		});
		oldUserCategories.entrySet().stream()
		.filter(entry -> !userCategories.containsKey(entry.getKey()))
		.sorted(categoryEntrySorter)
		.forEachOrdered(deletedCategory -> {
			Double oldScore = deletedCategory.getValue();
			reportWriter.println("\t<tr><td>"+deletedCategory.getKey()+"</td><td>"+oldScore+"</td><td>"+(-oldScore)+"</td><td>"+0.0+"</td></tr>");
		});
		reportWriter.println("</table>");
		
		oldUserCategories = new HashMap<>(userCategories);
	}
	
	@Override
	public void close() throws IOException {
		reportWriter.close();
	}

}
