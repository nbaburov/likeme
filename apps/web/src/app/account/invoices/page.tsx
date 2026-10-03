"use client";
import { useInvoicesByClientId } from "@/hooks/useInvoices";
import LikeMeTable from "@components/ui/LikeMeTable";
import { useRouter } from "next/navigation";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { useAuth } from "@/hooks/useAuth";

export default function ClientInvoicesPage() {
	const { getUserId } = useAuth();
	const { data: invoices, isLoading } = useInvoicesByClientId(
		getUserId() ?? 0
	);
	const router = useRouter();

	if (isLoading) {
		return <CustomSkeleton count={3} />;
	}

	const formattedInvoices = (invoices ?? []).map((invoice) => ({
		id: invoice.id,
		amount: `$${invoice.amount}`,
		status: invoice.status,
		createdOn: new Date(invoice.createdOn).toLocaleDateString(),
	}));

	return (
		<div className="space-y-6">
			<h1 className="text-2xl font-semibold">My Invoices</h1>
			<LikeMeTable
				data={formattedInvoices}
				onRowClick={(id) => router.push(`/account/invoices/${id}`)}
				filterableColumns={["status"]}
			/>
		</div>
	);
}
