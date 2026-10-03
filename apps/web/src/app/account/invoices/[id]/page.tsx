"use client";
import { useParams, useRouter } from "next/navigation";
import { useInvoiceById, useInvoices } from "@/hooks/useInvoices";
import { InvoiceInfo } from "@components/client/invoice/InvoiceInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function ClientInvoiceDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { data: invoice, isLoading } = useInvoiceById(Number(id));
	const { pay, isPaying } = useInvoices();

	const handlePay = async () => {
		await pay(Number(id));
		router.push("/account/invoices");
	};

	if (isLoading || !invoice) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<InvoiceInfo
				invoice={invoice}
				onPay={handlePay}
				onBack={() => router.push("/account/invoices")}
				isPaying={isPaying}
			/>
		</div>
	);
}
