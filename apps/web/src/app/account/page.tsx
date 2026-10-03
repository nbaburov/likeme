"use client";

import { useOrdersByClient } from "@/hooks/useOrders";
import { useInvoicesByClientId } from "@/hooks/useInvoices";
import { ChartComponent } from "@/components/ui/Chart";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { InvoiceStatus } from "@dto/InvoiceDTO";
import { useAuth } from "@/hooks/useAuth";

export default function ClientDashboardPage() {
	const { getUserId } = useAuth();
	const clientId = getUserId() ?? 0;

	const { data: orders, isLoading: isLoadingOrders } =
		useOrdersByClient(clientId);
	const { data: invoices, isLoading: isLoadingInvoices } =
		useInvoicesByClientId(clientId);

	if (isLoadingOrders || isLoadingInvoices) {
		return <CustomSkeleton count={4} className="h-64 mb-4" />;
	}

	// Theme colors
	const themeColors = {
		primary: "#FF5757",
		secondary: "#F2E9E9",
		accent: "#E4593E",
		yellow: "#ffa526",
		red: "#ff265c",
		green: "#b0ff26",
	};

	// Calculate totals
	const totalSpent =
		invoices?.reduce((total, invoice) => {
			if (invoice.status === InvoiceStatus.PAID) {
				return total + invoice.amount;
			}
			return total;
		}, 0) || 0;

	const paidInvoices =
		invoices?.filter((invoice) => invoice.status === InvoiceStatus.PAID)
			.length || 0;

	const unpaidInvoices =
		invoices?.filter((invoice) => invoice.status === InvoiceStatus.PENDING)
			.length || 0;

	// Calculate invoice status distribution
	const invoiceStatusData =
		invoices?.reduce(
			(acc: Record<InvoiceStatus, number>, invoice) => {
				acc[invoice.status] = (acc[invoice.status] || 0) + 1;
				return acc;
			},
			{
				[InvoiceStatus.PENDING]: 0,
				[InvoiceStatus.PAID]: 0,
				[InvoiceStatus.FAILED]: 0,
			}
		) || {};

	// Chart data
	const invoiceStatusChartData = {
		labels: Object.keys(invoiceStatusData),
		datasets: [
			{
				label: "Invoice Status Distribution",
				data: Object.values(invoiceStatusData) as number[],
				backgroundColor: [
					themeColors.yellow, // PENDING
					themeColors.green, // PAID
					themeColors.red, // FAILED
				],
			},
		],
	};

	return (
		<div className="space-y-6 p-4">
			<h1 className="text-2xl font-bold text-likeme-text">
				Client Dashboard
			</h1>

			{/* Summary Cards */}
			<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Total Orders
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{orders?.length || 0}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Paid Invoices
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{paidInvoices}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Pending Invoices
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{unpaidInvoices}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Total Spent
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						${totalSpent.toLocaleString()}
					</p>
				</div>
			</div>

			{/* Chart */}
			<div className="mt-6">
				<ChartComponent
					type="doughnut"
					data={invoiceStatusChartData}
					className="h-[300px] max-w-[500px] mx-auto"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Invoice Status Distribution",
								font: {
									size: 16,
									weight: "bold",
								},
							},
							legend: {
								position: "bottom",
							},
						},
					}}
				/>
			</div>
		</div>
	);
}
