package com.intelligent.custom.handlers;

public class ClassificationCustom {

	private int id;
	private String partNumber;
	private String category;
	private String description;
	private String value;
	private String tolerance;
	private String ratedVoltage;
	private String libraryPath;
	private String libraryRef;
	private String footprintPath;
	private String footprintRef;
	private String supplier;
	private String supplierPartNumber;
	private String pricing;
	private String componentLink1Description;
	private String componentLink1URL;
	private Integer order;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTolerance() {
		return tolerance;
	}

	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}

	public String getRatedVoltage() {
		return ratedVoltage;
	}

	public void setRatedVoltage(String ratedVoltage) {
		this.ratedVoltage = ratedVoltage;
	}

	public String getLibraryPath() {
		return libraryPath;
	}

	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

	public String getLibraryRef() {
		return libraryRef;
	}

	public void setLibraryRef(String libraryRef) {
		this.libraryRef = libraryRef;
	}

	public String getFootprintPath() {
		return footprintPath;
	}

	public void setFootprintPath(String footprintPath) {
		this.footprintPath = footprintPath;
	}

	public String getFootprintRef() {
		return footprintRef;
	}

	public void setFootprintRef(String footprintRef) {
		this.footprintRef = footprintRef;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplierPartNumber() {
		return supplierPartNumber;
	}

	public void setSupplierPartNumber(String supplierPartNumber) {
		this.supplierPartNumber = supplierPartNumber;
	}

	public String getPricing() {
		return pricing;
	}

	public void setPricing(String pricing) {
		this.pricing = pricing;
	}

	public String getComponentLink1Description() {
		return componentLink1Description;
	}

	public void setComponentLink1Description(String componentLink1Description) {
		this.componentLink1Description = componentLink1Description;
	}

	public String getComponentLink1URL() {
		return componentLink1URL;
	}

	public void setComponentLink1URL(String componentLink1URL) {
		this.componentLink1URL = componentLink1URL;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order=order;
	}

	public String toERPTemplate() {
		return "'" + getPartNumber() + "','" + getCategory() + "','Y','"
				+ getDescription() + "','"
				+ (getValue() == "" ? getSupplierPartNumber() : getValue())
				+ "','" + getTolerance() + "','','" + getRatedVoltage()
				+ "','','','" + getLibraryPath() + "','" + getLibraryRef()
				+ "','" + getFootprintPath() + "','" + getFootprintRef()
				+ "','','','','','','','','','" + getSupplier() + "','"
				+ getSupplierPartNumber() + "','" + getPricing() + "','"
				+ getComponentLink1Description() + "','"
				+ getComponentLink1URL() + "','','','','','','',''";

	}
}
