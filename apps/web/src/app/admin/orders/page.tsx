"use client";
import { useRouter } from "next/navigation";
import { useOrders } from "@/hooks/useOrders";
import LikeMeTable from "@components/ui/LikeMeTable";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { useInfluencer } from "@hooks/useInfluencer";
import { useClient } from "@hooks/useClient";

export default function OrdersPage() {
	const { data: orders, isLoading } = useOrders();
	const { data: influencers, isLoading: isInfluencersLoading } =
		useInfluencer();
	const { data: clients, isLoading: isClientsLoading } = useClient();
	const router = useRouter();

	if (isLoading || isInfluencersLoading || isClientsLoading) {
		return <CustomSkeleton count={3} />;
	}

	const formattedOrders = orders.map((order) => ({
		id: order.id,
		"Offer Title": order.offer.title,
		price: `$${order.offer.price}`,
		type: order.offer.type,
		client: order.orderedById
			? clients?.find((client) => client.id === order.orderedById)
					?.username
			: "Unknown",
		influencer: order.offer.createdById
			? influencers?.find(
					(influencer) => influencer.id === order.offer.createdById
			  )?.application.instagramHandle
			: "Unknown",
		"Order Status":
			order.status.charAt(0).toUpperCase() +
			order.status.slice(1).toLowerCase(),
		"Invoice Status":
			order.invoice.status.charAt(0).toUpperCase() +
			order.invoice.status.slice(1).toLowerCase(),
		"Created On": new Date(order.createdOn).toLocaleDateString(),
	}));

	return (
		<div className="p-6">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-2xl font-bold">Orders</h1>
			</div>

			<LikeMeTable
				data={formattedOrders}
				onRowClick={(id) => router.push(`/admin/orders/${id}`)}
				filterableColumns={[
					"type",
					"client",
					"influencer",
					"Order Status",
					"Invoice Status",
				]}
			/>
		</div>
	);
}
