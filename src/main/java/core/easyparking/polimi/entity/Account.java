package core.easyparking.polimi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Account implements Serializable {

	@Id
	@Column(name = "accountId", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

	@Column(nullable = false, unique = true, length = 64)
	private String username;

	@Basic
	@Hidden
	@JsonIgnore
	@Column(nullable = false, length = 64)
	private String password;

	@Basic
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Basic
	@Column(length = 64)
	private String resetCode;

	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime dateReset;

	public Account(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}
}
