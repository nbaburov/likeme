"use client";
import { useParams, useRouter } from "next/navigation";
import { useInvoiceById, useInvoices } from "@/hooks/useInvoices";
import { InvoiceInfo } from "@components/admin/invoice/InvoiceInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateInvoiceRequest } from "@/dto/InvoiceDTO";

export default function InvoiceDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { data: invoice, isLoading } = useInvoiceById(Number(id));
	const {
		update,
		delete: deleteInvoice,
		isUpdating,
		isDeleting,
	} = useInvoices();

	const handleDelete = async () => {
		await deleteInvoice(Number(id));
		router.push("/admin/invoices");
	};

	const handleUpdate = async (data: UpdateInvoiceRequest) => {
		await update({ id: Number(id), invoice: data });
	};

	if (isLoading || !invoice) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<InvoiceInfo
				invoice={invoice}
				onDelete={handleDelete}
				onUpdate={handleUpdate}
				onBack={() => router.push("/admin/invoices")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
			/>
		</div>
	);
}
