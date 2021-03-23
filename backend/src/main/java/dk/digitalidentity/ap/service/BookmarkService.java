package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.BookmarkDao;
import dk.digitalidentity.ap.dao.model.Bookmark;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.User;

@Service
public class BookmarkService {
	
	@Autowired
	private BookmarkDao bookmarkDao;

	public void delete(Bookmark bookmark) {
		bookmarkDao.delete(bookmark);
	}

	public Bookmark getByUserAndProcess(User user, Process process) {
		return bookmarkDao.getByUserAndProcess(user, process);
	}

	public Bookmark save(Bookmark bookmark) {
		return bookmarkDao.save(bookmark);
	}

	public List<Bookmark> getByUser(User user) {
		return bookmarkDao.getByUser(user);
	}
}
