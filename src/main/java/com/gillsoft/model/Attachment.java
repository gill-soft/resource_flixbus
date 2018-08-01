package com.gillsoft.model;

import java.io.Serializable;

public class Attachment implements Serializable {

	private static final long serialVersionUID = -7620577087605138120L;

	private String title;

	private String rel;

	private String href;

	private String type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
