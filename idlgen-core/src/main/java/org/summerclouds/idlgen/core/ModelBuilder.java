package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.summerclouds.common.core.error.ErrorException;
import org.summerclouds.common.core.error.MException;
import org.summerclouds.common.core.error.NotFoundException;
import org.summerclouds.common.core.error.TooDeepStructuresException;
import org.summerclouds.common.core.log.MLog;
import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.tool.MFile;
import org.summerclouds.common.core.util.MUri;
import org.summerclouds.common.core.util.Version;
import org.summerclouds.common.core.yaml.YList;
import org.summerclouds.common.core.yaml.YMap;
import org.summerclouds.idlgen.core.Field.SEQUENCE;

public class ModelBuilder extends MLog {

    protected Set<String> loadedUris = new HashSet<>();
	private Model gen;
    protected Schemes schemes = new Schemes();
    protected ConfigTypes types = new ConfigTypes();
	private String dir;
	private MProperties local = new MProperties();
	
	public ModelBuilder() {
		this(new Model());
	}
	
	public ModelBuilder(Model gen) {
		this.gen = gen;
		schemes.put("file", new FileScheme());
		types.put("yml",new YmlConfigType());
		types.put("yaml",new YmlConfigType());
	}
	
	public ModelBuilder load(File file) throws MException {
		dir = file.getParent();
		load(MUri.toUri(file));
		return this;
	}

	private void loadDefinition(MUri uri) throws MException {
        Scheme scheme = schemes.get(uri);
        String content = null;
        String path = null;
        if (scheme instanceof DirectLoadScheme) {
            content = ((DirectLoadScheme) scheme).loadContent(uri);
            path = uri.getPath();
        } else {
            File cf = null;
            try {
                cf = scheme.load(uri);
                if (cf != null) content = MFile.readFile(cf);
            } catch (IOException e) {
                throw new ErrorException(e);
            }
            if (cf != null) path = cf.getPath();
        }
        if (content == null) throw new NotFoundException("definition not found", uri);

        ConfigType type = types.getForPath(path);
        YMap docE = type.create(content);
        if (docE.isEmpty()) {
            log().w("Content is empty", uri);
            return;
        }

        Definition def = new Definition();
        String name = docE.getString("name");
        Version version = new Version( docE.getString("version") );
        def.setName(name);
        def.setVersion(version);
        
        for (String key : docE.getKeys()) {
        	if (key.startsWith("type:")) {
        		String typeName = key.substring(5);
        		YMap defE = docE.getMap(key);
        		FieldDefinition d = new FieldDefinition(defE, typeName);
        		def.addFieldType(d);
        	}
        }
                
        YMap aliasesE = docE.getMap("aliases");
        if (aliasesE != null) {
        	for (String from : aliasesE.getKeys()) {
        		String to = aliasesE.getString(from);
        		def.addAlias(from, to);
        	}
        }
                
        gen.addDefinition(def);
	}
	
	private void load(MUri uri) throws MException {
		if (loadedUris.contains(uri.toString())) {
			log().d("uri already loaded, skip", uri);
			return;
		}
		loadedUris.add(uri.toString());
		
        Scheme scheme = schemes.get(uri);
        String content = null;
        String path = null;
        if (scheme instanceof DirectLoadScheme) {
            content = ((DirectLoadScheme) scheme).loadContent(uri);
            path = uri.getPath();
        } else {
            File cf = null;
            try {
                cf = scheme.load(uri);
                if (cf != null) content = MFile.readFile(cf);
            } catch (IOException e) {
                throw new ErrorException(e);
            }
            if (cf != null) path = cf.getPath();
        }
        if (content == null) throw new NotFoundException("configuration not found", uri);
        
        ConfigType type = types.getForPath(path);
        YMap docE = type.create(content);
        if (docE.isEmpty()) {
            log().w("Content is empty", uri);
            return;
        }
        
        // load imports FIRST and fill before processing
        YList importE = docE.getList("imports");
        loadImports(importE);

        local = new MProperties();
        
        // load definitions
        YList definitionE = docE.getList("definitions");
        loadDefinitions(definitionE);
        
        // load parameters
        YMap propertiesE = docE.getMap("global");
        loadProperties(propertiesE);
        
        // load parameters
        YMap localE = docE.getMap("local");
        loadLocalProperties(localE);
        
        for (String key : docE.getKeys()) {
        	if (key.startsWith("struct:")) {
        		String name = key.substring(7);
        		YMap elementE = docE.getMap(key);
        		loadStruct(elementE, name, false);
        	} else
        	if (key.startsWith("service:")) {
        		String name = key.substring(8);
        		YMap elementE = docE.getMap(key);
        		loadService(elementE, name);
        	}
        }
        
	}

