package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.BookmarkDao;
import dk.digitalidentity.ap.dao.model.Bookmark;
import dk.digitalidentity.ap.dao.model.Process;

@Service
public class BookmarkService {
	
	@Autowired
	private BookmarkDao bookmarkDao;

	public void delete(Bookmark bookmark) {
		bookmarkDao.delete(bookmark);
	}

	public Bookmark getByUserAndProcess(long userId, Process process) {
		return bookmarkDao.getByUserIdAndProcess(userId, process);
	}

	public Bookmark save(Bookmark bookmark) {
		return bookmarkDao.save(bookmark);
	}

	public List<Bookmark> getByUser(long userId) {
		return bookmarkDao.getByUserId(userId);
	}
}
