package dk.digitalidentity.ap.service;

import dk.digitalidentity.ap.dao.ServiceDao;
import dk.digitalidentity.ap.dao.ServiceDao.ServiceUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceService {

	@Autowired
	private ServiceDao serviceDao;

	public List<dk.digitalidentity.ap.dao.model.Service> getServicesByName(String name) {
		return serviceDao.findByName(name);
	}

	public void saveAll(List<dk.digitalidentity.ap.dao.model.Service> services) {
		serviceDao.saveAll(services);
	}

	public Map<String, Long> getUsageGlobal() {
		List<ServiceUsage> usage = serviceDao.getUsage();

		Map<String, Long> map = new HashMap<>();
		for (ServiceUsage u : usage) {
			map.put(u.getName(), u.getCount());
		}

		return map;
	}

	public Map<String, Long> getUsageByCvr(String cvr) {
		List<ServiceUsage> usage = serviceDao.getUsageByCvr(cvr);

		Map<String, Long> map = new HashMap<>();
		for (ServiceUsage u : usage) {
			map.put(u.getName(), u.getCount());
		}

		return map;
	}
}
