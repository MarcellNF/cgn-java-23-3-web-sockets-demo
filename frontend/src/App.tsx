import './App.css'
import useWebSocket from "react-use-websocket";
import {useState} from "react";
import {toast, ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

type CustomMessage = {
    message: string,
    timestamp: string,
}

function App() {

    const [messages, setMessages] = useState<CustomMessage[]>([]);
    const [sentMessages, setSentMessages] = useState<string[]>([]);
    const [message, setMessage] = useState<string>("");

    const ws = useWebSocket("ws://localhost:8080/api/ws/chat", {
        onOpen: () => console.log("Connection established"),
        onClose: () => console.log("Connection closed"),
        onMessage: (e) => {
            setMessages(prev => [prev, JSON.parse(e.data)]);
        },
    });

    function sendMessage() {
        if (ws.readyState === WebSocket.OPEN) {
            ws.sendMessage(message);
            setSentMessages([...sentMessages, message])
            toast.success("Message was sent!")
        } else {
            toast.error("Something went wrong!")
        }
    }

    return (
        <>
            {
                messages.map((message, index) => {
                    const timestamp = new Date(message.timestamp).toLocaleDateString()
                    return <p key={index}>{message.message} {timestamp}</p>
                })
            }
            <input type={"text"} onChange={(e) => setMessage(e.target.value)}/>
            <button onClick={sendMessage}>Send Message</button>
            {
                sentMessages.map((message, index) => <p key={index}>{message}</p>)
            }
            <p>{ws.lastMessage?.timeStamp}</p>
            <ToastContainer theme={"dark"}/>
        </>
    )
}

export default App
