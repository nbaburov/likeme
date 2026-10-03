/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState } from "react";
import { useClient } from "@/hooks/useClient";
import { useOrders } from "@/hooks/useOrders";
import { useOffers } from "@/hooks/useOffers";
import { useInvoices } from "@/hooks/useInvoices";
import { useInfluencer } from "@/hooks/useInfluencer";
import { useInfluencerApplications } from "@/hooks/useInfluencerApplications";
import { ChartComponent } from "@/components/ui/Chart";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { Select } from "@/components/ui/Select";
import { useInfluencerOrderAnalytics } from "@/hooks/useAnalytics";
import { InvoiceStatus } from "@dto/InvoiceDTO";
import { usePlatformSettings } from "@/hooks/usePlatformSettings";

export default function AdminPage() {
	const [selectedInfluencer, setSelectedInfluencer] = useState<string>("");
	const { data: clients, isLoading: isLoadingClients } = useClient();
	const { data: orders, isLoading: isLoadingOrders } = useOrders();
	const { data: offers, isLoading: isLoadingOffers } = useOffers();
	const { data: invoices, isLoading: isLoadingInvoices } = useInvoices();
	const { data: influencers, isLoading: isLoadingInfluencers } =
		useInfluencer();
	const { data: applications, isLoading: isLoadingApplications } =
		useInfluencerApplications();
	const { data: analytics, isLoading: isLoadingAnalytics } =
		useInfluencerOrderAnalytics(parseInt(selectedInfluencer) || 0);
	const { data: platformSettings } = usePlatformSettings();

	if (
		isLoadingClients ||
		isLoadingOrders ||
		isLoadingOffers ||
		isLoadingInvoices ||
		isLoadingInfluencers ||
		isLoadingApplications ||
		isLoadingAnalytics
	) {
		return <CustomSkeleton count={7} className="h-64 mb-4" />;
	}

	const influencerOptions =
		influencers?.map((influencer) => ({
			value: influencer.id.toString(),
			label: `${influencer.application.billingDetails.firstName} ${influencer.application.billingDetails.lastName}`,
		})) || [];

	// Theme colors from config
	const themeColors = {
		primary: "#FF5757",
		secondary: "#F2E9E9",
		third: "#efeadb",
		accent: "#E4593E",
		text: "#282525",
		yellow: "#ffa526",
		red: "#ff265c",
		pink: "#ff26b7",
		green: "#b0ff26",
	};

	// Add invoice status analytics
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

	const invoiceStatusChartData = {
		labels: Object.keys(invoiceStatusData),
		datasets: [
			{
				label: "Invoice Status Distribution",
				data: Object.values(invoiceStatusData),
				backgroundColor: [
					themeColors.yellow, // PENDING
					themeColors.green, // PAID
					themeColors.red, // FAILED
				],
				borderColor: themeColors.secondary,
				borderWidth: 1,
			},
		],
	};

	// Update existing chart colors
	const ordersByCountryChartData = {
		labels: Object.keys(analytics?.ordersByCountry || {}),
		datasets: [
			{
				label: "Orders by Country",
				data: Object.values(analytics?.ordersByCountry || {}),
				backgroundColor: [
					themeColors.primary,
					themeColors.accent,
					themeColors.third,
					"#FF8C42", // complementary
					"#FFB566", // complementary light
				],
			},
		],
	};

	const totalOrdersByType =
		orders?.reduce((acc: Record<string, number>, order) => {
			const type = order.offer.type;
			acc[type] = (acc[type] || 0) + 1;
			return acc;
		}, {}) || {};

	const applicationsByCountry =
		applications?.reduce((acc: Record<string, number>, app) => {
			const country = app.billingDetails.country;
			acc[country] = (acc[country] || 0) + 1;
			return acc;
		}, {}) || {};

	const orderChartData = {
		labels: Object.keys(totalOrdersByType),
		datasets: [
			{
				label: "Orders by Type",
				data: Object.values(totalOrdersByType),
				backgroundColor: [
					themeColors.primary,
					themeColors.accent,
					themeColors.third,
				],
			},
		],
	};

	// Calculate monthly revenue from paid invoices
	const monthlyRevenue =
		invoices?.reduce((acc: Record<string, number>, invoice) => {
			if (invoice.status === InvoiceStatus.PAID) {
				const month = new Date(invoice.createdOn).toLocaleString(
					"default",
					{ month: "long" }
				);
				acc[month] = (acc[month] || 0) + invoice.amount;
				return acc;
			}
			return acc;
		}, {}) || {};

	const applicationChartData = {
		labels: Object.keys(applicationsByCountry),
		datasets: [
			{
				label: "Applications by Country",
				data: Object.values(applicationsByCountry),
				backgroundColor: themeColors.primary,
				borderColor: themeColors.primary,
			},
		],
	};

	// Additional chart data calculations
	const offersByPrice =
		offers?.reduce((acc: Record<string, number>, offer) => {
			const priceRange = `$${Math.floor(offer.price / 100) * 100}-${
				Math.floor(offer.price / 100) * 100 + 100
			}`;
			acc[priceRange] = (acc[priceRange] || 0) + 1;
			return acc;
		}, {}) || {};

	const offersByType =
		offers?.reduce((acc: Record<string, number>, offer) => {
			acc[offer.type] = (acc[offer.type] || 0) + 1;
			return acc;
		}, {}) || {};

	const clientsByCountry =
		clients?.reduce((acc: Record<string, number>, client) => {
			const country = client.billingDetails.country;
			acc[country] = (acc[country] || 0) + 1;
			return acc;
		}, {}) || {};

	const monthlyClientSignups =
		clients?.reduce((acc: Record<string, number>, client) => {
			const month = new Date(client.createdOn).toLocaleString("default", {
				month: "long",
			});
			acc[month] = (acc[month] || 0) + 1;
			return acc;
		}, {}) || {};

	// New chart data objects
	const offerPriceChartData = {
		labels: Object.keys(offersByPrice),
		datasets: [
			{
				label: "Offers by Price Range",
				data: Object.values(offersByPrice),
				backgroundColor: [
					themeColors.primary,
					themeColors.accent,
					themeColors.third,
				],
				borderColor: themeColors.secondary,
			},
		],
	};

	// Summary calculations
	const totalStats = {
		clients: clients?.length || 0,
		influencers: influencers?.length || 0,
		orders: orders?.length || 0,
		invoices: invoices?.length || 0,
		offers: offers?.length || 0,
		applications: applications?.length || 0,
	};

	// Updated chart data with theme colors
	const offerTypeChartData = {
		labels: Object.keys(offersByType),
		datasets: [
			{
				label: "Offers by Type",
				data: Object.values(offersByType),
				backgroundColor: [
					themeColors.primary,
					themeColors.accent,
					themeColors.third,
				],
			},
		],
	};

	const clientCountryChartData = {
		labels: Object.keys(clientsByCountry),
		datasets: [
			{
				label: "Clients by Country",
				data: Object.values(clientsByCountry),
				backgroundColor: [
					themeColors.accent,
					themeColors.pink,
					themeColors.yellow,
					themeColors.red,
				],
			},
		],
	};

	// Combine monthly data
	const combinedMonthlyData = {
		labels: Array.from(
			new Set([
				...Object.keys(monthlyClientSignups),
				...Object.keys(monthlyRevenue),
			])
		).sort((a, b) => {
			const months = [
				"January",
				"February",
				"March",
				"April",
				"May",
				"June",
				"July",
				"August",
				"September",
				"October",
				"November",
				"December",
			];
			return months.indexOf(a) - months.indexOf(b);
		}),
		datasets: [
			{
				label: "Monthly Client Signups",
				data: Object.values(monthlyClientSignups),
				fill: false,
				borderColor: themeColors.primary,
				backgroundColor: themeColors.primary,
				tension: 0.4,
				yAxisID: "y",
			},
			{
				label: "Monthly Revenue ($)",
				data: Object.values(monthlyRevenue),
				fill: false,
				borderColor: themeColors.accent,
				backgroundColor: themeColors.accent,
				tension: 0.4,
				yAxisID: "y1",
			},
		],
	};

	// Calculate total revenue from all paid invoices
	const totalRevenue =
		invoices?.reduce((total, invoice) => {
			if (invoice.status === InvoiceStatus.PAID) {
				return total + invoice.amount;
			}
			return total;
		}, 0) || 0;

	// Get commission percentage from platform settings
	const commissionSetting = platformSettings?.find(
		(setting) => setting.type === "COMMISSION_PERCENTAGE"
	);
	const commissionPercentage = commissionSetting
		? parseFloat(commissionSetting.value)
		: 0;

	// Calculate platform profit
	const platformProfit = (totalRevenue * commissionPercentage) / 100;

	return (
		<div className="space-y-6 p-4">
			<h1 className="text-2xl font-bold text-likeme-text">
				Platform Analytics
			</h1>
			{/* Summary Cards */}
			<div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">Clients</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{totalStats.clients}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Influencers
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{totalStats.influencers}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">Orders</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{totalStats.orders}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">Invoices</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{totalStats.invoices}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">Offers</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{totalStats.offers}
					</p>
				</div>
				<div className="bg-white p-4 rounded-lg shadow-md border border-likeme-primary">
					<h3 className="text-likeme-text font-semibold">
						Applications
					</h3>
					<p className="text-2xl font-bold text-likeme-primary">
						{totalStats.applications}
					</p>
				</div>
			</div>

			{/* Revenue and Profit Cards */}
			<div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-8">
				<div className="bg-white p-6 rounded-lg shadow-md border-l-4 border-likeme-primary">
					<div className="flex items-center justify-between">
						<div>
							<h3 className="text-likeme-text text-lg font-semibold mb-2">
								Total Platform Revenue
							</h3>
							<p className="text-3xl font-bold text-likeme-primary">
								${totalRevenue.toLocaleString()}
							</p>
						</div>
						<div className="text-likeme-primary opacity-20">
							<svg
								xmlns="http://www.w3.org/2000/svg"
								fill="none"
								viewBox="0 0 24 24"
								strokeWidth={1.5}
								stroke="currentColor"
								className="w-12 h-12"
							>
								<path
									strokeLinecap="round"
									strokeLinejoin="round"
									d="M12 6v12m-3-2.818.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
								/>
							</svg>
						</div>
					</div>
					<p className="text-sm text-likeme-text mt-2">
						Total revenue from all paid invoices
					</p>
				</div>

				<div className="bg-white p-6 rounded-lg shadow-md border-l-4 border-likeme-accent">
					<div className="flex items-center justify-between">
						<div>
							<h3 className="text-likeme-text text-lg font-semibold mb-2">
								Platform Profit
							</h3>
							<p className="text-3xl font-bold text-likeme-accent">
								${platformProfit.toLocaleString()}
							</p>
							<p className="text-sm text-likeme-text mt-1">
								Commission Rate: {commissionPercentage}%
							</p>
						</div>
						<div className="text-likeme-accent opacity-20">
							<svg
								xmlns="http://www.w3.org/2000/svg"
								fill="none"
								viewBox="0 0 24 24"
								strokeWidth={1.5}
								stroke="currentColor"
								className="w-12 h-12"
							>
								<path
									strokeLinecap="round"
									strokeLinejoin="round"
									d="M2.25 18.75a60.07 60.07 0 0 1 15.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 0 1 3 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 0 0-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 0 1-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 0 0 3 15h-.75M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Zm3 0h.008v.008H18V10.5Zm-12 0h.008v.008H6V10.5Z"
								/>
							</svg>
						</div>
					</div>
					<p className="text-sm text-likeme-text mt-2">
						Platform profit based on commission rate
					</p>
				</div>
			</div>

			<Select
				label="Select Influencer"
				name="influencer"
				value={selectedInfluencer}
				onChange={(e) => setSelectedInfluencer(e.target.value)}
				options={[
					{ value: "", label: "Select an influencer" },
					...influencerOptions,
				]}
				className="max-w-md"
			/>

			{selectedInfluencer && (
				<ChartComponent
					type="pie"
					data={ordersByCountryChartData}
					className="h-[400px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Orders by Country Distribution",
							},
						},
					}}
				/>
			)}

			{/* New analytics charts */}
			<div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
				<ChartComponent
					type="doughnut"
					data={invoiceStatusChartData}
					className="h-[400px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Invoice Status Distribution",
								color: themeColors.text,
								font: {
									size: 16,
									weight: "bold",
								},
							},
							legend: {
								labels: {
									color: themeColors.text,
								},
							},
						},
					}}
				/>

				<ChartComponent
					type="bar"
					data={orderChartData}
					className="h-[400px] md:col-span-2"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Orders by Type",
								color: themeColors.text,
								font: {
									size: 16,
									weight: "bold",
								},
							},
							legend: {
								labels: {
									color: themeColors.text,
								},
							},
						},
						scales: {
							y: {
								beginAtZero: true,
								ticks: {
									stepSize: 1,
									color: themeColors.text,
								},
								grid: {
									color: `${themeColors.text}22`,
								},
							},
							x: {
								ticks: {
									color: themeColors.text,
								},
								grid: {
									color: `${themeColors.text}22`,
								},
							},
						},
					}}
				/>

				<ChartComponent
					type="bar"
					data={applicationChartData}
					className="h-[400px] md:col-span-3"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Influencer Applications by Country",
							},
						},
						scales: {
							y: {
								beginAtZero: true,
								ticks: {
									stepSize: 1,
								},
							},
						},
					}}
				/>

				<ChartComponent
					type="bar"
					data={offerPriceChartData}
					className="h-[400px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Offer Distribution by Price Range",
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
					type="pie"
					data={offerTypeChartData}
					className="h-[400px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Offer Type Distribution",
							},
						},
					}}
				/>

				<ChartComponent
					type="doughnut"
					data={clientCountryChartData}
					className="h-[400px]"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Client Distribution by Country",
							},
						},
					}}
				/>

				<ChartComponent
					type="line"
					data={combinedMonthlyData}
					className="h-[400px] md:col-span-3"
					options={{
						plugins: {
							title: {
								display: true,
								text: "Monthly Revenue & Client Signups",
								color: themeColors.text,
								font: {
									size: 16,
									weight: "bold",
								},
							},
							legend: {
								labels: {
									color: themeColors.text,
								},
							},
						},
						scales: {
							y: {
								type: "linear",
								display: true,
								position: "left",
								title: {
									display: true,
									text: "Number of Signups",
									color: themeColors.primary,
								},
								ticks: {
									color: themeColors.primary,
									stepSize: 1,
								},
								grid: {
									color: `${themeColors.text}11`,
								},
							},
							y1: {
								type: "linear",
								display: true,
								position: "right",
								title: {
									display: true,
									text: "Revenue ($)",
									color: themeColors.accent,
								},
								ticks: {
									color: themeColors.accent,
									callback: (value: any) => `$${value}`,
								},
								grid: {
									drawOnChartArea: false,
								},
							},
							x: {
								ticks: {
									color: themeColors.text,
								},
								grid: {
									color: `${themeColors.text}22`,
								},
							},
						},
					}}
				/>
			</div>
		</div>
	);
}
