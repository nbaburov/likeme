// src/app/influencer/orders/page.tsx
"use client";
import { useAuth } from "@/hooks/useAuth";
import { useInfluencerById } from "@/hooks/useInfluencer";
import { useOrdersByInfluencer } from "@/hooks/useOrders";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import LikeMeTable from "@components/ui/LikeMeTable";
import { InvoiceStatus } from "@dto/InvoiceDTO";
import { useClient } from "@hooks/useClient";
import { useRouter } from "next/navigation";

export default function InfluencerOrdersPage() {
	const router = useRouter();
	const { getUserId } = useAuth();
	const { data: influencer, isLoading: isLoadingInfluencer } =
		useInfluencerById(getUserId() ?? 0);
	const { data: orders, isLoading: isLoadingOrders } = useOrdersByInfluencer(
		influencer?.id ?? 0
	);
	const { data: clients, isLoading: isLoadingClients } = useClient();
	if (isLoadingInfluencer || isLoadingOrders || isLoadingClients) {
		return <CustomSkeleton count={3} />;
	}

	const tableData =
		orders?.map((order) => ({
			id: order.id,
			offer: order.offer.title,
			price: `$${order.offer.price}`,
			client: order.orderedById ? clients?.find(client => client.id === order.orderedById)?.username : "Unknown",
			status: order.status.charAt(0).toUpperCase() + order.status.slice(1),
			createdOn: new Date(order.createdOn).toLocaleDateString(),
			paid: order.invoice?.status === InvoiceStatus.PAID ? "Yes" : "No",
		})) ?? [];

	return (
		<div className="space-y-6">
			<h1 className="text-2xl font-semibold">Orders</h1>
			<LikeMeTable
				data={tableData}
				title="My Orders"
				onRowClick={(id) => router.push(`/influencer/orders/${id}`)}
				filterableColumns={["client", "status", "paid"]}
			/>
		</div>
	);
}
