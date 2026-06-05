package com.cts.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="audit_package")
public class AuditPackage {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="package_id")
	private Long packageId;

	@Column(name="period_start")
	private LocalDateTime periodStart;

	@Column(name="period_end")
	private LocalDateTime periodEnd;

	@Column(name="contentsjson",columnDefinition = "TEXT")
	private String contentsJSON;

	@Column(name="generated_at")
	private LocalDateTime generatedAt;

	@Column(name="package_uri")
	private String packageUri;


}
