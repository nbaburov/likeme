"use client";
import { useOrderById } from "@/hooks/useOrders";
import { OrderInfo } from "@/components/admin/order/OrderInfo";
import { useRouter } from "next/navigation";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function OrderDetailsPage({
	params,
}: {
	params: { id: string };
}) {
	const router = useRouter();
	const { data: order, isLoading } = useOrderById(Number(params.id));

	if (isLoading) {
		return <CustomSkeleton count={7} />;
	}

	if (!order) {
		return null;
	}


	return (
		<OrderInfo
			order={order}
			onBack={() => router.push("/account/orders")}
			onNavigateToOffer={(offerId) => router.push(`/offers/${offerId}`)}
			onNavigateToInvoice={(invoiceId) =>
				router.push(`/account/invoices/${invoiceId}`)
			}
		/>
	);
}
