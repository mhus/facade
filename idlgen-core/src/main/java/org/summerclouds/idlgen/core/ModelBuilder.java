package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.summerclouds.idlgen.core.Field.SEQUENCE;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.Version;
import de.mhus.lib.core.yaml.YList;
import de.mhus.lib.core.yaml.YMap;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.errors.TooDeepStructuresException;

public class ModelBuilder extends MLog {

    protected Set<String> loadedUris = new HashSet<>();
	private Model gen;
    protected Schemes schemes = new Schemes();
    protected ConfigTypes types = new ConfigTypes();
	private String dir;
	
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
                throw new MException(e);
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
        
        YList fieldTypesE = docE.getList("fieldTypes");
        if (fieldTypesE != null) {
        	for (String fieldType : fieldTypesE.toStringList()) 
        		def.addFieldType(fieldType);
        }
        
        YMap aliasesE = docE.getMap("aliases");
        if (aliasesE != null) {
        	for (String from : aliasesE.getKeys()) {
        		String to = aliasesE.getString(from);
        		def.addAlias(from, to);
        	}
        }
        
        YMap formatsE = docE.getMap("formats");
        if (formatsE != null) {
        	for (String field : formatsE.getKeys()) {
        		String format = formatsE.getString(field);
        		def.addFormat(field, format);
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
                throw new MException(e);
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

        // load definitions
        YList definitionE = docE.getList("definitions");
        loadDefinitions(definitionE);
        
        // load parameters
        YMap propertiesE = docE.getMap("properties");
        loadProperties(propertiesE);
        
        YList elementsE = docE.getList("elements");
        loadElements(elementsE);
        
	}

    private void loadElements(YList elementsE) {
		if (elementsE == null) return;
		 for (YMap elementE : elementsE.toMapList()) {
			 String service = elementE.getString("service", "");
			 String struct = elementE.getString("struct", "");
			 if (MString.isSetTrim(struct)) {
				 loadStruct(elementE);
			 } else
			 if (MString.isSetTrim(service)) {
				 loadService(elementE);
			 } else
				 log().w("Unknown element type", elementE);
		 }
	}

	private void loadService(YMap e) {
		String name = e.getString("service").trim();
		
		Field result = null;
		if (e.getElement("result") != null) {
			String resultType = null;
			Field.SEQUENCE sequence = Field.SEQUENCE.SINGLE;
			if (e.isString("result"))
				resultType = e.getString("result", "");
			else {
				YMap resultE = e.getMap("result");
				resultType = resultE.getString("type");
				sequence = getSequence(resultE.getString("sequence", ""));
			}
			result = new Field(e, null, resultType, sequence);
		}
		ArrayList<Field> parameters = new ArrayList<>();
		if (e.getElement("parameters") != null) {
			YList paramsE = e.getList("parameters");
			for (YMap paramE : paramsE.toMapList()) {
				String paramName = paramE.getString("name");
				String paramType = paramE.getString("type");
				SEQUENCE sequence = getSequence(paramE.getString("sequence", ""));
				Field field = new Field(paramE, paramName, paramType, sequence);
				parameters.add(field);
			}
		}
		
		Service service = new Service(e,name,parameters,result);
		gen.addService(name, service);
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

	private void loadStruct(YMap e) {
		String name = e.getString("struct").trim();
		
		ArrayList<Field> fields = new ArrayList<>();
		
		YMap fieldsE = e.getMap("fields");
		if (fieldsE != null) {
			for (String key : fieldsE.getKeys() ) {
				Field field = null;
				if (fieldsE.isString(key)) {
					String type = fieldsE.getString(key);
					field = new Field(null, key, type, SEQUENCE.SINGLE);
				} else {
					YMap x = fieldsE.getMap(key);
					String type = x.getString("type");
					SEQUENCE sequence = getSequence(x.getString("sequence", ""));
					field = new Field(x, key, type, sequence);
				}
				fields.add(field);
			}
		}
		
		Struct struct = new Struct(e, name, fields);
		gen.addStruct(name, struct);
	}

	private void loadProperties(YMap propertiesE) {
    	if (propertiesE == null || !propertiesE.isMap()) return;
    	for (String key : propertiesE.getKeys()) {
    		Object value = propertiesE.getObject(key);
    		gen.setProperty(key, value);
    	}
	}

	protected void loadDefinitions(YList importE) throws MException {
        if (importE == null) return;
        for (String uriStr : importE.toStringList()) {
            MUri uri = MUri.toUri("file:" + absolutPath(uriStr));
            loadDefinition(uri);
        }
    }

    private String absolutPath(String path) {
    	if (!path.startsWith("/")) // not in windows ...
    		path = new File(dir, path).getAbsolutePath();
		return path;
	}

	protected void loadImports(YList importE) throws MException {
        if (importE == null) return;
        for (String uriStr : importE.toStringList()) {
            MUri uri = MUri.toUri("file:" + absolutPath(uriStr));
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
		if (gen.getDefinition().getFieldTypes().contains(field.getType())) {
			
		} else
		if (gen.getStructs().containsKey(field.getType())) {
			field.setStruct(gen.getStructs().get(field.getType()));
		} else
			throw new NotFoundException("field type not found", field.getType());
		
		
	}
	
	
}
