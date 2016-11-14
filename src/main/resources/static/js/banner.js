/**
 * Created by vbabin on 05.10.2016.
 */
function createIframe() {
    var i = document.createElement("iframe");
    i.src = "/baners/banner.jsp";
    i.scrolling = "none";
    i.frameborder = "0";
    i.width = "100%";
    i.height = "220px";
    document.getElementById("bannerContainer").appendChild(i).style.border = "none";

}

if (window.addEventListener) window.addEventListener("load", createIframe, false);
else if (window.attachEvent) window.attachEvent("onload", createIframe);
else window.onload = createIframe;