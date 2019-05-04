var timeout;
var bidAmountelement;
var cardElement;

document.addEventListener('DOMContentLoaded', function(){
    bidAmountelement = document.getElementById("bidAmount");
    cardElement = document.getElementById("card");
    if(bidAmountelement != null){
        bidAmountelement.addEventListener("change", stopTimeout);
    }
    if(cardElement != null){
        cardElement.addEventListener("change", stopTimeout);
    }
    startTimer();
}, false)

function startTimer() {
    timeout = setTimeout(reloadPage, 5000);
}

function reloadPage(){
    location.reload();
}


function stopTimeout(){
 clearTimeout(timeout);
}