	private void loadService(YMap e, String name) {
		
		Field result = null;
		if (e.getElement("result") != null) {
			if (e.isString("result")) {
				String resultType = e.getString("result", "");
				result = new Field(e, "result", resultType, Field.SEQUENCE.SINGLE);
			} else {
				YMap resultE = e.getMap("result");
				String resultType = resultE.getString("type");
				SEQUENCE sequence = getSequence(resultE.getString("sequence", ""));
				if (resultType.equals("struct")) {
					resultType = "service_" + name + "_result";
					loadStruct(resultE, resultType, true);
					result = new Field(e, "result", resultType, sequence);
				} else {
					result = new Field(e, "result", resultType, sequence);
					loadFieldProperties(result,e);
				}
			}
		}
		ArrayList<Field> parameters = new ArrayList<>();
		if (e.getElement("parameters") != null) {
			YList paramsE = e.getList("parameters");
			@SuppressWarnings("unused")
			int cnt = 0;
			for (YMap paramE : paramsE.toMapList()) {
				String paramName = paramE.getString("name");
				String paramType = paramE.getString("type");
				SEQUENCE sequence = getSequence(paramE.getString("sequence", ""));
				if (paramType.equals("struct")) {
					paramType = "service_" + name + /* "_parameter" + cnt + */ "_" + paramName;
					loadStruct(paramE, paramType, true);
				}
				Field field = new Field(paramE, /* "parameter" + cnt + "_" + */ paramName, paramType, sequence);
				loadFieldProperties(field, paramE);
				parameters.add(field);
				cnt++;
			}
		}
		
		Service service = new Service(e,name,parameters,result, local);
		
		YMap propertiesE = e.getMap("properties");
		if (propertiesE != null) {
			for (String key : propertiesE.getKeys()) {
				Object value = propertiesE.getObject(key);
				service.getProperties().put(key, value);
			}
		}

		gen.addService(name, service);
	}

	private void loadFieldProperties(Field field, YMap e) {
		YMap propertiesE = e.getMap("properties");
		if (propertiesE == null) return;
		if (propertiesE != null) {
			for (String key : propertiesE.getKeys()) {
				Object value = propertiesE.getObject(key);
				field.getProperties().put(key, value);
			}
		}
	}

	private Field.SEQUENCE getSequence(String value) {
		value = value.trim().toLowerCase();
		if ("single".equals(value))
			return Field.SEQUENCE.SINGLE;
		if ("array".equals(value))
			return Field.SEQUENCE.ARRAY;
		if ("map".equals(value))
			return Field.SEQUENCE.MAP;
		return Field.SEQUENCE.SINGLE;
	}

