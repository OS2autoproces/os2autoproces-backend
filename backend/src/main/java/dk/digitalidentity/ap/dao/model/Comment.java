package dk.digitalidentity.ap.dao.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
public class Comment {

	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 255)
	@CreatedBy
	private String name;

	@Size(max = 1200)
	private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Europe/Copenhagen")
	@CreationTimestamp
	private Date created;

    @JsonIgnore
	@ManyToOne
	@JoinColumn(name = "process_id")
	private Process process;
}