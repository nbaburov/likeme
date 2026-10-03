"use client";
import { useParams, useRouter } from "next/navigation";
import { useOrderById } from "@/hooks/useOrders";
import { OrderInfo } from "@components/admin/order/OrderInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function OrderDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { data: order, isLoading } = useOrderById(Number(id));

	if (isLoading || !order) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<OrderInfo
				order={order}
				onBack={() => router.push("/admin/orders")}
				onNavigateToOffer={(offerId) => router.push(`/admin/offers/${offerId}`)}
				onNavigateToInvoice={(invoiceId) => router.push(`/admin/invoices/${invoiceId}`)}
			/>
		</div>
	);
}
