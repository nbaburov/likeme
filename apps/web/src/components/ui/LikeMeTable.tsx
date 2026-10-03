/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";
import React, { useState, useMemo } from "react";
import Papa from "papaparse";
import { IoIosCloudDownload } from "react-icons/io";
import { MdOutlineKeyboardArrowRight } from "react-icons/md";
import { Select } from "@/components/ui/Select";

interface TableProps {
	data: any[];
	buttons?: boolean;
	title?: string;
	onRowClick?: (id: number) => void;
	filterableColumns?: string[];
}

const LikeMeTable: React.FC<TableProps> = ({
	data,
	buttons,
	title,
	onRowClick,
	filterableColumns = [],
}) => {
	const [currentPage, setCurrentPage] = useState(1);
	const [filters, setFilters] = useState<Record<string, string>>({});

	// Generate available filter options from the data
	const filterOptions = useMemo(() => {
		const options: Record<string, string[]> = {};

		filterableColumns.forEach((column) => {
			const uniqueValues = Array.from(
				new Set(
					data.map((item) => item[column]?.toString()).filter(Boolean) // Remove null/undefined values
				)
			);

			// Only include columns that have more than one unique value
			if (uniqueValues.length > 1) {
				options[column] = uniqueValues;
			}
		});

		return options;
	}, [data, filterableColumns]);

	// Filter data based on selected filters
	const filteredData = data.filter((row) => {
		return Object.entries(filters).every(([key, value]) => {
			if (!value) return true;
			return row[key]?.toString() === value;
		});
	});

	const rowsPerPage = 20;

	// Calculate the current data slice
	const currentData = filteredData.slice(
		(currentPage - 1) * rowsPerPage,
		currentPage * rowsPerPage
	);

	// Generate column headers from keys
	const columns = data[0] ? Object.keys(data[0]) : [];

	// Handle pagination
	const totalPages = Math.ceil(filteredData.length / rowsPerPage);
	const changePage = (page: number) => {
		setCurrentPage(page);
	};

	const downloadCSV = () => {
		const csv = Papa.unparse(data);
		const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
		const link = document.createElement("a");
		link.href = URL.createObjectURL(blob);
		link.download = "data.csv";
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
	};

	const handleRowClick = (id: number) => {
		onRowClick?.(id);
	};

	// Early return if no data at all
	if (!data || data.length === 0) {
		return (
			<section className="container w-full mx-auto">
				{title && (
					<div className="sm:flex sm:items-center sm:justify-between">
						<h2 className="text-lg font-medium text-gray-800">
							{title}
						</h2>
					</div>
				)}
				<div className="mt-6 flex flex-col items-center justify-center p-8 border border-likeme-text rounded-lg">
					<p className="text-gray-500">No results found</p>
				</div>
			</section>
		);
	}

	return (
		<section className="container w-full mx-auto">
			<div className="sm:flex sm:items-center sm:justify-between">
				<h2 className="text-lg font-medium text-gray-800">{title}</h2>
				{buttons && (
					<div className="flex items-center mt-4 gap-x-3">
						<button
							onClick={downloadCSV}
							className="flex items-center justify-center w-1/2 px-5 py-2 text-sm tracking-wide text-white transition-colors duration-200 bg-likeme-primary rounded-lg sm:w-auto gap-x-2 hover:bg-orange-900"
						>
							<IoIosCloudDownload />
							<span>Download</span>
						</button>
					</div>
				)}
			</div>

			{/* Only show filter section if there are filterable columns */}
			{Object.keys(filterOptions).length > 0 && (
				<div className="grid grid-cols-1 md:grid-cols-5 gap-4 mt-6">
					{Object.entries(filterOptions).map(([column, values]) => (
						<Select
							key={column}
							label={
								column.charAt(0).toUpperCase() + column.slice(1)
							}
							name={column}
							value={filters[column] || ""}
							onChange={(e) => {
								setFilters((prev) => ({
									...prev,
									[column]: e.target.value,
								}));
								setCurrentPage(1);
							}}
							options={[
								{ value: "", label: `No Filter` },
								...values.map((value) => ({
									value: value,
									label: value,
								})),
							]}
						/>
					))}
				</div>
			)}

			<div className="flex flex-col mt-6">
				<div className="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
					<div className="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
						<div className="overflow-hidden border border-likeme-text md:rounded-lg">
							<table className="min-w-full divide-y divide-likeme-text">
								<thead className="bg-gradient-to-br from-orange-100 to-amber-100">
									<tr>
										{columns.map((column) => (
											<th
												key={column}
												className="px-4 py-3.5 capitalize text-sm font-normal text-left rtl:text-right text-likeme-text"
											>
												{column}
											</th>
										))}
										<th className="px-4 py-3.5"></th>
									</tr>
								</thead>
								<tbody className="bg-white divide-y divide-gray-200">
									{currentData.length > 0 ? (
										currentData.map((row, index) => (
											<tr
												key={index}
												onClick={() =>
													handleRowClick(row.id)
												}
												className="cursor-pointer hover:bg-gray-100 transition-colors duration-200"
											>
												{columns.map((column) => (
													<td
														key={column}
														className={`px-4 py-4 text-sm text-gray-700 whitespace-nowrap`}
													>
														{column.toLowerCase() ===
														"isapproved" ? (
															<div
																className={
																	row[
																		column
																	] === "Yes"
																		? "bg-green-200 py-1.5 px-1 rounded-xl text-center"
																		: "bg-rose-300 py-1.5 px-1 rounded-xl text-center"
																}
															>
																{row[column]}
															</div>
														) : (
															<>{row[column]}</>
														)}
													</td>
												))}
												<td className="px-4 py-4 text-sm text-gray-700 whitespace-nowrap">
													<MdOutlineKeyboardArrowRight className="h-5 w-5" />
												</td>
											</tr>
										))
									) : (
										<tr>
											<td
												colSpan={columns.length + 1}
												className="px-4 py-8 text-center text-gray-500"
											>
												No results found
											</td>
										</tr>
									)}
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>

			{/* Pagination */}
			{filteredData.length > 0 && (
				<div className="flex items-center justify-between mt-6">
					<button
						onClick={() => changePage(currentPage - 1)}
						disabled={currentPage === 1}
						className="flex items-center px-5 py-2 text-sm text-gray-700 capitalize transition-colors duration-200 bg-white border rounded-md gap-x-2 hover:bg-gray-100"
					>
						<svg
							xmlns="http://www.w3.org/2000/svg"
							fill="none"
							viewBox="0 0 24 24"
							strokeWidth="1.5"
							stroke="currentColor"
							className="w-5 h-5 rtl:-scale-x-100"
						>
							<path
								strokeLinecap="round"
								strokeLinejoin="round"
								d="M6.75 15.75L3 12m0 0l3.75-3.75M3 12h18"
							/>
						</svg>
						<span>Previous</span>
					</button>

					<div className="items-center hidden md:flex gap-x-3">
						{Array.from(
							{ length: totalPages },
							(_, i) => i + 1
						).map((page) => (
							<button
								key={page}
								onClick={() => changePage(page)}
								className={`px-2 py-1 text-sm rounded-md ${
									currentPage === page
										? "text-slate-500 bg-orange-100/80"
										: "text-gray-500 hover:bg-gray-100"
								}`}
							>
								{page}
							</button>
						))}
					</div>

					<button
						onClick={() => changePage(currentPage + 1)}
						disabled={currentPage === totalPages}
						className="flex items-center px-5 py-2 text-sm text-gray-700 capitalize transition-colors duration-200 bg-white border rounded-md gap-x-2 hover:bg-gray-100"
					>
						<span>Next</span>
						<svg
							xmlns="http://www.w3.org/2000/svg"
							fill="none"
							viewBox="0 0 24 24"
							strokeWidth="1.5"
							stroke="currentColor"
							className="w-5 h-5 rtl:-scale-x-100"
						>
							<path
								strokeLinecap="round"
								strokeLinejoin="round"
								d="M17.25 8.25L21 12m0 0l-3.75 3.75M21 12H3"
							/>
						</svg>
					</button>
				</div>
			)}
		</section>
	);
};

export default LikeMeTable;
