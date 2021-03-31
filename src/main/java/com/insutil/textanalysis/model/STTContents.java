package com.insutil.textanalysis.model;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@Table(value = "t_ta_stt_contents")
public class STTContents extends BaseModel {
	@Id
	private Long id;
	private String uniquekey;
	private String contractNo;
	private LocalDate callDate;
	private LocalDateTime recordEndTime;
	private Long agentId;
	private String customerId;
	private String sttText;
	private String similarityCode;
	private String customerSSN;
	private String customerName;
	private String customerTelNum;
	private Long productId;
	private Long stateCode;

	@Transient
	@With
	private Product product;

	@Transient
	@With
	private User agent;

	public STTContents update(STTContents data) {
		if (data.uniquekey != null) this.uniquekey = data.uniquekey;
		if (data.contractNo != null) this.contractNo = data.contractNo;
		if (data.callDate != null) this.callDate = data.callDate;
		if (data.recordEndTime != null) this.recordEndTime = data.recordEndTime;
		if (data.agentId != null) this.agentId = data.agentId;
		if (data.customerId != null) this.customerId = data.customerId;
		if (data.sttText != null) this.sttText = data.sttText;
		if (data.similarityCode != null) this.similarityCode = data.similarityCode;
		if (data.customerSSN != null) this.customerSSN = data.customerSSN;
		if (data.customerName != null) this.customerName = data.customerName;
		if (data.customerTelNum != null) this.customerTelNum = data.customerTelNum;
		if (data.productId != null) this.productId = data.productId;
		if (data.stateCode != null) this.stateCode = data.stateCode;
		if (data.updateUser != null) this.updateUser = data.updateUser;
		return this;
	}
}

/*
CREATE TABLE `t_ta_stt_contents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uniquekey` varchar(24) NOT NULL,
  `contract_no` varchar(12) NOT NULL,
  `call_date` varchar(8) NOT NULL,
  `record_end_time` datetime NOT NULL,
  `agent_id` int(11) NOT NULL,
  `customer_id` varchar(15) NOT NULL,
  `stt_text` mediumtext NOT NULL,
  `similarity_code` varchar(10) DEFAULT NULL,
  `customer_ssn` varchar(13) DEFAULT NULL,
  `customer_name` varchar(16) DEFAULT NULL,
  `customer_tel_num` varchar(64) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_ta_stt_contents_uniquekey_UN` (`uniquekey`),
  KEY `t_ta_stt_contents_state_code_FK` (`state_code`),
  CONSTRAINT `t_ta_stt_contents_state_code_FK` FOREIGN KEY (`state_code`) REFERENCES `t_ins_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
*/