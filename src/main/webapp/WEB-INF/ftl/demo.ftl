<#include "/header.html"> 

<#if list?? && list?size gt 0>
	<#list list as item>
		<#if item_index == 0>
			${item.id}
		<#elseif item_index == 1>
		
		</#if>
	</#list>
</#if>

<#assign foo = foo + 1>