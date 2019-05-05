var timeout;
var bidAmountelement;
var cardElement;

document.addEventListener('DOMContentLoaded', function(){
    bidAmountelement = document.getElementById("bidAmount");
    cardElement = document.getElementById("card");
    if(bidAmountelement != null){
        bidAmountelement.addEventListener("keydown", stopTimeout);
    }
    if(cardElement != null){
        cardElement.addEventListener("keydown", checkForValue);
        //cardElement.addEventListener("focus", checkForValue);
    }
    startTimer();
}, false);

function startTimer() {
    timeout = setTimeout(reloadPage, 7000);
}

function reloadPage(){
    console.log('reload method');
    location.reload();
}


function stopTimeout(){
    console.log('stop timeout method');
 clearTimeout(timeout);
}

function checkForValue(){
    console.log('check for value');
    if(cardElement != null){
        if(cardElement.value != ""){
            stopTimeout();
        }
    }

    if(bidAmountelement != null){
        console.log('a');
        if(bidAmountelement.value != ""){
            console.log('b');
            stopTimeout();
        }
    }


}