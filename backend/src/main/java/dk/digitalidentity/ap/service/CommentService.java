package dk.digitalidentity.ap.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.CommentDao;
import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.Process;

@Service
public class CommentService {

	@Autowired
	private CommentDao commentDao;

	public Comment save(Comment comment) {
		return commentDao.save(comment);
	}

	public List<Comment> getByProcess(Process process) {
		return commentDao.getByProcess(process);
	}
	
	public List<Comment> getByCreatedBetween(Date start, Date end) {
		return commentDao.getByCreatedBetween(start, end);
	}
}
