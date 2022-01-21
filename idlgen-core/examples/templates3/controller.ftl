<#assign Flavor=statics['org.summerclouds.idlgen.core.JavaFlavor']>
package ${_model.getProperties().package};

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPath;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.Autowired;

@RestController
public class ${classModule}Controller {

  @Autowired
  private ${classModule}Service service;

  <#list _services as service>
    <#if service.getProperties().mapping?? >
      <#if (service.getProperties().method)??>
  	    <#if service.getProperties().method == "get" >
  @GetMapping("${service.getProperties().mapping}")
  	    <#else>
  	      <#if service.getProperties().method == "post" >
  @PostMapping("${service.getProperties().mapping}")
          </#if>
  	      <#if service.getProperties().method == "put" >
  @PutMapping("${service.getProperties().mapping}")
          </#if>
  	      <#if service.getProperties().method == "delete" >
  @DeleteMapping("${service.getProperties().mapping}")
          </#if>
        </#if>
      <#else>
  @GetMapping("${service.getProperties().mapping}")
      </#if>
    </#if>
  public ${Flavor.toField(service.result)} do${service.getProperties().className}(<#list Flavor.toParametersList(service) as field
  ><#if field.field.getProperties().origin??
  ><#if field.field.getProperties().origin.startsWith("var:") >@PathVariable("${field.field.getProperties().origin[4..]}") </#if
  ><#if field.field.getProperties().origin == "path" >@RequestPath </#if
  ><#if field.field.getProperties().origin == "body" >@RequestBody </#if
  ></#if>${field.separator}${field.type} ${field.name}</#list>) {
    <#if service.result??>
    return service.do${service.getProperties().className}(${Flavor.toParametersCall(service)});
    </#if>
  }
  </#list>

}