var ledStatus = 0
var detectedDistance = 5000


var logTableBody = document.getElementById('logTableBody')
var sonarDistanceRange = document.getElementById('sonarDistanceRange')
var distanceValue = document.getElementById('distanceValue')
var ledImg = document.getElementById('ledStatusImg')
var ledStatusText = document.getElementById('ledStatus')
var ledTr = document.getElementById('ledTr')
var tresholdValue = document.getElementById('tresholdDistanceValue')
var logDiv = document.getElementById('logDiv')
//var wsUrl = document.getElementById('wsurl')

var websocket 
connectTo('localhost:8000/ledsonarws')
    
/*
websocket.onerror = function(event) {
    logError("WebSocket: error: " + event.data)
}
websocket.onopen = function(event) {
    logSuccess("WebSocket: connection established")
}

websocket.onclose = function(event) {
    logWarning("Websocket: connection closed [CODE: " + event.code + "]")
}

websocket.onmessage = function(event) {
    logInfo("Websocket: received message [\'" + event.data + "\']")
    onMessage(event.data)
}*/

var MAX_DISTANCE = 5000
var MIN_DISTANCE = 0
var DISTANCE_CMD = "DISTANCE_CMD"
var LED_CMD = "LED_CMD"
var TRESHOLD_CMD = "TRESHOLD_CMD"
var LED_ON = 1
var LED_OFF = 0


logSuccess("Page Loaded")
updateDistance(detectedDistance)

function updateDistance(distance) {
    detectedDistance = parseInt(distance)
    sonarDistanceRange.value = distance
    distanceValue.textContent = distance
}

function updateDistanceAndSend(distance) {
    if(websocket.readyState == WebSocket.OPEN) {
        var msg = {cmd : DISTANCE_CMD, value : distance}
        updateDistance(distance)
        websocket.send(JSON.stringify(msg))
    } else {
        logError("Unable to set send new distance: WebSocket not opened. Please check connection")
        updateDistance(detectedDistance)
    }
}

function powerOnLed() {
    ledStatus = LED_ON
    ledImg.src = "img/led-on.jpg"
    ledStatusText.innerHTML = "ON"
    ledTr.className = "table-success"
    logInfo("Led ON")
}

function powerOffLed() {
    ledStatus = LED_OFF
    ledImg.src = "img/led-off.jpg"
    ledStatusText.innerHTML = "OFF"
    ledTr.className = "table-danger"
    logInfo("Led OFF")
}

function switchLed() {
    if(ledStatus == LED_OFF) powerOnLed()
    else powerOffLed()
}

function logInfo(text) {
    logTableBody.innerHTML += "<tr class=\"table-info\">" +
    "<td class=\"text-start px-3\" style=\"width: 20%;\">" + new Date().toLocaleTimeString() +"</td>" +
    "<td style=\"width: 79%;\">" + text + "</td>" +
    "</tr>"
    logDiv.scrollTop = logDiv.scrollHeight
}

function logError(text) {
    logTableBody.innerHTML += "<tr class=\"table-danger\">" +
    "<td class=\"text-start px-3\" style=\"width: 20%;\">" + new Date().toLocaleTimeString() +"</td>" +
    "<td style=\"width: 79%;\">" + text + "</td>" +
    "</tr>"
    logDiv.scrollTop = logDiv.scrollHeight
}

function logWarning(text) {
    logTableBody.innerHTML += "<tr class=\"table-warning\">" +
    "<td class=\"text-start px-3\" style=\"width: 20%;\">" + new Date().toLocaleTimeString() +"</td>" +
    "<td style=\"width: 79%;\">" + text + "</td>" +
    "</tr>"
    logDiv.scrollTop = logDiv.scrollHeight
}

function logSuccess(text) {
    logTableBody.innerHTML += "<tr class=\"table-success\">" +
    "<td class=\"text-start px-3\" style=\"width: 20%;\">" + new Date().toLocaleTimeString() +"</td>" +
    "<td style=\"width: 79%;\">" + text + "</td>" +
    "</tr>"
    logDiv.scrollTop = logDiv.scrollHeight
}

function onSetTresholdClick() {
    sendTresholdUpdate(parseInt(tresholdValue.value))
}

function onMessage(msg) {
    var json = JSON.parse(msg)
    switch(json.cmd) {
        case LED_CMD: {
            if(json.value != 0 && json.value != 1) {
                logError("Received wrong led status: " + json.value)
            } else {
                while(ledStatus != json.value) switchLed()
            }
            break
        }

        case DISTANCE_CMD: {
            if(json.value < MIN_DISTANCE || json.value > MAX_DISTANCE) {
                logError("Received wrong detected distance: " + json.value)
            } else {
                updateDistance(json.value)
            }
            break
        }

        case TRESHOLD_CMD: {
            if(json.value < MIN_DISTANCE || json.value > MAX_DISTANCE) {
                logError("Received wrong treshold distance: " + json.value)
            } else {
               updateTreshold(json.value)
            }
            break
        }
    }
}

function sendTresholdUpdate(newValue) {
    if(newValue < MIN_DISTANCE || newValue > MAX_DISTANCE) {
        logError("Treshold is out of range. Please choose a value in [" + MIN_DISTANCE + ", " + MAX_DISTANCE + "]")
    } else {
        if(websocket.readyState == WebSocket.OPEN) {
            var obj = {cmd : TRESHOLD_CMD, value : newValue}
            websocket.send(JSON.stringify(obj))
            logInfo("Request new treshold: " + newValue)
        } else {
            logError("Unable to set new treshold: WebSocket not opened")
        }
    }
}

function updateTreshold(newValue) {
    tresholdValue.value = newValue
}

function onSetWsUrl() {
    connectTo(wsUrl.value)
}

function connectTo(newUrl) {
    logInfo("Trying to open websocket with \'" + newUrl + "\'")
    if(websocket != null) {
        if(websocket.readyState == WebSocket.OPEN) websocket.close()
    }
    websocket = new WebSocket('ws://' + newUrl)
    
    websocket.onerror = function(event) {
        logError("WebSocket: error: " + event.data)
    }
    websocket.onopen = function(event) {
        logSuccess("WebSocket: connection established with \'" + newUrl + "\'")
        onSetTresholdClick()
        updateDistanceAndSend(5000)
    }
    
    websocket.onclose = function(event) {
        logWarning("Websocket: connection closed [CODE: " + event.code + "]")
    }
    
    websocket.onmessage = function(event) {
        logInfo("Websocket: received message [\'" + event.data + "\']")
        onMessage(event.data)
    }

    return websocket

}