var NUM_ITEMS_PER_PAGE = 2;
var NUM_QUESTIONS = 4; // really 5
var NUM_ANSWERS = 5;
function getContent() {
    let xhr = new XMLHttpRequest();
    xhr.open("GET", "get_content", false);
    xhr.send();
    if (xhr.status >= 200 && xhr.status < 300) {
        content = JSON.parse(xhr.response);
        console.log(content);
//        for (var i = 0; i < NUM_ITEMS_PER_PAGE; i++) {
//            console.log(content[i]);
//        }
        return content;
    } else {
        console.log("dynamic content request failed:");
        console.log(xhr.statusText);
    }
}

var clock = 0;
var clockurl = "";
var clockform = "";
function updateClock() {
    if (clock >= 0) {
        var s = Math.floor(clock % 60);
        s = ("00" + s).substr(-2);
        var m = "" + Math.floor(clock / 60);
        var text = m + ":" + s;
        document.getElementById("clock").innerText = text;
        clock -= 1;
    } else {
        if (clockurl !== null) {
            location.assign(clockurl);
        } else {
            document.getElementById(clockform).submit();
        }
    }
}

function createClock(seconds, url, formid) {
    clock = seconds;
    clockurl = url;
    clockform = formid;
    updateClock();
    setInterval(updateClock, 1000);
}


function checkForced() {
    let xhr = new XMLHttpRequest();
    xhr.open("GET", "get_forcedsettings", false);
    xhr.send();
    if (xhr.status >= 200 && xhr.status < 300) {
    } else {
        console.log("check for forced assignment failed: ");
        console.log(xhr.statusText);
    }

    var body = document.getElementsByTagName("BODY")[0];
    if (xhr.response === "0") {
        document.title = "Forced Control: " + document.title;
        body.style.backgroundColor = "LightCyan";
    } else if (xhr.response === "1") {
        document.title = "Forced Treatment: " + document.title;
        body.style.backgroundColor = "LightCyan";
    }
}

function checkStage(stage) {
    let xhr = new XMLHttpRequest();
    xhr.open("GET", "current_stage?stage=" + stage, false);
    xhr.send();
    if (xhr.status >= 200 && xhr.status < 300) {
        if (xhr.response !== "ok") {
            location.assign(xhr.response);
        }
    } else {
        console.log("check for current stage failed: ");
        console.log(xhr.statusText);
    }

}


function justDeleteContent() {
    document.getElementById("content").innerHTML = "<br><h4>Content secured</h4><br>";
}

function deleteContent(button, url, formid) {
    if (url !== null) {
        justDeleteContent();
        button.innerText = "Continue";
        button.onclick = function () {
            location.assign(url);
        };
    }
}

function deleteContentOnUnload() {
    var body = document.getElementsByTagName("BODY")[0];
    body.onbeforeunload = deleteContent;
}
