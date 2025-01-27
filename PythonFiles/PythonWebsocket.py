import asyncio
from websockets.asyncio.server import serve

async def handle_client(websocket):
    """Handle client connections."""
    # Send a connection confirmation message
    await websocket.send("Connection confirmed!")
    try:
        async for message in websocket:
            print(f"Received message: {message}")
            # Echo the message back to the client
            await websocket.send(f"Echo: {message}")
    except Exception as e:
        print(f"Error: {e}")

async def main():
    """Start the WebSocket server."""
    async with serve(handle_client, "localhost", 8765) as server:
        print("WebSocket server running on ws://localhost:8765")
        await server.serve_forever()

if __name__ == "__main__":
    asyncio.run(main())
