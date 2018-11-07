<#import "macros/common.ftl" as c>
<#import "macros/pager.ftl" as p>

<@c.page>
<#list page.content as item>
    <div class="container">
        <h1>
            <#if item.htmlContent?hasContent>
                <a href="/news/${item.id}">${item.title}</a>
            <#else>
                ${item.title}
            </#if>
        </h1>
        <p>${item.pubDate}</p>
        <blockquote class="blockquote-reverse">
            <div>${item.description}</div>
            <footer><a href="${item.link}">Ссылка на источник (${item.source.description})</a></footer>
        </blockquote>
    </div>
    <#sep><hr></#sep>
</#list>
<@p.pager "/news" page/>
</@c.page>
