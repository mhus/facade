<#assign Flavor=statics['org.summerclouds.facade.core.JavaFlavor']>
package ${_model.getProperties().package};

public class ${classModule}Service {

  private ${classModule}Target target;

  public void setTarget(${classModule}Target target) {
    this.target = target;
  }

  <#list _services as service>
  public ${Flavor.toField(service.result)} do${service.getProperties().className}(${Flavor.toParameters(service)}) {
    <#if service.result??>
    return target.do${service.getProperties().className}(${Flavor.toParametersCall(service)});
    </#if>
  }
  </#list>

}