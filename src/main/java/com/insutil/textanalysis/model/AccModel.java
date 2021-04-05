package com.insutil.textanalysis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class AccModel {
	private float value;
	private int count;
	public float getAverage() {
		return value/count;
	}
	public float getValue() {
		return value;
	}
	public int getCount() {
		return count;
	}
	public void addValue(float value) {
		this.value += value;
	}
	public void addCount() {
		this.count++;
	}
}
