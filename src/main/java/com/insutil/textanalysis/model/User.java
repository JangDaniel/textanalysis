package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_qa_user")
@Getter
@Setter
@Builder
@ToString
public class User {
	@Id
	private Long id;
	private String userId;
	private String userName;
	private Long departmentId;
	private Long roleId;
	private Boolean evaluator;
	private Boolean active;
	private Boolean enabled;

	@Transient
	@With
	private List<Allocation> allocations;
}
