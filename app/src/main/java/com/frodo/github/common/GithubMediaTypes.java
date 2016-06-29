package com.frodo.github.common;

/**
 * Created by frodo on 2016/6/3.
 */
public class GitHubMediaTypes {
	public static final String Basic = "application/vnd.github.v3";
	public static final String BasicJson = Basic + "+json";

	public static final String Diff = Basic + ".diff";
	public static final String Patch = Basic + ".patch";
	public static final String Sha = Basic + ".sha";
	public static final String Raw = Basic + ".raw";
	public static final String Text = Basic + ".text";
	public static final String Html = Basic + ".html";
	public static final String Base64 = Basic + ".base64";

	public static final String RAW = Raw + "+json";
	public static final String TEXT = Text + "+json";
	public static final String HTML = Html + "+json";
	public static final String Full = Basic + ".full+json";
}
