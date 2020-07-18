var NUM_ITEMS_PER_PAGE = 2;
var NUM_QUESTIONS = 4;// really 5
var NUM_ANSWERS = 5;

function getContent() {
    let xhr = new XMLHttpRequest();
    xhr.open("GET", "get_content", false);
    xhr.send();
    if (xhr.status >= 200 && xhr.status < 300) {
        content = JSON.parse(xhr.response);
        console.log(content);
        for (var i=0; i<4; i++) {
            console.log(content[i]);
        }
        return content;
    } else {
        console.log("dynamic content request failed:");
        console.log(xhr.statusText);
    }
}

