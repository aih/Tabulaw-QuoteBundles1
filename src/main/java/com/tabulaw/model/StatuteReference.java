package com.tabulaw.model;

import com.tabulaw.util.StringUtil;


public class StatuteReference extends Reference {

	private static final long serialVersionUID = 1L;

	private String title;
	
	private String reporter;
	
	private int section;
	
	private int year;
	
	private String subSection;
	
	private String subSubSection;

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getReporter() {
		return reporter;
	}
	
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getSubSection() {
		return subSection;
	}
	
	public void setSubSection(String subSection) {
		this.subSection = subSection;
	}
	
	public String getSubSubSection() {
		return subSubSection;
	}
	
	public void setSubSubSection(String subSubSection) {
		this.subSubSection = subSubSection;
	}

	public int getSection() {
		return section;
	}
	
	public void setSection(int section) {
		this.section = section;
	}

	@Override
	protected IEntity newInstance() {
		return new StatuteReference();
	}

	@Override
	public String getEntityType() {
		return null;
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		StatuteReference ref = (StatuteReference) cln;
		ref.reporter = reporter;
		ref.title = title;
		ref.subSection = subSection;
		ref.section = section;
		ref.subSubSection = subSubSection;
		ref.year = year;
	}

	@Override
	public String format(ReferenceFormat format, Quote quote) {
		switch (format) {
			case TITLE : 
				return title;
			case HTML:
			case PLAIN:
				return formatFull();
			case SUB_TITLE:
			case SHORT_SUB_TITLE:
				return formatSubTitle();			
		}
		return formatFull();
	}
	
	private String formatFull() {
		StringBuilder sb = new StringBuilder(512);
		sb.append(title);
		sb.append(" ");
		sb.append(reporter);
		sb.append(" § ");
		sb.append(section);
		if (! StringUtil.isEmpty(subSection)) {
			sb.append("(");
			sb.append(subSection);
			sb.append(")");
		}
		if (! StringUtil.isEmpty(subSubSection)) {
			sb.append("(");
			sb.append(subSubSection);
			sb.append(")");
		}
		if (year > 0) {
			sb.append(" (");
			sb.append(year);
			sb.append(")");
		}
		return sb.toString();
	}
	
	private String formatSubTitle() {
		StringBuilder sb = new StringBuilder(512);
		sb.append(reporter);
		sb.append(" ");
		sb.append(section);
		if (! StringUtil.isEmpty(subSection)) {
			sb.append("(");
			sb.append(subSection);
			sb.append(")");
		}
		if (! StringUtil.isEmpty(subSubSection)) {
			sb.append("(");
			sb.append(subSubSection);
			sb.append(")");
		}
		if (year > 0) {
			sb.append(" (");
			sb.append(year);
			sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public String serializeToString() {
		StringBuilder sb = new StringBuilder(512); 
		sb.append("|title::");
		sb.append(title);
		sb.append("|reporter::");
		sb.append(reporter);
		sb.append("|section::");
		sb.append(section);
		sb.append("|subSection::");
		sb.append(subSection);
		sb.append("|subSubSection::");
		sb.append(subSubSection);
		sb.append("|year::");
		sb.append(year);		
		sb.append("|url::");
		sb.append(url);
		return sb.toString();
	}

	@Override
	public void deserializeFromString(String data) {
		String[] sarr1 = data.split("\\|");
		
		for(String sub : sarr1) {
			String[] sarr2 = sub.split("::");
			String fieldName = sarr2[0];
			String value = (sarr2.length == 2) ? sarr2[1] : "";
			if("title".equals(fieldName)) {
				title = value;
			}
			else if("reporter".equals(fieldName)) {
				reporter = value;
			}
			else if("subSection".equals(fieldName)) {
				subSection = value;
			}
			else if("url".equals(fieldName)) {
				url = value;
			}
			else if("section".equals(fieldName)) {
				section = Integer.parseInt(value);
			} 
			else if ("subSubSection".equals(fieldName)) {
				subSubSection = value;
			}
			else if("year".equals(fieldName)) {
				year = Integer.parseInt(value);
			} 			
		}
	}	
}
