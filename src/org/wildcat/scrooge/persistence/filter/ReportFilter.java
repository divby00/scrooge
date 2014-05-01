package org.wildcat.scrooge.persistence.filter;


public class ReportFilter {

	private String	operator;
	private Double	importe;
	private Integer	fDay;
	private Integer	fMonth;
	private Integer	fYear;
	private Integer	tDay;
	private Integer	tMonth;
	private Integer	tYear;
	private String	category;


	public Double getImporte() {
		return importe;
	}


	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	public void setImporte(Double importe) {
		this.importe = importe;
	}


	public Integer getfDay() {
		return fDay;
	}


	public void setfDay(Integer fDay) {
		this.fDay = fDay;
	}


	public Integer getfMonth() {
		return fMonth;
	}


	public void setfMonth(Integer fMonth) {
		this.fMonth = fMonth;
	}


	public Integer getfYear() {
		return fYear;
	}


	public void setfYear(Integer fYear) {
		this.fYear = fYear;
	}


	public Integer gettDay() {
		return tDay;
	}


	public void settDay(Integer tDay) {
		this.tDay = tDay;
	}


	public Integer gettMonth() {
		return tMonth;
	}


	public void settMonth(Integer tMonth) {
		this.tMonth = tMonth;
	}


	public Integer gettYear() {
		return tYear;
	}


	public void settYear(Integer tYear) {
		this.tYear = tYear;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public ReportFilter(Double importe, String operator, Integer fDay, Integer fMonth, Integer fYear, Integer tDay, Integer tMonth, Integer tYear, String category) {
		super();
		this.importe = importe;
		this.operator = operator;
		this.fDay = fDay;
		this.fMonth = fMonth;
		this.fYear = fYear;
		this.tDay = tDay;
		this.tMonth = tMonth;
		this.tYear = tYear;
		this.category = category;
	}
}
