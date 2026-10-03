/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";
import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { toast } from "react-toastify";
import { useAuth } from "./useAuth";
import SockJS from "sockjs-client";

interface NotificationMessage {
	type: string;
	message: string;
	timestamp: string;
}

// Custom hook for managing WebSocket connections using STOMP protocol
// Handles real-time notifications for both Influencer and Client roles
export const useWebSocket = () => {
	const stompClient = useRef<Client | null>(null);
	const { getUserId, getStoredToken, getUserRole } = useAuth();
	const baseUrl = process.env.NEXT_PUBLIC_API_URL;

	useEffect(() => {
		const connectWebSocket = () => {
			// Verify all required authentication data is present
			const userId = getUserId();
			const token = getStoredToken();
			const userRole = getUserRole();

			// Early return if authentication prerequisites are not met
			if (!userId || !token || !userRole) {
				console.error("WebSocket: Missing credentials");
				return;
			}

			console.log("WebSocket: Attempting connection to", `${baseUrl}/ws`);

			try {
				// Initialize STOMP client with SockJS as the WebSocket transport layer
				stompClient.current = new Client({
					webSocketFactory: () => {
						// SockJS provides fallback options if WebSocket is not available
						const socket = new SockJS(`${baseUrl}/ws`);

						// Lifecycle event handlers for the SockJS connection
						socket.onopen = () => {
							console.log("WebSocket: SockJS connection opened");
						};

						socket.onclose = (event) => {
							console.error(
								"WebSocket: SockJS connection closed",
								event
							);
						};

						socket.onerror = (error) => {
							console.error("WebSocket: SockJS error", error);
						};

						return socket;
					},

					// Authentication header for the WebSocket connection
					connectHeaders: {
						Authorization: `Bearer ${token}`,
					},

					// Connection success handler - sets up role-specific subscriptions
					onConnect: () => {
						// Dynamic subscription path based on user role
						const subscriptionPath =
							userRole === "INFLUENCER"
								? `/user/${userId}/influencer/notifications`
								: `/user/${userId}/client/notifications`;

						// Subscribe to user-specific notification channel
						stompClient.current?.subscribe(
							subscriptionPath,
							(message) => {
								console.log("STOMP: Received message", message);
								const notification: NotificationMessage =
									JSON.parse(message.body);
								toast.success(notification.message, {
									position: "top-right",
									autoClose: 5000,
								});
							},
							{ id: `sub-${userId}` }
						);
					},

					// Error handling and connection management configuration
					onStompError: (frame) => {
						console.error("STOMP: Protocol error", frame);
					},
					onWebSocketError: (event) => {
						console.error("WebSocket: Error", event);
					},
					onWebSocketClose: (event) => {
						console.error("WebSocket: Connection closed", event);
					},
					reconnectDelay: 5000, // Reconnection attempt delay in ms
					heartbeatIncoming: 4000, // Expected server heartbeat interval
					heartbeatOutgoing: 4000, // Client heartbeat interval
				});

				// Initiate the WebSocket connection
				console.log("WebSocket: Activating connection");
				stompClient.current.activate();
			} catch (error) {
				console.error("WebSocket: Setup failed", error);
			}
		};

		// Initialize connection when component mounts
		connectWebSocket();

		// Cleanup function to properly close connection on unmount
		return () => {
			if (stompClient.current?.active) {
				console.log("WebSocket: Deactivating connection");
				try {
					stompClient.current.deactivate();
				} catch (error) {
					console.error("WebSocket: Deactivation failed", error);
				}
			}
		};
	}, [baseUrl]);

	// Expose connection status for component usage
	return {
		isConnected: !!stompClient.current?.active,
	};
};
