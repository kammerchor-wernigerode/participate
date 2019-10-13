<#-- @formatter:off -->
Hallo ${singer.firstName},

<#if event.location??>
    <#if event.startDate == event.endDate>
Am ${event.startDate} findet ein ${event.eventType} in ${event.location} statt.
    <#else>
Vom ${event.startDate} bis zum ${event.endDate} findet ein ${event.eventType} in ${event.location} statt.
    </#if>
<#else>
    <#if event.startDate == event.endDate>
Am ${event.startDate} findet ein ${event.eventType} statt.
    <#else>
Vom ${event.startDate} bis zum ${event.endDate} findet ein ${event.eventType}.
    </#if>
Ein Ort wurde noch nicht festgelegt.
</#if>

Sag uns bitte, ob und wie viel Zeit du hast, ob du einen Schlafplatz brauchst oder ob du Schlafplätze anbieten kannst.
<#if deadline??>Beachte bitte, dass die Organisation der Schlafplätze zwei Woche vor Beginn abgeschlossen ist. Alle Antworten, die nach dem ${deadline?date} bearbeitet werden, werden nicht mehr berücksichtigt.</#if>

${acceptLink}

Du kannst deine Angabe jederzeit ändern, indem du erneut auf den Link klickst.
<#-- @formatter:on -->
