function urlWithoutWindowId(base) {
    var query = base;
    var vars = query.split(/&|\?/g);
    var newQuery = "";
    var iParam = 0;
    for (var i=0; vars != null && i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair.length == 1) {
            newQuery = pair[0];
        }
        else {
            if (pair[0] != "windowId") {
                var amp = iParam++ > 0 ? "&" : "?";
                newQuery =  newQuery + amp + pair[0] + "=" + pair[1];
            }
        }
    }
    return newQuery;
}

function assertWindowId() {
    var freshWindow = window.name.length < 1;
    if (freshWindow) {
        url = urlWithoutWindowId(window.location.href);
        window.name = "window";
        window.location = url;
    }
}
