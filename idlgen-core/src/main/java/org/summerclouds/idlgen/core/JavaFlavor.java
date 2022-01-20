package org.summerclouds.idlgen.core;

import de.mhus.lib.core.MProperties;

public class JavaFlavor implements Flavor {

	@Override
	public void prepare(MProperties prop) {
		if (prop.containsKey("package")) {
			prop.setString("javaPackageAsPath", prop.getString("package","").replace('.', '/') );
		}
		if (prop.containsKey("_name")) {
			prop.setString("methodName", toJavaMethod(prop.getString("_name","") ) );
			prop.setString("className", toJavaClass(prop.getString("_name","") ) );
		}
		if (prop.containsKey("module")) {
			prop.setString("methodModule", toJavaMethod(prop.getString("module","") ) );
			prop.setString("classModule", toJavaClass(prop.getString("module","") ) );
		}
	}
	
	public String toJavaMethod(String in) {
		if (in.length() < 2) return in.toUpperCase();
		while (true) {
			int p = in.indexOf('_');
			if (p < 0 || p == in.length()-1) break;
			in = in.substring(0,p) + in.substring(p+1, p+2).toUpperCase() + in.substring(p+2);
		}
		
		return in;
	}
	
	public String toJavaClass(String in) {
		if (in.length() < 2) return in.toUpperCase();
		while (true) {
			int p = in.indexOf('_');
			if (p < 0 || p == in.length()-1) break;
			in = in.substring(0,p) + in.substring(p+1, p+2).toUpperCase() + in.substring(p+2);
		}
		
		return in.substring(0,1).toUpperCase() + in.substring(1);
	}
	
}
