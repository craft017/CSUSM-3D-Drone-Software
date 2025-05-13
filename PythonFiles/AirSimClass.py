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
import math

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
         #Velocity of the drone
         #Degrees per second when turning
         #Every command runs for 200 milliseconds

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

            list_of_commands = message.split(",")
            if len(list_of_commands) > 1:
                self.yawRate = float(list_of_commands[1])
                self.velocity = float(list_of_commands[2])
                self.commandTime = float(list_of_commands[3])

            action = list_of_commands[0]
            #print(list_of_commands)

            #Beginning movement commands ↓-------------------

            if action == "takeoff":
                self.client.armDisarm(True)
                self.client.takeoffAsync().join()
                print(self.client.getRotorStates())
                await websocket.send(json.dumps({"status": "success", "message": "Drone took off"}))

            elif action == "land":
                self.client.landAsync().join()
                self.client.armDisarm(False)
                print(self.client.getRotorStates())
                await websocket.send(json.dumps({"status": "success", "message": "Drone landed"}))

            # Continuous movement: Forward
            elif action == "forward":
                self.moveX(self.velocity, 0, 0, self.commandTime)

                await websocket.send(json.dumps({"status": "success", "message": "Moving forward"}))

            # Continuous movement: Backward
            elif action == "backward":
                self.moveX(-self.velocity, 0, 0, self.commandTime)
                await websocket.send(json.dumps({"status": "success", "message": "Moving backward"}))

            # Continuous movement: Left
            elif action == "left":
                self.moveY(0, -self.velocity, 0, self.commandTime)
                await websocket.send(json.dumps({"status": "success", "message": "Moving left"}))

            # Continuous movement: Right
            elif action == "right":
                self.moveY(0, self.velocity, 0, self.commandTime)
                await websocket.send(json.dumps({"status": "success", "message": "Moving right"}))

            #Continuous movement: Right Turn
            elif action == "right_turn":
                self.client.rotateByYawRateAsync(self.yawRate, self.commandTime)

            #Continuous movement: Left Turn
            elif action == "left_turn":
                self.client.rotateByYawRateAsync(-self.yawRate, self.commandTime)

            #Continuous movement: Up
            elif action == "up":
                self.client.moveByVelocityAsync(0, 0, -self.velocity, self.commandTime)

            #Continuous movement: Down
            elif action == "down":
                self.client.moveByVelocityAsync(0, 0, self.velocity, self.commandTime)

            elif action == "stop":
                print(self.client.getRotorStates())

            elif action == "forward_left":
                self.moveTurnLeft(self.velocity, 0, 0, self.commandTime, self.yawRate)

            elif action == "forward_right":
                self.moveTurnRight(self.velocity, 0, 0, self.commandTime, self.yawRate)

            #End movement commands ↑-------------------
            #Beginning GPS commands ↓-------------------

            elif action == "getGPS":
                gpsData = self.client.getGpsData()
                latitude = str(gpsData.gnss.geo_point.latitude)
                longitude = str(gpsData.gnss.geo_point.longitude)   #Get GPS coordinates
                altitude = str(gpsData.gnss.geo_point.altitude)
                message = "getGPS," + latitude + "," + longitude + "," + altitude   #Create gps message
                await websocket.send(message)

            #End GPS commands ↑-------------------
            #Beginning Heading/Speed commands ↓-------------------

            elif action == "getSpeed":

                state = self.client.getMultirotorState()
                speed = state.kinematics_estimated.linear_velocity.get_length()
                if speed > 0.001:
                    message = "getSpeed," + str(speed)
                else:
                    message = "getSpeed,0"
                await websocket.send(message)


            elif action == "getHeading":
                state = self.client.getMultirotorState()
                yaw = airsim.to_eularian_angles(state.kinematics_estimated.orientation)[2]
                yaw_degrees = str(math.degrees(yaw))
                message = "getHeading," + yaw_degrees
                await websocket.send(message)

            else:
                await websocket.send(json.dumps({"status": "error", "message": "Unknown action"}))
        except Exception as e:
            print(f"Error while processing message: {e}")
            await websocket.send(json.dumps({"status": "error", "message": str(e)}))

    #Moving in x axis relative to drone perspective
    def moveX(self, vx, vy, vz, duration):
        # Get drone orientation
        pose = self.client.simGetVehiclePose()
        #Convert quaternion to yaw angle
        yaw = airsim.to_eularian_angles(pose.orientation)[2]
        local_vx = self.velocity    #Velocity in x direction
        local_vy = 0                #No movement in y direction
        # Compute world frame velocity
        newVX = local_vx*math.cos(yaw)-local_vy*math.sin(yaw)
        newVY = local_vx*math.sin(yaw)+local_vy*math.cos(yaw)
        newVZ = 0
        if(vx<0):   #Check if going backwards
            self.client.moveByVelocityAsync(-newVX, -newVY, newVZ, duration)
        else:
            self.client.moveByVelocityAsync(newVX, newVY, newVZ, duration)
    #Moving in y axis relative to drone perspective
    def moveY(self, vx, vy, vz, duration):
        # Get drone orientation
        pose = self.client.simGetVehiclePose()
        #Convert quaternion to yaw angle
        yaw = airsim.to_eularian_angles(pose.orientation)[2]
        local_vy = self.velocity    #Velocity in y direction
        local_vx = 0                #No movement in x direction
        # Compute world frame velocity
        newVX = local_vx*math.cos(yaw)-local_vy*math.sin(yaw)
        newVY = local_vx*math.sin(yaw)+local_vy*math.cos(yaw)
        newVZ = 0
        if(vy<0):   #Check if going backwards
            self.client.moveByVelocityAsync(-newVX, -newVY, newVZ, duration)
        else:
            self.client.moveByVelocityAsync(newVX, newVY, newVZ, duration)
    def moveTurnRight(self, vx, vy, vz, duration, yawRate):
        # Get drone orientation
        pose = self.client.simGetVehiclePose()
        #Convert quaternion to yaw angle
        yaw = airsim.to_eularian_angles(pose.orientation)[2]
        local_vx = self.velocity    #Velocity in x direction
        local_vy = 0                #No movement in y direction
        # Compute world frame velocity
        newVX = local_vx*math.cos(yaw)-local_vy*math.sin(yaw)
        newVY = local_vx*math.sin(yaw)+local_vy*math.cos(yaw)
        newVZ = self.client.getMultirotorState().kinematics_estimated.position.z_val
        if(vx<0):   #Check if going backwards
            self.client.moveByVelocityZAsync(-newVX, -newVY, newVZ, duration, drivetrain=airsim.DrivetrainType.ForwardOnly, yaw_mode=airsim.YawMode(is_rate=True, yaw_or_rate=yawRate))
        else:
            self.client.moveByVelocityZAsync(newVX, newVY, newVZ, duration, drivetrain=airsim.DrivetrainType.ForwardOnly, yaw_mode=airsim.YawMode(is_rate=True, yaw_or_rate=yawRate))
    def moveTurnLeft(self, vx, vy, vz, duration, yawRate):
        # Get drone orientation
        pose = self.client.simGetVehiclePose()
        #Convert quaternion to yaw angle
        yaw = airsim.to_eularian_angles(pose.orientation)[2]
        local_vx = self.velocity    #Velocity in x direction
        local_vy = 0                #No movement in y direction
        # Compute world frame velocity
        newVX = local_vx*math.cos(yaw)-local_vy*math.sin(yaw)
        newVY = local_vx*math.sin(yaw)+local_vy*math.cos(yaw)
        newVZ = self.client.getMultirotorState().kinematics_estimated.position.z_val
        if(vx<0):   #Check if going backwards
            self.client.moveByVelocityZAsync(-newVX, -newVY, newVZ, duration, drivetrain=airsim.DrivetrainType.ForwardOnly, yaw_mode=airsim.YawMode(is_rate=True, yaw_or_rate=-yawRate))
        else:
            self.client.moveByVelocityZAsync(newVX, newVY, newVZ, duration, drivetrain=airsim.DrivetrainType.ForwardOnly, yaw_mode=airsim.YawMode(is_rate=True, yaw_or_rate=-yawRate))
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