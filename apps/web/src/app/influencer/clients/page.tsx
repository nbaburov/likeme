"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import { useOrdersByInfluencer } from "@/hooks/useOrders";
import { useClient } from "@/hooks/useClient";
import LikeMeTable from "@/components/ui/LikeMeTable";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { useRouter } from "next/navigation";

interface ClientTableData {
	id: number;
	username: string;
	email: string;
	instagram: string;
	active: string;
	lastOrder: string;
}

export default function InfluencerClientsPage() {
	const router = useRouter();
	const { getUserId } = useAuth();
	const influencerId = getUserId();
	const { data: influencerOrders, isLoading: isLoadingOrders } =
		useOrdersByInfluencer(influencerId || 0);
	const { data: allClients, isLoading: isLoadingClients } = useClient();
	const [clients, setClients] = useState<ClientTableData[]>([]);

	useEffect(() => {
		if (!isLoadingOrders && !isLoadingClients && influencerOrders) {
			// Get unique client IDs from orders
			const uniqueClientIds = Array.from(
				new Set(influencerOrders.map((order) => order.orderedById))
			);

			// Filter and map clients
			const influencerClients = allClients
				.filter((client) => uniqueClientIds.includes(client.id))
				.map((client) => {
					// Find the latest order for this client
					const clientOrders = influencerOrders
						.filter((order) => order.orderedById === client.id)
						.sort((a, b) => {
							return (
								new Date(b.createdOn).getTime() -
								new Date(a.createdOn).getTime()
							);
						});

					const lastOrderDate = clientOrders[0]
						? new Date(
								clientOrders[0].createdOn
						  ).toLocaleDateString()
						: "N/A";

					return {
						id: client.id,
						username: client.username,
						email: client.email,
						instagram: client.instagramHandle,
						country: client.billingDetails.country,
						active: client.isActive ? "Yes" : "No",
						lastOrder: lastOrderDate,
					};
				});

			setClients(influencerClients);
		}
	}, [influencerOrders, allClients, isLoadingOrders, isLoadingClients]);

	const handleRowClick = (clientId: number) => {
		router.push(`/influencer/clients/${clientId}`);
	};

	if (isLoadingOrders || isLoadingClients) {
		return (
			<div className="space-y-4">
				<CustomSkeleton className="h-8 w-1/4" />
				<CustomSkeleton className="h-96" />
			</div>
		);
	}

	return (
		<div className="space-y-6">
			<h1 className="text-2xl font-semibold">My Clients</h1>
			<LikeMeTable
				data={clients}
				onRowClick={handleRowClick}
				filterableColumns={["country", "active"]}
			/>
		</div>
	);
}
