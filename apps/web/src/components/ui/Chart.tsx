/* eslint-disable @typescript-eslint/no-explicit-any */
import { Chart as ChartJS, registerables } from "chart.js";
import { Chart } from "react-chartjs-2";

ChartJS.register(...registerables);

interface ChartProps {
	type: "line" | "bar" | "pie" | "doughnut";
	data: {
		labels: string[];
		datasets: {
			label: string;
			data: number[];
			backgroundColor?: string | string[];
			borderColor?: string | string[];
			borderWidth?: number;
		}[];
	};
	options?: any;
	className?: string;
}

export const ChartComponent = ({
	type,
	data,
	options,
	className,
}: ChartProps) => {
	return (
		<div className={`bg-white p-4 rounded-lg shadow-md ${className}`}>
			<Chart
				type={type}
				data={data}
				options={{
					responsive: true,
					maintainAspectRatio: false,
					...options,
				}}
			/>
		</div>
	);
};
