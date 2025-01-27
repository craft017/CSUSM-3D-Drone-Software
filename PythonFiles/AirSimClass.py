import asyncio
from functools import partial
import websockets
import setup_path
import airsim
import json
import numpy as np
import os
import tempfile
import pprint
import cv2

class AirSimWebSocketServer:
    def __init__(self, host="localhost", port=8765):
        self.host = host
        self.port = port
        self.client = None

    async def start_server(self):
        """
        Start the WebSocket server.
        """
        print(f"Starting WebSocket server on ws://{self.host}:{self.port}")
        self.client = airsim.MultirotorClient()
        self.client.confirmConnection()
        self.client.enableApiControl(True)

        # Start the WebSocket server
        server = websockets.serve(partial(self.handle_client), self.host, self.port)
        await server
        await asyncio.Future()  # Run indefinitely

    async def handle_client(self, websocket):
        """
        Handle incoming WebSocket connections and messages.
        """
        print("AirSim Client connected")
        try:
            async for message in websocket:
                await self.process_message(websocket, message)
        except Exception as e:
            print(f"Connection error: {e}")
            await websocket.send(json.dumps({"status": "error", "message": str(e)}))

    async def process_message(self, websocket, message):
        """
        Process incoming messages and send responses.
        """
        try:
            command = json.loads(message)
            action = command.get("action")
            params = command.get("params", {})

            if action == "takeoff":
                self.client.armDisarm(True)
                self.client.takeoffAsync().join()
                await websocket.send(json.dumps({"status": "success", "message": "Drone took off"}))

            elif action == "land":
                self.client.landAsync().join()
                self.client.armDisarm(False)
                await websocket.send(json.dumps({"status": "success", "message": "Drone landed"}))

            elif action == "moveToPosition":
                x = params.get("x", 0)
                y = params.get("y", 0)
                z = params.get("z", -10)
                self.client.moveToPositionAsync(x, y, z, velocity=5).join()
                await websocket.send(json.dumps({"status": "success", "message": f"Moved to position: ({x}, {y}, {z})"}))

            else:
                await websocket.send(json.dumps({"status": "error", "message": "Unknown action"}))
        except Exception as e:
            print(f"Error while processing message: {e}")
            await websocket.send(json.dumps({"status": "error", "message": str(e)}))

    def cleanup(self):
        """
        Perform cleanup when the server stops.
        """
        if self.client:
            self.client.enableApiControl(False)
            self.client.armDisarm(False)
            print("Disconnected from AirSim client")

if __name__ == "__main__":
    server = AirSimWebSocketServer()

    try:
        asyncio.run(server.start_server())
    except KeyboardInterrupt:
        print("\nShutting down server...")
        server.cleanup()
    except Exception as e:
        print(f"Unexpected error: {e}")
        server.cleanup()