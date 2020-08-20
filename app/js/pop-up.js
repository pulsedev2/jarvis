const net = require('net')

let HOST = '127.0.0.1'; // parameterize the IP of the Listen
let PORT = 2008; // TCP LISTEN port

net.createServer(socket => {
    socket.on('data', data => {
        socket.write(data);
        let str = data.toString();
        let bubbleBox = document.getElementById("bubbles")
        if(bubbleBox.childElementCount > 0){
            bubbleBox.childNodes.forEach(node => {
                bubbleBox.removeChild(node)
            })
            setTimeout(suite(str,data,bubbleBox), 2000)
        }
        else{
            suite(str,data,bubbleBox)
        }
    })
    function suite(str, data, bubbleBox){
        let bubble = document.createElement("div")
        bubble.classList.add("bubble")
        if(str.includes("Jarvis")){
            str = str.replace("Jarvis: ", "")
            bubble.classList.add("left")
            bubble.classList.add("bubble-bottom-left")
            let node = document.createTextNode(str)
            bubble.appendChild(node)
            bubbleBox.appendChild(bubble)
        }
        else if(str.includes("User")){
            str = str.replace("User: ", "")
            bubble.classList.add("right")
            bubble.classList.add("bubble-bottom-right")
            let node = document.createTextNode(str)
            bubble.appendChild(node)
            bubbleBox.appendChild(bubble)
        }
    }
}).listen(PORT)