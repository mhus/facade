<#assign Flavor=statics['org.summerclouds.idlgen.core.JavaFlavor']>
public class ${classModule}Util {
<#list _services as service>
  public ${Flavor.toField(service.result)} do${service.getProperties().className}(${Flavor.toParameters(service)}) {
  }
</#list>

}