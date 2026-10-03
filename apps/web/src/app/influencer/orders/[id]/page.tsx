// src/app/influencer/orders/[id]/page.tsx
"use client";
import { useOrderById, useOrders } from "@/hooks/useOrders";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { useRouter } from "next/navigation";
import { OrderInfo } from "@/components/influencer/admin/orders/OrderInfo";

export default function InfluencerOrderDetailsPage({
    params,
}: {
    params: { id: string };
}) {
    const router = useRouter();
    const { data: order, isLoading } = useOrderById(Number(params.id));
    const { complete, isCompleting } = useOrders();

    if (isLoading) {
        return <CustomSkeleton count={7} />;
    }

    if (!order) {
        return null;
    }

    const handleComplete = async () => {
        await complete(order.id);
        router.push("/influencer/orders");
    };

    return (
        <OrderInfo
            order={order}
            onComplete={handleComplete}
            onBack={() => router.push("/influencer/orders")}
            onNavigateToOffer={(offerId) => router.push(`/influencer/offers/${offerId}`)}
            onNavigateToInvoice={(invoiceId) =>
                router.push(`/influencer/invoices/${invoiceId}`)
            }
            isCompleting={isCompleting}
        />
    );
}