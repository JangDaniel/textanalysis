package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_qa_user")
@Getter
@Setter
@Builder
public class User {
	@Id
	private Long id;
	private String userId;
	private String userName;
	private Long departmentId;
	private Long roleId;
	private Boolean active;
	private Boolean enabled;
}
