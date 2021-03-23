package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "attachments")
public class Attachment {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	@Size(max = 255)
	private String fileName;

	@Column
	@Size(max = 255)
	private String url;
	
	@Column
	@NotNull
	private boolean visibleToOtherMunicipalities;

    @JsonIgnore
	@ManyToOne
	@JoinColumn(name = "process_id")
	private Process process;
}
