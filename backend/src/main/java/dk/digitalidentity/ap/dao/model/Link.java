package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Link {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 1024)
	private String url;

	@Column(nullable = false)
	private boolean internal;

	public Link cloneMe() {
		Link link = new Link();
		link.setId(id);
		link.setUrl(url);
		link.setInternal(internal);
		
		return link;
	}
}
