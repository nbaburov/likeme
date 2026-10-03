"use client";
import { useOrdersByClient } from "@/hooks/useOrders";
import { useRouter } from "next/navigation";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { useAuth } from "@/hooks/useAuth";
import { Card, CardBody, Typography, Button } from "@material-tailwind/react";
import { OrderStatus } from "@dto/OrderDTO";

export default function ClientOrdersPage() {
	const { getUserId } = useAuth();
	const { data: orders, isLoading } = useOrdersByClient(getUserId() ?? 0);
	const router = useRouter();

	if (isLoading) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="space-y-6">
			<h1 className="text-2xl font-semibold">My Orders</h1>
			<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
				{(orders ?? []).map((order) => (
					<Card
						key={order.id}
						className="cursor-pointer hover:scale-[1.02] transition-transform"
						onClick={() =>
							router.push(`/account/orders/${order.id}`)
						}
					>
						<CardBody className="space-y-4">
							<div className="flex justify-between items-start">
								<Typography variant="h5" color="inherit">
									{order.offer.title}
								</Typography>
								<Button
									size="sm"
									color={
										order.status === OrderStatus.COMPLETE
											? "success"
											: "info"
									}
									className="px-4 py-2"
								>
									{order.status}
								</Button>
							</div>

							<div className="space-y-2">
								<Typography className="font-semibold text-lg">
									${order.offer.price}
								</Typography>
								<Typography color="inherit" className="text-sm">
									Created:{" "}
									{new Date(
										order.createdOn
									).toLocaleDateString()}
								</Typography>
								{order.details.comment && (
									<Typography
										color="inherit"
										className="text-sm line-clamp-2"
									>
										{order.details.comment}
									</Typography>
								)}
							</div>
						</CardBody>
					</Card>
				))}
			</div>

			{orders?.length === 0 && (
				<div className="text-center py-8">
					<Typography color="inherit">No orders found</Typography>
				</div>
			)}
		</div>
	);
}
