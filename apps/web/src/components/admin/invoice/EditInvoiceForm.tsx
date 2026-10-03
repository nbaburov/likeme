import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import {
	InvoiceResponse,
	UpdateInvoiceRequest,
	InvoiceStatus,
} from "@/dto/InvoiceDTO";

interface EditInvoiceFormProps {
	invoice: InvoiceResponse;
	onSubmit: (data: UpdateInvoiceRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
}

export const EditInvoiceForm = ({
	invoice,
	onSubmit,
	onClose,
	isLoading,
}: EditInvoiceFormProps) => {
	const [formData, setFormData] = useState<UpdateInvoiceRequest>({
		amount: invoice.amount,
		status: invoice.status,
	});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => ({
			...prev,
			[name]: name === "amount" ? Number(value) : value,
		}));
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		if (formData?.amount && formData.amount <= 0) return;
		await onSubmit(formData);
	};

	return (
		<form onSubmit={handleSubmit} className="space-y-6">
			<div className="space-y-4">
				<h3 className="text-lg font-semibold">Invoice Details</h3>
				<div className="grid grid-cols-2 gap-4">
					<Input
						label="Amount"
						name="amount"
						type="number"
						value={formData.amount}
						onChange={handleInputChange}
						required
						min={0}
						disabled={isLoading}
					/>
					<Select
						label="Status"
						name="status"
						value={formData.status}
						onChange={handleInputChange}
						options={Object.values(InvoiceStatus).map((status) => ({
							value: status,
							label: status,
						}))}
						required
						disabled={isLoading}
					/>
				</div>
			</div>

			<div className="flex gap-4">
				<Button
					type="button"
					onClick={onClose}
					className="w-full"
					color="secondary"
					disabled={isLoading}
				>
					Cancel
				</Button>
				<Button
					type="submit"
					color="primary"
					className="w-full"
					disabled={isLoading}
				>
					{isLoading ? "Processing..." : "Save Changes"}
				</Button>
			</div>
		</form>
	);
};
