<#assign Flavor=statics['org.summerclouds.facade.core.JavaFlavor']>
package ${_model.getProperties().package};

public interface ${classModule}Target {

  <#list _services as service>
  ${Flavor.toField(service.result)} do${service.getProperties().className}(${Flavor.toParameters(service)});
  </#list>

}