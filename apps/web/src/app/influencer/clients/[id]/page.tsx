"use client";

import { useEffect, useState } from "react";
import { useOrdersByClient } from "@/hooks/useOrders";
import { useClientById } from "@/hooks/useClient";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import LikeMeTable from "@/components/ui/LikeMeTable";
import { OrderResponse } from "@/dto/OrderDTO";
import { BubbleButton } from "@/components/ui/BubbleButton";
import { useRouter } from "next/navigation";

interface OrderTableData {
	id: number;
	offer: string;
	type: string;
	status: string;
	created: string;
	price: string;
}

interface InvoiceTableData {
	id: number;
	orderId: number;
	amount: string;
	status: string;
	date: string;
}

interface PageProps {
	params: {
		id: string;
	};
}

export default function ClientDetailsPage({ params }: PageProps) {
	const router = useRouter();
	const clientId = parseInt(params.id);
	const { data: client, isLoading: isLoadingClient } =
		useClientById(clientId);
	const { data: orders, isLoading: isLoadingOrders } =
		useOrdersByClient(clientId);
	const [ordersList, setOrdersList] = useState<OrderTableData[]>([]);
	const [invoicesList, setInvoicesList] = useState<InvoiceTableData[]>([]);

	useEffect(() => {
		if (!isLoadingOrders && orders) {
			// Format orders for table display
			const formattedOrders = orders.map((order: OrderResponse) => ({
				id: order.id,
				offer: order.offer.title,
				type: order.offer.type,
				status: order.status,
				created: new Date(order.createdOn).toLocaleDateString(),
				price: `$${order.offer.price}`,
			}));
			setOrdersList(formattedOrders);

			// Format invoices for table display
			const formattedInvoices = orders.map((order: OrderResponse) => ({
				id: order.invoice.id,
				orderId: order.id,
				amount: `$${order.invoice.amount}`,
				status: order.invoice.status,
				date: new Date(order.invoice.createdOn).toLocaleDateString(),
			}));
			setInvoicesList(formattedInvoices);
		}
	}, [orders, isLoadingOrders]);

	if (isLoadingClient || isLoadingOrders) {
		return (
			<div className="space-y-4">
				<CustomSkeleton className="h-8 w-1/4" />
				<CustomSkeleton className="h-96" />
			</div>
		);
	}

	return (
		<div className="space-y-8">
			<div className="flex justify-between items-center">
				<h1 className="text-2xl font-semibold">Client Details</h1>
				<BubbleButton onClick={() => router.back()}>Back</BubbleButton>
			</div>

			{/* Client Information Card */}
			<div className="bg-white rounded-lg shadow p-6">
				<div className="grid grid-cols-2 gap-4">
					<div>
						<h2 className="text-lg font-semibold mb-4">
							Profile Information
						</h2>
						<div className="space-y-2">
							<p>
								<span className="font-medium">Username:</span>{" "}
								{client?.username}
							</p>
							<p>
								<span className="font-medium">Email:</span>{" "}
								{client?.email}
							</p>
							<p>
								<span className="font-medium">Instagram:</span>{" "}
								{client?.instagramHandle}
							</p>
							<p>
								<span className="font-medium">Status:</span>{" "}
								<span
									className={`${
										client?.isActive
											? "text-green-600"
											: "text-red-600"
									}`}
								>
									{client?.isActive ? "Active" : "Inactive"}
								</span>
							</p>
						</div>
					</div>
					<div>
						<h2 className="text-lg font-semibold mb-4">
							Billing Details
						</h2>
						<div className="space-y-2">
							<p>
								<span className="font-medium">Name:</span>{" "}
								{`${client?.billingDetails.firstName} ${client?.billingDetails.lastName}`}
							</p>
							<p>
								<span className="font-medium">Address:</span>{" "}
								{client?.billingDetails.streetAddress}
							</p>
							<p>
								<span className="font-medium">City:</span>{" "}
								{client?.billingDetails.city}
							</p>
							<p>
								<span className="font-medium">Country:</span>{" "}
								{client?.billingDetails.country}
							</p>
						</div>
					</div>
				</div>
			</div>

			{/* Orders Table */}
			<div className="space-y-4">
				<h2 className="text-xl font-semibold">Orders</h2>
				<LikeMeTable
					data={ordersList}
					title="Order History"
				/>
			</div>

			{/* Invoices Table */}
			<div className="space-y-4">
				<h2 className="text-xl font-semibold">Invoices</h2>
				<LikeMeTable
					data={invoicesList}
					title="Invoice History"
				/>
			</div>
		</div>
	);
}
