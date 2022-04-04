<#assign Flavor=statics['org.summerclouds.facade.core.JavaFlavor']>
package ${_model.getProperties().package};

public interface ${className} {

<#list _fields as field>
  ${Flavor.toField(field)} ${Flavor.getterName(field)}();
</#list>

}