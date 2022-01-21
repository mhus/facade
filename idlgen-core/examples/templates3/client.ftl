<#assign Flavor=statics['org.summerclouds.idlgen.core.JavaFlavor']>
package ${_model.getProperties().package};

@RestController
public class ${classModule}Client {

  @Autowired
  private RestTemplateBuilder builder
  
  <#list _services as service>
      <#if service.getProperties().mapping?? >
  public ${Flavor.toField(service.result)} do${service.getProperties().className}(${Flavor.toParameters(service)}) {
    
    String uri = baseUri + "${service.getProperties().mapping}";
    ${Flavor.toField(service.result)} template = builder.build();
    
    <#if service.result??>
    ${Flavor.toField(service.result)} value = template.getForObject( uri, param, ${Flavor.toField(service.result)}.class);
    return value;
    </#if></#if>
  }
  </#list>

}