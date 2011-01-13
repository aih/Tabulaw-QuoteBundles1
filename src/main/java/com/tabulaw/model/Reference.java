package com.tabulaw.model;

public abstract class Reference extends EntityBase {
	public static enum ReferenceFormat {
		PLAIN,
		HTML,
		TITLE,
		SUB_TITLE,
		SHORT_SUB_TITLE;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Surrogate primary key.
	 */
	protected String id;
	
	protected String url;	
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if(id == null) throw new NullPointerException();
		this.id = id;
	}
	
	/**
	 * @return the sourcing url of this case.
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		Reference ref = (Reference) cln;
		ref.id = id;
		ref.url = url;
	}
		
	@Override
	public String descriptor() {
		return format(ReferenceFormat.PLAIN, null);
	}

	public abstract String format(ReferenceFormat format, Quote quote);	
	
	public abstract String serializeToString();
	
	public abstract void deserializeFromString(String data);
	
	public static Reference createReferenceByType(String type) {
		if ("casedoc".equals(type)) {
			return new CaseRef();
		} else if ("statutedoc".equals(type)) {
			return new StatuteReference();
		} else if ("regulationdoc".equals(type)) {
			return new RegulationReference();
		}
		throw new IllegalArgumentException("unsupported reference type");
	}
	
	public static String getReferenceType(Reference ref) {
		if (ref instanceof CaseRef) {
			return "casedoc";
		} else if (ref instanceof StatuteReference) {
			return "statutedoc";
		} else if (ref instanceof RegulationReference) {
			return "regulationdoc";
		}
		throw new IllegalArgumentException("unsupported reference type");
	}
}
