package org.summerclouds.facade.core;

public class Source {

    private Schemes schemes = new Schemes();
    private ConfigTypes types = new ConfigTypes();
    
    public Source() {
		schemes.put("file", new FileScheme());
		schemes.put("mvn", new MvnScheme());
		types.put("yml",new YmlConfigType());
		types.put("yaml",new YmlConfigType());
    }
    
	public Schemes getSchemes() {
		return schemes;
	}

	public ConfigTypes getTypes() {
		return types;
	}

}
