"use client";
import { useInvoiceById } from "@/hooks/useInvoices";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { useRouter } from "next/navigation";
import { InvoiceInfo } from "@/components/influencer/admin/invoices/InvoiceInfo";

export default function InfluencerInvoiceDetailsPage({
	params,
}: {
	params: { id: string };
}) {
	const router = useRouter();
	const { data: invoice, isLoading } = useInvoiceById(Number(params.id));

	if (isLoading) {
		return <CustomSkeleton count={7} />;
	}

	if (!invoice) {
		return null;
	}

	return (
		<InvoiceInfo
			invoice={invoice}
			onBack={() => router.push("/influencer/invoices")}
		/>
	);
}
