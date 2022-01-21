<#assign Flavor=statics['org.summerclouds.idlgen.core.JavaFlavor']>
package ${_model.getProperties().package};

public class ${className} {

<#list _fields as field>
  private ${Flavor.toField(field)} ${field.getProperties().methodName};
</#list>

<#list _fields as field>
  public ${Flavor.toField(field)} ${Flavor.getterName(field)}() {
    return ${field.getProperties().methodName};
  }
  
  public void ${Flavor.setterName(field)}(${Flavor.toField(field)} value) {
    ${field.getProperties().methodName} = value;
  }
  
</#list>

}