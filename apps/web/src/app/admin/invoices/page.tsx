"use client";
import { useInvoices } from "@/hooks/useInvoices";
import LikeMeTable from "@components/ui/LikeMeTable";
import { useRouter } from "next/navigation";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function InvoicesPage() {
	const { data: invoices, isLoading } = useInvoices();
	const router = useRouter();

	if (isLoading) {
		return <CustomSkeleton count={3} />;
	}

	const formattedInvoices = invoices.map((invoice) => ({
		id: invoice.id,
		amount: `$${invoice.amount}`,
		status: invoice.status.charAt(0).toUpperCase() + invoice.status.slice(1).toLowerCase(),
		createdOn: new Date(invoice.createdOn).toLocaleDateString(),
	}));

	return (
		<div className="p-6">
			<LikeMeTable
				data={formattedInvoices}
				onRowClick={(id) => router.push(`/admin/invoices/${id}`)}
				filterableColumns={["status"]}
			/>
		</div>
	);
}
