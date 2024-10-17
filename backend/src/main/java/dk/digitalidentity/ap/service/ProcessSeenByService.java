package dk.digitalidentity.ap.service;

import dk.digitalidentity.ap.dao.ProcessSeenByDao;
import dk.digitalidentity.ap.service.model.ProcessSeenByChartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessSeenByService {
	@Autowired
	private ProcessSeenByDao processSeenByDao;

	public List<ProcessSeenByChartDTO> getTop10ProcessIdsWithTitleAndCount() {
		List<Object[]> results = processSeenByDao.findTop10ProcessIdsWithTitleCountAndRank();
		List<ProcessSeenByChartDTO> dtos = new ArrayList<>();

		for (Object[] result : results) {
			int sortOrder = ((Number) result[0]).intValue();
			Long processId = ((Number) result[1]).longValue();
			String title = (String) result[2];
			Long count = ((Number) result[3]).longValue();

			dtos.add(new ProcessSeenByChartDTO(processId, title, count, sortOrder));
		}
		return dtos;
	}
}
