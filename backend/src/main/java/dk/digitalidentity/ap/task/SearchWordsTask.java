package dk.digitalidentity.ap.task;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.dao.SearchWordDao;
import dk.digitalidentity.ap.dao.model.SearchWord;
import dk.digitalidentity.ap.service.ProcessService;

@Component
@EnableScheduling
public class SearchWordsTask {

	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Autowired
	private ProcessService processService;

	@Autowired
	private SearchWordDao searchWordDao;

	// run every 15 minutes
	@Scheduled(fixedRate = 15 * 60 * 1000)
	@Transactional(rollbackFor = Exception.class)
	public void processChanges() {
		if (!runScheduled) {
			return;
		}
		
		List<String> allSearchWords = processService.getAllSearchWords();

		List<SearchWord> searchWords = allSearchWords.stream()
	               .flatMap(p -> Arrays.stream(p.split(" ")))
	               .filter(s->!s.isEmpty())
	               .map(s -> s.toLowerCase())
	               .distinct()
	               .map(s -> new SearchWord(s))
	               .collect(Collectors.toList());

		List<SearchWord> existingSearchWords = searchWordDao.findAll();

		// add new ones
		for (SearchWord searchWord : searchWords) {
			boolean found = false;

			for (SearchWord existingSearchWord : existingSearchWords) {
				if (existingSearchWord.getSearchWord().equals(searchWord.getSearchWord())) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				searchWordDao.save(searchWord);
			}
		}
		
		// delete those that are no longer used
		for (SearchWord existingSearchWord : existingSearchWords) {
			boolean found = false;

			for (SearchWord searchWord : searchWords) {
				if (existingSearchWord.getSearchWord().equals(searchWord.getSearchWord())) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				searchWordDao.delete(existingSearchWord);
			}
		}
	}
}
