package backend.model;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "comic")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;

	@Column(nullable = false)
	private String name;

	@Column
	private String signlessName;

	@Column()
	private String description;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@Nullable
	private User publisher;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "chapter_id")
	@JsonIgnore
	private List<Chapter> chapterList;

	@CreationTimestamp
	private Date createdDate;

	@UpdateTimestamp
	private Date updatedDate;

	@Column
	private String writer;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name  = "comic_tag", 
	joinColumns = @JoinColumn(name = "comic_id"),
	inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@JsonIgnore
	@Nullable
	private Set<Tag> tags = new HashSet<>();
}
