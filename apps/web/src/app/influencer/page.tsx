"use client";

import { useOrdersByInfluencer } from "@/hooks/useOrders";
import { useInvoicesByInfluencer } from "@/hooks/useInvoices";
import { ChartComponent } from "@/components/ui/Chart";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { InvoiceStatus } from "@dto/InvoiceDTO";
import { useAuth } from "@/hooks/useAuth";

export default function InfluencerDashboardPage() {
	const { getUserId } = useAuth();
	const influencerId = getUserId() ?? 0;

	const { data: orders, isLoading: isLoadingOrders } =
		useOrdersByInfluencer(influencerId);
	const { data: invoices, isLoading: isLoadingInvoices } =
		useInvoicesByInfluencer(influencerId);

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

	// Calculate total earnings (sum of paid invoices)
	const totalEarnings =
		invoices?.reduce((total, invoice) => {
			if (invoice.status === InvoiceStatus.PAID) {
				return total + invoice.amount;
			}
			return total;
		}, 0) || 0;

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

	// Calculate orders by type
	const ordersByType =
		orders?.reduce((acc: Record<string, number>, order) => {
			const type = order.offer.type;
			acc[type] = (acc[type] || 0) + 1;
			return acc;
		}, {}) || {};

	// Calculate weekly orders
	const weeklyOrders = orders?.reduce(
		(acc: Record<string, number>, order) => {
			const week = new Date(order.createdOn).toLocaleDateString("en-US", {
				year: "numeric",
				month: "short",
				day: "2-digit",
			});
			acc[week] = (acc[week] || 0) + 1;
			return acc;
		},
		{}
	);

	// Chart data
	const invoiceStatusChartData = {
		labels: Object.keys(invoiceStatusData),
		datasets: [
			{
				label: "Invoice Status Distribution",
				data: Object.values(invoiceStatusData) as number[],
				backgroundColor: [
					themeColors.yellow,
					themeColors.green,
					themeColors.red,
				],
			},
		],
	};

	const orderTypeChartData = {
		labels: Object.keys(ordersByType),
		datasets: [
			{
				label: "Orders by Type",
				data: Object.values(ordersByType) as number[],
				backgroundColor: [
					themeColors.primary,
					themeColors.accent,
					themeColors.secondary,
				],
			},
		],
	};

	const weeklyOrdersChartData = {
		labels: Object.keys(weeklyOrders || {}),
		datasets: [
			{
				label: "Weekly Orders",
				data: Object.values(weeklyOrders || {}) as number[],
				borderColor: themeColors.primary,
				tension: 0.4,
				fill: false,
			},
		],
	};

	return (
		<div className="space-y-6 p-4">
			<h1 className="text-2xl font-bold text-likeme-text">
				Influencer Dashboard
			</h1>

			{/* Summary Cards */}
			<div className="grid grid-cols-1 md:grid-cols-3 gap-4">
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
						Total Invoices
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{invoices?.length || 0}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Total Earnings 
					</h3>
					<p className="text-likeme-text/40 font-extralight text-xs lowercase">
						Before Commission
					</p>
					<p className="text-2xl font-bold text-likeme-primary">
						${totalEarnings.toLocaleString()}
					</p>
				</div>
			</div>

			{/* Charts */}
			<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
				<ChartComponent
					type="doughnut"
					data={invoiceStatusChartData}
					className="h-[300px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Invoice Status Distribution",
							},
						},
					}}
				/>

				<ChartComponent
					type="bar"
					data={orderTypeChartData}
					className="h-[300px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Orders by Type",
							},
						},
						scales: {
							y: {
								beginAtZero: true,
								ticks: { stepSize: 1 },
							},
						},
					}}
				/>

				<ChartComponent
					type="line"
					data={weeklyOrdersChartData}
					className="h-[300px] md:col-span-2"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Weekly Orders",
							},
						},
						scales: {
							y: {
								beginAtZero: true,
								ticks: { stepSize: 1 },
							},
						},
					}}
				/>
			</div>
		</div>
	);
}
