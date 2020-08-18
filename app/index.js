const net = require('net')

let HOST = '127.0.0.1'; // parameterize the IP of the Listen
let PORT = 2008; // TCP LISTEN port

net.createServer(function (sock) {
    console.log("Connected to: " + sock.remoteAddress  + ":" + sock.remotePort)

    sock.on('data', function (data){
        console.log("received: " + data)
        sock.write(data);
        let str = data.toString();
        if(str.includes("Jarvis")){
            let para = document.createElement("p")
            para.classList.add("jarvis")
            let node = document.createTextNode(data)
            para.appendChild(node)
            let textBox = document.getElementById("text-box")
            textBox.appendChild(para)
        }
        else if(str.includes("User")){
            let para = document.createElement("p")
            para.classList.add("user")
            let node = document.createTextNode(data)
            para.appendChild(node)
            let textBox = document.getElementById("text-box")
            textBox.appendChild(para)
        }
    })

    sock.on('close',function (data){
        console.log("Connection closed with: " + sock.remoteAddress + ":" + sock.remotePort)
    })
}).listen(PORT)