	private void loadStruct(YMap e, String name, boolean nested) {
		ArrayList<Field> fields = new ArrayList<>();
		
		YMap fieldsE = e.getMap("fields");
		if (fieldsE != null) {
			for (String key : fieldsE.getKeys() ) {
				Field field = null;
				if (fieldsE.isString(key)) {
					String type = fieldsE.getString(key);
					field = new Field(null, key, type, SEQUENCE.SINGLE);
				} else {
					if (key.startsWith("struct:")) {
						YMap x = fieldsE.getMap(key);
						SEQUENCE sequence = getSequence(x.getString("sequence", ""));
						String innerName = name + "_" + key.substring(7);
						loadStruct(x, innerName, true);
						field = new Field(x, key.substring(7), innerName, sequence);
					} else {
						YMap x = fieldsE.getMap(key);
						String type = x.getString("type");
						SEQUENCE sequence = getSequence(x.getString("sequence", ""));
						field = new Field(x, key, type, sequence);
						loadFieldProperties(field,x);
					}
				}
				fields.add(field);
			}
		}
		
		Struct struct = new Struct(e, name, fields, local, nested);
		
		YMap propertiesE = e.getMap("properties");
		if (propertiesE != null) {
			for (String key : propertiesE.getKeys()) {
				Object value = propertiesE.getObject(key);
				struct.getProperties().put(key, value);
			}
		}
		
		gen.addStruct(name, struct);
	}

	private void loadProperties(YMap propertiesE) {
    	if (propertiesE == null || !propertiesE.isMap()) return;
    	for (String key : propertiesE.getKeys()) {
    		Object value = propertiesE.getObject(key);
    		gen.setProperty(key, value);
    	}
	}

	private void loadLocalProperties(YMap propertiesE) {
    	if (propertiesE == null || !propertiesE.isMap()) return;
    	for (String key : propertiesE.getKeys()) {
    		Object value = propertiesE.getObject(key);
    		local.put(key, value);
    	}
	}
	
	protected void loadDefinitions(YList importE) throws MException {
        if (importE == null) return;
        for (String uriStr : importE.toStringList()) {
            MUri uri = MUri.toUri(absolutPath(uriStr));
            loadDefinition(uri);
        }
    }

    private String absolutPath(String path) {
    	int p = path.indexOf(':');
    	// already have a schema - max size 10
    	if (p > 1 && p < 10)
    		return path;
    	// windows C:/
    	if (p == 1 && path.length() > 2 && (path.charAt(3) == '/' || path.charAt(3) == '\\'))
    		return "file:" + path;
    	// linux /
    	if (path.startsWith("/"))
    		return "file:" + path;
    	// relative path
    	return "file:" + new File(dir, path).getAbsolutePath();
	}

	protected void loadImports(YList importE) throws MException {
		local = new MProperties();
        if (importE == null) return;
        for (String uriStr : importE.toStringList()) {
            MUri uri = MUri.toUri(absolutPath(uriStr));
            load(uri);
        }
    }
	
	public Model build() throws NotFoundException {
		// connect all together
		for (Struct struct : gen.getStructs().values()) {
			for (Field field : struct.getFields()) {
				connect(field);
			}
		}
		for (Service service : gen.getServices().values()) {
			for (Field field : service.getParameters()) {
				connect(field);
			}
			if (service.getResult() != null)
				connect(service.getResult());
		}
		
		validate();
		
		return gen;
	}

	private void validate() {
		// check for loops
		for (Struct struct : gen.getStructs().values()) {
			try {
				follow(struct, 100);
			} catch (TooDeepStructuresException e) {
				log().e("looping strcuture",struct.getName());
				throw(e);
			}
		}
		
	}

	private void follow(Struct struct, int cnt) {
		for (Field field : struct.getFields()) {
			follow(field, cnt);
		}
	}
	
	private void follow(Field field, int cnt) {
		if (cnt < 0)
			throw new TooDeepStructuresException();
		if (field.getStruct() != null)
			follow(field.getStruct(), cnt--);
		
	}

	private void connect(Field field) throws NotFoundException {
		// alias ?
		String org = gen.getAliasFor(field.getType());
		if (org != null)
			field.doAlias(org);
		// type ?
		if (gen.getDefinition().getFieldTypes().containsKey(field.getType())) {
			field.setFieldDefinition(gen.getDefinition().getFieldTypes().get(field.getType()));
		} else
		if (gen.getStructs().containsKey(field.getType())) {
			field.setStruct(gen.getStructs().get(field.getType()));
		} else
			throw new NotFoundException("field type not found", field.getType());
	}
	
	
}
