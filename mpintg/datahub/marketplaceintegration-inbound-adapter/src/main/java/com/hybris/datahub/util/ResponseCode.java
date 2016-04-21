package com.hybris.datahub.util;


public enum ResponseCode {
	
	DATAHUB_MISSING_TYPE("10005", "Cannot find handler for the type"),

	INTERNAL_SERVER_ERROR("sys_0023", "Internal server error.")

	;

	private String code;

	private String value;

	private ResponseCode(final String code, final String value) {
		this.code = code;
		this.value = value;

	}

	/**
	 * @param code
	 * @return value
	 */
	public static String getExtValueByCode(final String code) {
		for (final ResponseCode e : ResponseCode.values()) {
			if (e.getCode().equals(code)) {
				return e.value;
			}
		}
		return null;
	}

	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
