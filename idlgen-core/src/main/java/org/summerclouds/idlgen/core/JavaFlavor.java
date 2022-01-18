package org.summerclouds.idlgen.core;

import de.mhus.lib.core.MProperties;

public class JavaFlavor implements Flavor {

	@Override
	public void prepare(MProperties prop) {
		if (prop.containsKey("package")) {
			prop.setString("javaPackageAsPath", prop.getString("package","").replace('.', '/') );
		}
		if (prop.containsKey("_name")) {
			prop.setString("javaName", toJavaMethod(prop.getString("_name","") ) );
		}
	}

	
	public String toJavaMethod(String in) {
		if (in.length() < 2) return in.toUpperCase();
		return in.substring(0,1).toUpperCase() + in.substring(1);
	}
	
}
