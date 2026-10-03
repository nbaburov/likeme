"use client";
import { useAuth } from "@/hooks/useAuth";
import { useInfluencerById } from "@/hooks/useInfluencer";
import { useInvoicesByInfluencer } from "@/hooks/useInvoices";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import LikeMeTable from "@components/ui/LikeMeTable";
import { useRouter } from "next/navigation";

export default function InfluencerInvoicesPage() {
	const router = useRouter();
	const { getUserId } = useAuth();
	const { data: influencer, isLoading: isLoadingInfluencer } =
		useInfluencerById(getUserId() ?? 0);
	const { data: invoices, isLoading: isLoadingInvoices } =
		useInvoicesByInfluencer(influencer?.id ?? 0);

	if (isLoadingInfluencer || isLoadingInvoices) {
		return <CustomSkeleton count={3} />;
	}

	const tableData =
		invoices?.map((invoice) => ({
			id: invoice.id,
			amount: `$${invoice.amount}`,
			status: invoice.status,
			createdOn: new Date(invoice.createdOn).toLocaleDateString(),
			updatedOn: new Date(invoice.updatedOn).toLocaleDateString(),
		})) ?? [];

	return (
		<div className="space-y-6">
			<h1 className="text-2xl font-semibold">Invoices</h1>
			<LikeMeTable
				data={tableData}
				title="My Invoices"
				onRowClick={(id) => router.push(`/influencer/invoices/${id}`)}
				filterableColumns={["status"]}
			/>
		</div>
	);
}
