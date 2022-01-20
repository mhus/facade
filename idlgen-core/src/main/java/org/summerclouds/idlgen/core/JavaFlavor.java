package org.summerclouds.idlgen.core;

import org.summerclouds.idlgen.core.Field.SEQUENCE;

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
	
	public static String toField(Field field) {
		if (field == null) return "void";
		StringBuilder sb = new StringBuilder();
		if (field.getSequence() == SEQUENCE.SINGLE) {
			if (field.isField())
				sb.append( field.getFieldDefinition().getDefinition().getString("java", field.getType()) );
			else
				sb.append( field.getStruct().getProperties().getString("package", "") + "." + field.getStruct().getProperties().getString("className", "") );
		} else
		if (field.getSequence() == SEQUENCE.ARRAY) {
			sb.append("java.util.List<");
			if (field.isField())
				sb.append( field.getFieldDefinition().getDefinition().getString("java", field.getType()) );
			else
				sb.append( field.getStruct().getProperties().getString("package", "") + "." + field.getStruct().getProperties().getString("className", "") );
			sb.append(">");
		} else {
			sb.append("java.util.Map<");
			if (field.isField())
				sb.append( field.getFieldDefinition().getDefinition().getString("java", field.getType()) );
			else
				sb.append( field.getStruct().getProperties().getString("package", "") + "." + field.getStruct().getProperties().getString("className", "") );
			sb.append(">");
		}
		return sb.toString();
	}
	
	public static String toParameters(Service service) {
		StringBuilder sb = new StringBuilder();
		for (Field p : service.getParameters()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(toField(p));
			sb.append(" ");
//			sb.append(p.getProperties().getString("methodName"));
			sb.append(p.getName());
		}
		return sb.toString();
	}
	
}
