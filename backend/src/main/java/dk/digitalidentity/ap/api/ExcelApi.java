package dk.digitalidentity.ap.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.querydsl.core.types.Predicate;

import dk.digitalidentity.ap.dao.AttachmentDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Attachment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.xls.ProcessXlsView;

@RestController
@CrossOrigin(exposedHeaders = "x-csrf-token")
@RequestMapping("/api/excel")
public class ExcelApi {
	
	@Autowired
	private ProcessDao processDao;

	@Autowired
	private AttachmentDao attachmentDao;

	@GetMapping
	public ModelAndView getExcel(HttpServletResponse response, @QuerydslPredicate(root = Process.class) Predicate predicate) {
		final List<Process> processes = new ArrayList<>();
		final Map<Long, List<Attachment>> attachmentMap = new HashMap<>();
		
		processDao.findAll(predicate)
				.forEach(p -> processes.add(p));
		
		for (Process process : processes) {
			List<Attachment> attachments = attachmentDao.findByProcess(process);
			
			if (attachments != null && attachments.size() > 0) {
				attachmentMap.put(process.getId(), attachments);
			}
		}
		
		Map<String, Object> model = new HashMap<>();
		model.put("processes", processes);
		model.put("attachmentMap", attachmentMap);

		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"processes.xls\"");

		return new ModelAndView(new ProcessXlsView(), model);
	}
}
