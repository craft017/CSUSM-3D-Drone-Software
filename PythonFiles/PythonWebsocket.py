import asyncio
from websockets.asyncio.server import serve

clients = set()

async def handle_client(websocket):
    """Handle client connections."""
    # Send a connection confirmation message
    clients.add(websocket)
    await websocket.send("Connection confirmed!")
    try:
        async for message in websocket:
            print(f"Received message: {message}")
            # Broadcast message to all clients except the sender
            tasks = [client.send(message) for client in clients if client != websocket]
            await asyncio.gather(*tasks)
    except Exception as e:
        print(f"Error: {e}")

    finally:
        # Remove client when it disconnects
        clients.remove(websocket)

async def main():
    """Start the WebSocket server."""
    async with serve(handle_client, "localhost", 8766) as server:
        print("WebSocket server running on ws://localhost:8766")
        await server.serve_forever()

if __name__ == "__main__":
    asyncio.run(main())