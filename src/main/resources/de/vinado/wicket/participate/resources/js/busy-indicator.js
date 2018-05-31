if (typeof Function.empty === undefined)
    Function.empty = function () {
    };

window.onload = setupFunc;

function setupFunc() {
    document.getElementsByTagName('body')[0].onclick = clickFunc;
    hideBusysign();
    Wicket.Event.subscribe('/ajax/call/beforeSend', function (attributes, jqXHR, settings) {
        if (jqXHR.e == "click") {
            showBusysign();
        }
    });
    Wicket.Event.subscribe('/ajax/call/complete', function (attributes, jqXHR, textStatus) {
        hideBusysign();
    });
}

function hideBusysign() {
    document.getElementById('busy-indicator').style.display = 'none';
}

function showBusysign() {
    document.getElementById('busy-indicator').style.display = 'inline';
}

function clickFunc(eventData) {
    var clickedElement = (window.event) ? event.srcElement : eventData.target;
    var e = clickedElement;
    var noBusy4Classes = ['nobusy', 'yui-skin-sam', 'select2-container', 'select2-choice',
        'dropdown-toggle', 'accordion-toggle', 'closeOnError', 'brand',
        'disabled', 'btn-navbar', 'sortable', 'dropdown-menu', 'navbar-toggle', 'alert-dismissible'];
    while (e != null) {
        if (e.nodeType == 1) {
            for (var i = 0; i < noBusy4Classes.length; i++) {
                if (e.className.search('\\b' + noBusy4Classes[i] + '\\b') != -1) {
                    return;
                }
            }
        }
        e = e.parentNode;
    }
    var noBusy4IDs = ['wicketDebugLink', 'wicketAjaxDebugScrollLock'];
    for (var j = 0; j < noBusy4IDs.length; j++) {
        if (clickedElement.id == noBusy4IDs[j]) return;
    }

    var isElementForBusy =

        clickedElement.tagName.toUpperCase() == 'BUTTON' ||

        clickedElement.tagName.toUpperCase() == 'A' ||

        clickedElement.parentNode.tagName.toUpperCase() == 'A' ||

        (clickedElement.tagName.toUpperCase() == 'INPUT' &&
        (clickedElement.type.toUpperCase() == 'BUTTON' || clickedElement.type.toUpperCase() == 'SUBMIT'));

    var isNotElementForBusy =

        clickedElement.hasAttribute('DISABLED');
    if (isElementForBusy && clickedElement.parentNode.id.toUpperCase() != 'NOBUSY' && !isNotElementForBusy) {
        showBusysign();
    }
}
